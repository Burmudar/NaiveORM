package co.naive.orm.test.db;

import javax.persistence.Column;
import javax.persistence.Embedded;

public class DoubleField {
	@Embedded
	private StringField stringField;
	
	@Column(name="FloatField")
	private double floatField;

	public StringField getStringField() {
		return stringField;
	}

	public void setStringField(StringField stringField) {
		this.stringField = stringField;
	}

	public double getFloatField() {
		return floatField;
	}

	public void setFloatField(double floatField) {
		this.floatField = floatField;
	}
	
	
}
