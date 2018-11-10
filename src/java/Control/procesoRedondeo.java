package Control;

import Coneciones.ConecionOracle;
import Model.RedondeoJornada;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Esta clase se utiliza para realizar el rendondeo de las marcaciones, de
 * acuerdo a la programacion que tenga , el trabajador
 *
 * @author: Juan David Castrillon
 * @version: 11/12/2017
 */
public class procesoRedondeo {

    ArrayList<RedondeoJornada> list_jornada = new ArrayList();
    ConecionOracle poolOracle = new ConecionOracle();

    public static void main(String[] juan) throws SQLException {
        procesoRedondeo p = new procesoRedondeo();
        String fecha = "31/10/2018";
        Date fechaD = null;
        SimpleDateFormat formatoDelTexto = new SimpleDateFormat("dd/M/yyyy");
        try {
            fechaD = formatoDelTexto.parse(fecha);
        } catch (ParseException ex) {
            Logger.getLogger(procesoRedondeo.class.getName()).log(Level.SEVERE, null, ex);
        }
        SimpleDateFormat format2 = new SimpleDateFormat("dd/M/yyyy");
        p.RedondiarEntrada(new Date());
        p.RedondiarSalida(new Date());
        p.redondiarSalidaTrasnocho(fechaD);
    }

    /**
     * Metodo para redondear las marcaciones de entrada , de acuerdo a la
     * programacion establecida , por cada trabajador , en las fechas
     * seleccionadas. para la entrada son 59 minutos antes y 15 minutos despues
     * , si cumple estas condiciones entonces se ajusta la marcacion de entrada.
     *
     * @param fecha : Fecha inicial para realizar el filtro de las marcacion a
     * ajustar
     */
    public void RedondiarEntrada(Date fecha) throws SQLException {
        try {
            SimpleDateFormat formato = new SimpleDateFormat("dd/M/yyyy");
            Calendar c = Calendar.getInstance();
            c.setTime(fecha);
            c.add(Calendar.DATE, -20);

            poolOracle.con = poolOracle.dataSource.getConnection();
            SimpleDateFormat format2 = new SimpleDateFormat("dd/M/yyyy");
            list_jornada.clear();
            String query = "select  distinct MA_CODIGO,MA_FECHA,\n"
                    + "case when hora>12 then hora-12 else hora end||':'||MINU||':00' as HORAREAL,\n"
                    + "case when hora>=12 then 'PM' else 'AM' end,MARCACION from \n"
                    + "AJUSTETIEMPOSENTRADA\n"
                    + "where ma_fecha between TO_DATE('" + formato.format(c.getTime()) + "', 'DD/MM/YYYY') and TO_DATE('" + formato.format(fecha) + "', 'DD/MM/YYYY')";
            System.out.println(query);
            ResultSet rs = poolOracle.query(query);
            while (rs.next()) {
                list_jornada.add(new RedondeoJornada(rs.getString(1),
                        rs.getDate(2), rs.getString(3),
                        rs.getString(4), rs.getString(5)));
            }
            poolOracle.getconecion().setAutoCommit(false);
            for (RedondeoJornada list_jornada1 : list_jornada) {
                System.out.println("Ajusto entrada");
                poolOracle.transaccion("update RE_MARCACIONES set \n"
                        + "ma_hora=to_date('" + format2.format(list_jornada1.getFecha()) + " " + list_jornada1.getFechaReal() + " " + list_jornada1.getFormato() + "','DD/MM/YYYY HH:MI:SS pm') , tipoajuste='Redondeo' "
                        + " where ma_fecha=TO_DATE('" + format2.format(list_jornada1.getFecha()) + "','DD/MM/YYYY')"
                        + " and  TO_CHAR (ma_hora, 'HH24:MI:SS')='" + list_jornada1.getMarcacion() + "' and ma_codigo=" + list_jornada1.getCodigo()
                        + "and ma_tipo='E'");
            }

        } catch (SQLException ex) {
            System.out.println("Error redondiarEntrada :  " + ex.toString());
        } finally {
            poolOracle.getconecion().commit();
            poolOracle.getconecion().setAutoCommit(true);
            poolOracle.con.close();
        }
    }

