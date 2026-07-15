package org.dbviz.model;

import java.util.List;

public class TableNode {

    private String id;         // same as name, used by React Flow
    private String name;
    private List<ColumnInfo> columns;

    // Derived counts for quick UI display
    private int pkCount;
    private int fkCount;
    private int columnCount;

    public TableNode(String id, String name, List<ColumnInfo> columns, int pkCount, int fkCount, int columnCount) {
        this.id = id;
        this.name = name;
        this.columns = columns;
        this.pkCount = pkCount;
        this.fkCount = fkCount;
        this.columnCount = columnCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ColumnInfo> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnInfo> columns) {
        this.columns = columns;
    }

    public int getPkCount() {
        return pkCount;
    }

    public void setPkCount(int pkCount) {
        this.pkCount = pkCount;
    }

    public int getFkCount() {
        return fkCount;
    }

    public void setFkCount(int fkCount) {
        this.fkCount = fkCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }
}
