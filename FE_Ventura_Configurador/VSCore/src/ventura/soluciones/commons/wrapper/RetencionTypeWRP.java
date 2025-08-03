package ventura.soluciones.commons.wrapper;

import oasis.names.specification.ubl.schema.xsd.retention_2.RetentionType;

public class RetencionTypeWRP {

    public static RetencionTypeWRP instance = null;

    protected RetencionTypeWRP() {

    }

    public static RetencionTypeWRP getInstance() {
        if (instance == null) {
            instance = new RetencionTypeWRP();

        }
        return instance;
    }

    public RetentionType getRetentionType() {
        return retentionType;
    }

    public void setRetentionType(RetentionType retentionType) {
        this.retentionType = retentionType;
    }

    private RetentionType retentionType;

}
