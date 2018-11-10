package Control;

import Coneciones.ConecionOracle;
import Coneciones.sqlServer10_1;
import Coneciones.sqlServer10_19;
import Model.biometrico;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Esta clase se utiliza para realizar todo el proceso , de Traspaso de
 * marcaciones del servidor (Sql Server 10.19) hacia el servidor (Oracle 10.215)
 * que es el sistema de Geminus, Funciona de la siguiente manera. se carga toda
 * la informacion de (Trabajadores , Programacion , Marcaciones ) del sistema de
 * Geminus , para la fecha seleccionada. internamente en el procedimiento (Sql
 * server 10.1) GeminusTraspasoMarcaciones , es donde se realizan las
 * validaciones pertinentes para realizar el traspaso de marcaciones
 *
 * @author: Juan David Castrillon
 * @version: 11/12/2017
 */
public class procesoMarcacionesBiometricoGeminus implements Serializable {

    ConecionOracle poolOracle = new ConecionOracle();
    sqlServer10_1 poolSql_1 = new sqlServer10_1();
    sqlServer10_19 poolSql_19 = new sqlServer10_19();
    static CallableStatement cstmt;

    ArrayList<biometrico> ListMarcacionesGeminus = new ArrayList();
    ArrayList<biometrico> GeminusPersonalActivo = new ArrayList();
    ArrayList<biometrico> GeminusProgramacion = new ArrayList();

