package org.ventura.soluciones.commons.wrapper;

import oasis.names.specification.ubl.schema.xsd.creditnote_2.CreditNoteType;

public class NotaCreditoTypeWRP {

    public static NotaCreditoTypeWRP instance = null;

    protected NotaCreditoTypeWRP() {

    }

    public static NotaCreditoTypeWRP getInstance() {
        if (instance == null) {
            instance = new NotaCreditoTypeWRP();
        }
        return instance;
    }

    private CreditNoteType creditNoteType;

    public CreditNoteType getCreditNoteType() {
        return creditNoteType;
    }

    public void setCreditNoteType(CreditNoteType creditNoteType) {
        this.creditNoteType = creditNoteType;
    }

}
