package co.naive.orm.test.db;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import co.naive.orm.db.DBFactory;
import co.naive.orm.db.DBManagerUtil;
import co.naive.orm.db.exception.ServiceFailureException;
import co.naive.orm.db.query.DefaultQueryAdapter;
import co.naive.orm.db.query.PreQueryAdapter;
import co.naive.orm.db.query.SelectQuery;
import co.naive.orm.db.query.UpdateQuery;


public class DBManagerTest {
  private DBManagerUtil db = null;
  
  @DataProvider(name="db-records")
  public Object[][] getRecordsForInsert() throws IOException {
	  ByteArrayOutputStream baos = new ByteArrayOutputStream();
	  ObjectOutputStream oos = new ObjectOutputStream(baos);
	  ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
	  oos.writeObject(new TestDB());
	  
	  return new Object[][] {
			  {1, 1.0, "1",bais,new StringBuffer(4000).toString(), new Date(), new Date()},
			  {2, 2.0, "2",bais,new StringBuffer(4000).toString(), new Date(), new Date()},
			  {3, 3.0, "3",bais,new StringBuffer(4000).toString(), new Date(), new Date()},
			  {4, 4.0, "4",bais,new StringBuffer(4000).toString(), new Date(), new Date()},
			  {5, 5.0, "5",bais,new StringBuffer(4000).toString(), new Date(), new Date()},
			  {6, 6.0, "6",bais,new StringBuffer(4000).toString(), new Date(), new Date()},
			  {7, 7.0, "7",bais,new StringBuffer(4000).toString(), new Date(), new Date()},
			  {8, 8.0, "8",bais,new StringBuffer(4000).toString(), new Date(), new Date()}
	  };
  }
  
  @Test(dataProvider="db-records")
  public void testInsert(Object ... values) throws ServiceFailureException {
	UpdateQuery<Object> insert = TestDB.INSERT_RECORD;
	insert.setQueryParameterAdapter(new DefaultQueryAdapter(values));
	int result = db.executeQuery(insert);
	Assert.assertTrue(result > 0);
  }
  
  @Test(dependsOnMethods= {"testInsert"})
  public void testGetAll() throws ServiceFailureException, IOException {
	  SelectQuery<TestRecord> query = TestDB.SELECT_QUERY;
	  List<TestRecord> results = db.executeQuery(query);
	  Assert.assertTrue(results.size() > 0);
	  Assert.assertTrue(results.size() == 8);
	  int i = 0;
	  Object[][] insertRecords = getRecordsForInsert();
	  for(TestRecord record : results) {
		  verifyTestRecord(record, insertRecords[i]);
		  i++;
	  }
  }
  
  @Test(dependsOnMethods = {"testInsert"})
  public void testGetMap() throws ServiceFailureException {
	  SelectQuery<TestRecord> query = TestDB.SELECT_QUERY;
	  Map<Integer, TestRecord> map = db.<Integer,TestRecord>executeQueryForKeyMappedList(query, false);
	  List<TestRecord> list = db.<TestRecord>executeQuery(query);
	  for(TestRecord test : list) {
		  Assert.assertNotNull(map.get(test.getId()));
	  }
  }
  
  protected void verifyTestRecord(TestRecord record, Object[] values) {
	  Assert.assertNotNull(record);
	  Assert.assertEquals(record.getIntField(), values[0]);
	  Assert.assertEquals(record.getFloatField(), values[1]);
	  Assert.assertEquals(record.getStringField(), values[2]);
	  Assert.assertTrue(record.getBlobData().length >= 0);
  }
  
  protected void verifyTestEmbedded(TestEmbedded record, Object[] values) {
	  Assert.assertNotNull(record);
	  Assert.assertEquals(record.getIntField(), values[0]);
	  Assert.assertEquals(record.getFloatField(), values[1]);
	  Assert.assertEquals(record.getStringField(), values[2]);
	  Assert.assertTrue(record.getBlobs().getBlobData().length >= 0);
  }
  
  @Test(dependsOnMethods={"testInsert"})
  public void testEmbeddedMapping() throws IOException, ServiceFailureException {
	  SelectQuery<TestEmbedded> query = TestDB.SELECT_QUERY_EMBEDDED;
	  List<TestEmbedded> results = db.executeQuery(query);
	  Assert.assertTrue(results.size() > 0);
	  Assert.assertTrue(results.size() == 8);
	  int i = 0;
	  Object[][] insertRecords = getRecordsForInsert();
	  for(TestEmbedded record : results) {
		  verifyTestEmbedded(record, insertRecords[i]);
		  i++;
	  }
  }
  @Test(dependsOnMethods={"testInsert"}, dataProvider="db-records")
  public void testInsertWithGeneratedMapping(Object ... values) throws ServiceFailureException {
	  TestRecord testRecord = new TestRecord();
	  testRecord.setIntField((Integer) values[0]);
	  testRecord.setFloatField((Double) values[1]);
	  testRecord.setStringField((String) values[2]);
	  UpdateQuery<TestRecord> insert = new UpdateQuery<TestRecord>(TestDB.INSERT_RECORD_SQL, testRecord, (PreQueryAdapter)(new DefaultQueryAdapter(values)));
	  db.executeQuery(insert);
	  List<TestRecord> instances = insert.getInstances();
	  Assert.assertEquals(instances.size(), 1);
	  Assert.assertTrue(instances.get(0).getId() > 0);
	  Assert.assertTrue(testRecord.getId() > 0);
	  
  }
  
  @Test(dependsOnMethods={"testGetAll"})
  public void testDeeplyEmbedded() throws ServiceFailureException {
	  SelectQuery<NestedTest> query = TestDB.SELECT_QUERY_NESTED;
	  List<NestedTest> results = db.executeQuery(query);
	  Assert.assertTrue(results.size() > 0);
	  for(NestedTest nested : results) {
		  IntField intField = nested.getIntField();
		  Assert.assertNotNull(intField);
		  Assert.assertTrue(intField.getIntField() > -1);
		  DoubleField doubleField = intField.getDoubleField();
		  Assert.assertNotNull(doubleField);
		  Assert.assertTrue(doubleField.getFloatField() > -1);
		  StringField stringField = doubleField.getStringField();
		  Assert.assertNotNull(stringField);
		  Assert.assertTrue(stringField.getStringField().length() > 0);
		  DateField dateField = stringField.getDateField();
		  Assert.assertNotNull(dateField);
		  TestRecord testRecord = dateField.getTestRecord();
		  Assert.assertNotNull(testRecord);
	  }
  }
  
  @BeforeClass
  public void beforeSuite() throws ServiceFailureException {
	Map<String,String> meta = new HashMap<String,String>();
	meta.put("host", "./");
	meta.put("dbname", "TestDB");
	db = DBFactory.initDerbyDB(meta);
	try {
		db.executeQuery(TestDB.DROP_TABLE);
	} catch (ServiceFailureException e) {
		
	}
	db.executeQuery(TestDB.CREATE_TABLE);
  }

  @AfterClass
  public void afterSuite() throws ServiceFailureException {
	  db.executeQuery(TestDB.DROP_TABLE);
  }

}
