package org.ventura.cpe.core.exception;

public class ConectionSunatException extends Exception implements java.io.Serializable {

    /**
     * The fault that caused this exception.
     */
    private ErrorObj error;

    /**
     * Constructor.
     */
    public ConectionSunatException(String message) {
        super(message);
    }


    /**
     * Constructor.
     *
     * @param e exception to be wrapped by this one
     */
    public ConectionSunatException(Throwable e) {
        super(e);
    }

    /**
     * Constructor.
     *
     * @param message exception message
     * @param e       exception to be wrapped by this one
     */
    public ConectionSunatException(String message, Throwable e) {
        super(message, e);
    }

    public ConectionSunatException(ErrorObj error) {
        super(error.getMessage());
        this.error = error;
    }

    public ConectionSunatException(ErrorObj error, Throwable e) {
        super(error.getMessage(), e);
        this.error = error;
    } //SunatGenericException

    public ErrorObj getError() {
        return error;
    } //getError

    public void setError(ErrorObj error) {
        this.error = error;
    } //setError

}
