package Beans;

import Coneciones.ConecionOracle;
import Coneciones.sqlServer10_1;
import Control.procesoRepInconsistencias;
import Model.Compensado;
import Model.Jorndas;
import Model.ListTurnos;
import Model.LogErroresTurnos;
import Model.LogTurnos;
import Model.Novedades;
import java.io.BufferedReader;
import java.io.Serializable;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.primefaces.event.SelectEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.util.Calendar;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.primefaces.component.growl.Growl;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 * Clase que se utiliza para , cargar la informacion de los dias compensados y
 * liquidarlos
 */
@Named(value = "geminusCompen")
@SessionScoped
public class GeminusCompen implements Serializable {

    ArrayList<Compensado> Listcomp = new ArrayList();
    private ArrayList<LogTurnos> ListLog = new ArrayList();
    private LogTurnos objLog;
    private ArrayList<ListTurnos> ListTurnos = new ArrayList();
    private Date fecha1;
    private Date fecha2;
    private String f1;
    private String f2;
    private String f3;
    private String fechaQuery1;
    private String fechaQuery2;
    Growl growl = new Growl();
    ConecionOracle poolOracle = new ConecionOracle();
    sqlServer10_1 poolSql_1 = new sqlServer10_1();
    private UploadedFile uploadedFile;
    private ArrayList<LogErroresTurnos> listaErroresTurnos = new ArrayList();
    private String codigoEmpleado;
    private Jorndas a;

    /**
     * Metodo constructor
     *
     */
    @PostConstruct
    public void init() {
        getListLogTurnos();
        growl.setLife(5000);
        Listcomp.clear();
    }

