package Model;

import java.util.Date;

/**
 * Clase Modelo de redondeo de jornadas
 *
 * @author: Juan David Castrillon
 * @version: 11/12/2017
 */
public class RedondeoJornada {

    private String codigo;
    private Date fecha;
    private String fechaReal;
    private String jornada;
    private String formato;
    private String marcacion;

    public RedondeoJornada() {
    }

    public RedondeoJornada(String codigo, Date fecha, String fechaReal, String jornada, String formato, String marcacion) {
        this.codigo = codigo;
        this.fecha = fecha;
        this.fechaReal = fechaReal;
        this.jornada = jornada;
        this.formato = formato;
        this.marcacion = marcacion;
    }

    public RedondeoJornada(String codigo, Date fecha, String fechaReal, String formato, String marcacion) {
        this.codigo = codigo;
        this.fecha = fecha;
        this.fechaReal = fechaReal;
        this.formato = formato;
        this.marcacion = marcacion;

    }

    public RedondeoJornada(String codigo, String formato) {
        this.codigo = codigo;
        this.formato = formato;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getFechaReal() {
        return fechaReal;
    }

    public void setFechaReal(String fechaReal) {
        this.fechaReal = fechaReal;
    }

    public String getJornada() {
        return jornada;
    }

    public void setJornada(String jornada) {
        this.jornada = jornada;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public String getMarcacion() {
        return marcacion;
    }

    public void setMarcacion(String marcacion) {
        this.marcacion = marcacion;
    }

    @Override
    public String toString() {
        return "RedondeoJornada{" + "codigo=" + codigo + ", fecha=" + fecha + ", fechaReal=" + fechaReal + ", jornada=" + jornada + ", formato=" + formato + '}';
    }

}
