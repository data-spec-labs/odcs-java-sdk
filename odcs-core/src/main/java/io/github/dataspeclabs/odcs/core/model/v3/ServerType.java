package io.github.dataspeclabs.odcs.core.model.v3;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Known ODCS server types, plus {@link #OTHER} as a fallback for unknown values.
 */
public enum ServerType {
    API("api"),
    ATHENA("athena"),
    AZURE("azure"),
    BIGQUERY("bigquery"),
    CLICKHOUSE("clickhouse"),
    CLOUDSQL("cloudsql"),
    CUSTOM("custom"),
    DATABRICKS("databricks"),
    DB2("db2"),
    DENODO("denodo"),
    DREMIO("dremio"),
    DUCKDB("duckdb"),
    GLUE("glue"),
    HIVE("hive"),
    IMPALA("impala"),
    INFORMIX("informix"),
    KAFKA("kafka"),
    KINESIS("kinesis"),
    LOCAL("local"),
    MYSQL("mysql"),
    ORACLE("oracle"),
    POSTGRES("postgres"),
    POSTGRESQL("postgresql"),
    PRESTO("presto"),
    PUBSUB("pubsub"),
    REDSHIFT("redshift"),
    S3("s3"),
    SFTP("sftp"),
    SNOWFLAKE("snowflake"),
    SQLSERVER("sqlserver"),
    SYNAPSE("synapse"),
    TRINO("trino"),
    VERTICA("vertica"),
    ZEN("zen"),
    OTHER("other");

    private final String value;

    ServerType(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @JsonCreator
    public static ServerType fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (ServerType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        return OTHER;
    }
}