    public static void main(String[] args) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, 0);//cambiar a - 1 o - 2 dependiendo de la fecha de las marcaciones
        InicioProceso(c.getTime());
    }

    /**
     * Metodo statico , que se llama desde la clase principal del hilo , para
     * empezar a relizar todo el trabajo del traspaso de marcaciones
     *
     * @param fecha : revice la fecha que se va a realizar el proceso , para
     * tener el proceso completo se requiere la fecha y un dia siguiente de la
     * fecha recibida como parametro.
     */
    public static void InicioProceso(Date fecha) {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formato2 = new SimpleDateFormat("dd/MMM/yyyy");

        Calendar c = Calendar.getInstance();
        c.setTime(fecha);
        c.add(Calendar.DATE, 1);

        procesoMarcacionesBiometricoGeminus proceso = new procesoMarcacionesBiometricoGeminus();

        proceso.cargaPersonalActivo();
        proceso.cargaMarcacionesGeminus(formato2.format(fecha), formato2.format(c.getTime()));
        proceso.cargaPersonalActivoConProgramacion(formato2.format(fecha));
        proceso.cargarInformacionBaseDedatos();
        proceso.Transaccion(formato.format(fecha));
    }

    /**
     * Metodo que se encarga de cargar la informacion de los trabajadores , que
     * estan creados en el sistema Geminus
     */
    public void cargaPersonalActivo() {
        GeminusPersonalActivo.clear();
        try {
            System.out.println("------------------");
            poolOracle.con = poolOracle.dataSource.getConnection();
            ResultSet rs = poolOracle.query("select em_codigo,em_cedula,em_centrocosto,em_retirado from re_empleados where em_cargo not in (76)");
            while (rs.next()) {
                GeminusPersonalActivo.add(new biometrico(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), 1));
            }
        } catch (SQLException ex) {
            System.out.println("Error cargaPersonalActivo : " + ex.toString());
        } finally {
            try {
                poolOracle.con.close();
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        }
    }

    /**
     * Metodo que se realiza una cargar la programacion , de cada trabajador que
     * este creado en Geminus , y que tenga programacion para la fecha
     * seleccionada.
     *
     * @param fecha : Parametro de fecha en la cual se esta evaluando la
     * programacion , para traer a las personas de esa fecha
     */
    public void cargaPersonalActivoConProgramacion(String fecha) {
        GeminusProgramacion.clear();
        try {
            poolOracle.con = poolOracle.dataSource.getConnection();
            ResultSet rs = poolOracle.query("select pj_empleado,jt_codigo,B.jt_horainicialnovedades,B.jt_minutoinicialnovedades,B.jt_horafinalnovedades,\n"
                    + "B.jt_minutofinalnovedades from RE_PROGRAMACION_JORNADAS A ,RE_JORNADASTRABAJO B where \n"
                    + "A.pj_jornada=B.jt_codigo and \n"
                    + "pj_fecha='" + fecha + "'");

            while (rs.next()) {
                GeminusProgramacion.add(new biometrico(rs.getString(1), rs.getString(2), rs.getInt(3), rs.getInt(4), rs.getInt(5), rs.getInt(6)));
            }

        } catch (SQLException ex) {
            System.out.println("Error  cargaPersonalActivoConProgramacion : " + ex.toString());
        } finally {
            try {
                poolOracle.con.close();
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        }
    }

    /**
     * Metodo que realiza una cargar de las marcaciones que ya se pasaron al
     * sistema de Geminus, con que objectivo: para no volver a pasar la misma
     * informacion , y poder validar si ya se paso.
     *
     * @param fechaIni : Fecha inicial , en la cual se comienza a consultar las
     * marcaciones realizadas
     * * @param FechaFin : Fecha final , en la cual se Finalza la consulta de
     * las marcaciones realizadas , la fecha siempre es un dia despues , por las
     * programaciones de trasnocho
     */
    public void cargaMarcacionesGeminus(String fechaIni, String FechaFin) {
        ListMarcacionesGeminus.clear();
        System.out.println(": " + fechaIni);
        try {
            poolOracle.con = poolOracle.dataSource.getConnection();
            // poolOracle.transaccion("update RE_MARCACIONES set ma_tipo='S' where ma_tipo='T' and ma_fecha>='01/DEC/2017'");
            System.out.println("---");
            ResultSet rs = poolOracle.query("select ma_codigo,ma_fecha,ma_hora,ma_tipo from re_marcaciones where"
                    + " ma_fecha between '" + fechaIni + "' and '" + FechaFin + "'");
            System.out.println("--- 2");
            while (rs.next()) {
                ListMarcacionesGeminus.add(new biometrico(rs.getString(1), rs.getString(2), rs.getTimestamp(3), rs.getString(4)));
            }

        } catch (SQLException ex) {
            System.out.println("Error cargaMarcacionesGeminus : " + ex.toString());
        } finally {
            try {
                poolOracle.con.close();
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        }
    }

    /**
     * Metodo que se encargar de montar toda la informacion al servidor (Sql
     * server 10.1) , antes de montar la informacion elimina la que ya exista ,
     * para comenzar un proceso nuevo.
     *
     */
    public void cargarInformacionBaseDedatos() {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            poolSql_1.con = poolSql_1.dataSource.getConnection();
            String insertMarcaciones = "";
            String insertProgramacion = "";
            String insertTrabajdadores = "";
            System.out.println("10.19 : " + ListMarcacionesGeminus.size());
            System.out.println("programacion : " + GeminusProgramacion.size());
            System.out.println("activos : " + GeminusPersonalActivo.size());
            for (biometrico marcacion : ListMarcacionesGeminus) {
                insertMarcaciones += "insert into cpt_GeminusProcesoMarcaciones values(" + marcacion.getCodigo() + ",'" + formato.format(marcacion.getFechaMarcacion()) + "','" + marcacion.getTipoLlegada() + "');";
            }

            for (biometrico programacion : GeminusProgramacion) {
                insertProgramacion += "insert into cpt_GeminusProcesoProgramacion values(" + programacion.getCodigo().trim() + ",'" + programacion.getTurno().trim() + "',"
                        + programacion.getHoraInicial() + "," + programacion.getMinInicial() + "," + programacion.getHoraFinal() + "," + programacion.getMinFinal() + ");";
            }

            for (biometrico trabajador : GeminusPersonalActivo) {
                insertTrabajdadores += "insert into cpt_GeminusProcesoTrabajadores values(" + trabajador.getCodigo().trim() + ",'" + trabajador.getEstado().trim() + "','"
                        + trabajador.getTurno().trim() + "');";
            }

            poolSql_1.transaccion("delete from cpt_GeminusProcesoMarcaciones");
            poolSql_1.transaccion("delete from cpt_GeminusProcesoProgramacion");
            poolSql_1.transaccion("delete from cpt_GeminusProcesoTrabajadores");
            poolSql_1.transaccion("delete from cpt_GeminusProcesoInsercciones");
            poolSql_1.transaccion(insertMarcaciones);
            poolSql_1.transaccion(insertProgramacion);
            poolSql_1.transaccion(insertTrabajdadores);

        } catch (SQLException ex) {
            System.out.println("Error cargarInformacionBaseDedatos : " + ex.toString());
        } finally {
            try {
                poolSql_1.con.close();
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        }
    }

    /**
     * Metodo que se encarga de correr el procedimiento que se encuentra en (Sql
     * server) 10.1 , el cual se encarga de validar la informacion proporcionada
     * de las marcaciones , y pasarlas al sistema de Geminus.
     *
     * @param fecha : parametro que que el procedimiento pueda capturar las
     * marcaciones de los huelleros de esa fecha.
     */
    public void Transaccion(String fecha) {
        try {
            ArrayList<biometrico> ListPersonaParaRegistrar = new ArrayList();
            poolSql_1.con = poolSql_1.dataSource.getConnection();

            cstmt = poolSql_1.con.prepareCall("{call GeminusTraspasoMarcaciones (?,?)}");
            cstmt.setString(1, fecha);
            cstmt.registerOutParameter(2, java.sql.Types.INTEGER);
            cstmt.execute();

            if (cstmt.getInt(2) == 0) {
                ActualizacionMarcacionesBiometrico();
                ResultSet rs = poolSql_1.query("select distinct EM_CODIGO,EM_CEDULA,EM_NOMBRE,EM_CARGO,EM_FECHAINGRESO,\n"
                        + "EM_CENTROCOSTO,EM_FECHANACIMIENTO,EM_CODIGONOMINA,EM_ID_DEPENDENCIA,\n"
                        + "EM_RECARGONOCTURNO,EM_EXTRASFESTIVAS,EM_RECARGONOCTURNOFESTIVOS,EM_ENTURNO,EM_PAGAN_DOMINICALES,\n"
                        + "EM_FECHAMOD from cpt_GeminusProcesoInsercciones A , re_empleados_geminus B\n"
                        + "where A.Codigo=B.EM_CODIGO and A.activo='N'");

                while (rs.next()) {
                    ListPersonaParaRegistrar.add(new biometrico(rs.getString(1), rs.getString(2), rs.getString(3),
                            rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9),
                            rs.getString(10), rs.getString(11), rs.getString(12), rs.getString(13), rs.getString(14)));

                }
//                GeminusPersonalNoRegistrado(ListPersonaParaRegistrar);
            }

        } catch (SQLException ex) {
            System.out.println("Error Transaccion : " + ex.toString());
        } finally {
            try {
                poolSql_1.con.close();
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        }
    }

    /**
     * Metodo que se encarga de actualizar el estado de la marcacion , en el
     * huellero para que no la vuelva a pasar
     *
     */
    public void ActualizacionMarcacionesBiometrico() {
        try {
            poolSql_19.con = poolSql_19.dataSource.getConnection();
            cstmt = poolSql_19.con.prepareCall("{call GeminusUpdateMarcaciones}");
            cstmt.execute();
        } catch (SQLException ex) {
            System.out.println("Error  ActualizacionMarcacionesBiometrico: " + ex.toString());
        } finally {
            try {
                poolSql_19.con.close();
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        }
    }

    /**
     * Metodo que se encarga , de registrar personal que esta marcando , y no se
     * encuentra registrado
     *
     */
    public void GeminusPersonalNoRegistrado(ArrayList<biometrico> x) {
        try {
            ArrayList<biometrico> ListUpdateBiometrico = new ArrayList();
            poolOracle.con = poolOracle.dataSource.getConnection();
            String query = "";
            for (biometrico object : x) {
                query = "insert into re_empleados values('" + object.getCodigo().trim() + "'," + object.getCedula() + ",'" + object.getNombre().trim() + "',0,'"
                        + object.getCargo().trim() + "',null,null,TO_DATE('" + object.getFechaIngreso().trim() + "', 'DD/MM/YYYY')" + ",null,'01','"
                        + object.getCentroCosto().trim() + "','N','N',NULL,NULL,'N',TO_DATE('" + object.getFecNacimiento().trim() + "', 'DD/MM/YYYY')"
                        + ",NULL,'01',NULL,NULL,NULL,'01','" + object.getCodigo().trim() + "','" + object.getDependencia().trim() + "','" + object.getExNocturnas().trim() + "','"
                        + object.getExFestivas().trim() + "','" + object.getExNocfestvias().trim() + "','" + object.getExEnturno().trim() + "',TO_DATE('01/01/1900', 'DD/MM/YYYY'),'" + object.getExDominicales().trim() + "',0)";
                poolOracle.transaccion(query);
            }
        } catch (SQLException ex) {
            System.out.println("Error -: " + ex.toString());
        } finally {
            try {
                poolOracle.con.close();
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        }
    }

   
}
