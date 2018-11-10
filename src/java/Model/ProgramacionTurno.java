package Model;

/**
 * Clase Modelo de Programacion de turnos
 *
 * @author: Juan David Castrillon
 * @version: 11/12/2017
 */
public class ProgramacionTurno {

    private String codigo;
    private String dependencia;
    private String fecha;
    private String turno;
    private String estado;

    public ProgramacionTurno() {
    }

    public ProgramacionTurno(String codigo, String dependencia, String fecha, String turno) {
        this.codigo = codigo;
        this.dependencia = dependencia;
        this.fecha = fecha;
        this.turno = turno;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDependencia() {
        return dependencia;
    }

    public void setDependencia(String dependencia) {
        this.dependencia = dependencia;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

}
