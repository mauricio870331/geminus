package Control;

import Coneciones.ConecionOracle;
import Coneciones.sqlServer10_1;
import static Control.Email.cstmt;
import Model.Festivos;
import Model.Horasextras;
import Model.Novedades;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Esta clase se utiliza para obtener las horas extras , previamente liquidadas
 * en el sistema de Geminus(10.215 Oracle) Comparando con las horas extras con
 * las novedades generadas en la quincena.
 *
 * @author: Juan David Castrillon
 * @version: 11/12/2017
 */
public class procesoReporteLiq implements Serializable {

    ArrayList<Horasextras> listExtras = new ArrayList();
    ArrayList<Horasextras> listExtrasFinal = new ArrayList();
    ArrayList<Horasextras> listTrabajadores = new ArrayList();
    ArrayList<Horasextras> listIncapacidades = new ArrayList();
    ArrayList<Festivos> listFestivos = new ArrayList();
    ArrayList<Novedades> list_novedades = new ArrayList();
    ArrayList<String> trabajadores = new ArrayList();
    static CallableStatement cstmt;

    ConecionOracle poolOracle = new ConecionOracle();
    sqlServer10_1 poolSql_1 = new sqlServer10_1();

    /**
     * Metodo para cargar la informacion de las horas extras de Geminus (Oracle
     * 10.215)
     *
     * @param fechaIni : Fecha inicial para filtrar el reporte de las horas
     * extras liquidadas
     *
     * @param fechaFin : Fecha final para filtrar el reporte de las horas extras
     * liquidadas
     *
     * @param empresa : Filtro para realizar la carga de los trabajadores de la
     * empresa seleccioanda
     *
     * @param condicion :Filtro para seleccionar que listado se retornara al
     * final , si todas las horas extras o solo los dias no laborados
     */
    public List<Horasextras> cargarReporte(String fechaIni, String fechaFin, String empresa, int condicion) throws ClassNotFoundException, SQLException, ParseException {
        try {
            poolOracle.con = poolOracle.dataSource.getConnection();
            listExtras.clear();
            //Consulta Para Traer las horas extras liquidadas en Geminus
            String query = "select A.em_cedula,A.codigo,A.valor,A.cod_emp,A.li_fecha,A.em_codigo,A.em_id_dependencia"
                    + ", (to_char(A.li_fecha, 'DAY', 'NLS_DATE_LANGUAGE=SPANISH')) from ReporteHorasExtras A where"
                    + "  A.li_fecha between TO_DATE('" + fechaIni + "', 'DD/MM/YYYY') and TO_DATE('" + fechaFin + "', 'DD/MM/YYYY') order by 1";
            System.out.println("- " + query);
            ResultSet rs = poolOracle.query(query);
            while (rs.next()) {
                listExtras.add(new Horasextras(rs.getString(1), rs.getString(2),
                        rs.getDouble(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8)));

            }

        } catch (SQLException ex) {
            System.out.println("Error cargarReporte :  " + ex.toString());
        } finally {
            try {
                poolOracle.con.close();
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        }

        //Cargamos los trabajadores de Sql server
        cargaDeTrabajadores(empresa);
        cargaDeIncapacidades(fechaIni, fechaFin, empresa);
        //Ajustamos las extras que trajo Geminis contra (Novedades y trabajadores)
        AjusteExtras(fechaIni, fechaFin, empresa, condicion);
        return listExtrasFinal;
    }

    /**
     * Metodo para cargar los trabajadores activos en el sistema de Nodum (10.1)
     * , de acuerdo a la empresa seleccioanda
     *
     * @param empresa : Filtro para realizar la carga de los trabajadores
     */
    public void cargaDeTrabajadores(String empresa) throws SQLException {
        try {
            poolSql_1.con = poolSql_1.dataSource.getConnection();
            listTrabajadores.clear();

            ResultSet rs = poolSql_1.query("select cod_trabajador,cod_emp from ct_RHTrabajador where  cod_emp='" + empresa + "' and estado_trab='ACTIVO'");
            while (rs.next()) {
                listTrabajadores.add(new Horasextras(rs.getString(1), rs.getString(2)));
            }
        } catch (SQLException ex) {
            System.out.println("Error Al cargar Los trabajadores : " + ex.toString());
        } finally {
            try {
                poolSql_1.con.close();
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        }
    }

    /**
     * Metodo para cargar las incapacidades de los trabajadores del sistema de
     * Nodum (Sql server 10.1)
     *
     * @param fechaIni : Fecha inicial para filtrar las incapacidades
     *
     * @param fechaFin : Fecha final para filtrar las incapacidades
     *
     * @param empresa : Filtro para realizar las incapacidades de los
     * trabajdores por la empresa.
     *
     */
    public void cargaDeIncapacidades(String fechaIni, String fechaFin, String empresa) throws SQLException {
        String f1[] = fechaIni.split("/");
        String f2[] = fechaFin.split("/");
        String fechaInicial = f1[2] + "-" + f1[1] + "-" + f1[0];
        String fechaFinal = f2[2] + "-" + f2[1] + "-" + f2[0];

        listIncapacidades.clear();
        try {
            poolSql_1.con = poolSql_1.dataSource.getConnection();
            cstmt = poolSql_1.con.prepareCall("{call ReporteIncapacidadesGeminus (?,?,?)}");
            cstmt.setString(1, fechaInicial.trim());
            cstmt.setString(2, fechaFinal.trim());
            cstmt.setString(3, empresa.trim());
            ResultSet rs = cstmt.executeQuery();
            while (rs.next()) {
                listIncapacidades.add(new Horasextras(rs.getString(1), rs.getString(2), true));
            }
        } catch (Exception e) {
            System.out.println("Error SQL procedure reporte: " + e.toString());
        } finally {
            try {
                poolSql_1.con.close();
                cstmt.close();
            } catch (SQLException ex) {
                Logger.getLogger(Email.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Metodo para cargar las incapacidades de los trabajadores del sistema de
     * Nodum (Sql server 10.1)
     *
     * @param fechaIni : Fecha inicial para filtrar las incapacidades
     *
     * @param fechaFin : Fecha final para filtrar las incapacidades
     *
     * @param empresa : Filtro para realizar las incapacidades de los
     * trabajdores por la empresa.
     *
     * @param condicion :Filtro para seleccionar que listado se retornara al
     * final , si todas las horas extras o solo los dias no laborados
     */
    public void AjusteExtras(String fechaIni, String fechaFin, String empresa, int condicion) throws ClassNotFoundException, ParseException {
        try {
            poolOracle.con = poolOracle.dataSource.getConnection();

            //Arreglos de ayuda, Para eliminar Extras
            ArrayList<Horasextras> listCodigos = new ArrayList();
            ArrayList<Horasextras> listCodigos3 = new ArrayList();
            ArrayList<Novedades> listCodigos2 = new ArrayList();

            listFestivos.clear();
            //Cargamos los Festivos del Sistema de Geminus
            ResultSet rs2 = poolOracle.query("select * from festivos where fecha between TO_DATE('" + fechaIni + "', 'DD/MM/YYYY')  and TO_DATE('" + fechaFin + "', 'DD/MM/YYYY') ");
            while (rs2.next()) {
                listFestivos.add(new Festivos(rs2.getString(1)));
            }

            //Asignamos la empresa correspondiente a cada trabajador.
            for (Horasextras listExtra : listExtras) {
                for (Horasextras listExtra1 : listTrabajadores) {
                    if (listExtra.getCodigo().trim().equalsIgnoreCase(listExtra1.getCodigo().trim())) {
                        listExtra.setEmpresa(listExtra1.getEmpresa());
                    }
                }
            }
            //Si no fue asignada ninguna empresa. la extra se elimina
            for (Horasextras listExtra : listExtras) {
                if (listExtra.getEmpresa() == null) {
                    listCodigos.add(listExtra);
                }
            }

            //Asignacion de letra a cada dia, esto por tema de los dominicales.
            for (Horasextras listExtra2 : listExtras) {
                if (listExtra2.getDiaSemana().trim().equalsIgnoreCase("Domingo")) {
                    listExtra2.setDiaSemana("D");
                } else {
                    listExtra2.setDiaSemana("N");
                }
            }

            //Asignacion de Festivos.
            for (Horasextras listExtra2 : listExtras) {
                for (Festivos diaFes : listFestivos) {
                    if (listExtra2.getFecha().equalsIgnoreCase(diaFes.getFecha())) {
                        listExtra2.setDiaSemana("F");
                    }
                }
            }

            //Recorrer las horas extras , para cuando se liquidaron dominicales para dias diferentes a domingos y festivos
            String v[] = null;
            for (Horasextras listExtra2 : listExtras) {
                if (listExtra2.getCodigoExtra().trim().equalsIgnoreCase("035") && listExtra2.getDiaSemana().equalsIgnoreCase("N")) {
                    listCodigos.add(listExtra2);
                }
            }

            //Si se encuentra un dominical para un dia diferente , se elimina
            for (Horasextras listCodigo : listCodigos) {
                listExtras.remove(listCodigo);
            }

            //Se cambia el valor del dominical a 1 por cada dia
            for (Horasextras listExtra2 : listExtras) {
                if (listExtra2.getCodigoExtra().trim().equalsIgnoreCase("035")) {
                    listExtra2.setValor(1);
                }
            }

            //Recuperamos las inconsistencias de la quincena
            listCodigos.clear();
            procesoRepInconsistencias proceso = new procesoRepInconsistencias();
            list_novedades = (ArrayList) proceso.cargarNovedades(fechaIni, fechaFin);
            System.out.println("************************ Novedades : " + list_novedades.size());

            for (Novedades list_novedade : list_novedades) {
                if (list_novedade.getCedula().equalsIgnoreCase("38641533")) {
                    System.out.println("-- La persona se encuentra en las novedades");
                }
            }

            //Recorremos inconsistencias , para compararlas con los trabajadores de Nodum
            boolean encontro = false;
            for (Novedades novedad : list_novedades) {
                encontro = false;
                for (Horasextras listExtra1 : listTrabajadores) {
                    if (novedad.getCodigo().trim().equalsIgnoreCase(listExtra1.getCodigo().trim())) {
                        encontro = true;
                        break;
                    }
                }
                if (encontro == false) {
                    listCodigos2.add(novedad);
                }
            }

            //Eliminamos las inconsistencias para los trabajadores que no fueron encontrados
            for (Novedades codigos : listCodigos2) {
                list_novedades.remove(codigos);
            }

            //Comparamos las horas extras generadas contra las inconsistencias para quitar horas extras dependiente si no fue resuelta la novedad
            String p[] = null;
            String fechaNov = "";
            for (Horasextras listExtra2 : listExtras) {
                fechaNov = "";
                for (Novedades novedad : list_novedades) {
                    if (listExtra2.getFecha().substring(0, 10).trim().equalsIgnoreCase(novedad.getFechaProgramada().substring(0, 10).trim())
                            && listExtra2.getCodigo().trim().equalsIgnoreCase(novedad.getCodigo().trim())) {
                        listCodigos.add(listExtra2);
                    }
                }
            }

            for (Horasextras codigos : listCodigos) {
                listCodigos3.add(codigos);
            }

            //Eliminamos horas extras por motivimos de inconsistencias
            for (Horasextras codigos : listCodigos3) {
                listExtras.remove(codigos);
            }

            //Recorremos las novedades para determinar dia no laborado
            boolean DianoLaborado = false;
            ArrayList<Horasextras> fechas = new ArrayList();
            for (Horasextras cod : listCodigos3) {
                DianoLaborado = false;

                for (Horasextras fecha : fechas) {

                    if (cod.getFecha().substring(0, 10).trim().equalsIgnoreCase(fecha.getFecha().substring(0, 10).trim())
                            && cod.getCodigo().trim().equalsIgnoreCase(fecha.getCodigo().trim())) {
                        DianoLaborado = true;
                    }
                }
                if (DianoLaborado == false) {
                    fechas.add(cod);
                }
            }

            //Agregamos dia no laborado
            int countNovedades = 0;
            boolean Incapacidad = false;

            for (Novedades novedad : list_novedades) {

                if (novedad.getTurno() != null) {
                    if (!novedad.getTurno().trim().equalsIgnoreCase("Z")) {
                        if (!novedad.getTurno().trim().equalsIgnoreCase("CT")) {
                            DianoLaborado = false;
                            Incapacidad = false;
                            for (Horasextras fecha : fechas) {

                                if (novedad.getFechaProgramada().substring(0, 10).trim().equalsIgnoreCase(fecha.getFecha().substring(0, 10).trim())
                                        && novedad.getCodigo().trim().equalsIgnoreCase(fecha.getCodigo().trim())) {
                                    DianoLaborado = true;
                                    break;
                                }
                            }
                            String fecha2 = "";
                            if (DianoLaborado == false) {
                                for (Horasextras trabajador : listIncapacidades) {
                                    String f1[] = novedad.getFechaProgramada().trim().substring(0, 10).split("-");
//
                                    String fechaInicial = f1[0] + "-" + f1[1] + "-" + f1[2];
                                    fecha2 = f1[2] + "/" + f1[1] + "/" + f1[0];

                                    if (trabajador.getCodigo().trim().equalsIgnoreCase(novedad.getCodigo().trim())
                                            && trabajador.getFecha().trim().equalsIgnoreCase(fechaInicial)) {
                                        Incapacidad = true;
                                    }
                                }

                                if (Incapacidad == false) {
                                    String f1[] = novedad.getFechaProgramada().trim().substring(0, 10).split("-");//
                                    fecha2 = f1[2] + "/" + f1[1] + "/" + f1[0];
//                                    if (novedad.getCedula().equalsIgnoreCase("5244437")) {
//                                        
//                                    }
                                    Horasextras extra = new Horasextras(novedad.getCedula(), "452", 1, novedad.getCodigo());
                                    extra.setFecha(fecha2);
                                    listExtras.add(extra);
                                }

                            }
                            countNovedades = 0;
                        }
                    }
                }
            }
            for (Horasextras fecha : fechas) {
                String f1[] = fecha.getFecha().trim().substring(0, 10).split("-");
                Horasextras extra = new Horasextras(fecha.getCedula(), "452", 1, fecha.getCodigo());

                extra.setFecha(f1[2] + "/" + f1[1] + "/" + f1[0]);
                if (fecha.getCedula().equalsIgnoreCase("5244437")) {
                    System.out.println("-- " + extra.getFecha());
                }
                listExtras.add(extra);
            }

            ArrayList<Horasextras> CompensadosNuevos = new ArrayList();
            // Personal que no se le liquido dia compesado en el sistema de geminus y si lo merecia 
            ResultSet rs = poolOracle.query("select B.em_cedula,A.dl_cod_empleado,count(A.dl_fecha)   from  re_descansos_laborados A , re_empleados B where \n"
                    + "                    A.dl_cod_empleado=B.em_codigo and\n"
                    + "                    A.dl_fecha between TO_DATE('" + fechaIni + "', 'DD/MM/YYYY') and TO_DATE('" + fechaFin + "', 'DD/MM/YYYY')\n"
                    + "                    and dl_cod_empleado not in (select li_codigo from RE_LIQUIDACIONES where li_fecha=A.dl_fecha  and \n"
                    + "                    li_dl>=1)\n"
                    + "group by  B.em_cedula,A.dl_cod_empleado		");
            while (rs.next()) {
                CompensadosNuevos.add(new Horasextras(rs.getString(1), "040", 1, rs.getString(2), rs.getInt(3)));
            }

            for (Horasextras trabajador2 : CompensadosNuevos) {
                for (Horasextras trabajador : listTrabajadores) {
                    if (trabajador2.getCodigo().trim().equalsIgnoreCase(trabajador.getCodigo().trim())) {
                        Horasextras horaExt = new Horasextras(trabajador2.getCedula().trim(), "040", 1, trabajador2.getCodigo().trim());
                        horaExt.setValor(trabajador2.getValor());
                        listExtras.add(horaExt);
                    }
                }

            }

//            CompensadosNuevos.clear();
            //Validamos y hacemos el ultimo ajuste a las horas extras.
            listExtrasFinal.clear();
            if (condicion == 1) {
                //Añadimos las horas extras
                double valor = 0;
                boolean yaEsta = false;

                for (Horasextras listExtra : listExtras) {
                    yaEsta = false;
                    valor = 0;
                    for (Horasextras listExtra2 : listExtrasFinal) {

                        if (listExtra.getCodigo().trim().equals(listExtra2.getCodigo().trim())
                                && listExtra.getCodigoExtra().trim().equalsIgnoreCase(listExtra2.getCodigoExtra().trim())
                                && !listExtra.getCodigoExtra().trim().equalsIgnoreCase("452")) {
                            yaEsta = true;

                        }
                    }
                    if (yaEsta == false) {
                        for (Horasextras listExtra3 : listExtras) {
                            if (listExtra.getCodigo().trim().equals(listExtra3.getCodigo().trim())
                                    && listExtra.getCodigoExtra().trim().equalsIgnoreCase(listExtra3.getCodigoExtra().trim())) {

                                valor = valor + listExtra3.getValor();
                                listExtra.setFechaIni("");
                                listExtra.setFechaFin("");
                            }
                        }
                        if (!listExtra.getCodigoExtra().trim().equalsIgnoreCase("452")) {
                            listExtra.setValor(valor);
                            listExtrasFinal.add(listExtra);
                        }

                    }
                }
            } else if (condicion == 2) {
                //solo Añadimos las horas extras con el concepto de dia no laborado (452)
                for (Horasextras listExtra : listExtras) {
                    if (listExtra.getCodigoExtra().trim().equalsIgnoreCase("452")) {
                        listExtrasFinal.add(listExtra);
                    }
                }
            }

            TrabajadoresConNovedades();
            CargarFechas(empresa.trim());

        } catch (SQLException ex) {
            System.out.println("Error Sql :  " + ex.toString());
        } finally {
            try {
                poolOracle.con.close();
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        }
    }

    /**
     * Metodo para cargar los trabajadores que tienen novedades de
     * inconsistencias
     *
     */
    public void TrabajadoresConNovedades() {
        boolean Trabajador = false;

        for (Horasextras novedad : listExtras) {
            Trabajador = false;
            if (novedad.getCodigoExtra().trim().equalsIgnoreCase("452")) {
                for (String trab : trabajadores) {
                    if (trab.trim().equalsIgnoreCase(novedad.getCodigo().trim())) {
                        Trabajador = true;
                    }
                }
                if (Trabajador == false) {
                    trabajadores.add(novedad.getCodigo().trim());
                }
            }
        }
    }

    /**
     * Metodo para cargar las fechas de los dias no laborados , colocando una
     * fecha inicial y una fechas final , de acuerdo a que trabajadores tienen
     * novedades de dias no laborados
     *
     */
    public void CargarFechas(String empresa) throws ParseException, SQLException {

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            poolSql_1.con = poolSql_1.dataSource.getConnection();
//            poolSql_1.getconecion().setAutoCommit(false);
            poolSql_1.transaccion("delete from GeminusDiasNoLaboradosXTrab");
            for (String trab : trabajadores) {
                for (Horasextras novedad : listExtras) {
                    if (novedad.getCodigo().trim().equalsIgnoreCase(trab) && novedad.getCodigoExtra().trim().equalsIgnoreCase("452")) {
                        poolSql_1.transaccion("insert into GeminusDiasNoLaboradosXTrab values(" + novedad.getCodigo() + ",convert(date,'" + novedad.getFecha() + "',103)" + ")");
                    }
                }
            }

            cstmt = poolSql_1.con.prepareCall("{call Geminus_DiasNoLaborados(?)}");
            cstmt.setString(1, empresa.trim());
            int r = cstmt.executeUpdate();
            System.out.println("Corrio Procedimiento r:" + r);
            if (r > 0) {
                ResultSet rs = poolSql_1.query("select cedula,cantidadDias,convert(varchar(10),fechaInicial,103),convert(varchar(10),fechafinal,103) from GeminusDiasNoLaborados\n"
                        + "order by cedula");
                while (rs.next()) {
                    System.out.println("Agrego dia no laborado");
//                    if (rs.getString(4).equalsIgnoreCase("4377495")) {
                    System.out.println("------ " + rs.getString(2) + " - " + rs.getString(3));
                    listExtrasFinal.add(new Horasextras(rs.getString(1), "452", rs.getInt(2), rs.getString(3), rs.getString(4)));
//                    }
                }

            }

            for (Horasextras horasextras : listExtrasFinal) {
//                if (horasextras.getCodigo().equalsIgnoreCase("452")) {
                if (horasextras.getCedula().equalsIgnoreCase("4377495")) {
                    System.out.println("-- " + horasextras.toString());
                }

//                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(procesoReporteLiq.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            poolSql_1.getconecion().commit();
//            poolSql_1.getconecion().setAutoCommit(true);
            poolSql_1.con.close();
            cstmt.close();
        }

    }

    public ArrayList<Horasextras> getListExtras() {
        return listExtras;
    }

    public void setListExtras(ArrayList<Horasextras> listExtras) {
        this.listExtras = listExtras;
    }

    public ArrayList<Horasextras> getListExtrasFinal() {
        return listExtrasFinal;
    }

    public void setListExtrasFinal(ArrayList<Horasextras> listExtrasFinal) {
        this.listExtrasFinal = listExtrasFinal;
    }

    public ArrayList<Horasextras> getListTrabajadores() {
        return listTrabajadores;
    }

    public void setListTrabajadores(ArrayList<Horasextras> listTrabajadores) {
        this.listTrabajadores = listTrabajadores;
    }

    public ArrayList<Horasextras> getListIncapacidades() {
        return listIncapacidades;
    }

    public void setListIncapacidades(ArrayList<Horasextras> listIncapacidades) {
        this.listIncapacidades = listIncapacidades;
    }

    public ArrayList<Festivos> getListFestivos() {
        return listFestivos;
    }

    public void setListFestivos(ArrayList<Festivos> listFestivos) {
        this.listFestivos = listFestivos;
    }

    public ArrayList<Novedades> getList_novedades() {
        return list_novedades;
    }

    public void setList_novedades(ArrayList<Novedades> list_novedades) {
        this.list_novedades = list_novedades;
    }

    public ConecionOracle getPoolOracle() {
        return poolOracle;
    }

    public void setPoolOracle(ConecionOracle poolOracle) {
        this.poolOracle = poolOracle;
    }

    public sqlServer10_1 getPoolSql_1() {
        return poolSql_1;
    }

    public void setPoolSql_1(sqlServer10_1 poolSql_1) {
        this.poolSql_1 = poolSql_1;
    }

}
