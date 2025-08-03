package org.ventura.cpe.core.util;

import java.util.List;

public class CompanyResponse {

    private List<ConnectedCompany> connectedCompanies;

    public CompanyResponse() {
    }

    public CompanyResponse(List<ConnectedCompany> connectedCompanies) {
        this.connectedCompanies = connectedCompanies;
    }

    public List<ConnectedCompany> getConnectedCompanies() {
        return connectedCompanies;
    }

    public void setConnectedCompanies(List<ConnectedCompany> connectedCompanies) {
        this.connectedCompanies = connectedCompanies;
    }
}
