package org.dbviz.model;

public class SchemaEdge {

    private String id;              // e.g. "orders_users_fk"
    private String source;          // source table name
    private String sourceColumn;    // FK column
    private String target;          // referenced table name
    private String targetColumn;    // referenced column (usually PK)
    private String cardinality;     // "ONE_TO_MANY" | "MANY_TO_ONE" | "ONE_TO_ONE"

    public String getId() {
        return id;
    }

    public SchemaEdge(String id, String source, String sourceColumn, String target, String targetColumn, String cardinality) {
        this.id = id;
        this.source = source;
        this.sourceColumn = sourceColumn;
        this.target = target;
        this.targetColumn = targetColumn;
        this.cardinality = cardinality;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSourceColumn() {
        return sourceColumn;
    }

    public void setSourceColumn(String sourceColumn) {
        this.sourceColumn = sourceColumn;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getTargetColumn() {
        return targetColumn;
    }

    public void setTargetColumn(String targetColumn) {
        this.targetColumn = targetColumn;
    }

    public String getCardinality() {
        return cardinality;
    }

    public void setCardinality(String cardinality) {
        this.cardinality = cardinality;
    }
}
