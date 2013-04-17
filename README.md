NaiveORM
========

Small ORM which doesn't do much. Except map results to objects and generate insert queries.

Example of a class that will be mapped:

    @Table(schema="TESTSCHEMA", name="TEST_TABLE")
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

As long as the @Embedded class also contains valid mapping annotations (i.e @Column, @Embedded) it will be mapped. In the above example Blobs looks like the following:

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

Another concept depicted in the above example of Blobs class is how to read Clob or Blob streams by still staying inside the bounds of the framework. The reason the class has the additional fields blobData and ClobData is due to the fact, after the framework has called the set methods for the annotated fields the streams to the fields will be closed.
Therefore when the fields are accessed at a later stage an exception will be thrown. Thus inside each of the set methods in the above example the stream is immediately read and assigned to another field.

Example for a SelectQuery with a specific QueryAdapter:

	AdImage adInfo = new AdImage();

	Map<String,String> meta = new HashMap<String,String>();
	meta.put("host", "./");
	meta.put("dbname", "TestDB");
	db = DBFactory.initDerbyDB(meta);
	String queryString = "select * from MYSCHEMA.AD_IMAGES";
	SelectQuery<AdImage> query = new SelectQuery<AdImage>(queryString, AdImage.class);
	query.setQueryParameterAdapter(new QueryParameterAdapter() {
		
		public void setParametersOn(PreparedStatement statement)
				throws SQLException {
			statement.setInt(1, imageId);
		}
	});
	try {
		List<AdImage> images = db.<AdImage>executeQuery(query);
		if(images.isEmpty() == false) {
			adInfo = images.get(0);
		} else {
			logger.debug("<AdInfo> There is no Ad information in the database.");
		}
	} catch (DatabaseManagerException e) {
		logger.debug(e);
	}

Example for a SelectQuery with where clause:

	Map<String,String> meta = new HashMap<String,String>();
	meta.put("host", "./");
	meta.put("dbname", "TestDB");
	db = DBFactory.initDerbyDB(meta);
	AdImage adInfo = new AdImage();
	String queryString = "select * from MYSCHEMA.AD_IMAGES where id = ?";
	SelectQuery<AdImage> query = new SelectQuery<AdImage>(queryString, AdImage.class);
	query.setQueryParameterAdapter(new QueryParameterAdapter() {
		
		public void setParametersOn(PreparedStatement statement)
				throws SQLException {
			statement.setInt(1, imageId);
		}
	});
	try {
		List<AdImage> images = db.<AdImage>executeQuery(query);
		if(images.isEmpty() == false) {
			adInfo = images.get(0);
		} else {
			logger.debug("<AdInfo> There is no Ad information in the database.");
		}
	} catch (DatabaseManagerException e) {
		logger.debug(e);
	}

Example of a SelectQuery that utilizes the DefaultQueryAdapter. The default adapter just sequentially goes through the given values to be used for mapping and sets them on the prepared statement.

	Map<String,String> meta = new HashMap<String,String>();
	meta.put("host", "./");
	meta.put("dbname", "TestDB");
	db = DBFactory.initDerbyDB(meta);
    UpdateQuery<Object> insert = TestDB.INSERT_RECORD;
	insert.setQueryParameterAdapter(new DefaultQueryAdapter(values));
	int result = db.executeQuery(insert);


Example for Generated InsertQuery:

	Map<String,String> meta = new HashMap<String,String>();
	meta.put("host", "./");
	meta.put("dbname", "TestDB");
	db = DBFactory.initDerbyDB(meta);
	MobileAdImage adImage = new MobileAdImage();
	InsertQueryGenerator<MobileAdImage> generator = new InsertQueryGenerator<MobileAdImage>(MobileAdImage.class);
	UpdateQuery query = generator.generate(adImage);
	int rowsChanged = db.<MobileAdImage>executeQuery(query);

Note:
- InsertQueryGenerator will not put fields that are annotated with the @Generated annotation in the generated query.
- For Insert Queries to be generated properly, a class has to be annotated with @Table(schema="MYSCHEMA", name="MYTABLENAME")
- For fields to be recognized within a class for mapping or for insert queries, they should be annotated with @Column(name="MYCOLUMNNAME_AS_IN_TABLE")


