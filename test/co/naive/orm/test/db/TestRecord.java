package co.naive.orm.test.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;

import co.naive.orm.annotation.Key;


public class TestRecord {
	@Key
	@GeneratedValue
	@Column(name="Id")
	private int id;
	@Column(name="IntField")
	private int intField;
	@Column(name="FloatField")
	private double floatField;
	@Column(name="StringField")
	private String stringField;
	@Column(name="BlobField")
	private Blob blobField;
	@Column(name="ClobField")
	private Clob clobField;
	@Column(name="DateField")
	private Date dateField;
	@Column(name="TimestampField")
	private Timestamp timestampField;
	
	private byte[] blobData;
	private String clobData;
	
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getIntField() {
		return intField;
	}
	public void setIntField(int intField) {
		this.intField = intField;
	}
	public double getFloatField() {
		return floatField;
	}
	public void setFloatField(double floatField) {
		this.floatField = floatField;
	}
	
	public String getStringField() {
		return stringField;
	}
	public void setStringField(String stringField) {
		this.stringField = stringField;
	}
	public Blob getBlobField() {
		return blobField;
	}
	public void setBlobField(Blob blobField) {
		this.blobField = blobField;
		try {
			setBlobData(blobField.getBytes(1, (int) blobField.length()));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public Clob getClobField() {
		return clobField;
	}
	public void setClobField(Clob clobField) {
		this.clobField = clobField;
		BufferedReader reader = null;
		try {
		reader = new BufferedReader(clobField.getCharacterStream());
		setClobData(reader.readLine());
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public byte[] getBlobData() {
		return blobData;
	}
	public void setBlobData(byte[] blobData) {
		this.blobData = blobData;
	}
	public String getClobData() {
		return clobData;
	}
	public void setClobData(String clobData) {
		this.clobData = clobData;
	}
	public Date getDateField() {
		return dateField;
	}
	public void setDateField(Date dateField) {
		this.dateField = dateField;
	}
	public Timestamp getTimestampField() {
		return timestampField;
	}
	public void setTimestampField(Timestamp timestampField) {
		this.timestampField = timestampField;
	}
	
	
	
	
}
