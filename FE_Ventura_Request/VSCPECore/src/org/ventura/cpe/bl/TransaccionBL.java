/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.bl;

import org.ventura.cpe.dao.controller.TransaccionBajaJC;
import org.ventura.cpe.dao.controller.TransaccionErrorJC;
import org.ventura.cpe.dao.controller.TransaccionJC;
import org.ventura.cpe.dao.exceptions.NonexistentEntityException;
import org.ventura.cpe.dao.exceptions.PreexistingEntityException;
import org.ventura.cpe.dto.TransaccionRespuesta;
import org.ventura.cpe.dto.hb.Transaccion;
import org.ventura.cpe.dto.hb.TransaccionError;
import org.ventura.cpe.excepciones.VenturaExcepcion;
import org.ventura.cpe.log.LoggerTrans;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

/**
 * @author Percy
 */
public class TransaccionBL {

    private static final SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");

    public static Transaccion ListarTransaccionCola(String id) {
        try {
            Transaccion t = TransaccionJC.findTransaccionCola(id);
            return t;
        } catch (Exception ex) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, ex.getMessage());
            throw ex;
        }
    }

    public static int OrdenDeLlegada() {

        int iMaximoReg = TransaccionJC.findMaxSalto();

        return iMaximoReg + 1;

    }

    public static List<Transaccion> ListarAll() {
        try {
            List<Transaccion> l = TransaccionJC.findAll();
            return l;
        } catch (Exception ex) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, ex.getMessage());
            throw ex;
        }
    }

    public static boolean MarcarEnviado(Transaccion tc) {
        try {
            tc.setFEEstado("E");
            TransaccionJC.edit(tc);
            return true;
        } catch (Exception ex) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, ex.getMessage());
            return false;
        }
    }

    public static boolean MarcarPDFBorrador(Transaccion tc) {
        try {
            tc.setFEEstado("W");
            TransaccionJC.edit(tc);
            return true;
        } catch (Exception ex) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, ex.getMessage());
            return false;
        }
    }

    public static void AcumularReintentos(Transaccion tc) {
        tc.setFEErrores(tc.getFEErrores() + 1);
        if (tc.getFEErrores() == TransaccionRespuesta.MAX_ERRORES) {
            tc.setFEErrores(0);
            tc.setFESaltos(0);
            tc.setFEMaxSalto(tc.getFEMaxSalto() + 1);
            LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}]: El documento ha alcanzado el máximo de reintentos ({1}). Se disminuye su frecuencia de envios.",
                    new Object[]{
                            tc.getDOCId(),
                            TransaccionRespuesta.MAX_ERRORES});
        }
        TransaccionBL.Actualizar(tc);
    }

    public static boolean SaltearIntentos(Transaccion tc) {
        if (tc.getFESaltos() == null) {
            tc.setFESaltos(0);
        }
        if (tc.getFEMaxSalto() == null) {
            tc.setFEMaxSalto(0);
        }
        if (tc.getFESaltos() < tc.getFEMaxSalto()) {
            LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}]: el documento se enviará en {1} iteracion(es) más.", new Object[]{tc.getDOCId(), tc.getFEMaxSalto() - tc.getFESaltos()});
            tc.setFESaltos(tc.getFESaltos() + 1);
            TransaccionBL.Actualizar(tc);
            return true;
        } else {
            tc.setFESaltos(0);
            TransaccionBL.Actualizar(tc);
            return false;
        }
    }

    /**
     * Este método solo es efectivo si la trasacción es de tipo [B]aja. En los
     * demás casos no tiene efecto. Este método ejectar genera y asigna el ID de
     * comunicación de baja siguiente a la transacción (Repositorio).
     *
     * @param tc la trasacción de tipo [B]aja a la que se le generará el ID.
     * @return true si el proceso terminó satisfactoriamente. false en los demás
     * casos.
     */
    public static boolean GenerarIDyFecha(Transaccion tc) {
        try {
            if (tc.getFETipoTrans().compareTo("E") == 0) {
                return true;
            }
            String idbaja = "";

            idbaja = TransaccionBajaJC.idBaja();
            int indexOf = idbaja.lastIndexOf("-");
            String fin = idbaja.substring(indexOf);
            LocalDateTime date = LocalDateTime.now() /*LocalDateTime.of(2019, Month.OCTOBER, 3, 1, 1)*/;
            Date fecha = Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            String baja = "RA-" + simpleDateFormat.format(fecha) + fin;
            System.out.println("Nombre del docummento.");
            System.out.println();
            System.out.println();
            System.out.println(baja);
            System.out.println();
            System.out.println();
            tc.setANTICIPOId(baja);
//            tc.setDOCFechaVencimiento(f.parse(idbaja.substring(3, 11)));
            TransaccionJC.edit(tc);
            return true;
        } catch (Exception ex) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, ex.getMessage());
            return false;
        }
    }

    public static boolean ReenviarTransaccion(Transaccion t) {
        try {
            t.setFEEstado("C");
            TransaccionJC.edit(t);
            return true;
        } catch (Exception e) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, e.getMessage());
            return false;
        }
    }

    public static boolean ReenviarTransaccionNuevo(Transaccion t) {
        try {
            t.setFEEstado("N");
            TransaccionJC.edit(t);
            return true;
        } catch (Exception e) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, e.getMessage());
            return false;
        }
    }

    public static boolean MarcarConsultaEnviado(Transaccion tc) {
        try {
            tc.setFEEstado("S");
            TransaccionJC.edit(tc);
            return true;
        } catch (Exception ex) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, ex.getMessage());
            return false;
        }
    }

    public static boolean MarcarFinalizado(Transaccion tc) {
        try {
            tc.setFEEstado("F");
            TransaccionJC.edit(tc);
            return true;
        } catch (Exception ex) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, ex.getMessage());
            return false;
        }
    }

    public static boolean MarcarRecepcionado(Transaccion tc) {
        try {
            tc.setFEEstado("R");
            tc.setFEErrores(0);
            tc.setFEMaxSalto(0);
            tc.setFESaltos(0);
            TransaccionJC.edit(tc);
            return true;
        } catch (Exception ex) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, ex.getMessage());
            return false;
        }
    }

    public static boolean Eliminar(Transaccion tc) {
        try {
            TransaccionJC.destroy(tc.getFEId());
        } catch (NonexistentEntityException nee) {
        } catch (Exception ex) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, ex.getMessage());
            return false;
        }
        return true;
    }

    /**
     * @param tc la transaccion a crear
     * @return estado de la creación. 0 = creación exitosa, 1 = transacción
     * existente, 2 = otro error
     */
    public static int Crear(Transaccion tc) {
        try {
            if (TransaccionJC.findTransaccionCola(tc.getFEId()) == null) {
                TransaccionJC.create(tc);
            } else {
                TransaccionJC.edit(tc);
            }
        } catch (PreexistingEntityException ex) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, ex.getMessage());
            return 1;
        } catch (VenturaExcepcion ex) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, ex.getMessage());
            return 2;
        } catch (Exception ex) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, ex.getMessage());
        }
        return 0;
    }

    /**
     * @return la lista de trnsacciones pendientes de envio. Es decir las que
     * tengan el estado [N]uevo,[C]orregido,[E]nviado
     */
    public static List<Transaccion> ListarDisponibles() {
        return TransaccionJC.findDisponibles();
    }

    public static boolean PonerEnCorreccion(Transaccion tc, int codigo, String mensaje) {
        try {
            TransaccionError te = new TransaccionError();
            te.setDocentry(tc.getFEDocEntry());
            te.setErrCodigo(codigo);
            te.setErrMensaje(mensaje);
            te.setFEId(tc.getFEId());
            te.setFEObjectType(tc.getFEObjectType());
            te.setFETipoTrans(tc.getFETipoTrans());
            te.setDocnum(tc.getFEDocNum());
            te.setFEFormSAP(tc.getFEFormSAP());

            TransaccionErrorJC.create(te);
            return true;
        } catch (Exception ex) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, ex.getMessage());
            return false;
        }
    }

    public static boolean Actualizar(Transaccion tc) {
        try {
            TransaccionJC.edit(tc);
        } catch (NonexistentEntityException nee) {
        } catch (Exception ex) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, ex.getMessage());
            return false;
        }
        return true;
    }

    public static boolean ActualizarTique(Transaccion tc, String tique) {
        try {
            System.out.println("Aqui se debe actualizar el ticket " + tique);
            tc.setREFDOCId(tique);
            TransaccionJC.edit(tc);
            System.out.println("Aqui se debio actualizar");
            return true;
        } catch (Exception ex) {
            System.out.println("No se actualizo el numero de Ticket");
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, ex.getMessage());
            return false;
        }
    }

    public static boolean Nueva_Excepcion(Transaccion oTransaccion) {

        try {
            int iMaximoRegistro = TransaccionBL.OrdenDeLlegada();
            oTransaccion.setFEMaxSalto(iMaximoRegistro);
            oTransaccion.setFEEstado("N");
            Actualizar(oTransaccion);
            return true;
        } catch (Exception ex) {
            return false;
        }

    }

    public static boolean Nueva_Excepcion_1033(Transaccion oTransaccion) {
        try {
            oTransaccion.setFEEstado("R");
            Actualizar(oTransaccion);
            return true;
        } catch (Exception ex) {
            return false;
        }

    }

    public static boolean CdrNuloNoPorExcepcion(Transaccion oTransaccion) {
        try {
            oTransaccion.setFEEstado("C");
            Actualizar(oTransaccion);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean Excepcion_Anexo(Transaccion oTransaccion) {

        try {
            int iMaximoRegistro = TransaccionBL.OrdenDeLlegada();
            oTransaccion.setFEMaxSalto(iMaximoRegistro);
            Actualizar(oTransaccion);
            return true;
        } catch (Exception ex) {
            return false;
        }

    }

}
