package Model;

/**
 * Clase Modelo usuario
 *
 * @author: Juan David Castrillon
 * @version: 11/12/2017
 */
public class usuario {

    private String usuario;
    private String clave;
    private String rol;
    private String ccost;

    public usuario(String usuario, String clave) {
        this.usuario = usuario;
        this.clave = clave;
    }

    public usuario(String usuario, String clave, String rol, String ccost) {
        this.usuario = usuario;
        this.clave = clave;
        this.rol = rol;
        this.ccost = ccost;
    }

    public usuario() {
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getCcost() {
        return ccost;
    }

    public void setCcost(String ccost) {
        this.ccost = ccost;
    }

}
