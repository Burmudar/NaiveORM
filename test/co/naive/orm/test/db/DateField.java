package co.naive.orm.test.db;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embedded;

public class DateField {
	@Embedded
	private TestRecord testRecord;
	
	@Column(name="DateField")
	private Date date;

	public TestRecord getTestRecord() {
		return testRecord;
	}

	public void setTestRecord(TestRecord testRecord) {
		this.testRecord = testRecord;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	
}
