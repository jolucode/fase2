/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.utilidades.entidades;

/**
 *
 * @author VSUser
 */
public class PuertoVSCPE {
    
    public static int PuertoAbierto_Configurador=7000;
    public static int PuertoAbierto_Request=7001;
    public static int PuertoAbierto_Response=7002;
    public static int PuertoAbierto_Resumen=7003;
    public static int PuertoAbierto_PublicWS=7004;
    public static int PuertoAbierto_Extractor=7005;

    public int getPuertoVSCPE() {
        return puertoVSCPE;
    }

    public void setPuertoVSCPE(int puertoVSCPE) {
        this.puertoVSCPE = puertoVSCPE;
    }
    
    private int puertoVSCPE;
    
}
