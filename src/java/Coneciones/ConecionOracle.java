package Coneciones;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import javax.swing.JOptionPane;
import org.apache.commons.dbcp.BasicDataSource;

/**
 * Esta clase se utiliza para conectar el sistema con oracle
 *
 * @author: Juan David Castrillon
 * @version: 11/12/2017
 */
public class ConecionOracle {

    public static String driver = "new oracle.jdbc.driver.OracleDriver()";
    public static String query;
    public static Statement stat;
    public static ResultSet rs;
    public Connection con;
    public DataSource dataSource;

    public ConecionOracle() {
        conectar();
    }

    public void conectar() {
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        basicDataSource.setUsername("geminusi4");
        basicDataSource.setPassword("ep43");
        basicDataSource.setUrl("jdbc:oracle:thin:@localhost:1521:XE");
        basicDataSource.setMaxActive(50);
        dataSource = basicDataSource;
    }

    private void btnProbarconexionActionPerformed() {
        ConecionOracle metodospool = new ConecionOracle();
        java.sql.Connection cn = null;
        try {
            cn = metodospool.dataSource.getConnection();

            if (cn != null) {

                JOptionPane.showMessageDialog(null, "Conectado");

            }

        } catch (SQLException e) {

            System.out.println(e);

        } finally {

            try {

                cn.close();

            } catch (SQLException ex) {

                System.out.println(ex);

            }

        }

    }

    public ResultSet query(String query) throws SQLException {
        stat = con.createStatement();
        ResultSet res = stat.executeQuery(query);
        return res;
    }

    public int transaccion(String insertQuery) throws SQLException {
        stat = con.createStatement();
        int result = stat.executeUpdate(insertQuery);
        return result;
    }

    public Connection getconecion() {
        return con;
    }

    public static boolean ejecuteQuery(String x) throws SQLException {
        boolean r = true;
        try {
            rs = stat.executeQuery(x);
        } catch (SQLException e) {
            System.out.println("ERROR AL HACER QUERY " + e.toString());

            r = false;
        }
        return r;
    }

    public static boolean ejecuteUpdate(String query) {
        boolean r = true;
        try {
            stat.executeUpdate(query);
            r = true;
        } catch (SQLException e) {
            System.out.println("ERROR Al HACER UPDTAPE" + e.toString());
            r = false;
        }
        return r;
    }
//
//    public static void cerrarConexion() {
//        try {
//            stat.close();
//            con.close();
//        } catch (SQLException e) {
//            System.out.println("Error en cerrar la base de datos" + e.toString());
//        }
//    }

    public Connection getCon() {
        return con;
    }

    public void setCon(Connection con) {
        this.con = con;
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        ConecionOracle c = new ConecionOracle();
        c.btnProbarconexionActionPerformed();
    }
}
