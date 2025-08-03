package org.ventura.cpe.core.erp.sapbo;

import com.sap.smb.sbo.api.IField;
import com.sap.smb.sbo.api.IFields;
import com.sap.smb.sbo.api.IRecordset;
import com.sap.smb.sbo.api.SBOCOMException;
import org.ventura.cpe.core.domain.*;
import org.ventura.cpe.core.erp.interfaces.ERPObjectCreator;
import org.ventura.cpe.core.repository.UsuariocamposRepository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

public class SBOObjectCreator<T> implements ERPObjectCreator<T> {

    public T construirReflection(IRecordset rs, T entity) throws SBOCOMException {
        Class clase = entity.getClass();
        Object valor = null;
        for (int i = 0; i < rs.getFields().getCount(); i++) {
            IField icampo = rs.getFields().item(i);
            String ncampo = icampo.getName();
            Integer tcampo = icampo.getType();
            Class tipocampo = null;
            switch (tcampo) {
                case 0:
                case 1:  //Alpha,Memo
                    tipocampo = String.class;
                    valor = icampo.getValueString();
                    break;
                case 2:  //Numeric
                    tipocampo = Integer.class;
                    valor = icampo.getValueInteger();
                    break;
                case 3:  //Date
                    tipocampo = Date.class;
                    valor = icampo.getValueDate();
                    break;
                case 4: //Float
                    tipocampo = BigDecimal.class;
                    valor = BigDecimal.valueOf(icampo.getValueDouble());
                    break;
            }
            if (!ncampo.startsWith("U_")) {
                ncampo = ncampo.replace("_", "");
                try {
                    Method m = clase.getMethod("set" + ncampo, tipocampo);
                    //System.out.println("set"+ ncampo +  "El vVALOR ES :" + valor);
                    m.invoke(entity, valor);
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
                    System.out.println("Error en campo '" + ncampo + "' : " + ex.getMessage());
                }
            }
        }
        return entity;
    }

    public List<TransaccionLineasUsucampos> getTransaccionLineaCamposUsuarios(String FE_ID, int nroorden, IFields campos, List<Usuariocampos> usuariocampos) {
        final Set<TransaccionLineasUsucampos> camposUsuario = new HashSet<>();
        for (int i = 0; i < campos.getCount(); i++) {
            IField icampo = campos.item(i);
            String ncampo = icampo.getName();
            Integer tcampo = icampo.getType();
            if (ncampo.startsWith("U_")) {
                String campo = ncampo.substring(2);
                Optional<Usuariocampos> optional = usuariocampos.parallelStream().filter(usuariocampo -> campo.equalsIgnoreCase(usuariocampo.getNombre())).findAny();
                optional.ifPresent(usuariocampo -> {
                    try {
                        TransaccionLineasUsucampos cu = new TransaccionLineasUsucampos(new TransaccionLineasUsucamposPK(FE_ID, nroorden, usuariocampo.getId()));
                        Object objeto = getFieldValue(tcampo, icampo);
                        cu.setValor(objeto == null ? "" : objeto.toString());
                        camposUsuario.add(cu);
                    } catch (SBOCOMException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
        return new ArrayList<>(camposUsuario);
    }

    private Object getFieldValue(Integer tcampo, IField icampo) throws SBOCOMException {
        Object objeto = new Object();
        switch (tcampo) {
            case 0:
            case 1:  //Alpha,Memo
                objeto = icampo.getValueString();
                break;
            case 2:  //Numeric
                objeto = icampo.getValueInteger();
                break;
            case 3:  //Date
                objeto = icampo.getValueDate();
                break;
            case 4: //Float
                objeto = BigDecimal.valueOf(icampo.getValueDouble());
                break;
        }
        return objeto;
    }

    public List<TransaccionUsucampos> getTransaccionCamposUsuarios(String FE_ID, IFields campos, List<Usuariocampos> usucamposList) {
        final List<TransaccionUsucampos> camposUsuario = new LinkedList<>();
        for (int i = 0; i < campos.getCount(); i++) {
            IField icampo = campos.item(i);
            String ncampo = icampo.getName();
            if (ncampo.startsWith("U_")) {
                final String valor = ncampo.substring(2);
                Optional<Usuariocampos> optional = usucamposList.parallelStream().filter(transaccionUsucampo -> valor.equalsIgnoreCase(transaccionUsucampo.getNombre())).findAny();
                optional.ifPresent(transaccionUsucampo -> {
                    TransaccionUsucampos cu = new TransaccionUsucampos(new TransaccionUsucamposPK(FE_ID, transaccionUsucampo.getId()));
                    cu.setValor(icampo.getValue() == null ? "" : icampo.getValue().toString());
                    camposUsuario.add(cu);
                });
            }
        }
        return camposUsuario;
    }

    public List<TransaccionContractdocref> getTransaccionContractdocref(IRecordset rs, String feID, UsuariocamposRepository repository, List<Usuariocampos> usucamposList) {
        Set<TransaccionContractdocref> transaccionContractdocrefs = new HashSet<>();
        IFields campos = rs.getFields();
        for (int i = 0; i < campos.getCount(); i++) {
            IField icampo = campos.item(i);
            String ncampo = icampo.getName();
            if (icampo.getValue() != null) {
                String vcampo = icampo.getValue().toString();
                if (!vcampo.isEmpty()) {
                    Optional<Usuariocampos> optional = usucamposList.parallelStream().filter(transaccionUsucampo -> ncampo.equalsIgnoreCase(transaccionUsucampo.getNombre())).findAny();
                    Integer id;
                    if (optional.isPresent()) {
                        Usuariocampos usuariocampos = optional.get();
                        id = usuariocampos.getId();
                    } else {
                        Usuariocampos usuariocampo = new Usuariocampos();
                        usuariocampo.setNombre(ncampo);
                        repository.saveAndFlush(usuariocampo);
                        id = usuariocampo.getId();
                        usucamposList.add(usuariocampo);
                    }
                    TransaccionContractdocref docref = new TransaccionContractdocref(new TransaccionContractdocrefPK(feID, id));
                    docref.setValor(vcampo);
                    transaccionContractdocrefs.add(docref);
                }
            }
        }
        return new ArrayList<>(transaccionContractdocrefs);
    }
}
