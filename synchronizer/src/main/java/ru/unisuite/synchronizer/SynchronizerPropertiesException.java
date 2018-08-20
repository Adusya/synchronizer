package ru.unisuite.synchronizer;

public class SynchronizerPropertiesException extends Exception {

	private static final long serialVersionUID = 1L;

	private int errorCode;
	
    public SynchronizerPropertiesException(final String message)
    {
        this(0, "See configuration file: " + message);
    }
 
    public SynchronizerPropertiesException(final int errorCode, final String message)
    {
        super(message);

        this.errorCode = errorCode;
    }
    
    public SynchronizerPropertiesException(String message, Throwable cause) {
        super(message, cause);
    }

 
    public int getErrorCode()
    {
        return errorCode;
    }
	
}
