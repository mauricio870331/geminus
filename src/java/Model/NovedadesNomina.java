package Model;

/**
 * Clase Modelo de Novedades de nomina
 *
 * @author: Juan David Castrillon
 * @version: 11/12/2017
 */
public class NovedadesNomina {

    private String cedula;
    private int cant;
    private String fechaIni;
    private String fechaFin;

    public NovedadesNomina() {
    }

    public NovedadesNomina(String cedula, int cant, String fechaIni, String fechaFin) {
        this.cedula = cedula;
        this.cant = cant;
        this.fechaIni = fechaIni;
        this.fechaFin = fechaFin;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public int getCant() {
        return cant;
    }

    public void setCant(int cant) {
        this.cant = cant;
    }

    public String getFechaIni() {
        return fechaIni;
    }

    public void setFechaIni(String fechaIni) {
        this.fechaIni = fechaIni;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    @Override
    public String toString() {
        return "NovedadesNomina{" + "cedula=" + cedula + ", cant=" + cant + ", fechaIni=" + fechaIni + ", fechaFin=" + fechaFin + '}';
    }

}
