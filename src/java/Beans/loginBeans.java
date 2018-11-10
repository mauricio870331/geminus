package Beans;

import Coneciones.sqlServer10_19;
import Control.Email;
import Control.Hilos.Mihilo;
import Model.usuario;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.primefaces.component.growl.Growl;

/**
 * Beans que se utiliza para logear a los usuarios del sistema
 *
 * @author: Juan David Castrillon
 * @version: 11/12/2017
 */
@Named(value = "loginBeans")
@SessionScoped
public class loginBeans implements Serializable {

    sqlServer10_19 poolSql_19 = new sqlServer10_19();
    Growl growl = new Growl();
    usuario usu;
    String menu;
    String sensori;
    String ciudad;
    static Mihilo hilo = null;
    String ccosto = "";

    /**
     * Metodo constructor
     *
     */
    @PostConstruct
    public void init() {
        growl.setLife(5000);
    }

    /**
     * Metodo constructor , inicializa el hilo con los trabajos que se requieran
     * correr
     *
     */
    public loginBeans() {
        usu = new usuario();
//        if (hilo == null) {
//            try {
//                hilo = new Mihilo();         
//            } catch (Exception ex) {
//                Email mail = new Email();
//                mail.getMailNovedades(" Error de Traspaso de Marcaciones Error : " + ex.toString());
//                System.out.println("Error aaaaa: " + ex.toString());
//            }
//        }
    }

    /**
     * Metodo iniciar sesion , con usuario y clave
     *
     */
 public String iniciarSesion() throws SQLException {
        usuario getusuario = null;
        try {
            ciudad = "";
            poolSql_19.con = poolSql_19.dataSource.getConnection();
            ResultSet rs = poolSql_19.query("select A.usuario,A.clave,A.idrol, A.ccosto from UserBiometrico A"
                    + "  where A.usuario='" + usu.getUsuario() + "' and clave='" + usu.getClave() + "'");
            String ruta = "";
            while (rs.next()) {
                getusuario = new usuario(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4));
            }
            if (getusuario == null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Error usuario o clave erronea", ""));
            } else {
                System.out.println("INICIO SESION");
                if (getusuario.getRol().trim().equalsIgnoreCase("3")) {
                    menu = "MenuDisciplinario.xhtml";
                } else if (getusuario.getRol().trim().equalsIgnoreCase("4")) {
                    menu = "MenuVisitante.xhtml";
                } else if (getusuario.getRol().trim().equalsIgnoreCase("2")) {
                    menu = "MenuDisciplinario.xhtml";
                } else if (getusuario.getRol().trim().equalsIgnoreCase("5")) {
                    menu = "MenuAdmon.xhtml";
                } else if (getusuario.getRol().trim().equalsIgnoreCase("6")) {
                    menu = "MenuVisitanteAgencia.xhtml";
                    ciudad = getusuario.getUsuario().trim().toUpperCase();
                    if (getusuario.getUsuario().trim().equalsIgnoreCase("cali")) {
                        sensori = "11";
                    } else if (getusuario.getUsuario().trim().equalsIgnoreCase("palmira")) {
                        sensori = "16";
                    } else if (getusuario.getUsuario().trim().equalsIgnoreCase("buga")) {
                        sensori = "10";
                    } else if (getusuario.getUsuario().trim().equalsIgnoreCase("tulua")) {
                        sensori = "20";
                    } else if (getusuario.getUsuario().trim().equalsIgnoreCase("cartago")) {
                        sensori = "12";
                    } else if (getusuario.getUsuario().trim().equalsIgnoreCase("armenia")) {
                        sensori = "7";
                    } else if (getusuario.getUsuario().trim().equalsIgnoreCase("pereira")) {
                        sensori = "17";
                    } else if (getusuario.getUsuario().trim().equalsIgnoreCase("manizales")) {
                        sensori = "14";
                    } else if (getusuario.getUsuario().trim().equalsIgnoreCase("popayan")) {
                        sensori = "18";
                    } else if (getusuario.getUsuario().trim().equalsIgnoreCase("buenaventura")) {
                        sensori = "9";
                    } else if (getusuario.getUsuario().trim().equalsIgnoreCase("ibague")) {
                        sensori = "13";
                    } else if (getusuario.getUsuario().trim().equalsIgnoreCase("bogota")) {
                        sensori = "8";
                    }
                } else if (getusuario.getRol().trim().equalsIgnoreCase("7")) {
                    menu = "MenuXArea.xhtml";
                    ccosto = getusuario.getCcost();
                }
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("mns", menu);
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("sensori", sensori);
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("ciudad", ciudad);
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("usuario", getusuario);
                if (!ccosto.equals("")) {
                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("ccosto", ccosto);
                }
            }
        } catch (SQLException ex) {
            System.out.println("error ex :" + ex.toString());
        } finally {
            try {
                poolSql_19.con.close();
            } catch (SQLException ex) {
                Logger.getLogger(BiometricoBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return getusuario != null ? menu : "";
    }

    public usuario getUsu() {
        return usu;
    }

    public void setUsu(usuario usu) {
        this.usu = usu;
    }

    public Growl getGrowl() {
        return growl;
    }

    public void setGrowl(Growl growl) {
        this.growl = growl;
    }

    public String getMenu() {
        menu = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("mns");
        return menu;
    }
    
    public String getUsuarioLog() {
        usuario u = (usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario");
        return u.getUsuario();
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public sqlServer10_19 getPoolSql_19() {
        return poolSql_19;
    }

    public void setPoolSql_19(sqlServer10_19 poolSql_19) {
        this.poolSql_19 = poolSql_19;
    }

    public String getSensori() {
        return sensori;
    }

    public void setSensori(String sensori) {
        this.sensori = sensori;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

}
