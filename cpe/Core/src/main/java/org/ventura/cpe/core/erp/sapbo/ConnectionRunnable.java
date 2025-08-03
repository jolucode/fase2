package org.ventura.cpe.core.erp.sapbo;

import com.sap.smb.sbo.api.ICompany;

import java.util.Optional;

public class ConnectionRunnable implements Runnable {

    private final SBOConnection sboConnection;

    public ConnectionRunnable(SBOConnection sboConnection) {
        this.sboConnection = sboConnection;
    }

    @Override
    public void run() {
        Optional<ICompany> optional = this.sboConnection.connectToErp();
        optional.ifPresent(iCompany -> SAPBOService.addCompany(iCompany, iCompany.getCompanyDB()));
    }
}
