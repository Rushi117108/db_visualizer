package org.dbviz.model;

import java.util.List;

public class GraphDiagram {

    private List<TableNode> tableNodes;

    private List<SchemaEdge> schemaEdges;

    private String dialect;

    private int totalTables;

    private int totalRelationships;

    private String parseWarning;   // non-fatal issues during parsing

    public List<TableNode> getTableNodes() {
        return tableNodes;
    }

    public void setTableNodes(List<TableNode> tableNodes) {
        this.tableNodes = tableNodes;
    }

    public List<SchemaEdge> getSchemaEdges() {
        return schemaEdges;
    }

    public void setSchemaEdges(List<SchemaEdge> schemaEdges) {
        this.schemaEdges = schemaEdges;
    }

    public int getTotalTables() {
        return totalTables;
    }

    public void setTotalTables(int totalTables) {
        this.totalTables = totalTables;
    }

    public int getTotalRelationships() {
        return totalRelationships;
    }

    public void setTotalRelationships(int totalRelationships) {
        this.totalRelationships = totalRelationships;
    }

    public String getParseWarning() {
        return parseWarning;
    }

    public void setParseWarning(String parseWarning) {
        this.parseWarning = parseWarning;
    }

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    public GraphDiagram(){};

    public GraphDiagram(List<TableNode> tableNodes, List<SchemaEdge> schemaEdges, String dialect, int totalTables, int totalRelationships, String parseWarning) {
        this.tableNodes = tableNodes;
        this.schemaEdges = schemaEdges;
        this.dialect = dialect;
        this.totalTables = totalTables;
        this.totalRelationships = totalRelationships;
        this.parseWarning = parseWarning;
    }
}
