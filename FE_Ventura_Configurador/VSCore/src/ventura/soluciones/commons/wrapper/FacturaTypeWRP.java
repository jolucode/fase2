package ventura.soluciones.commons.wrapper;

import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;

public class FacturaTypeWRP {

    /**
     * ****************************
     *
     * Patron Singleton
     *
     * ************************
     */
    private static FacturaTypeWRP instance = null;

    protected FacturaTypeWRP() {

    }

    public static FacturaTypeWRP getInstance() {
        if (instance == null) {
            instance = new FacturaTypeWRP();

        }
        return instance;
    }

    private InvoiceType invoiceType;

    public InvoiceType getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(InvoiceType invoiceType) {
        this.invoiceType = invoiceType;
    }

}
