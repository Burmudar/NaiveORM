package co.naive.orm.test.db;

import javax.persistence.Column;
import javax.persistence.Embedded;

public class IntField {
	@Embedded
	private DoubleField doubleField;
	@Column(name="IntField")
	private int intField;
	
	public DoubleField getDoubleField() {
		return doubleField;
	}
	public void setDoubleField(DoubleField doubleField) {
		this.doubleField = doubleField;
	}
	public int getIntField() {
		return intField;
	}
	public void setIntField(int intField) {
		this.intField = intField;
	}
	
	
}
