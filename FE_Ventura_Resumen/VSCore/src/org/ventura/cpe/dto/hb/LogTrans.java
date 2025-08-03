/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.dto.hb;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author VSUser
 */
@Entity
@Table(name = "LOG_TRANS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "LogTrans.findAll", query = "SELECT l FROM LogTrans l"),
    @NamedQuery(name = "LogTrans.findByKeylog", query = "SELECT l FROM LogTrans l WHERE l.keylog = :keylog"),
    @NamedQuery(name = "LogTrans.findByDocId", query = "SELECT l FROM LogTrans l WHERE l.docId = :docId"),
    @NamedQuery(name = "LogTrans.findByFecha", query = "SELECT l FROM LogTrans l WHERE l.fecha = :fecha"),
    @NamedQuery(name = "LogTrans.findByHora", query = "SELECT l FROM LogTrans l WHERE l.hora = :hora"),
    @NamedQuery(name = "LogTrans.findByProceso", query = "SELECT l FROM LogTrans l WHERE l.proceso = :proceso"),
    @NamedQuery(name = "LogTrans.findByTarea", query = "SELECT l FROM LogTrans l WHERE l.tarea = :tarea"),
    @NamedQuery(name = "LogTrans.findByConector", query = "SELECT l FROM LogTrans l WHERE l.conector = :conector")})
public class LogTrans implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "Keylog")
    private Integer keylog;
    @Column(name = "Doc_Id")
    private String docId;
    @Lob
    @Column(name = "TramaEnvio")
    private String tramaEnvio;
    @Column(name = "Fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Column(name = "Hora")
    private Integer hora;
    @Lob
    @Column(name = "TramaRespuesta")
    private String tramaRespuesta;
    @Column(name = "Proceso")
    private String proceso;
    @Column(name = "Tarea")
    private String tarea;
    @Column(name = "Conector")
    private String conector;

    public LogTrans() {
    }

    public LogTrans(Integer keylog) {
        this.keylog = keylog;
    }

    public Integer getKeylog() {
        return keylog;
    }

    public void setKeylog(Integer keylog) {
        this.keylog = keylog;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getTramaEnvio() {
        return tramaEnvio;
    }

    public void setTramaEnvio(String tramaEnvio) {
        this.tramaEnvio = tramaEnvio;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Integer getHora() {
        return hora;
    }

    public void setHora(Integer hora) {
        this.hora = hora;
    }

    public String getTramaRespuesta() {
        return tramaRespuesta;
    }

    public void setTramaRespuesta(String tramaRespuesta) {
        this.tramaRespuesta = tramaRespuesta;
    }

    public String getProceso() {
        return proceso;
    }

    public void setProceso(String proceso) {
        this.proceso = proceso;
    }

    public String getTarea() {
        return tarea;
    }

    public void setTarea(String tarea) {
        this.tarea = tarea;
    }

    public String getConector() {
        return conector;
    }

    public void setConector(String conector) {
        this.conector = conector;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (keylog != null ? keylog.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof LogTrans)) {
            return false;
        }
        LogTrans other = (LogTrans) object;
        if ((this.keylog == null && other.keylog != null) || (this.keylog != null && !this.keylog.equals(other.keylog))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.ventura.cpe.dto.hb.LogTrans[ keylog=" + keylog + " ]";
    }
    
}
