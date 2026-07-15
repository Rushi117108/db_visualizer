package org.dbviz.service;

import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.table.ForeignKeyIndex;
import net.sf.jsqlparser.statement.create.table.Index;
import org.dbviz.model.ColumnInfo;
import org.dbviz.model.GraphDiagram;
import org.dbviz.model.SchemaEdge;
import org.dbviz.model.TableNode;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DbVisualizeService {

    public GraphDiagram parse(String sqlContent) {
        List<String> warnings = new ArrayList<>();
        String cleaned = preprocessSql(sqlContent);

        Statements statements;
        try {
            statements = CCJSqlParserUtil.parseStatements(cleaned);
        } catch (Exception e) {
            return parseStatementByStatement(cleaned, warnings);
        }

        // Maps: tableName -> data
        Map<String, List<ColumnDefinition>> tableColumns = new LinkedHashMap<>();
        Map<String, List<ForeignKeyIndex>> tableFks = new LinkedHashMap<>();
        Map<String, Set<String>> tablePks = new LinkedHashMap<>();

        for (Statement stmt : statements.getStatements()) {
            if (!(stmt instanceof CreateTable ct)) continue;

            String tableName = stripQuotes(ct.getTable().getName());
            List<ColumnDefinition> colDefs = ct.getColumnDefinitions() != null
                    ? ct.getColumnDefinitions() : new ArrayList<>();

            tableColumns.put(tableName, colDefs);

            Set<String> pkCols = new HashSet<>();
            List<ForeignKeyIndex> fkList = new ArrayList<>();

            if (ct.getIndexes() != null) {
                for (Index index : ct.getIndexes()) {
                    if (index instanceof ForeignKeyIndex fk) {
                        fkList.add(fk);
                    } else if ("PRIMARY KEY".equalsIgnoreCase(index.getType())) {
                        index.getColumnsNames().forEach(c -> pkCols.add(stripQuotes(c)));
                    }
                }
            }

            // Check inline PRIMARY KEY in column specs
            for (ColumnDefinition col : colDefs) {
                if (col.getColumnSpecs() != null) {
                    String specs = String.join(" ", col.getColumnSpecs()).toUpperCase();
                    if (specs.contains("PRIMARY KEY")) {
                        pkCols.add(stripQuotes(col.getColumnName()));
                    }
                }
            }

            tablePks.put(tableName, pkCols);
            tableFks.put(tableName, fkList);
        }

        // FK column lookup: tableName -> { colName -> ForeignKeyIndex }
        Map<String, Map<String, ForeignKeyIndex>> fkColumnMap = new LinkedHashMap<>();
        for (Map.Entry<String, List<ForeignKeyIndex>> entry : tableFks.entrySet()) {
            Map<String, ForeignKeyIndex> colToFk = new HashMap<>();
            for (ForeignKeyIndex fk : entry.getValue()) {
                if (fk.getColumnsNames() != null) {
                    fk.getColumnsNames().forEach(c -> colToFk.put(stripQuotes(c), fk));
                }
            }
            fkColumnMap.put(entry.getKey(), colToFk);
        }

        // Build TableNode list
        List<TableNode> tables = new ArrayList<>();
        for (Map.Entry<String, List<ColumnDefinition>> entry : tableColumns.entrySet()) {
            String tableName = entry.getKey();
            List<ColumnDefinition> colDefs = entry.getValue();
            Set<String> pkCols = tablePks.getOrDefault(tableName, new HashSet<>());
            Map<String, ForeignKeyIndex> colFkMap = fkColumnMap.getOrDefault(tableName, new HashMap<>());

            List<ColumnInfo> columns = new ArrayList<>();
            for (ColumnDefinition col : colDefs) {
                String colName = stripQuotes(col.getColumnName());
                String colType = resolveType(col.getColDataType());
                boolean isPk = pkCols.contains(colName);
                boolean isFk = colFkMap.containsKey(colName);
                boolean isNullable = true;
                boolean isUnique = false;
                String refTable = null;
                String refCol = null;

                if (col.getColumnSpecs() != null) {
                    String specs = String.join(" ", col.getColumnSpecs()).toUpperCase();
                    if (specs.contains("NOT NULL")) isNullable = false;
                    if (specs.contains("UNIQUE")) isUnique = true;
                }

                if (isFk) {
                    ForeignKeyIndex fk = colFkMap.get(colName);
                    refTable = stripQuotes(fk.getTable().getName());
                    refCol = fk.getReferencedColumnNames() != null && !fk.getReferencedColumnNames().isEmpty()
                            ? stripQuotes(fk.getReferencedColumnNames().get(0)) : "id";
                }

                columns.add(new ColumnInfo(colName, colType, isPk, isFk, isNullable, isUnique, refTable, refCol));
            }

            int pkCount = (int) columns.stream().filter(ColumnInfo::isPrimaryKey).count();
            int fkCount = (int) columns.stream().filter(ColumnInfo::isForeignKey).count();

            tables.add(new TableNode(tableName, tableName, columns, pkCount, fkCount, columns.size()));
        }

        // Build edges from FK relationships
        List<SchemaEdge> edges = new ArrayList<>();
        for (Map.Entry<String, List<ForeignKeyIndex>> entry : tableFks.entrySet()) {
            String sourceTable = entry.getKey();
            for (ForeignKeyIndex fk : entry.getValue()) {
                if (fk.getTable() == null) continue;
                String targetTable = stripQuotes(fk.getTable().getName());
                String sourceCol = fk.getColumnsNames() != null && !fk.getColumnsNames().isEmpty()
                        ? stripQuotes(fk.getColumnsNames().get(0)) : "";
                String targetCol = fk.getReferencedColumnNames() != null && !fk.getReferencedColumnNames().isEmpty()
                        ? stripQuotes(fk.getReferencedColumnNames().get(0)) : "id";

                String edgeId = sourceTable + "_" + sourceCol + "_" + targetTable + "_fk";
                edges.add(new SchemaEdge(edgeId, sourceTable, sourceCol, targetTable, targetCol, "MANY_TO_ONE"));
            }
        }

        String warning = warnings.isEmpty() ? null : String.join("; ", warnings);
        return new GraphDiagram(tables, edges, detectDialect(sqlContent), tables.size(), edges.size(), warning);
    }

    private String stripQuotes(String name) {
        if (name == null) return "";
        return name.replaceAll("^[`\"'\\[]|[`\"'\\]]$", "").trim();
    }

    private String detectDialect(String sql) {
        String upper = sql.toUpperCase();
        if (upper.contains("AUTO_INCREMENT")) return "MySQL";
        if (upper.contains("SERIAL") || upper.contains("RETURNING")) return "PostgreSQL";
        if (upper.contains("AUTOINCREMENT")) return "SQLite";
        if (upper.contains("NUMBER(") || upper.contains("VARCHAR2")) return "Oracle";
        return "Generic SQL";
    }

    private String resolveType(net.sf.jsqlparser.statement.create.table.ColDataType dataType) {
        if (dataType == null) return "UNKNOWN";
        String base = dataType.getDataType().toUpperCase();
        if (dataType.getArgumentsStringList() != null && !dataType.getArgumentsStringList().isEmpty()) {
            return base + "(" + String.join(", ", dataType.getArgumentsStringList()) + ")";
        }
        return base;
    }

    public GraphDiagram getGraphStructure(String sqlContent) {

        List<TableNode> tableNodeList = new ArrayList<>();
        List<SchemaEdge> schemaEdges = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        String cleaned = preprocessSql(sqlContent);

        Statements statements;
        try {
            statements = CCJSqlParserUtil.parseStatements(cleaned);
        } catch (Exception e) {
            // Try parsing statement by statement for partial recovery
            return parseStatementByStatement(cleaned, warnings);
        }

        Map<String, List<ColumnDefinition>> tableColumns = new LinkedHashMap<>();
        Map<String, List<ForeignKeyIndex>> tableFks = new LinkedHashMap<>();
        Map<String, Set<String>> tablePks = new LinkedHashMap<>();

        for (Statement stmt : statements.getStatements()) {
            if (!(stmt instanceof CreateTable ct)) continue;

            String tableName = stripQuotes(ct.getTable().getName());
            List<ColumnDefinition> colDefs = ct.getColumnDefinitions() != null
                    ? ct.getColumnDefinitions() : new ArrayList<>();

            tableColumns.put(tableName, colDefs);

            Set<String> pkCols = new HashSet<>();
            List<ForeignKeyIndex> fkList = new ArrayList<>();

            if (ct.getIndexes() != null) {
                for (Index index : ct.getIndexes()) {
                    if (index instanceof ForeignKeyIndex fk) {
                        fkList.add(fk);
                    } else if ("PRIMARY KEY".equalsIgnoreCase(index.getType())) {
                        index.getColumnsNames().forEach(c -> pkCols.add(stripQuotes(c)));
                    }
                }
            }

            // Check inline PRIMARY KEY in column specs
            for (ColumnDefinition col : colDefs) {
                if (col.getColumnSpecs() != null) {
                    String specs = String.join(" ", col.getColumnSpecs()).toUpperCase();
                    if (specs.contains("PRIMARY KEY")) {
                        pkCols.add(stripQuotes(col.getColumnName()));
                    }
                }
            }

            tablePks.put(tableName, pkCols);
            tableFks.put(tableName, fkList);
        }

        // FK column lookup: tableName -> { colName -> ForeignKeyIndex }
        Map<String, Map<String, ForeignKeyIndex>> fkColumnMap = new LinkedHashMap<>();
        for (Map.Entry<String, List<ForeignKeyIndex>> entry : tableFks.entrySet()) {
            Map<String, ForeignKeyIndex> colToFk = new HashMap<>();
            for (ForeignKeyIndex fk : entry.getValue()) {
                if (fk.getColumnsNames() != null) {
                    fk.getColumnsNames().forEach(c -> colToFk.put(stripQuotes(c), fk));
                }
            }
            fkColumnMap.put(entry.getKey(), colToFk);
        }

        // Build TableNode list
        List<TableNode> tables = new ArrayList<>();
        for (Map.Entry<String, List<ColumnDefinition>> entry : tableColumns.entrySet()) {
            String tableName = entry.getKey();
            List<ColumnDefinition> colDefs = entry.getValue();
            Set<String> pkCols = tablePks.getOrDefault(tableName, new HashSet<>());
            Map<String, ForeignKeyIndex> colFkMap = fkColumnMap.getOrDefault(tableName, new HashMap<>());

            List<ColumnInfo> columns = new ArrayList<>();
            for (ColumnDefinition col : colDefs) {
                String colName = stripQuotes(col.getColumnName());
                String colType = resolveType(col.getColDataType());
                boolean isPk = pkCols.contains(colName);
                boolean isFk = colFkMap.containsKey(colName);
                boolean isNullable = true;
                boolean isUnique = false;
                String refTable = null;
                String refCol = null;

                if (col.getColumnSpecs() != null) {
                    String specs = String.join(" ", col.getColumnSpecs()).toUpperCase();
                    if (specs.contains("NOT NULL")) isNullable = false;
                    if (specs.contains("UNIQUE")) isUnique = true;
                }

                if (isFk) {
                    ForeignKeyIndex fk = colFkMap.get(colName);
                    refTable = stripQuotes(fk.getTable().getName());
                    refCol = fk.getReferencedColumnNames() != null && !fk.getReferencedColumnNames().isEmpty()
                            ? stripQuotes(fk.getReferencedColumnNames().get(0)) : "id";
                }

                columns.add(new ColumnInfo(colName, colType, isPk, isFk, isNullable, isUnique, refTable, refCol));
            }

            int pkCount = (int) columns.stream().filter(ColumnInfo::isPrimaryKey).count();
            int fkCount = (int) columns.stream().filter(ColumnInfo::isForeignKey).count();

            tables.add(new TableNode(tableName, tableName, columns, pkCount, fkCount, columns.size()));
        }

        // Build edges from FK relationships
        List<SchemaEdge> edges = new ArrayList<>();
        for (Map.Entry<String, List<ForeignKeyIndex>> entry : tableFks.entrySet()) {
            String sourceTable = entry.getKey();
            for (ForeignKeyIndex fk : entry.getValue()) {
                if (fk.getTable() == null) continue;
                String targetTable = stripQuotes(fk.getTable().getName());
                String sourceCol = fk.getColumnsNames() != null && !fk.getColumnsNames().isEmpty()
                        ? stripQuotes(fk.getColumnsNames().get(0)) : "";
                String targetCol = fk.getReferencedColumnNames() != null && !fk.getReferencedColumnNames().isEmpty()
                        ? stripQuotes(fk.getReferencedColumnNames().get(0)) : "id";

                String edgeId = sourceTable + "_" + sourceCol + "_" + targetTable + "_fk";
                edges.add(new SchemaEdge(edgeId, sourceTable, sourceCol, targetTable, targetCol, "MANY_TO_ONE"));
            }
        }

        String warning = warnings.isEmpty() ? null : String.join("; ", warnings);
        return new GraphDiagram(tables, edges, detectDialect(sqlContent), tables.size(), edges.size(), warning);
    }

    private String preprocessSql(String sql) {
        // Remove single-line comments
        String noLineComments = sql.replaceAll("--[^\n]*", "");
        // Remove block comments
        String noBlockComments = noLineComments.replaceAll("/\\*[\\s\\S]*?\\*/", "");
        return noBlockComments.trim();
    }

    private GraphDiagram parseStatementByStatement(String sql, List<String> warnings) {
        String[] parts = sql.split("(?i)(?=CREATE\\s+TABLE)");
        StringBuilder recovered = new StringBuilder();
        int skipped = 0;

        for (String part : parts) {
            try {
                if (!part.trim().isEmpty()) {
                    CCJSqlParserUtil.parse(part.trim().replaceAll(";$", ""));
                    recovered.append(part).append("\n");
                }
            } catch (Exception e) {
                skipped++;
            }
        }

        if (skipped > 0) warnings.add(skipped + " statement(s) skipped due to parse errors");

        if (recovered.isEmpty()) {
            return new GraphDiagram(new ArrayList<>(), new ArrayList<>(), "Unknown", 0, 0,
                    "Could not parse any valid CREATE TABLE statements");
        }

        try {
            return parse(recovered.toString());
        } catch (Exception e) {
            return new GraphDiagram(new ArrayList<>(), new ArrayList<>(), "Unknown", 0, 0,
                    "Parsing failed: " + e.getMessage());
        }
    }
}
