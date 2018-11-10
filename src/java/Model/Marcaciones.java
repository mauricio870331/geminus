
package Model;

import java.util.Date;

/**
 * Clase Modelo de Marcaciones
 *
 * @author: Juan David Castrillon
 * @version: 11/12/2017
 */
public class Marcaciones {
    public int codigo;
    public String fecha;
    public Date marcacion;
    public String turno;
    public String fechaProgramada;
    public int cantidadNovedad;
    public int id;

    public Marcaciones(int codigo, String fecha, Date marcacion, String turno, String fechaProgramada) {
        this.codigo = codigo;
        this.fecha = fecha;
        this.marcacion = marcacion;
        this.turno = turno;
        this.fechaProgramada = fechaProgramada;
    }

    public Marcaciones(int codigo, int cantidadNovedad, int id) {
        this.codigo = codigo;
        this.cantidadNovedad = cantidadNovedad;
        this.id = id;
    }

 
    
    

    public Marcaciones() {
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Date getMarcacion() {
        return marcacion;
    }

    public void setMarcacion(Date marcacion) {
        this.marcacion = marcacion;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    public String getFechaProgramada() {
        return fechaProgramada;
    }

    public void setFechaProgramada(String fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }

    public int getCantidadNovedad() {
        return cantidadNovedad;
    }

    public void setCantidadNovedad(int cantidadNovedad) {
        this.cantidadNovedad = cantidadNovedad;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    
    
    
}
