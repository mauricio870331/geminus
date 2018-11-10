package Model;

/**
 * Clase Modelo de Correo por Agencia
 *
 * @author: Juan David Castrillon
 * @version: 11/12/2017
 */
public class correosXagencia {

    private int codigo;
    private String agencia;
    private String correo;
    private boolean estado;

    public correosXagencia() {
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    @Override
    public String toString() {
        return "correosXagencia{" + "agencia=" + agencia + ", correo=" + correo + ", estado=" + estado + '}';
    }

}
