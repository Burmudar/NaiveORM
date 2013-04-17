package co.naive.orm.test.db;

import javax.persistence.Column;
import javax.persistence.Embedded;

public class StringField {
	@Embedded
	private DateField dateField;
	
	@Column(name="StringField")
	private String stringField;

	public DateField getDateField() {
		return dateField;
	}

	public void setDateField(DateField dateField) {
		this.dateField = dateField;
	}

	public String getStringField() {
		return stringField;
	}

	public void setStringField(String stringField) {
		this.stringField = stringField;
	}
	
	
}
