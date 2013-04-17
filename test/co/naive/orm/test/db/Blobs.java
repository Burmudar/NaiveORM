package co.naive.orm.test.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;

import javax.persistence.Column;

public class Blobs {
	@Column(name="BlobField")
	private Blob blobField;
	@Column(name="ClobField")
	private Clob clobField;
	
	private byte[] blobData;
	private String clobData;
	
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
	
	
}
