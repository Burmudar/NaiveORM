package co.naive.orm.db.exception;

import java.sql.SQLException;

public class DatabaseManagerException extends Exception{

	public DatabaseManagerException(String msg) {
		super(msg);
	}

	public DatabaseManagerException(String msg, Throwable e) {
		super(msg,e);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -7891645013594713577L;
	
	

}
