package org.ventura.cpe.core.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ventura.cpe.core.domain.Transaccion;
import org.ventura.cpe.core.domain.TransaccionError;
import org.ventura.cpe.core.repository.TransaccionRepository;
import org.ventura.cpe.core.ws.response.TransaccionRespuesta;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransaccionService {

    private final TransaccionRepository transaccionRepository;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    @Transactional(readOnly = true)
    public List<Transaccion> findPendientes() {
        return transaccionRepository.findPendientes();
    }

    @Transactional(readOnly = true)
    public List<Transaccion> findDisponibles() {
        return transaccionRepository.findDisponibles();
    }

    @Transactional
    public boolean marcarEnviado(Transaccion transaccion) {
        transaccion.setFEEstado("E");
        transaccionRepository.saveAndFlush(transaccion);
        return true;
    }

    @Transactional
    public void actualizar(Transaccion transaccion) {
        transaccionRepository.saveAndFlush(transaccion);
    }

    public boolean marcarPDFBorrador(Transaccion transaccion) {
        transaccion.setFEEstado("W");
        transaccionRepository.saveAndFlush(transaccion);
        return true;
    }

    public void acumularReintentos(Transaccion transaccion) {
        transaccion.setFEErrores(transaccion.getFEErrores() + 1);
        if (transaccion.getFEErrores() == TransaccionRespuesta.MAX_ERRORES) {
            transaccion.setFEErrores(0);
            transaccion.setFESaltos(0);
            transaccion.setFEMaxSalto(transaccion.getFEMaxSalto() + 1);
            log.info("[{}]: El documento ha alcanzado el máximo de reintentos ({}). Se disminuye su frecuencia de envios.", transaccion.getDOCId(), TransaccionRespuesta.MAX_ERRORES);
        }
        transaccionRepository.saveAndFlush(transaccion);
    }

    public boolean saltearIntentos(Transaccion transaccion) {
        if (transaccion.getFESaltos() == null) {
            transaccion.setFESaltos(0);
        }
        if (transaccion.getFEMaxSalto() == null) {
            transaccion.setFEMaxSalto(0);
        }
        if (transaccion.getFESaltos() < transaccion.getFEMaxSalto()) {
            log.info("[{}]: el documento se enviará en {} iteracion(es) más.", new Object[]{transaccion.getDOCId(), transaccion.getFEMaxSalto() - transaccion.getFESaltos()});
            transaccion.setFESaltos(transaccion.getFESaltos() + 1);
            transaccionRepository.saveAndFlush(transaccion);
            return true;
        } else {
            transaccion.setFESaltos(0);
            transaccionRepository.saveAndFlush(transaccion);
            return false;
        }
    }

    /**
     * Este método solo es efectivo si la trasacción es de tipo [B]aja. En los
     * demás casos no tiene efecto. Este método ejectar genera y asigna el ID de
     * comunicación de baja siguiente a la transacción (Repositorio).
     *
     * @param transaccion la trasacción de tipo [B]aja a la que se le generará el ID.
     * @return true si el proceso terminó satisfactoriamente. false en los demás
     * casos.
     */
    public boolean generarIDyFecha(Transaccion transaccion) {
        if (transaccion.getFETipoTrans().compareTo("E") == 0) {
            return true;
        }
        String idbaja = "";
//            idbaja = TransaccionBajaJC.idBaja();
        int indexOf = idbaja.lastIndexOf("-");
        String fin = idbaja.substring(indexOf);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String baja = "RA-" + simpleDateFormat.format(new Date()) + fin;
        System.out.println("Nombre del docummento.");
        transaccion.setANTICIPOId(baja);
//            transaccion.setDOCFechaVencimiento(f.parse(idbaja.substring(3, 11)));
        transaccionRepository.saveAndFlush(transaccion);
        return true;
    }

    public boolean reenviarTransaccion(Transaccion t) {
        try {
            t.setFEEstado("C");
            transaccionRepository.saveAndFlush(t);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    public boolean reenviarTransaccionNuevo(Transaccion t) {
        try {
            t.setFEEstado("N");
            transaccionRepository.saveAndFlush(t);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    public boolean marcarConsultaEnviado(Transaccion transaccion) {
        try {
            transaccion.setFEEstado("S");
            transaccionRepository.saveAndFlush(transaccion);
            return true;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return false;
        }
    }

    public boolean marcarFinalizado(Transaccion transaccion) {
        try {
            transaccion.setFEEstado("F");
            transaccionRepository.saveAndFlush(transaccion);
            return true;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return false;
        }
    }

    public boolean marcarRecepcionado(Transaccion transaccion) {
        try {
            transaccion.setFEEstado("R");
            transaccion.setFEErrores(0);
            transaccion.setFEMaxSalto(0);
            transaccion.setFESaltos(0);
            transaccionRepository.saveAndFlush(transaccion);
            return true;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return false;
        }
    }

    public boolean eliminarTransaccion(Transaccion transaccion) {
        transaccionRepository.deleteById(transaccion.getFEId());
        return true;
    }

    public boolean ponerEnCorreccion(Transaccion transaccion, int codigo, String mensaje) {
        try {
            TransaccionError te = new TransaccionError();
            te.setDocentry(transaccion.getFEDocEntry());
            te.setErrCodigo(codigo);
            te.setErrMensaje(mensaje);
            te.setFEId(transaccion.getFEId());
            te.setFEObjectType(transaccion.getFEObjectType());
            te.setFETipoTrans(transaccion.getFETipoTrans());
            te.setDocnum(transaccion.getFEDocNum());
            te.setFEFormSAP(transaccion.getFEFormSAP());

//            TransaccionErrorJC.create(te);
            return true;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return false;
        }
    }

    @Transactional
    public boolean actualizarTique(Transaccion transaccion, String tique) {
        transaccion.setREFDOCId(tique);
        transaccionRepository.saveAndFlush(transaccion);
        return true;
    }

    @Transactional
    public boolean nuevaExcepcion(Transaccion transaccion) {
//        int iMaximoRegistro = TransaccionBL.OrdenDeLlegada();
//        transaccion.setFEMaxSalto(iMaximoRegistro);
        transaccion.setFEEstado("N");
        transaccionRepository.saveAndFlush(transaccion);
        return true;
    }

    @Transactional
    public boolean nuevaExcepcion1033(Transaccion transaccion) {
        transaccion.setFEEstado("R");
        transaccionRepository.saveAndFlush(transaccion);
        return true;
    }

    @Transactional
    public boolean cdrNuloNoPorExcepcion(Transaccion transaccion) {
        transaccion.setFEEstado("C");
        transaccionRepository.saveAndFlush(transaccion);
        return true;
    }
}