    /**
     * Metodo para redondear las marcaciones de Salida , de acuerdo a la
     * programacion establecida , por cada trabajador , en las fechas
     * seleccionadas. para la salida son 15 minutos antes y 59 minutos despues ,
     * si cumple estas condiciones entonces se ajusta la marcacion de salida.
     *
     * @param fecha : Fecha para realizar el filtro de las marcacion a ajustar
     */
    public void RedondiarSalida(Date fecha) throws SQLException {
        try {
            SimpleDateFormat formato = new SimpleDateFormat("dd/M/yyyy");
            Calendar c = Calendar.getInstance();
            c.setTime(fecha);
            c.add(Calendar.DATE, -20);

            poolOracle.con = poolOracle.dataSource.getConnection();
            SimpleDateFormat format2 = new SimpleDateFormat("dd/M/yyyy");
            list_jornada.clear();
            String query = "select  distinct  MA_CODIGO,MA_FECHA,\n"
                    + "case when hora>12 then hora-12 else hora end||':'||MINU||':00' as HORAREAL,jornada,\n"
                    + "case when hora>=12 then 'PM' else 'AM' end,MARCACION from \n"
                    + "AJUSTETIEMPOSALIDA\n"
                    + "where ma_fecha between TO_DATE('" + formato.format(c.getTime()) + "', 'DD/MM/YYYY') "
                    + "and TO_DATE('" + formato.format(fecha) + "', 'DD/MM/YYYY')";
            System.out.println(query);
            ResultSet rs = poolOracle.query(query);
            while (rs.next()) {
                list_jornada.add(new RedondeoJornada(rs.getString(1),
                        rs.getDate(2), rs.getString(3),
                        rs.getString(4), rs.getString(5), rs.getString(6)));
            }
            poolOracle.getconecion().setAutoCommit(false);
            for (RedondeoJornada list_jornada1 : list_jornada) {
                System.out.println("Ajusto Salida");
                poolOracle.transaccion("update RE_MARCACIONES set \n"
                        + "ma_hora=to_date('" + format2.format(list_jornada1.getFecha()) + " " + list_jornada1.getFechaReal() + " " + list_jornada1.getFormato() + "','DD/MM/YYYY HH:MI:SS pm') , tipoajuste='Redondeo' "
                        + " where ma_fecha=TO_DATE('" + format2.format(list_jornada1.getFecha()) + "','DD/MM/YYYY')" + ""
                        + " and  TO_CHAR (ma_hora, 'HH24:MI:SS')='" + list_jornada1.getMarcacion() + "' and ma_codigo=" + list_jornada1.getCodigo()
                        + " and ma_tipo='S'");
            }

        } catch (Exception ex) {

        } finally {
            poolOracle.getconecion().commit();
            poolOracle.getconecion().setAutoCommit(true);
            poolOracle.con.close();
        }
    }

    /**
     * Metodo para redondear las marcaciones de Salida de los turnos de
     * trasnocho , de acuerdo a la programacion establecida , por cada
     * trabajador , en las fechas seleccionadas. para la salida son 15 minutos
     * antes y 59 minutos despues , si cumple estas condiciones entonces se
     * ajusta la marcacion de salida.
     *
     * @param fecha : Fecha para realizar el filtro de las marcacion a ajustar
     */
    public void redondiarSalidaTrasnocho(Date fecha) throws SQLException {
        SimpleDateFormat format2 = new SimpleDateFormat("dd/M/yyyy");
        try {
            poolOracle.con = poolOracle.dataSource.getConnection();
            list_jornada.clear();
            String query = "select distinct  MA_CODIGO, (to_date(MA_FECHA) + 1), \n"
                    + "case when hora>12 then hora-12 else (case when hora=0 then 12 else hora end) end||':'||MINU||':00' as HORAREAL,\n"
                    + "case when hora>=12 then 'PM' else 'AM' end,MARCACION from \n"
                    + "AJUSTETIMEPOSALIDATRAS\n"
                    + "where ma_fecha=TO_DATE('" + format2.format(fecha) + "', 'DD/MM/YYYY')";
            System.out.println(query);
            ResultSet rs = poolOracle.query(query);
            while (rs.next()) {
                list_jornada.add(new RedondeoJornada(rs.getString(1),
                        rs.getDate(2), rs.getString(3),
                        rs.getString(4), rs.getString(5)));
            }
            poolOracle.getconecion().setAutoCommit(false);
            for (RedondeoJornada list_jornada1 : list_jornada) {
                System.out.println("Ajusto Trasnocho");
//                System.out.println("update RE_MARCACIONES set \n"
//                        + "ma_hora=to_date('" + format2.format(list_jornada1.getFecha()) + " " + list_jornada1.getFechaReal() + " " + list_jornada1.getFormato() + "','DD/MM/YYYY HH:MI:SS pm') , tipoajuste='Redondeo'"
//                        + " where ma_fecha=TO_DATE('" + format2.format(list_jornada1.getFecha()) + "', 'DD/MM/YYYY')"
//                        + " and  TO_CHAR (ma_hora, 'HH24:MI:SS')='" + list_jornada1.getMarcacion() + "' and ma_codigo=" + list_jornada1.getCodigo()
//                        + "and ma_tipo='S'");
                poolOracle.transaccion("update RE_MARCACIONES set \n"
                        + "ma_hora=to_date('" + format2.format(list_jornada1.getFecha()) + " " + list_jornada1.getFechaReal() + " " + list_jornada1.getFormato() + "','DD/MM/YYYY HH:MI:SS pm') , tipoajuste='Redondeo'"
                        + " where ma_fecha=TO_DATE('" + format2.format(list_jornada1.getFecha()) + "', 'DD/MM/YYYY')"
                        + " and  TO_CHAR (ma_hora, 'HH24:MI:SS')='" + list_jornada1.getMarcacion() + "' and ma_codigo=" + list_jornada1.getCodigo()
                        + "and ma_tipo='S'");

            }

        } catch (Exception ex) {

        } finally {
            poolOracle.getconecion().commit();
            poolOracle.getconecion().setAutoCommit(true);
            poolOracle.con.close();
        }
    }

}
