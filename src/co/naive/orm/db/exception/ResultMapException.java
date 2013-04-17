package co.naive.orm.db.exception;

public class ResultMapException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -701103003422901005L;

	public ResultMapException(String message) {
		super(message);
	}
	
	public ResultMapException(String message, Throwable rootCause) {
		super(message,rootCause);
	}
}
