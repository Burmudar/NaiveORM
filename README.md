NaiveORM
========

Small ORM which doesn't do much. Except map results to objects and generate insert queries.

Example for a SelectQuery:

	AdImage adInfo = new AdImage();

	DatabaseManager db = new DatabaseManager(getDatasource());
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

	AdImage adInfo = new AdImage();
	DatabaseManager db = new DatabaseManager(getDatasource());
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

Example for Generated InsertQuery:

	DatabaseManager db = new DatabaseManager(getDatasource());
	MobileAdImage adImage = new MobileAdImage();
	InsertQueryGenerator<MobileAdImage> generator = new InsertQueryGenerator<MobileAdImage>(MobileAdImage.class);
	UpdateQuery query = generator.generate(adImage);
	int rowsChanged = db.<MobileAdImage>executeQuery(query);

Note:
- getDataSource() is a method that uses JNDI lookup for retrieval of a AppServer configured Datasource.
- InsertQueryGenerator will not put fields that are annotated with the @Generated annotation in the generated query.
- For Insert Queries to be generated properly, a class has to be annotated with @Table(schema="MYSCHEMA", name="MYTABLENAME")
- For fields to be recognized within a class for mapping or for insert queries, they should be annotated with @Column(name="MYCOLUMNNAME_AS_IN_TABLE")


