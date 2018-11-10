package Model;

import java.util.Date;

/**
 * Clase Modelo de Biometrico
 *
 * @author: Juan David Castrillon
 * @version: 11/12/2017
 */
public class biometrico {

    private int logid;
    private String cedula;
    private String codigo;
    private String fechaIni;
    private String fechaFin;
    private String horaini;
    private String horafin;
    private String nombre;
    private String area;
    private String turno;
    private String programacion;
    private String fechaProgramacion;
    private String tipoLlegada;
    private Date fechaMarcacion;
    private String estado;
    //Variables para Geminus
    private int HoraInicial;
    private int HoraFinal;
    private int MinInicial;
    private int MinFinal;
    //Variables Para Registro de Personal
    private String cargo;
    private String fechaIngreso;
    private String centroCosto;
    private String fecNacimiento;
    private String Dependencia;
    private String ExNocturnas;
    private String ExFestivas;
    private String ExNocfestvias;
    private String ExEnturno;
    private String ExDominicales;
    private String FechaMOd;

    public biometrico(String cedula, String codigo, String fechaIni, String fechaFin, String horaini, String horafin, String nombre, String area) {
        this.cedula = cedula;
        this.codigo = codigo;
        this.fechaIni = fechaIni;
        this.fechaFin = fechaFin;
        this.horaini = horaini;
        this.horafin = horafin;
        this.nombre = nombre;
        this.area = area;
    }

    public biometrico(int logid, String codigo, Date fechaMarcacion, String llegada, String area) {
        this.logid = logid;
        this.codigo = codigo;
        this.area = area;
        this.fechaMarcacion = fechaMarcacion;
        this.tipoLlegada = llegada;
    }

    public biometrico(String codigo, String area) {
        this.codigo = codigo;
        this.area = area;
    }

    public biometrico(String codigo, String turno, String programacion, String fechaProgramacion) {
        this.codigo = codigo;
        this.turno = turno;
        this.programacion = programacion;
        this.fechaProgramacion = fechaProgramacion;
    }

    public biometrico(String fechaIni, String horaini, String turno) {
        this.fechaIni = fechaIni;
        this.horaini = horaini;
        this.turno = turno;
    }

    //Constructor para Personal Activo en Geminus
    public biometrico(String codigo, String cedula, String turno, String estado, int comodin) {
        this.codigo = codigo;
        this.cedula = cedula;
        this.turno = turno;
        this.estado = estado;
    }

    public biometrico(String codigo, String fechaIni, Date fechaMarcacion, String tipoLlegada) {
        this.codigo = codigo;
        this.fechaIni = fechaIni;
        this.tipoLlegada = tipoLlegada;
        this.fechaMarcacion = fechaMarcacion;
    }

    public biometrico(String codigo, String turno, int HoraInicial, int MinInicial, int HoraFinal, int MinFinal) {
        this.codigo = codigo;
        this.turno = turno;
        this.HoraInicial = HoraInicial;
        this.HoraFinal = HoraFinal;
        this.MinInicial = MinInicial;
        this.MinFinal = MinFinal;
    }

    //Arreglo para actualizar las marcaciones del Biometrico
    public biometrico(int logid, String codigo) {
        this.logid = logid;
        this.codigo = codigo;
    }

    //Constructor Para Registro de Personal
    public biometrico(String cedula, String codigo, String nombre, String cargo, String fechaIngreso, String centroCosto, String fecNacimiento, String Dependencia, String ExNocturnas, String ExFestivas, String ExNocfestvias, String ExEnturno, String ExDominicales, String FechaMOd) {
        this.cedula = cedula;
        this.codigo = codigo;
        this.nombre = nombre;
        this.cargo = cargo;
        this.fechaIngreso = fechaIngreso;
        this.centroCosto = centroCosto;
        this.fecNacimiento = fecNacimiento;
        this.Dependencia = Dependencia;
        this.ExNocturnas = ExNocturnas;
        this.ExFestivas = ExFestivas;
        this.ExNocfestvias = ExNocfestvias;
        this.ExEnturno = ExEnturno;
        this.ExDominicales = ExDominicales;
        this.FechaMOd = FechaMOd;
    }

