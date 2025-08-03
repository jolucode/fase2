/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.bl;

import javax.persistence.EntityManager;
import org.ventura.cpe.dao.controller.UsuariocamposJC;
import static org.ventura.cpe.dao.controller.UsuariocamposJC.getEntityManager;
import org.ventura.cpe.dto.hb.Usuariocampos;

/**
 *
 * @author VSUser
 */
public class UsuarioCampoBL {
    
   public static Usuariocampos getCampoUsuaro(Integer id) {
       return UsuariocamposJC.findUsuariocampos(id);
    }
    
}
