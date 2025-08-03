/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.utilidades.plugin;

import com.json.parsers.JSONParser;
import com.json.parsers.JsonParserFactory;
import org.ventura.utilidades.entidades.Propiedad;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 *
 * @author VSUser
 */
public class pluginLoaderJAR {

    static JSONParser parser = null;

    public static List<Propiedad> cargarJar(String RutaJAR) {
        List<Propiedad> js = new ArrayList<>();
        try {

            JarFile jf = new JarFile(RutaJAR);
            Manifest mf = jf.getManifest();
            String config = mf.getMainAttributes().getValue("config");

            JsonParserFactory factory = JsonParserFactory.getInstance();
            parser = factory.newJsonParser();
            js = LoaderManifest.getEstructura(parser.parseJson(config));

        } catch (Exception e) {
        }
        return js;
    }

}