    public biometrico() {
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getFechaIni() {
        return fechaIni.trim().equalsIgnoreCase("1899-12-30") ? "No marco" : fechaIni;
    }

    public void setFechaIni(String fechaIni) {
        this.fechaIni = fechaIni;
    }

    public String getFechaFin() {
        return fechaFin.trim().equalsIgnoreCase("1899-12-30") ? "No marco" : fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getHoraini() {
        return horaini;
    }

    public void setHoraini(String horaini) {
        this.horaini = horaini;
    }

    public String getHorafin() {
        return horafin;
    }

    public void setHorafin(String horafin) {
        this.horafin = horafin;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    public String getProgramacion() {
        return programacion;
    }

    public void setProgramacion(String programacion) {
        this.programacion = programacion;
    }

    public String getFechaProgramacion() {
        return fechaProgramacion;
    }

    public void setFechaProgramacion(String fechaProgramacion) {
        this.fechaProgramacion = fechaProgramacion;
    }

    public int getLogid() {
        return logid;
    }

    public void setLogid(int logid) {
        this.logid = logid;
    }

    public String getTipoLlegada() {
        return tipoLlegada;
    }

    public void setTipoLlegada(String tipoLlegada) {
        this.tipoLlegada = tipoLlegada;
    }

    public Date getFechaMarcacion() {
        return fechaMarcacion;
    }

    public void setFechaMarcacion(Date fechaMarcacion) {
        this.fechaMarcacion = fechaMarcacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getHoraInicial() {
        return HoraInicial;
    }

    public void setHoraInicial(int HoraInicial) {
        this.HoraInicial = HoraInicial;
    }

    public int getHoraFinal() {
        return HoraFinal;
    }

    public void setHoraFinal(int HoraFinal) {
        this.HoraFinal = HoraFinal;
    }

    public int getMinInicial() {
        return MinInicial;
    }

    public void setMinInicial(int MinInicial) {
        this.MinInicial = MinInicial;
    }

    public int getMinFinal() {
        return MinFinal;
    }

    public void setMinFinal(int MinFinal) {
        this.MinFinal = MinFinal;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(String fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public String getCentroCosto() {
        return centroCosto;
    }

    public void setCentroCosto(String centroCosto) {
        this.centroCosto = centroCosto;
    }

    public String getFecNacimiento() {
        return fecNacimiento;
    }

    public void setFecNacimiento(String fecNacimiento) {
        this.fecNacimiento = fecNacimiento;
    }

    public String getDependencia() {
        return Dependencia;
    }

    public void setDependencia(String Dependencia) {
        this.Dependencia = Dependencia;
    }

    public String getExNocturnas() {
        return ExNocturnas;
    }

    public void setExNocturnas(String ExNocturnas) {
        this.ExNocturnas = ExNocturnas;
    }

    public String getExFestivas() {
        return ExFestivas;
    }

    public void setExFestivas(String ExFestivas) {
        this.ExFestivas = ExFestivas;
    }

    public String getExNocfestvias() {
        return ExNocfestvias;
    }

    public void setExNocfestvias(String ExNocfestvias) {
        this.ExNocfestvias = ExNocfestvias;
    }

    public String getExEnturno() {
        return ExEnturno;
    }

    public void setExEnturno(String ExEnturno) {
        this.ExEnturno = ExEnturno;
    }

    public String getExDominicales() {
        return ExDominicales;
    }

    public void setExDominicales(String ExDominicales) {
        this.ExDominicales = ExDominicales;
    }

    public String getFechaMOd() {
        return FechaMOd;
    }

    public void setFechaMOd(String FechaMOd) {
        this.FechaMOd = FechaMOd;
    }

    @Override
    public String toString() {
        return "biometrico{" + "codigo=" + codigo + ", turno=" + turno + ", HoraInicial=" + HoraInicial + ", HoraFinal=" + HoraFinal + ", MinInicial=" + MinInicial + ", MinFinal=" + MinFinal + '}';
    }

}
