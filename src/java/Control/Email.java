package Control;

import Coneciones.sqlServer10_1;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Esta clase se utiliza para enviar Correos electronicos , o notificaciones por
 * medio de un procedimiento que se encuentra en la base de datos (Sql Server)
 *
 * @author: Juan David Castrillon
 * @version: 11/12/2017
 */

public class Email {

    sqlServer10_1 poolSql_1 = new sqlServer10_1();
    static CallableStatement cstmt;

    /**
     * Constructor Vacio
     */
    public Email() {
    }

    /**
     * Metodo para enviar novedades de marcacion , a las diferentes agencias.
     * Internamente llama a procedimiento EmailNovedadesCopy (SqlServer)
     *
     * @return La transaccion 0 : Existosa 1: Error
     */
    public int getMail() {
        int transaccion = -1;
        try {
            poolSql_1.con = poolSql_1.dataSource.getConnection();
            cstmt = poolSql_1.con.prepareCall("{call EmailNovedadesCopy}");
            ResultSet rs = cstmt.executeQuery();
            while (rs.next()) {
                transaccion = rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("Error : " + e.toString());
        } finally {
            try {
                poolSql_1.con.close();
                cstmt.close();
            } catch (SQLException ex) {
                Logger.getLogger(Email.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return transaccion;
    }

    /**
     * Metodo para enviar novedades de error mientras se esta ejecutando , el Job de Traspaso de marcaciones  
     * @param mns :  se recibe como parametro el mensaje  , que se va a enviar al correo como error.
     */
    public void getMailNovedades(String mns) {
        int transaccion = -1;
        try {
            poolSql_1.con = poolSql_1.dataSource.getConnection();
            cstmt = poolSql_1.con.prepareCall("{call EnviarEmail (?,?,?,?,?,?)}");
            cstmt.setString(1, "Email Sistemas");
            cstmt.setString(2, "desarrollo2@expresopalmira.com.co");
            cstmt.setString(3, "Traspaso Marcaciones");
            cstmt.setString(4, mns);
            cstmt.setString(5, "Proceso Marcaciones");
            cstmt.setInt(6, 1);

            cstmt.execute();

        } catch (Exception e) {
            System.out.println("Error : " + e.toString());
        } finally {
            try {
                poolSql_1.con.close();
                cstmt.close();
            } catch (SQLException ex) {
                Logger.getLogger(Email.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
