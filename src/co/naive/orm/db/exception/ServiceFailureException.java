package co.naive.orm.db.exception;


public class ServiceFailureException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9141518312043144664L;

	public ServiceFailureException(String message) {
		super(message);
	}
	
	public ServiceFailureException(String message, Throwable rootCause) {
		super(message,rootCause);
	}
	
}
