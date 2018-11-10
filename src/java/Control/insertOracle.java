/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Control;

import Coneciones.ConecionOracle;
import Coneciones.sqlServer10_1;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrador
 */
public class insertOracle {

    static ConecionOracle poolOracle = new ConecionOracle();
    static sqlServer10_1 poolSql_1 = new sqlServer10_1();
    static SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        String fecha = formato.format(new Date());
        System.out.println(fecha.substring(0, 10));
//        insert();
    }

    public static void insert() {
        try {
            String q1 = "select EM_CODIGO from RE_EMPLEADOS  where EM_apellido2 = '--'";
            poolOracle.con = poolOracle.dataSource.getConnection();
            ResultSet rs = poolOracle.query(q1);
            ArrayList<String> datos = new ArrayList();
            ArrayList<String> datosCompletos = new ArrayList();
            while (rs.next()) {
                datos.add(rs.getString(1));
            }

            poolSql_1.con = poolSql_1.dataSource.getConnection();
            for (int i = 0; i < datos.size(); i++) {
                String q2 = "select nombre1,nombre2,apellido1,apellido2 from v_rhtrabajador where cod_trabajador = " + datos.get(i) + "";
                ResultSet rs2 = poolSql_1.query(q2);
                while (rs2.next()) {
                    datosCompletos.add(datos.get(i).trim() + ";" + rs2.getString(1).trim() + ";" + rs2.getString(2).trim() + ";" + rs2.getString(3).trim() + ";" + rs2.getString(4).trim());
                }
            }

            for (String datosCompleto : datosCompletos) {
                System.out.println("datosCompleto " + datosCompleto);
                String[] d = datosCompleto.split(";");
                String query = "update RE_EMPLEADOS "
                        + "set EM_nombre1 = '" + d[1] + "', EM_nombre2 = '" + d[2] + "', EM_apellido1 = '" + d[3] + "', EM_apellido2 = '" + d[4] + "'"
                        + " where EM_CODIGO = " + d[0];
                PreparedStatement pstm = poolOracle.con.prepareStatement(query);
                pstm.executeUpdate();
            }

        } catch (SQLException ex) {
            System.out.println("error " + ex);
        }
    }
}
