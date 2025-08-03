package org.ventura.cpe.core.erp.interfaces;

import com.sap.smb.sbo.api.IRecordset;
import com.sap.smb.sbo.api.SBOCOMException;

public interface ERPObjectCreator<T> {

    T construirReflection(IRecordset rs, T entity) throws SBOCOMException;
}
