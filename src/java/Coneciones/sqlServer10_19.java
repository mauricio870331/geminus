package Coneciones;

import java.sql.*;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;

/**
 * Esta clase se utiliza para conectar el sistema con Sql server 10.19
 *
 * @author: Juan David Castrillon
 * @version: 11/12/2017
 */
public class sqlServer10_19 {

    public static String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    public static String user = "sa";
    public static String password = "26031980";//ya esta listo
    public static String query;
    public static Statement stat;
    public static ResultSet rs;
    public Connection con;
    public DataSource dataSource;

    public sqlServer10_19() {
        conectar();
    }

    public void conectar() {
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setDriverClassName(driver);
        basicDataSource.setUsername(user);
        basicDataSource.setPassword(password);
        basicDataSource.setUrl("jdbc:sqlserver://192.168.10.19:1433;databaseName=Biometrico");
        basicDataSource.setMaxActive(50);
        dataSource = basicDataSource;
    }

    public ResultSet query(String query) throws SQLException {
        con = dataSource.getConnection();
        stat = con.createStatement();
        ResultSet res = stat.executeQuery(query);
        return res;
    }

    public int transaccion(String insertQuery) throws SQLException {
        con = dataSource.getConnection();
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

//    public static void cerrarConexion() {
//        try {
//            stat.close();
//            con.close();
//        } catch (SQLException e) {
//            System.out.println("Error en cerrar la base de datos" + e.toString());
//        }
//    }
    public static void main(String[] args) throws ClassNotFoundException {
////
//       categoria cate=new categoria();
//       cate.setidcategoria(new BigDecimal(37));
//       cate.setdescripcion("kikiki-----++++-----");
//       cate.remove();
//     
    }
}
