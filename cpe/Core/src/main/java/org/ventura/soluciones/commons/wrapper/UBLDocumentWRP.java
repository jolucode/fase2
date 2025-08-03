package org.ventura.soluciones.commons.wrapper;

import oasis.names.specification.ubl.schema.xsd.creditnote_2.CreditNoteType;
import oasis.names.specification.ubl.schema.xsd.debitnote_2.DebitNoteType;
import oasis.names.specification.ubl.schema.xsd.despatchadvice_2.DespatchAdviceType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;
import org.ventura.cpe.core.domain.Transaccion;
import sunat.names.specification.ubl.peru.schema.xsd.perception_1.PerceptionType;
import sunat.names.specification.ubl.peru.schema.xsd.retention_1.RetentionType;

public class UBLDocumentWRP {

    /**
     * ************
     * <p>
     * Patron Singleton
     * <p>
     * **********************
     */
    private static UBLDocumentWRP instance = null;

    protected UBLDocumentWRP() {

    }

    public static UBLDocumentWRP getInstance() {
        if (instance == null) {
            instance = new UBLDocumentWRP();
        }
        return instance;
    }

    private Transaccion transaccion;

    private InvoiceType invoiceType;

    private InvoiceType boletaType;

    private CreditNoteType creditNoteType;

    private DebitNoteType debitNoteType;

    private PerceptionType perceptionType;

    private RetentionType retentionType;

    private DespatchAdviceType adviceType;


    public DespatchAdviceType getAdviceType() {
        return adviceType;
    }

    public void setAdviceType(DespatchAdviceType adviceType) {
        this.adviceType = adviceType;
    }

    public Transaccion getTransaccion() {
        return transaccion;
    }

    public void setTransaccion(Transaccion transaccion) {
        this.transaccion = transaccion;
    }

    public InvoiceType getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(InvoiceType invoiceType) {
        this.invoiceType = invoiceType;
    }

    public InvoiceType getBoletaType() {
        return boletaType;
    }

    public void setBoletaType(InvoiceType boletaType) {
        this.boletaType = boletaType;
    }

    public CreditNoteType getCreditNoteType() {
        return creditNoteType;
    }

    public void setCreditNoteType(CreditNoteType creditNoteType) {
        this.creditNoteType = creditNoteType;
    }

    public DebitNoteType getDebitNoteType() {
        return debitNoteType;
    }

    public void setDebitNoteType(DebitNoteType debitNoteType) {
        this.debitNoteType = debitNoteType;
    }

    public PerceptionType getPerceptionType() {
        return perceptionType;
    }

    public void setPerceptionType(PerceptionType perceptionType) {
        this.perceptionType = perceptionType;
    }

    public RetentionType getRetentionType() {
        return retentionType;
    }

    public void setRetentionType(RetentionType retentionType) {
        this.retentionType = retentionType;
    }

}
