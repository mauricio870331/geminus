/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.util.Date;

/**
 *
 * @author Administrador
 */
public class Jornadas {
    private String codEmpleado;
    private String dependencia;
    private String Jornada;
    private Date fecha;
    private String esAdicional;
    private String descripcion;
    private String jornadaProyectada;
    private int idLog;

    public Jornadas() {
    }

    public String getCodEmpleado() {
        return codEmpleado;
    }

    public void setCodEmpleado(String codEmpleado) {
        this.codEmpleado = codEmpleado;
    }

    public String getDependencia() {
        return dependencia;
    }

    public void setDependencia(String dependencia) {
        this.dependencia = dependencia;
    }

    public String getJornada() {
        return Jornada;
    }

    public void setJornada(String Jornada) {
        this.Jornada = Jornada;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getEsAdicional() {
        return esAdicional;
    }

    public void setEsAdicional(String esAdicional) {
        this.esAdicional = esAdicional;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getJornadaProyectada() {
        return jornadaProyectada;
    }

    public void setJornadaProyectada(String jornadaProyectada) {
        this.jornadaProyectada = jornadaProyectada;
    }

    public int getIdLog() {
        return idLog;
    }

    public void setIdLog(int idLog) {
        this.idLog = idLog;
    }
    
    
    
}
