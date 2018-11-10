/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.util.Date;

/**
 *
 * @author clopez
 */
public class LogTurnos {
    private int idLog;
    private String nomArchivo;
    private Date fechaLog;

    public LogTurnos(int idLog, String nomArchivo, Date fechaLog) {
        this.idLog = idLog;
        this.nomArchivo = nomArchivo;
        this.fechaLog = fechaLog;
    }

    public int getIdLog() {
        return idLog;
    }

    public void setIdLog(int idLog) {
        this.idLog = idLog;
    }

    public String getNomArchivo() {
        return nomArchivo;
    }

    public void setNomArchivo(String nomArchivo) {
        this.nomArchivo = nomArchivo;
    }

    public Date getFechaLog() {
        return fechaLog;
    }

    public void setFechaLog(Date fechaLog) {
        this.fechaLog = fechaLog;
    }

    @Override
    public String toString() {
        return "LogTurnos{" + "idLog=" + idLog + ", nomArchivo=" + nomArchivo + ", fechaLog=" + fechaLog + '}';
    }

    
    
    
}
