package Model;

/**
 * Clase Modelo de Festivos
 *
 * @author: Juan David Castrillon
 * @version: 11/12/2017
 */
public class Festivos {

    private String fecha;

    public Festivos() {
    }

    public Festivos(String fecha) {
        this.fecha = fecha;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

}
