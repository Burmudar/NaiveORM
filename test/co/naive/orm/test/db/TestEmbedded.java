package co.naive.orm.test.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;

import javax.persistence.Column;
import javax.persistence.Embedded;

public class TestEmbedded {
	@Column(name="Id")
	private int id;
	@Column(name="IntField")
	private int intField;
	@Column(name="FloatField")
	private double floatField;
	@Column(name="StringField")
	private String stringField;
	
	@Embedded
	private Blobs blobs;
	
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
	public Blobs getBlobs() {
		return blobs;
	}
	public void setBlobs(Blobs blobs) {
		this.blobs = blobs;
	}

	
	
	
}
