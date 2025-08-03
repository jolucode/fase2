/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.utilidades.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.ventura.utilidades.entidades.Propiedad;

/**
 *
 * @author VSUser
 */
public class LoaderManifest {

    public static List<Propiedad> getEstructura(Map mapinicial) {
        List<Propiedad> lista = new ArrayList<>();
        leer(mapinicial, "", lista);
        return lista;
    }

    public static void leer(Map props, String pId, List<Propiedad> p) {

        try {
            Object[] llaves = props.keySet().toArray();

            for (int i = 0; i < llaves.length; i++) {
                Object llave = llaves[i];
                Object obj = props.get(llave);

                if (obj instanceof Map) {
                    Map m = (Map) obj;
                    String id =pId  + "." + llave.toString() ;
                    if(pId.compareTo("")==0)
                        id=id.replace(".", "");
                    if (m.size() == 2 && m.get("encriptado") != null && m.get("descripcion") != null) {
                        Propiedad propiedad = new Propiedad();
                        propiedad.setPid(id);
                        propiedad.setPencriptado(Boolean.parseBoolean(m.get("encriptado").toString()));
                        propiedad.setPdescripcion(m.get("descripcion").toString());
                        p.add(propiedad);

                    } else {
                        leer((Map) obj, id, p);
                    }

                }
            }

        } catch (Exception ex) {
            System.out.println("ERROR " + ex.getMessage());
        }
    }

}
