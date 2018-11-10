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
public class ListTurnos {
    private int idEmpleado;
    private String dependencia;
    private String jornada;
    private Date fecha;

    public ListTurnos(int idEmpleado, String dependencia, String jornada, Date fecha) {
        this.idEmpleado = idEmpleado;
        this.dependencia = dependencia;
        this.jornada = jornada;
        this.fecha = fecha;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getDependencia() {
        return dependencia;
    }

    public void setDependencia(String dependencia) {
        this.dependencia = dependencia;
    }

    public String getJornada() {
        return jornada;
    }

    public void setJornada(String jornada) {
        this.jornada = jornada;
    }

    @Override
    public String toString() {
        return "ListTurnos{" + "idEmpleado=" + idEmpleado + ", dependencia=" + dependencia + ", jornada=" + jornada + ", fecha=" + fecha + '}';
    }
    
    
    
}