    /**
     * Metodo para pasar a un informe excel , una lista de informacion.
     *
     * @param document : documento o tabla , que se encuentra cargada en la
     * vista web , se requiere para pasar esa informacion a excel
     */
    public void postProcessXLS(Object document) {
        if (Listcomp.size() > 0) {
            HSSFWorkbook wb = (HSSFWorkbook) document;
            HSSFSheet sheet = wb.getSheetAt(0);
            HSSFRow header = sheet.getRow(0);
            HSSFCellStyle cellStyle = wb.createCellStyle();
            cellStyle.setFillForegroundColor(HSSFColor.WHITE.index);
            cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            for (int i = 0; i < header.getPhysicalNumberOfCells(); i++) {
                HSSFCell cell = header.getCell(i);
                cell.setCellStyle(cellStyle);
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Debe Primero cargar los datos", ""));
        }
    }

    /**
     * Metodo para castear una fecha , del framework de primefaces.
     *
     */
    public void onDateSelect(SelectEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Date Selected", format.format(event.getObject())));
    }

    /**
     * Metodo , para castear las fechas del calendar de primefaces , y
     * identificar si el mes , consta de 31 dias o menos.
     *
     */
    public void RecuperarFechas() {
        SimpleDateFormat format2 = new SimpleDateFormat("dd/MMM/yyyy");
        this.f1 = format2.format(fecha1);
        this.f2 = format2.format(fecha2);

        int dia = DiadelMes(fecha2.getMonth());

        if ((dia != 0 && dia == 31)) {

            this.f3 = format2.format(fecha2);
            this.f3 = "" + dia + "/" + this.f3.substring(3, 7).trim() + this.f3.substring(7, 11).trim();
        } else {
            this.f3 = "No aplica";
        }
    }

    /**
     * Metodo para identificar el ultimo dia del mes
     *
     * @param mes : Mes en el que se desea evaluar
     */
    public int DiadelMes(int mes) {
        int dia = 0;
        mes++;
        if (mes != 2) {
            if (mes <= 7) {
                if (mes % 2 == 0) {
                    dia = 30;
                } else {
                    dia = 31;
                }
            } else if (mes % 2 == 0) {
                dia = 31;
            } else {
                dia = 30;
            }
        } else {
            System.out.println("No hay informacion necesaria");
        }
        return dia;
    }

    /**
     * Metodo para cargar la informacion de los dias compensados , en un periodo
     * de fechas seleccionado
     *
     */
    public void reporteCompensado() {
        RecuperarFechas();
        Listcomp.clear();
        int num = 1;
        try {
            poolOracle.con = poolOracle.dataSource.getConnection();
            ResultSet rs = poolOracle.query("select A.dl_cod_empleado,B.em_nombre,count(*) from re_descansos_laborados A ,  re_empleados B where \n"
                    + "A.dl_cod_empleado=B.em_codigo and \n"
                    + " A.DL_fecha between TO_DATE('" + f1 + "', 'DD/MM/YYYY')  and TO_DATE('" + f2 + "', 'DD/MM/YYYY') \n"
                    + "group by A.dl_cod_empleado,B.em_nombre");
            while (rs.next()) {
                Listcomp.add(new Compensado(num, rs.getString(1), rs.getString(2), rs.getInt(3)));
                num++;
            }
        } catch (SQLException ex) {

        } finally {
            try {
                poolOracle.con.close();
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        }
    }

    /**
     * Metodo para cargar la informacion de los compensados que se van a
     * liquidar , en las fechas selecionadas , este proceso se realiza con el
     * sistema de Geminus (Oracle 10.215)
     *
     */
    public void cargarCompensados() throws SQLException, ClassNotFoundException {
        RecuperarFechas();
        Listcomp.clear();
        int num = 1;
        String query = "";

        try {
            poolOracle.con = poolOracle.dataSource.getConnection();
            if (!this.f3.trim().equalsIgnoreCase("No aplica")) {
                System.out.println("fecha del mes : " + f3);
                //Si tiene programado un descanzo para el dia 31 se descuenta un dia de compensado
                query = "select  A.ma_codigo,B.em_nombre,case when count(distinct A.ma_fecha)=14 then "
                        + " (select 1+((count(*)*-1)) from RE_PROGRAMACION_JORNADAS where pj_empleado=A.ma_codigo and  "
                        + "pj_fecha=TO_DATE('" + f3 + "', 'DD/MM/YYYY') and pj_jornada='Z')"
                        + "when count(distinct A.ma_fecha)>=15 then \n"
                        + "(select 2+((count(*)*-1)) from RE_PROGRAMACION_JORNADAS where\n"
                        + "pj_empleado=A.ma_codigo and \n"
                        + "pj_fecha=TO_DATE('" + f3 + "', 'DD/MM/YYYY') and pj_jornada='Z') end cant\n"
                        + "from re_marcaciones A , re_empleados B,RE_PROGRAMACION_JORNADAS C  where A.ma_codigo=B.Em_codigo and A.ma_codigo=C.pj_empleado and \n"
                        + " A.ma_fecha=C.pj_fecha and A.ma_fecha between TO_DATE('" + f1 + "', 'DD/MM/YYYY') \n"
                        + "and TO_DATE('" + f2 + "', 'DD/MM/YYYY')  \n"
                        + "and A.ma_tipo='E' \n"
                        + "group by A.ma_codigo,B.em_nombre\n"
                        + "having count(distinct A.ma_fecha)>=14";
            } else {
                query = "select  A.ma_codigo,B.em_nombre,case when count(distinct A.ma_fecha)=14 then 1 when count(distinct A.ma_fecha)>=15 then 2 end \n"
                        + " from re_marcaciones A , re_empleados B ,RE_PROGRAMACION_JORNADAS C where A.ma_codigo=B.Em_codigo and A.ma_codigo=C.pj_empleado\n"
                        + " and A.ma_fecha=C.pj_fecha and \n"
                        + " A.ma_fecha between TO_DATE('" + f1 + "', 'DD/MM/YYYY') \n"
                        + " and TO_DATE('" + f2 + "', 'DD/MM/YYYY')  \n"
                        + " and A.ma_tipo='E' \n"
                        + " group by A.ma_codigo,B.em_nombre\n"
                        + " having count(distinct A.ma_fecha)>=14";
            }

            System.out.println(query);

            ResultSet rs = poolOracle.query(query);

            while (rs.next()) {
                Listcomp.add(new Compensado(num, rs.getString(1), rs.getString(2), rs.getInt(3)));
                num++;
            }

        } catch (SQLException ex) {
            System.out.println("error cargarCompensados " + ex);
        } finally {
            try {
                poolOracle.con.close();
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        }
        //Proceso que revisa si la persona tiene novedades en la quincena
        ArrayList<Novedades> list_novedades = new ArrayList();

        if (!this.f3.trim().equalsIgnoreCase("No aplica")) {
            list_novedades = CargaNovedades(f1, f3);
        } else {
            list_novedades = CargaNovedades(f1, f2);
        }

        for (Compensado objecto : Listcomp) {
            for (Novedades objecto2 : list_novedades) {
                if (objecto.getTrabajador().trim().equalsIgnoreCase(objecto2.getCodigo().trim())) {
                    objecto.setDias(objecto.getDias() - objecto2.getCantNovedades());
                }
            }
        }
        ArrayList<Compensado> numeroCompensados = new ArrayList();
        for (Compensado objecto : Listcomp) {
            if (objecto.getDias() <= 0) {
                numeroCompensados.add(objecto);
            }
        }

        for (Compensado numeroCompensado : numeroCompensados) {
            Listcomp.remove(numeroCompensado);
        }
    }

    /**
     * Metodo para cargar novedades ò inconsistencias presentadas en la quincena
     * , en un periodo de fechas
     *
     */
    public ArrayList<Novedades> CargaNovedades(String f1, String f2) throws SQLException, ClassNotFoundException {
        procesoRepInconsistencias proceso = new procesoRepInconsistencias();
        ArrayList<Novedades> list_novedades = new ArrayList();

        list_novedades = (ArrayList) proceso.cargarNovedades(f1, f2);
        list_novedades = proceso.SoloFechas(list_novedades);
        return list_novedades;
    }

    /**
     * Metodo que se encarga de liquidar en el sistema de Geminus , los dias
     * compensados calculados previamente
     *
     */
    public void liquidarCompensados() throws IOException, SQLException {
        System.out.println("iniciamos");
        SimpleDateFormat format2 = new SimpleDateFormat("dd/M/yyyy");
        boolean r = false;
        try {
            poolOracle.con = poolOracle.dataSource.getConnection();
            poolOracle.con.setAutoCommit(false);
            ArrayList<Compensado> listFechas = new ArrayList();
            for (Compensado LC : Listcomp) {
                poolOracle.transaccion("delete from re_descansos_laborados where DL_fecha between TO_DATE('" + f1 + "', 'DD/MM/YYYY')  and TO_DATE('" + f2 + "', 'DD/MM/YYYY')  ");
            }
            ConecionOracle.ejecuteUpdate("commit");
            int a = 1;
            boolean añadio = false;
            for (Compensado LC : Listcomp) {
                a = 1;
                añadio = false;
                ResultSet rs = poolOracle.query("select pj_fecha from RE_PROGRAMACION_JORNADAS where pj_empleado='" + LC.getTrabajador().trim() + "' \n"
                        + "and pj_fecha between TO_DATE('" + f1 + "', 'DD/MM/YYYY')  and TO_DATE('" + f2 + "', 'DD/MM/YYYY')  "
                        + "and rtrim(ltrim(to_char(pj_fecha, 'DAY','NLS_DATE_LANGUAGE=SPANISH')))='DOMINGO' and  rownum<=" + LC.getDias());
                while (rs.next()) {
                    listFechas.add(new Compensado(LC.getTrabajador(), a, rs.getDate(1)));
                    a++;
                    añadio = true;
                }

            }

            if (listFechas.size() > 0) {
                for (Compensado listFecha : listFechas) {
                    poolOracle.transaccion("insert into re_descansos_laborados values('" + listFecha.getTrabajador() + "',"
                            + "to_date('" + format2.format(listFecha.getFecha()) + "','DD/MM/YYYY'))");
                }
                ConecionOracle.ejecuteUpdate("commit");
                r = true;
            } else {
                r = false;
            }
        } catch (SQLException ex) {
            System.out.println("Error ex : " + ex.toString());
            r = false;
        } finally {
            try {
                poolOracle.con.setAutoCommit(true);
                poolOracle.con.close();
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        }

        if (r) {
            Listcomp.clear();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Liquidacion Exitosa"));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Info", "Error Al realizar Liquidacion"));
        }

    }

    //cargar archivo de turos
    public void handleFileUpload(FileUploadEvent event) {
        FacesMessage msg = new FacesMessage("Aviso", event.getFile().getFileName() + " esta seleccionado, por favor presione cargar..!");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        uploadedFile = event.getFile();
    }

    public String cargarArchivo() throws IOException, SQLException {
        listaErroresTurnos.clear();
        if (uploadedFile != null) {
            ArrayList<String> lista = new ArrayList();
            InputStream input = uploadedFile.getInputstream();
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(input));
                String line = br.readLine();
                while (null != line) {
                    lista.add(line);
                    line = br.readLine();
                }
            } catch (IOException e) {
                System.out.println("error " + e);
            } finally {
                if (null != br) {
                    br.close();
                }
            }

            if (!validarSeparador(lista.get(0))) {
                listaErroresTurnos.add(new LogErroresTurnos("General", "El separador de campos debe estar definido por comas (,). Por favor corrigelo y vuelve a intentar...! :(", "Validación separador de campos"));
            }

            System.out.println("paso por aqui");

            int id_log = getNextIdLog();
            if (!insertLogTurnos(id_log, fecha1, uploadedFile.getFileName())) {
                listaErroresTurnos.add(new LogErroresTurnos("General", "Ocurrio un error al crear el log.. :(", "Crear Log de Registro"));
            }
            getListLogTurnos();

            for (int i = 0; i < lista.size(); i++) {
                String datos[] = lista.get(i).split(",");
//                System.out.println("lista.get(i) " + lista.get(i));
                int codEmpleado = getCotEmpleado(datos[0].trim());
                String dependencia = getIdDependencia(codEmpleado);
//                String nom_rependencia = getNomDependencia(dependencia);                
                if (codEmpleado < 0) {
                    listaErroresTurnos.add(new LogErroresTurnos(Integer.toString(i + 1), "El documento no existe o esta inactivo en la base de datos:\nTambien es posible que el documento contenga espacios vacios en el archivo cargado", datos[0]));

                }
                if (dependencia.equals("")) {
                    listaErroresTurnos.add(new LogErroresTurnos(Integer.toString(i + 1), "Este registro no tiene dependencia", datos[0]));

                }
            }

            for (int i = 0; i < lista.size(); i++) {
                String datos[] = lista.get(i).split(",");
                for (int j = 1; j < datos.length; j++) {
                    if (!datos[j].equals("")) {
                        if (!existJornada(datos[j])) {
                            listaErroresTurnos.add(new LogErroresTurnos(Integer.toString(i + 1), "Parace que la jornada '" + datos[j] + "' No existe", lista.get(i)));
                        }
                    }
                }

            }
            for (int i = 0; i < listaErroresTurnos.size(); i++) {
                System.out.println(listaErroresTurnos.get(i).toString());
            }

            if (listaErroresTurnos.size() > 0) {
                eliminarLog(id_log);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Aviso", "Soluciona los errores en el archivo"));
                return "CargarTurnos";
            } else {
                int result = 0;
                for (int i = 0; i < lista.size(); i++) {
                    String datos[] = lista.get(i).split(",");
                    int codEmpleado = getCotEmpleado(datos[0]);
                    String dependencia = getIdDependencia(codEmpleado);
                    for (int j = 1; j < datos.length; j++) {
                        result = insertGeminusTurnos(codEmpleado, dependencia.trim(), fecha1, datos[j], j - 1, id_log);
                    }
                }
                if (result > 0) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Los Turnos se cargaron correctamente ...! :)"));

                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Aviso", "Ocurrio un error..!"));
                }
                uploadedFile = null;
                return "CargarTurnos";
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Aviso", "Debes seleccionar primero un archivo..!"));
            return "CargarTurnos";
        }
    }

    public String cargarJornada() {
        SimpleDateFormat format2 = new SimpleDateFormat("dd/M/yyyy");
        try {
            poolOracle.con = poolOracle.dataSource.getConnection();
            poolOracle.con.setAutoCommit(false);

            String query = "select \n"
                    + "  JT_CODIGO,\n"
                    + "    JT_Descripcion,\n"
                    + "    JT_Horasjornadasemana,\n"
                    + "    jt_minutosjornadasemana,\n"
                    + "    jt_horasalmuerzosemana,\n"
                    + "    jt_horasjornadasabado,\n"
                    + "    jt_minutosjornadasabado,\n"
                    + "    jt_horasalmuerzosabado,\n"
                    + "    jt_minutosalmuerzosabado,\n"
                    + "    jt_horainicialnovedades,\n"
                    + "    jt_horafinalnovedades,\n"
                    + "    jt_minutoinicialnovedades,\n"
                    + "    jt_minutofinalnovedades,\n"
                    + "    jt_horasalmuerzofestivos,\n"
                    + "    jt_horasparadescontardoblecom,\n"
                    + "    jt_horas_jornada_domingo,\n"
                    + "    jt_minutos_jornada_domingo,\n"
                    + "    jt_horas_almuerzo_domingo,\n"
                    + "    jt_minutos_almuerzo_domingo,\n"
                    + "    jt_horas_jornada_festivo,\n"
                    + "    jt_minutos_jornada_festivo,\n"
                    + "    jt_horas_almuerzo_festivo,\n"
                    + "    jt_minutos_almuerzo_festivo,\n"
                    + "    jt_jornada_flexible,\n"
                    + "    jt_max_horas_pagar_dominicales,\n"
                    + "    jt_pagan_dominicales,\n"
                    + "    jt_recargo_nocturno,\n"
                    + "    jt_recargo_nocturno_festivos,\n"
                    + "    jt_extras_festivas,\n"
                    + "    jt_extras_diurnas_nocturnas,\n"
                    + "    jt_dia_inicial_semana,\n"
                    + "    re_reiniciar_turno_festivo,\n"
                    + "    jt_liquidacion_mensual,\n"
                    + "    jt_minutos_previa_restrictiva,\n"
                    + "    jt_minutos_poster_restrictiva,\n"
                    + "    jt_extrasantesturno,\n"
                    + "    jt_liquidacion_quincenal \n"
                    + "  from RE_JORNADASTRABAJO WHERE JT_CODIGO = '" + codigoEmpleado + "'";

            System.out.println("***********" + query);
            ResultSet rs = poolOracle.query(query);
            if (rs.next()) {
                getA();
                a.setJT_CODIGO(rs.getString(1));
                a.setJT_Descripcion(rs.getString(2));
                a.setJT_Horasjornadasemana(rs.getString(3));
                a.setJt_minutosjornadasemana(rs.getString(4));
                a.setJt_horasalmuerzosemana(rs.getString(5));
                a.setJt_horasjornadasabado(rs.getString(6));
                a.setJt_minutosjornadasabado(rs.getString(7));
                a.setJt_horasalmuerzosabado(rs.getString(8));
                a.setJt_minutosalmuerzosabado(rs.getString(9));
                a.setJt_horainicialnovedades(rs.getString(10));
                a.setJt_horafinalnovedades(rs.getString(11));
                a.setJt_minutoinicialnovedades(rs.getString(12));
                a.setJt_minutofinalnovedades(rs.getString(13));
                a.setJt_horasalmuerzofestivos(rs.getString(14));
                a.setJt_horasparadescontardoblecom(rs.getString(14));
                a.setJt_horas_jornada_domingo(rs.getString(15));
                a.setJt_minutos_jornada_domingo(rs.getString(16));
                a.setJt_horas_almuerzo_domingo(rs.getString(16));
                a.setJt_minutos_almuerzo_domingo(rs.getString(18));
                a.setJt_horas_jornada_festivo(rs.getString(19));
                a.setJt_minutos_jornada_festivo(rs.getString(20));
                a.setJt_horas_almuerzo_festivo(rs.getString(21));
                a.setJt_minutos_almuerzo_festivo(rs.getString(22));
                a.setJt_jornada_flexible(rs.getString(23));
                a.setJt_max_horas_pagar_dominicales(rs.getString(24));
                a.setJt_pagan_dominicales(rs.getString(25));
                a.setJt_recargo_nocturno(rs.getString(26));
                a.setJt_recargo_nocturno_festivos(rs.getString(1));
                a.setJt_extras_festivas(rs.getString(1));
                a.setJt_extras_diurnas_nocturnas(rs.getString(27));
                a.setJt_dia_inicial_semana(rs.getString(28));
                a.setRe_reiniciar_turno_festivo(rs.getString(29));
                a.setJt_liquidacion_mensual(rs.getString(30));
                a.setJt_minutos_previa_restrictiva(rs.getString(31));
                a.setJt_minutos_poster_restrictiva(rs.getString(32));
                a.setJt_extrasantesturno(rs.getString(33));
                a.setJt_liquidacion_quincenal(rs.getString(34));
            }
//            String datos[]  = a.getJt_horainicialnovedades().split(":");
//            datos[0]; //obtiene la primera parte de la lariable
            

//            for (Compensado LC : Listcomp) {
//                poolOracle.transaccion("delete from re_descansos_laborados where DL_fecha between TO_DATE('" + f1 + "', 'DD/MM/YYYY')  and TO_DATE('" + f2 + "', 'DD/MM/YYYY')  ");
//            }
//            ConecionOracle.ejecuteUpdate("commit");
//            int a = 1;
//            boolean añadio = false;
//            for (Compensado LC : Listcomp) {
//                a = 1;
//                añadio = false;
//                ResultSet rs = poolOracle.query("select pj_fecha from RE_PROGRAMACION_JORNADAS where pj_empleado='" + LC.getTrabajador().trim() + "' \n"
//                        + "and pj_fecha between TO_DATE('" + f1 + "', 'DD/MM/YYYY')  and TO_DATE('" + f2 + "', 'DD/MM/YYYY')  "
//                        + "and rtrim(ltrim(to_char(pj_fecha, 'DAY','NLS_DATE_LANGUAGE=SPANISH')))='DOMINGO' and  rownum<=" + LC.getDias());
//                while (rs.next()) {
//                    listFechas.add(new Compensado(LC.getTrabajador(), a, rs.getDate(1)));
//                    a++;
//                    añadio = true;
//                }
//
//            }
//
//            if (listFechas.size() > 0) {
//                for (Compensado listFecha : listFechas) {
//                    poolOracle.transaccion("insert into re_descansos_laborados values('" + listFecha.getTrabajador() + "',"
//                            + "to_date('" + format2.format(listFecha.getFecha()) + "','DD/MM/YYYY'))");
//                }
//                ConecionOracle.ejecuteUpdate("commit");
//                r = true;
//            } else {
//                r = false;
//            }
        } catch (SQLException ex) {
            System.out.println("Error ex : " + ex.toString());
        } finally {
            try {
                poolOracle.con.setAutoCommit(true);
                poolOracle.con.close();
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        }
        return "ModificarJornadas";
    }

    
    
    
    public void guardar(){
    
    
    }
    
    
    public void moreInfo(LogTurnos l) throws IOException, SQLException {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("idLog", l.getIdLog());
        FacesContext.getCurrentInstance().getExternalContext().redirect("/Geminus/faces/Disciplinario/ListurnosXCargue.xhtml");
    }

    public void regresar() throws IOException, SQLException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("/Geminus/faces/Disciplinario/CargarTurnos.xhtml");
    }

    private int getCotEmpleado(String codEmpleado) {
        int codigo = -1;
        try {
            poolSql_1.con = poolSql_1.dataSource.getConnection();
            poolSql_1.con.setAutoCommit(false);
            ResultSet res = poolSql_1.query("SELECT cod_trabajador FROM ct_RHTrabajador where cod_persona = " + codEmpleado.replaceAll("\\s", "") + " and estado_trab = 'ACTIVO'");
            if (res.next()) {
                codigo = res.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error " + e);
        } finally {
            try {
                poolSql_1.con.setAutoCommit(true);
                poolSql_1.con.close();
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        }
        return codigo;
    }

    private String getIdDependencia(int CodEmpleado) {
        String dependencia = "";
        try {
            String query = "SELECT EM_ID_DEPENDENCIA FROM RE_EMPLEADOS "
                    + "where EM_CODIGO =" + CodEmpleado;
            poolOracle.con = poolOracle.dataSource.getConnection();
            poolOracle.con.setAutoCommit(false);
            ResultSet rs = poolOracle.query(query);
            if (rs.next()) {
                dependencia = rs.getString(1);
            }
        } catch (SQLException e) {
            System.out.println("Error " + e);
        } finally {
            try {
                poolOracle.con.setAutoCommit(true);
                poolOracle.con.close();
            } catch (SQLException ex) {
                System.out.println("error linea 510 " + ex);
            }
        }
        return dependencia;
    }

    private void getListLogTurnos() {
        ListLog.clear();
        try {
            String queryN = "select  * from logCargaTurnos "
                    + "WHERE ROWNUM <= 5 "
                    + "order by FECHA_LOG desc";
            poolOracle.con = poolOracle.dataSource.getConnection();
            poolOracle.con.setAutoCommit(false);
            ResultSet rs2 = poolOracle.query(queryN);
            while (rs2.next()) {
                ListLog.add(new LogTurnos(rs2.getInt(1), rs2.getString(2), rs2.getDate(3)));
            }
        } catch (SQLException e) {
            System.out.println("Error " + e);
        } finally {
            try {
                poolOracle.con.setAutoCommit(true);
                poolOracle.con.close();
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        }
    }

    private boolean existJornada(String Jornada) {
        boolean find = false;
        try {
            String query = "Select JT_CODIGO FROM RE_JORNADASTRABAJO WHERE rtrim(ltrim(JT_CODIGO)) = rtrim(ltrim('" + Jornada + "'))";
            poolOracle.con = poolOracle.dataSource.getConnection();
            poolOracle.con.setAutoCommit(false);
            ResultSet rs = poolOracle.query(query);
            if (rs.next()) {
                find = true;
            }
        } catch (SQLException e) {
            System.out.println("Error " + e);
        } finally {
            try {
                poolOracle.con.setAutoCommit(true);
                poolOracle.con.close();
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        }
        return find;
    }

    private int getNextIdLog() {
        int idlog = 0;
        try {
            String query = "SELECT id_log FROM RE_PROGRAMACION_JORNADAS where id_log = (select max(id_log) from RE_PROGRAMACION_JORNADAS)";
            poolOracle.con = poolOracle.dataSource.getConnection();
            poolOracle.con.setAutoCommit(false);
            ResultSet rs = poolOracle.query(query);
            if (rs.next()) {
                idlog = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error " + e);
        } finally {
            try {
                poolOracle.con.setAutoCommit(true);
                poolOracle.con.close();
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        }
        return idlog = idlog + 1;
    }

    private boolean insertLogTurnos(int id_log, Date fechaLog, String ruta) throws SQLException {
        boolean cod = false;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy");
        System.out.println("sdf.format(fechaLog) " + sdf.format(fechaLog));
        try {
            String query = "insert into logCargaTurnos "
                    + "values(" + id_log + ",'" + ruta + "','" + sdf.format(fechaLog) + "')";
            System.out.println(query);

            poolOracle.con = poolOracle.dataSource.getConnection();
            poolOracle.con.setAutoCommit(false);
            poolOracle.transaccion(query);
            cod = true;
        } catch (SQLException e) {
            System.out.println("error " + e);
            poolOracle.con.rollback();
        } finally {
            try {
                ConecionOracle.ejecuteUpdate("commit");
                poolOracle.con.setAutoCommit(true);
                poolOracle.con.close();
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        }
        return cod;
    }

    private void eliminarLog(int id_log) throws SQLException {
        try {
            String query = "delete from logCargaTurnos where id_log =" + id_log;
            poolOracle.con = poolOracle.dataSource.getConnection();
            poolOracle.con.setAutoCommit(false);
            poolOracle.transaccion(query);
            System.out.println("Eliminado");
        } catch (SQLException e) {
            System.out.println("error " + e);
            poolOracle.con.rollback();
        } finally {
            try {
                ConecionOracle.ejecuteUpdate("commit");
                poolOracle.con.setAutoCommit(true);
                poolOracle.con.close();
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        }
    }

    private int insertGeminusTurnos(int codEmpleado, String dependencia, Date date, String dia, int sumdia, int id_log) throws SQLException {
        int registros = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date); // Configuramos la fecha que se recibe	
        calendar.add(Calendar.DAY_OF_MONTH, sumdia);
//        System.out.println(codEmpleado + " " + dependencia + " " + sdf.format(calendar.getTime()) + " " + dia);
        if (!dia.equals("")) {
            try {
                String query = "insert into RE_PROGRAMACION_JORNADAS "
                        + "values('" + codEmpleado + "','" + dependencia + "','" + dia + "','" + sdf.format(calendar.getTime()) + "', 'N','','', " + id_log + ")";
                poolOracle.con = poolOracle.dataSource.getConnection();
                poolOracle.con.setAutoCommit(false);
                poolOracle.transaccion(query);
                registros += 1;
            } catch (SQLException e) {
                System.out.println("error " + e);
                poolOracle.con.rollback();
            } finally {
                try {
                    ConecionOracle.ejecuteUpdate("commit");
                    poolOracle.con.setAutoCommit(true);
                    poolOracle.con.close();
                } catch (SQLException ex) {
                    System.out.println(ex);
                }
            }
        }
        sdf = null;
        return registros;

    }

    public void limpiarLista() {
        Listcomp.clear();
    }

    public ArrayList<Compensado> getListcomp() {
        return Listcomp;
    }

    public void setListcomp(ArrayList<Compensado> Listcomp) {
        this.Listcomp = Listcomp;
    }

    public Date getFecha1() {
        return fecha1;
    }

    public void setFecha1(Date fecha1) {
        this.fecha1 = fecha1;
    }

    public Date getFecha2() {
        return fecha2;
    }

    public void setFecha2(Date fecha2) {
        this.fecha2 = fecha2;
    }

    public String getF1() {
        return f1;
    }

    public void setF1(String f1) {
        this.f1 = f1;
    }

    public String getF2() {
        return f2;
    }

    public void setF2(String f2) {
        this.f2 = f2;
    }

    public String getFechaQuery1() {
        return fechaQuery1;
    }

    public void setFechaQuery1(String fechaQuery1) {
        this.fechaQuery1 = fechaQuery1;
    }

    public String getFechaQuery2() {
        return fechaQuery2;
    }

    public void setFechaQuery2(String fechaQuery2) {
        this.fechaQuery2 = fechaQuery2;
    }

    public Growl getGrowl() {
        return growl;
    }

    public void setGrowl(Growl growl) {
        this.growl = growl;
    }

    public String getF3() {
        return f3;
    }

    public void setF3(String f3) {
        this.f3 = f3;
    }

    public ConecionOracle getPoolOracle() {
        return poolOracle;
    }

    public void setPoolOracle(ConecionOracle poolOracle) {
        this.poolOracle = poolOracle;
    }

    private boolean validarSeparador(String lista) {
        return lista.contains(",");
    }

    public ArrayList<LogTurnos> getListLog() {
        return ListLog;
    }

    public void setListLog(ArrayList<LogTurnos> ListLog) {
        this.ListLog = ListLog;
    }

    public ArrayList<ListTurnos> getListTurnos() {
        int idLog = (int) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("idLog");
        ListTurnos.clear();
        try {
            String queryN = "select pj_empleado, pj_dependencia, pj_jornada, pj_fecha from RE_PROGRAMACION_JORNADAS  where id_log = " + idLog;
            poolOracle.con = poolOracle.dataSource.getConnection();
            poolOracle.con.setAutoCommit(false);
            ResultSet rs2 = poolOracle.query(queryN);
            while (rs2.next()) {
                ListTurnos.add(new ListTurnos(rs2.getInt(1), rs2.getString(2), rs2.getString(3), rs2.getDate(4)));
            }
        } catch (SQLException e) {
            System.out.println("Error " + e);
        } finally {
            try {
                poolOracle.con.setAutoCommit(true);
                poolOracle.con.close();
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        }
        return ListTurnos;
    }

    public void setListTurnos(ArrayList<ListTurnos> ListTurnos) {
        this.ListTurnos = ListTurnos;
    }

    public LogTurnos getObjLog() {
        return objLog;
    }

    public void setObjLog(LogTurnos objLog) {
        this.objLog = objLog;
    }

    public ArrayList<LogErroresTurnos> getListaErroresTurnos() {
        return listaErroresTurnos;
    }

    public void setListaErroresTurnos(ArrayList<LogErroresTurnos> listaErroresTurnos) {
        this.listaErroresTurnos = listaErroresTurnos;
    }

    public String getCodigoEmpleado() {
        return codigoEmpleado;
    }

    public void setCodigoEmpleado(String codigoEmpleado) {
        this.codigoEmpleado = codigoEmpleado;
    }

    public Jorndas getA() {
        if (a == null) {
            a = new Jorndas();
        }
        return a;
    }

    public void setA(Jorndas a) {
        this.a = a;
    }

}
