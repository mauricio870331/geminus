package Model;

/**
 * Clase Modelo de Novedades
 *
 * @author: Juan David Castrillon
 * @version: 11/12/2017
 */
public class Novedades {

    private int num;
    private String Fecha;
    private String FechaProgramada;
    private String ProgramacionInicial;
    private String ProgramacionFinal;
    private String Programacion;
    private String Marcacion;
    private String Tipo;
    private String Codigo;
    private String Cedula;
    private String Nombre;
    private String Costo;
    private String Turno;
    private String usuarioMod;
    private String FechaMod;
    private boolean activar;
    private boolean eliminar;
    private int cantNovedades;

    public Novedades() {
    }

    public Novedades(int num, String Fecha, String Programacion, String Marcacion, String Tipo, String Codigo, String Cedula, String Nombre, String Costo, String Turno, boolean activar, boolean eliminar, String FechaProgramada) {
        this.num = num;
        this.Fecha = Fecha;
        this.Programacion = Programacion;
        this.Marcacion = Marcacion;
        this.Tipo = Tipo;
        this.Codigo = Codigo;
        this.Cedula = Cedula;
        this.Nombre = Nombre;
        this.Costo = Costo;
        this.Turno = Turno;
        this.activar = activar;
        this.eliminar = eliminar;
        this.FechaProgramada = FechaProgramada;
    }

    public Novedades(int num, String Fecha, String Tipo, String Codigo, String Cedula, String usuarioMod, String FechaMod) {
        this.num = num;
        this.Fecha = Fecha;
        this.Tipo = Tipo;
        this.Codigo = Codigo;
        this.Cedula = Cedula;
        this.usuarioMod = usuarioMod;
        this.FechaMod = FechaMod;
    }

    public Novedades(int num, String Fecha, String FechaProgramada, String Codigo, String Cedula, String Nombre, String Costo, String Turno) {
        this.num = num;
        this.Fecha = Fecha;
        this.FechaProgramada = FechaProgramada;
        this.Codigo = Codigo;
        this.Cedula = Cedula;
        this.Nombre = Nombre;
        this.Costo = Costo;
        this.Turno = Turno;
    }

    public Novedades(String fecha, String Codigo) {
        this.FechaProgramada = fecha;
        this.Codigo = Codigo;
    }

    public String getFecha() {
        return Fecha;
    }

    public void setFecha(String Fecha) {
        this.Fecha = Fecha;
    }

    public String getProgramacion() {
        return Programacion;
    }

    public void setProgramacion(String Programacion) {
        this.Programacion = Programacion;
    }

    public String getMarcacion() {
        return Marcacion;
    }

    public void setMarcacion(String Marcacion) {
        this.Marcacion = Marcacion;
    }

    public String getTipo() {
        return Tipo;
    }

    public void setTipo(String Tipo) {
        this.Tipo = Tipo;
    }

    public String getCodigo() {
        return Codigo;
    }

    public void setCodigo(String Codigo) {
        this.Codigo = Codigo;
    }

    public String getCedula() {
        return Cedula;
    }

    public void setCedula(String Cedula) {
        this.Cedula = Cedula;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String Nombre) {
        this.Nombre = Nombre;
    }

    public String getCosto() {
        return Costo;
    }

    public void setCosto(String Costo) {
        this.Costo = Costo;
    }

    public String getTurno() {
        return Turno;
    }

    public void setTurno(String Turno) {
        this.Turno = Turno;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public boolean isActivar() {
        return activar;
    }

    public void setActivar(boolean activar) {
        this.activar = activar;
    }

    public boolean isEliminar() {
        return eliminar;
    }

    public void setEliminar(boolean eliminar) {
        this.eliminar = eliminar;
    }

    public String getUsuarioMod() {
        return usuarioMod;
    }

    public void setUsuarioMod(String usuarioMod) {
        this.usuarioMod = usuarioMod;
    }

    public String getFechaMod() {
        return FechaMod;
    }

    public void setFechaMod(String FechaMod) {
        this.FechaMod = FechaMod;
    }

    public String getFechaProgramada() {

        return FechaProgramada.trim().substring(0, 10);
    }

    public void setFechaProgramada(String FechaProgramada) {
        this.FechaProgramada = FechaProgramada;
    }

    public String getProgramacionInicial() {
        return ProgramacionInicial;
    }

    public void setProgramacionInicial(String ProgramacionInicial) {
        this.ProgramacionInicial = ProgramacionInicial;
    }

    public String getProgramacionFinal() {
        return ProgramacionFinal;
    }

    public void setProgramacionFinal(String ProgramacionFinal) {
        this.ProgramacionFinal = ProgramacionFinal;
    }

    public int getCantNovedades() {
        return cantNovedades;
    }

    public void setCantNovedades(int cantNovedades) {
        this.cantNovedades = cantNovedades;
    }

    @Override
    public String toString() {
        return "Novedades{" + "Fecha=" + Fecha + ", FechaProgramada=" + FechaProgramada + ", Codigo=" + Codigo + ", Cedula=" + Cedula + '}';
    }

}
