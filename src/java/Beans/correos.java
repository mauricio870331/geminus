package Beans;

import Coneciones.sqlServer10_1;
import Model.correosXagencia;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 * Clase que se utiliza para gestionar los correos , de las diferentes agencias
 * , para autorizar o denegar que le lleguen los correos de las novedades
 *
 */
@Named(value = "correos")
@SessionScoped
public class correos implements Serializable {

    ArrayList<correosXagencia> listCorreos = new ArrayList();
    ArrayList<correosXagencia> listCorreosMostrar = new ArrayList();
    ArrayList<correosXagencia> listAgencias = new ArrayList();
    sqlServer10_1 poolSql_1 = new sqlServer10_1();
    private String agencia;
    boolean estadoAgencia;

    /**
     * Metodo constructor , se cargar las agencias y los correos ya guardados en
     * la base de datos de (Sql Server 10.1) Nodum
     *
     */
    @PostConstruct
    public void init() {
        try {
            cargaragencias();
            cargarCorreos();
        } catch (SQLException ex) {
            Logger.getLogger(correos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public correos() {
    }

    /**
     * Metodo para cargar los correos del sistema.
     *
     */
    public void cargarCorreos() throws SQLException {
        try {
            listCorreos.clear();
            poolSql_1.con = poolSql_1.dataSource.getConnection();
            ResultSet rs = poolSql_1.query("select A.codigo,A.sucursal,A.correo,A.estado from CorreosAgencias A");
            boolean campo = false;
            while (rs.next()) {
                correosXagencia correo = new correosXagencia();
                correo.setAgencia(rs.getString(2));
                correo.setCorreo(rs.getString(3));
                campo = rs.getInt(4) == 0 ? true : false;
                correo.setCodigo(rs.getInt(1));
                correo.setEstado(campo);
                listCorreos.add(correo);
            }

        } catch (SQLException e) {
            System.out.println("Error proceso cargarCorreos : " + e.toString());
        } finally {
            poolSql_1.con.close();

        }
    }

    /**
     * Metodo para cargar las agencias del sistema.
     *
     */
    public void cargaragencias() throws SQLException {
        try {
            listAgencias.clear();
            poolSql_1.con = poolSql_1.dataSource.getConnection();
            boolean campo = false;
            ResultSet rs = poolSql_1.query("select a.nombre,a.estado from ct_sucursalesgeminus A  order by a.nombre");
            while (rs.next()) {
                correosXagencia correo = new correosXagencia();
                correo.setAgencia(rs.getString(1));
                campo = rs.getInt(2) == 0 ? true : false;
                correo.setEstado(campo);
                listAgencias.add(correo);
            }
        } catch (SQLException e) {
            System.out.println("Error proceso cargaragencias : " + e.toString());
        } finally {
            poolSql_1.con.close();

        }
    }

    /**
     * Metodo para filtrar los correos que pertenezcan , a la agencia
     * seleccionada
     *
     */
    public void filtrosCorreos() {
        this.estadoAgencia = false;
        listCorreosMostrar.clear();
        for (correosXagencia listCorreo : listCorreos) {
            if (listCorreo.getAgencia().trim().equalsIgnoreCase(agencia)) {
                listCorreosMostrar.add(listCorreo);
            }
        }
        for (correosXagencia agencias : listAgencias) {
            if (agencias.getAgencia().toString().trim().equalsIgnoreCase(agencia)) {
                this.estadoAgencia = agencias.isEstado();
            }
        }
    }

    /**
     * Metodo para Deshabilitar agencias del sistema.
     *
     */
    public void desabilitarAgencias() throws SQLException {
        try {
            int transacion = 0;
            poolSql_1.con = poolSql_1.dataSource.getConnection();
            poolSql_1.con.setAutoCommit(false);
            if (estadoAgencia) {
                //Activar
                transacion = poolSql_1.transaccion("update ct_sucursalesgeminus set estado=0 where nombre='" + agencia.trim() + "'");
            } else {
                //Deshabilitar
                transacion = poolSql_1.transaccion("update ct_sucursalesgeminus set estado=1 where nombre='" + agencia.trim() + "'");
            }

            if (transacion == 1) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informacion", "Exito Activa Agencia"));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Informacion", "Error Desabilitada Agencia"));
            }

        } catch (SQLException e) {
            System.out.println("Error proceso desabilitarAgencias : " + e.toString());
        } finally {
            poolSql_1.con.setAutoCommit(true);
            poolSql_1.con.close();

        }
    }

    /**
     * Metodo para Deshabilitar Correso de una agencia.
     *
     */
    public void cambioEstado(correosXagencia correo) throws SQLException {
        try {
            int transacion = 0;
            poolSql_1.con = poolSql_1.dataSource.getConnection();
            if (correo.isEstado()) {
                System.out.println("activar");
                transacion = poolSql_1.transaccion("update CorreosAgencias set estado=0 where codigo=" + correo.getCodigo());
            } else {
                System.out.println("Inactivar");
                transacion = poolSql_1.transaccion("update CorreosAgencias set estado=1 where codigo=" + correo.getCodigo());
            }

            if (transacion == 1) {
                System.out.println("exito");
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informacion", "Exito Actualizando"));
            } else {
                System.out.println("error");
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Informacion", "Error Actualizando"));
            }

        } catch (SQLException e) {
            System.out.println("Error proceso DB : " + e.toString());
        } finally {
            poolSql_1.con.close();

        }
    }

    public ArrayList<correosXagencia> getListCorreos() {
        return listCorreos;
    }

    public void setListCorreos(ArrayList<correosXagencia> listCorreos) {
        this.listCorreos = listCorreos;
    }

    public sqlServer10_1 getPoolSql_1() {
        return poolSql_1;
    }

    public void setPoolSql_1(sqlServer10_1 poolSql_1) {
        this.poolSql_1 = poolSql_1;
    }

    public ArrayList<correosXagencia> getListAgencias() {
        return listAgencias;
    }

    public void setListAgencias(ArrayList<correosXagencia> listAgencias) {
        this.listAgencias = listAgencias;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public ArrayList<correosXagencia> getListCorreosMostrar() {
        return listCorreosMostrar;
    }

    public void setListCorreosMostrar(ArrayList<correosXagencia> listCorreosMostrar) {
        this.listCorreosMostrar = listCorreosMostrar;
    }

    public boolean isEstadoAgencia() {
        return estadoAgencia;
    }

    public void setEstadoAgencia(boolean estadoAgencia) {
        this.estadoAgencia = estadoAgencia;
    }

}
