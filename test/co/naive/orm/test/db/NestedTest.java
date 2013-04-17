package co.naive.orm.test.db;

import javax.persistence.Embedded;

public class NestedTest {
	@Embedded
	private IntField intField;

	public IntField getIntField() {
		return intField;
	}

	public void setIntField(IntField intField) {
		this.intField = intField;
	}
}
