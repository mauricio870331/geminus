package Model;

import java.util.Date;

/**
 * Clase Modelo de Horas Extras
 *
 * @author: Juan David Castrillon
 * @version: 11/12/2017
 */
public class Horasextras {

    private String cedula;
    private String codigoExtra;
    private double valor;
    private String empresa;
    private String fecha;
    private String codigo;
    private String dependecia;
    private Date fechaLiq;
    private String DiaSemana;
    private String FechaIni;
    private String FechaFin;

    public Horasextras() {
    }

    public Horasextras(String cedula, String codigoExtra, double valor, String empresa, String fecha, String codigo, String dependecia, String DiaSemana) {
        this.cedula = cedula;
        this.codigoExtra = codigoExtra;
        this.valor = valor;
        this.empresa = empresa;
        this.fecha = fecha;
        this.codigo = codigo;
        this.dependecia = dependecia;
        this.DiaSemana = DiaSemana;

    }

    public Horasextras(String codigo, String empresa) {
        this.codigo = codigo;
        this.empresa = empresa;
    }

    public Horasextras(String codigo, String fecha, boolean condicion) {
        this.codigo = codigo;
        this.fecha = fecha;
    }

    public Horasextras(String cedula, String codigoExtra, int condicion, String codigo, int valor) {
        this.codigoExtra = codigoExtra;
        this.codigo = codigo;
        this.cedula = cedula;
        this.valor = valor;
    }

    public Horasextras(String cedula, String codigoExtra, double valor, String codigo) {
        this.cedula = cedula;
        this.codigoExtra = codigoExtra;
        this.valor = valor;
        this.codigo = codigo;
    }

    public Horasextras(String cedula, String codigoExtra, double valor, String FechaIni, String FechaFin) {
        this.cedula = cedula;
        this.codigoExtra = codigoExtra;
        this.valor = valor;
        this.FechaIni = FechaIni;
        this.FechaFin = FechaFin;
    }

    public Horasextras(String codigo, String FechaIni, String FechaFin) {
        this.codigo = codigo;
        this.FechaIni = FechaIni;
        this.FechaFin = FechaFin;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getCodigoExtra() {
        return codigoExtra;
    }

    public void setCodigoExtra(String codigoExtra) {
        this.codigoExtra = codigoExtra;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDependecia() {
        return dependecia;
    }

    public void setDependecia(String dependecia) {
        this.dependecia = dependecia;
    }

    public Date getFechaLiq() {
        return fechaLiq;
    }

    public void setFechaLiq(Date fechaLiq) {
        this.fechaLiq = fechaLiq;
    }

    public String getDiaSemana() {
        return DiaSemana;
    }

    public void setDiaSemana(String DiaSemana) {
        this.DiaSemana = DiaSemana;
    }

    public String getFechaIni() {
        return FechaIni;
    }

    public void setFechaIni(String FechaIni) {
        this.FechaIni = FechaIni;
    }

    public String getFechaFin() {
        return FechaFin;
    }

    public void setFechaFin(String FechaFin) {
        this.FechaFin = FechaFin;
    }

    @Override
    public String toString() {
        return "Horasextras{" + "cedula=" + cedula + ", codigoExtra=" + codigoExtra + ", valor=" + valor + ", empresa=" + empresa + ", fecha=" + fecha + ", codigo=" + codigo + ", dependecia=" + dependecia + ", fechaLiq=" + fechaLiq + ", DiaSemana=" + DiaSemana + ", FechaIni=" + FechaIni + ", FechaFin=" + FechaFin + '}';
    }

}
