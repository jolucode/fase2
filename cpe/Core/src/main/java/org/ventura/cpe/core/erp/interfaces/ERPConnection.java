package org.ventura.cpe.core.erp.interfaces;

import org.ventura.cpe.core.domain.Transaccion;
import org.ventura.cpe.core.exception.VenturaExcepcion;

import java.util.Optional;

public interface ERPConnection<T> {

    Optional<T> connectToErp();

    Optional<T> connectToErp(String servidorLicencia, String servidorBaseDatos, String tipoServidor, String database, String databaseUsername, String databasePassword, String erpUsername, String erpPassword);

    boolean actualizarMensaje(Transaccion tc, String mensaje, String estado, T sociedad) throws VenturaExcepcion;

    boolean actualizarEstado(Transaccion transaccion, String estado, T sociedad) throws VenturaExcepcion;
}
