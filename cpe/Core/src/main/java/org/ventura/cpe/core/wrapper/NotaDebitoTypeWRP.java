package org.ventura.cpe.core.wrapper;

import oasis.names.specification.ubl.schema.xsd.debitnote_2.DebitNoteType;

public class NotaDebitoTypeWRP {

    public static NotaDebitoTypeWRP instance = null;

    protected NotaDebitoTypeWRP() {

    }

    public static NotaDebitoTypeWRP getInstance() {
        if (instance == null) {
            instance = new NotaDebitoTypeWRP();
        }
        return instance;
    }

    private DebitNoteType debitNoteType;

    public DebitNoteType getDebitNoteType() {
        return debitNoteType;
    }

    public void setDebitNoteType(DebitNoteType debitNoteType) {
        this.debitNoteType = debitNoteType;
    }

}
