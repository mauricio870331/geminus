/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

/**
 *
 * @author clopez
 */
public class LogErroresTurnos {

    private String idLinea;
    private String mensajeError;
    private String registro;

    public LogErroresTurnos(String idLinea, String mensajeError, String registro) {
        this.idLinea = idLinea;
        this.mensajeError = mensajeError;
        this.registro = registro;
    }

    public String getIdLinea() {
        return idLinea;
    }

    public void setIdLinea(String idLinea) {
        this.idLinea = idLinea;
    }

    public String getMensajeError() {
        return mensajeError;
    }

    public void setMensajeError(String mensajeError) {
        this.mensajeError = mensajeError;
    }

    public String getRegistro() {
        return registro;
    }

    public void setRegistro(String registro) {
        this.registro = registro;
    }

    @Override
    public String toString() {
        return "LogErroresTurnos{" + "idLinea=" + idLinea + ", mensajeError=" + mensajeError + ", registro=" + registro + '}';
    }
    
    
    
}
