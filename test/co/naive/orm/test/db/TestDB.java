package co.naive.orm.test.db;

import java.io.Serializable;

import co.naive.orm.db.query.SelectQuery;
import co.naive.orm.db.query.UpdateQuery;


public class TestDB implements Serializable{
	public static UpdateQuery<Object> CREATE_TABLE = new UpdateQuery<Object>(
			  "CREATE TABLE TEST ("
			+ "Id INTEGER PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),"
			+ "IntField INTEGER NOT NULL,"
			+ "FloatField DOUBLE NOT NULL,"
			+ "StringField VARCHAR(255) NOT NULL,"
			+ "BlobField BLOB,"
			+ "ClobField CLOB,"
			+ "DateField DATE,"
			+ "TimestampField TIMESTAMP"
			+ ")");
	
	public static UpdateQuery<Object> DROP_TABLE = new UpdateQuery<Object>("DROP TABLE TEST");
	
	public static SelectQuery<TestRecord> SELECT_QUERY = new SelectQuery<TestRecord>(
			"SELECT * FROM TEST ORDER BY IntField ASC", TestRecord.class);
	
	public static SelectQuery<NestedTest> SELECT_QUERY_NESTED = new SelectQuery<NestedTest>(
			"SELECT * FROM TEST ORDER BY IntField ASC", NestedTest.class);
	
	public static SelectQuery<TestEmbedded> SELECT_QUERY_EMBEDDED = new SelectQuery<TestEmbedded>(
			"SELECT * FROM TEST ORDER BY IntField ASC", TestEmbedded.class);
	
	public static SelectQuery<TestRecord> SELECT_QUERY_INT_FIELD = new SelectQuery<TestRecord>(
			"SELCT * FROM TEST WHERE intField = ?", TestRecord.class);
	
	public static String INSERT_RECORD_SQL = 
			  "INSERT INTO TEST"
			+ "(IntField, FloatField, StringField, BlobField, ClobField, DateField, TimestampField) VALUES(?,?,?,?,?,?,?)";
	public static UpdateQuery<Object> INSERT_RECORD = new UpdateQuery<Object>(INSERT_RECORD_SQL);
	
}
