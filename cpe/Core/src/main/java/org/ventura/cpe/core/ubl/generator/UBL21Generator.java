package org.ventura.cpe.core.ubl.generator;

import oasis.names.specification.ubl.schema.xsd.creditnote_21.CreditNoteType;
import oasis.names.specification.ubl.schema.xsd.debitnote_21.DebitNoteType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import org.ventura.cpe.core.domain.Transaccion;
import org.ventura.cpe.core.domain.TransaccionResumen;
import sunat.names.specification.ubl.peru.schema.xsd.summarydocuments_1.SummaryDocumentsType;

public class UBL21Generator {

    public SummaryDocumentsType generateSummaryDocumentsType(TransaccionResumen transaction, String signerName) {
        return null;
    }

    public InvoiceType generateSummaryDocumentsType(Transaccion transaction, String signerName) {
        return null;
    }

    public InvoiceType generateBoletaDocumentsType(Transaccion transaction, String signerName) {
        return null;
    }

    public CreditNoteType generateCreditNoteDocumentsType(Transaccion transaction, String signerName) {
        return null;
    }

    public DebitNoteType generateDebitNoteDocumentsType(Transaccion transaction, String signerName) {
        return null;
    }

    public SummaryDocumentsType generateinvoiceType(TransaccionResumen transaction, String signerName) {
        return null;
    }
}
