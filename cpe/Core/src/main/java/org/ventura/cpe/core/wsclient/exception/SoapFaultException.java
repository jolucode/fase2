package org.ventura.cpe.core.wsclient.exception;

import org.ventura.cpe.core.exception.ErrorObj;

public class SoapFaultException extends Exception implements java.io.Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 4770411452264097320L;

    /**
     * The fault that caused this exception.
     */
    private ErrorObj error;

    /**
     * Constructor.
     */
    public SoapFaultException(String message) {
        super(message);
    }


    /**
     * Constructor.
     *
     * @param wrappedException exception to be wrapped by this one
     */
    public SoapFaultException(Throwable e) {
        super(e);
    }

    /**
     * Constructor.
     *
     * @param message          exception message
     * @param wrappedException exception to be wrapped by this one
     */
    public SoapFaultException(String message, Throwable e) {
        super(message, e);
    }

    public SoapFaultException(ErrorObj error) {
        super(error.getMessage());
        this.error = error;
    }

    public SoapFaultException(ErrorObj error, Throwable e) {
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