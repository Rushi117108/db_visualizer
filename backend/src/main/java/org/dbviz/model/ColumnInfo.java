package org.dbviz.model;

public class ColumnInfo {

    private String name;

    private String type;
    private boolean primaryKey;
    private boolean foreignKey;
    private boolean nullable;
    private boolean unique;

    // for foreignkey constraint
    private String referencedTable;
    private String referencedColumn;

    public ColumnInfo() {
    }

    public ColumnInfo(String name, String type, boolean primaryKey, boolean foreignKey, boolean nullable, boolean unique, String referencedTable, String referencedColumn) {
        this.name = name;
        this.type = type;
        this.primaryKey = primaryKey;
        this.foreignKey = foreignKey;
        this.nullable = nullable;
        this.unique = unique;
        this.referencedTable = referencedTable;
        this.referencedColumn = referencedColumn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public boolean isForeignKey() {
        return foreignKey;
    }

    public void setForeignKey(boolean foreignKey) {
        this.foreignKey = foreignKey;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public String getReferencedTable() {
        return referencedTable;
    }

    public void setReferencedTable(String referencedTable) {
        this.referencedTable = referencedTable;
    }

    public String getReferencedColumn() {
        return referencedColumn;
    }

    public void setReferencedColumn(String referencedColumn) {
        this.referencedColumn = referencedColumn;
    }
}
