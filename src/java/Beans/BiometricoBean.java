package Beans;

import Coneciones.ConecionOracle;
import Coneciones.sqlServer10_1;
import Coneciones.sqlServer10_19;
import Model.biometrico;
import Model.usuario;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import javax.inject.Named;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.primefaces.component.growl.Growl;
import org.primefaces.event.SelectEvent;
import net.sf.jasperreports.engine.JasperRunManager;

/**
 * Beans Para tratar toda la informacion del sistema de biometrico (SQL server
 * 10.19) , que es donde las Marcaciones caen. con un sistema que se llama Anviz
 *
 * @author: Juan David Castrillon
 * @version: 11/12/2017
 */
@Named(value = "biometrico")
@ViewScoped
public class BiometricoBean implements Serializable {

    sqlServer10_1 poolSql_1 = new sqlServer10_1();
    sqlServer10_19 poolSql_19 = new sqlServer10_19();
    ConecionOracle poolOracle = new ConecionOracle();

    ArrayList<biometrico> listBiometrico = new ArrayList();
    ArrayList<biometrico> listAreas = new ArrayList();
    private Date FechaInicial;
    private Date Fechafinal;
    private String f1;
    private String f2;
    private String empresa;
    private int cantidad;
    private String area;
    private String cedula = "";
    private String NameCodigo;
    private String name;
    private String sucursal;
    Growl growl = new Growl();

    /**
     * Metodo constructor , se inicializan primero las areas , del sistema
     *
     */
    @PostConstruct
    public void init() {
        cargarAreas();
        growl.setLife(5000);
        this.NameCodigo = "Exportar Informacion";
    }

    /**
     * Metodo para castear una fecha , del framework de primefaces.
     *
     */
    public void onDateSelect(SelectEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Date Selected", format.format(event.getObject())));
    }

    /**
     * Metodo para pasar a un informe excel , una lista de informacion.
     *
     * @param document : documento o tabla , que se encuentra cargada en la
     * vista web , se requiere para pasar esa informacion a excel
     */
    public void postProcessXLS(Object document) {
        if (listBiometrico.size() > 0) {
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
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Debe Primero cargar datos", ""));
        }
    }

    /**
     * Metodo , para castear las fechas del calendar de primefaces
     *
     * @param condicion : Se revice la condicion para saber de que manera ,
     * castear la fecha , ya que hay dos bases de datos (Oracle y Sql server)
     * las cuales reciven la fecha de manera diferente
     */
    public void RecuperarFechas(int condicion) {
        SimpleDateFormat format = null;
        if (condicion == 1) {
            format = new SimpleDateFormat("yyyy-MM-dd");
        } else if (condicion == 2) {
            format = new SimpleDateFormat("dd/MM/yyyy");
        }
        this.f1 = format.format(FechaInicial);
        this.f2 = format.format(Fechafinal);
    }

    /**
     * Metodo para cargar las areas , que se encuentren registradas en el
     * sistema de (Sql server 10.19) Biometrico
     *
     */
    public void cargarAreas() {
        try {
            poolSql_19.con = poolSql_19.dataSource.getConnection();
            ResultSet rs = poolSql_19.query("SELECT Deptid,DeptName from Dept where Deptid not in (1,3)");
            while (rs.next()) {
                listAreas.add(new biometrico(rs.getString(1), rs.getString(2)));
            }
        } catch (SQLException ex) {
            System.out.println("error ex :" + ex.toString());
        } finally {
            try {
                poolSql_19.con.close();
            } catch (SQLException ex) {
                System.out.println("Error 10.19 Close : " + ex.toString());
            }
        }
    }

    /**
     * Metodo para cargar las areas , que se encuentren registradas en el
     * sistema de (Sql server 10.19) Biometrico
     *
     */
    public void consultaBiometricoPersonal() {
        RecuperarFechas(1);
        listBiometrico.clear();
        this.name = "";
        this.NameCodigo = "";
        this.area = "";
        try {
            poolSql_1.con = poolSql_1.dataSource.getConnection();
            String query = "";
            query = "select fecha,marcacion,tipo from BiometricoReportePersonalMarcaciones('" + cedula + "','" + f1 + "','" + f2 + "') order by 1,2";
            ResultSet rs = poolSql_1.query(query);
            while (rs.next()) {
                listBiometrico.add(new biometrico(rs.getString(1), rs.getString(2), rs.getString(3)));
            }
            cantidad = listBiometrico.size();
            ResultSet rs2 = poolSql_1.query("select A.nombre_completo,convert(varchar(10),A.cod_trabajador)+' - '+A.nombre_completo,B.desc_seccion  from v_RHTrabajador A , ct_RHSecciones B \n"
                    + "where A.cod_emp=B.cod_emp and A.cod_seccion=B.cod_seccion and A.estado_trab='ACTIVO' and \n"
                    + "A.cod_persona=" + cedula);
            if (rs2.next()) {
                this.name = rs2.getString(1);
                this.NameCodigo = rs2.getString(2);
                this.area = rs2.getString(3);
            }
            if (listBiometrico.size() <= 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "El trabajador no exisite", ""));
                this.cedula = "";
            }
        } catch (SQLException ex) {
            System.out.println("error ex :" + ex.toString());
        } finally {
            try {
                poolSql_1.con.close();
            } catch (SQLException ex) {
                Logger.getLogger(BiometricoBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Metodo para cargar la informacion de las personas que han realizado el
     * proceso de marcacion en la empresa , por area o todas las areas
     */
    public void consultaBiometrico() {
        RecuperarFechas(1);
        listBiometrico.clear();
        try {
            poolSql_1.con = poolSql_1.dataSource.getConnection();
            String query = "";
            if (area.equalsIgnoreCase("54")) {
                this.area = "54,76";
            }
            if (area.equalsIgnoreCase("TODAS")) {
                this.area = "";
                for (biometrico object : listAreas) {
                    this.area += object.getCodigo().trim() + ",";
                }
                this.area = this.area.substring(0, this.area.length() - 1);
            }

            if (empresa.trim().equalsIgnoreCase("TODAS")) {
//                System.out.println("todas");
                query = "SELECT distinct T2.cod_persona,T1.userid,CONVERT(varchar(10),CONVERT(date,T1.entrada)) AS FechaIni, \n"
                        + "CONVERT(varchar(10),CONVERT(date,T1.salida)) AS FechaFin,CONVERT(time(0),T1.entrada) AS HoraIni,\n"
                        + "CONVERT(time(0),T1.salida) AS HoraFin,T1.name,T1.Desparea\n"
                        + "FROM dbo.calcula_llegadaBiometrico('" + this.f1 + " 00:00:00.000','" + this.f2 + " 23:59:59.000',0)\n"
                        + "T1 INNER JOIN [ct_RHTrabajador] T2 ON T1.userid = T2.cod_trabajador"
                        + " and T1.area in (" + area + ") order by 8,7";
            } else {
//                System.out.println("No todas");
                query = "SELECT distinct T2.cod_persona,T1.userid,CONVERT(varchar(10),CONVERT(date,T1.entrada)) AS FechaIni, \n"
                        + "CONVERT(varchar(10),CONVERT(date,T1.salida)) AS FechaFin,CONVERT(time(0),T1.entrada) AS HoraIni,\n"
                        + "CONVERT(time(0),T1.salida) AS HoraFin,T1.name,T1.Desparea\n"
                        + "FROM dbo.calcula_llegadaBiometrico('" + this.f1 + " 00:00:00.000','" + this.f2 + " 23:59:59.000',0)\n"
                        + "T1 INNER JOIN [ct_RHTrabajador] T2 ON T1.userid = T2.cod_trabajador"
                        + " and T2.cod_emp='" + this.empresa + "' "
                        + " and T1.area in (" + area + ") order by 8,7";

            }

            ResultSet rs = poolSql_1.query(query);
            while (rs.next()) {
                listBiometrico.add(new biometrico(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                        rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8)));
            }
            cantidad = listBiometrico.size();
        } catch (SQLException ex) {
            System.out.println("error ex :" + ex.toString());
        } finally {
            try {
                poolSql_1.con.close();
            } catch (SQLException ex) {
                Logger.getLogger(BiometricoBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void consultaBiometricoXArea() {
        RecuperarFechas(1);
        listBiometrico.clear();
        try {
            poolSql_1.con = poolSql_1.dataSource.getConnection();
            usuario u = (usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario");
            String query = "";
            String and = " T2.cod_persona IN (select cod_persona from ct_RHTrabajador where cod_dpto in (" + u.getCcost().trim() + ") "
                        + "and estado_trab = 'ACTIVO' and cod_persona not in(select cedula from [192.168.10.19].biometrico.dbo.userExcluidos where usuario='" + u.getUsuario() + "'))";
            if (u.getUsuario().equals("ssuarez")) {
                this.area = "";
                for (biometrico object : listAreas) {
                    this.area += object.getCodigo().trim() + ",";
                }
                this.area = this.area.substring(0, this.area.length() - 1);
                and = " T2.cod_emp='logisticaEP' and T1.area in (" + area + ") ";
            }
            if (cedula.equals("")) {
                query = "SELECT distinct T2.cod_persona,T1.userid,CONVERT(varchar(10),CONVERT(date,T1.entrada)) AS FechaIni, \n"
                        + "CONVERT(varchar(10),CONVERT(date,T1.salida)) AS FechaFin,CONVERT(time(0),T1.entrada) AS HoraIni,\n"
                        + "CONVERT(time(0),T1.salida) AS HoraFin,T1.name,T1.Desparea\n"
                        + "FROM dbo.calcula_llegadaBiometrico('" + this.f1 + " 00:00:00.000','" + this.f2 + " 23:59:59.000',0)\n"
                        + "T1 INNER JOIN [ct_RHTrabajador] T2 ON T1.userid = T2.cod_trabajador\n"
                        + "where "
                        + " " + and + " order by 8,7  ";
            } else {
                query = "SELECT distinct T2.cod_persona,T1.userid,CONVERT(varchar(10),CONVERT(date,T1.entrada)) AS FechaIni, \n"
                        + "CONVERT(varchar(10),CONVERT(date,T1.salida)) AS FechaFin,CONVERT(time(0),T1.entrada) AS HoraIni,\n"
                        + "CONVERT(time(0),T1.salida) AS HoraFin,T1.name,T1.Desparea\n"
                        + "FROM dbo.calcula_llegadaBiometrico('" + this.f1 + " 00:00:00.000','" + this.f2 + " 23:59:59.000',0)\n"
                        + "T1 INNER JOIN [ct_RHTrabajador] T2 ON T1.userid = T2.cod_trabajador\n"
                        + "where T2.cod_persona IN ('" + cedula + "') order by 8,7  ";
            }
            System.out.println("query " + query);
            ResultSet rs = poolSql_1.query(query);
            while (rs.next()) {
                listBiometrico.add(new biometrico(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                        rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8)));
            }
            cantidad = listBiometrico.size();
        } catch (SQLException ex) {
            System.out.println("error ex :" + ex.toString());
        } finally {
            try {
                poolSql_1.con.close();
            } catch (SQLException ex) {
                Logger.getLogger(BiometricoBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void excluir(String cc) {
        try {
            usuario u = (usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario");
            poolSql_19.con = poolSql_19.dataSource.getConnection();
            String c = "Select * from userExcluidos where usuario ='" + u.getUsuario() + "' and cedula = '" + cc + "'";
            ResultSet rs = poolSql_19.query(c);
            if (!rs.next()) {
                String query = "insert into userExcluidos values ('" + u.getUsuario() + "','" + cc + "')";
                PreparedStatement pstm = poolSql_19.con.prepareStatement(query);
                pstm.executeUpdate();
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Empleado excluido, por favor genere la consulta nuevamente..!", ""));
        } catch (SQLException e) {
            System.out.println("error " + e);
        } finally {
            try {
                poolSql_19.con.close();
            } catch (SQLException ex) {
                Logger.getLogger(BiometricoBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public void cambiarClave() {
        try {
            if (!cedula.equals("")) {
                usuario u = (usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario");
                System.out.println("cedula " + cedula + " " + u.getUsuario());
                poolSql_19.con = poolSql_19.dataSource.getConnection();
                String query = "update UserBiometrico set clave  = '" + cedula + "' where usuario = '" + u.getUsuario() + "'";
                PreparedStatement pstm = poolSql_19.con.prepareStatement(query);
                pstm.executeUpdate();
                cedula = "";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Clave actualizada", ""));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debes digitar la clave", ""));
            }
        } catch (SQLException e) {
            System.out.println("error " + e);
        } finally {
            try {
                poolSql_19.con.close();
            } catch (SQLException ex) {
                Logger.getLogger(BiometricoBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

//
//    public void agregarProgramacion() {
//        RecuperarFechas(2);
//        String ciudad = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("ciudad");
//        try {
//            poolOracle.con = poolOracle.dataSource.getConnection();
//            int b = 0;
//            System.out.println("select A.PJ_EMPLEADO,A.PJ_JORNADA,case when A.PJ_JORNADA<>'Z' then B.jt_horainicialnovedadeS||':'||       \n"
//                    + "B.jt_minutoinicialnovedades||'-'||        \n"
//                    + "B.jt_horafinalnovedades ||':'||B.jt_minutofinalnovedades else '0' end,A.PJ_FECHA from RE_PROGRAMACION_JORNADAS A , RE_JORNADASTRABAJO B  where  \n"
//                    + "A.PJ_JORNADA=B.JT_CODIGO AND  (B.JT_DESCRIPCION LIKE '%" + ciudad + "%' or  A.PJ_JORNADA='Z') and "
//                    + "A.PJ_FECHA  BETWEEN '" + this.f1 + "' AND '" + this.f2 + "'");
//            ResultSet rs = poolOracle.query("select A.PJ_EMPLEADO,A.PJ_JORNADA,case when A.PJ_JORNADA<>'Z' then B.jt_horainicialnovedadeS||':'||       \n"
//                    + "B.jt_minutoinicialnovedades||'-'||        \n"
//                    + "B.jt_horafinalnovedades ||':'||B.jt_minutofinalnovedades else '0' end,A.PJ_FECHA from RE_PROGRAMACION_JORNADAS A , RE_JORNADASTRABAJO B  where  \n"
//                    + "A.PJ_JORNADA=B.JT_CODIGO AND  (B.JT_DESCRIPCION LIKE '%" + ciudad + "%' or  A.PJ_JORNADA='Z') and "
//                    + "A.PJ_FECHA  BETWEEN TO_DATE('" + this.f1 + "', 'DD/MM/YYYY') AND TO_DATE('" + this.f2 + "', 'DD/MM/YYYY') ");
//            while (rs.next()) {
//                System.out.println("---");
//                listProgramacion.add(new biometrico(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)));
//            }
//            System.out.println("size programacion : " + listProgramacion.size());
//
//        } catch (SQLException ex) {
//            System.out.println("Erorr : " + ex.toString());
//        } finally {
//            try {
//                poolOracle.con.close();
//            } catch (SQLException ex) {
//                System.out.println(ex);
//            }
//        }
//        System.out.println("----------");
//        String fecha[] = null;
//        String fechafinal = "";
//        for (biometrico programacion : listProgramacion) {
//            System.out.println("" + programacion.getFechaProgramacion());
////            fecha = programacion.getFechaProgramacion().trim().split("/");
////            
////            programacion.setFechaProgramacion("" + fecha[2] + "-" + fecha[1] + "-" + fecha[0]);
//        }
//        if (this.f1.equalsIgnoreCase(this.f2)) {
//
//            fecha = this.f1.trim().split("/");
//            fechafinal = "" + fecha[2] + "-" + fecha[1] + "-" + fecha[0];
////            
////            programacion.setFechaProgramacion("" + fecha[2] + "-" + fecha[1] + "-" + fecha[0]);
//            System.out.println("son iguales");
//            System.out.println("f : " + fechafinal);
//            for (biometrico marcacion : listBiometrico) {
//                for (biometrico programacion : listProgramacion) {
//                    if (marcacion.getCodigo().trim().equalsIgnoreCase(programacion.getCodigo().trim())
//                            && fechafinal.trim().equalsIgnoreCase(programacion.getFechaProgramacion().trim().substring(0, 10))) {
//                        marcacion.setTurno(programacion.getTurno());
//                        marcacion.setProgramacion(programacion.getProgramacion());
//                    }
//                }
//            }
//        } else {
//            for (biometrico marcacion : listBiometrico) {
//                for (biometrico programacion : listProgramacion) {
//                    if (marcacion.getCodigo().trim().equalsIgnoreCase(programacion.getCodigo().trim())
//                            && marcacion.getFechaIni().trim().substring(0, 10).equalsIgnoreCase(programacion.getFechaProgramacion().trim().substring(0, 10))) {
//                        marcacion.setTurno(programacion.getTurno());
//                        marcacion.setProgramacion(programacion.getProgramacion());
//                    }
//                }
//            }
//        }
//    }
    /**
     * Metodo para imprimir en un formato pdf , las marcaciones de una persona ,
     * en un periodo de tiempo en especifico
     */
    public void imprimir(ActionEvent evt) throws IOException, SQLException {
        if (listBiometrico.size() > 0) {
            try {
                poolSql_1.con = poolSql_1.dataSource.getConnection();
                Map parametros = new HashMap();
                System.out.println("Ingreso a imr");
                File jasper = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/Reportes/marcacionPersonal.jasper"));
                parametros.put("cedula", cedula);
                parametros.put("fechaini", f1);
                parametros.put("fechafin", f2);
                parametros.put("nombre", name);
                parametros.put("agencia", area);
                byte[] jp = JasperRunManager.runReportToPdf(jasper.getPath(), parametros, poolSql_1.getconecion());
                HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
                response.setContentType("application/pdf");
                response.setContentLength(jp.length);
                try (ServletOutputStream outStream = response.getOutputStream()) {
                    outStream.write(jp, 0, jp.length);
                    outStream.flush();
                    outStream.close();
                }
                FacesContext.getCurrentInstance().responseComplete();

            } catch (Exception ex) {
                System.out.println("Error A imprimir : " + ex.toString());
            } finally {
                poolSql_1.con.close();
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Primeor debe cargar la informacion del trabajador", ""));
        }
    }

    public BiometricoBean() {
    }

    public Date getFechaInicial() {
        return FechaInicial;
    }

    public void setFechaInicial(Date FechaInicial) {
        this.FechaInicial = FechaInicial;
    }

    public Date getFechafinal() {
        return Fechafinal;
    }

    public void setFechafinal(Date Fechafinal) {
        this.Fechafinal = Fechafinal;
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

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public ArrayList<biometrico> getListBiometrico() {
        return listBiometrico;
    }

    public void setListBiometrico(ArrayList<biometrico> listBiometrico) {
        this.listBiometrico = listBiometrico;
    }

    public Growl getGrowl() {
        return growl;
    }

    public void setGrowl(Growl growl) {
        this.growl = growl;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public ArrayList<biometrico> getListAreas() {
        return listAreas;
    }

    public void setListAreas(ArrayList<biometrico> listAreas) {
        this.listAreas = listAreas;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public sqlServer10_1 getPoolSql_1() {
        return poolSql_1;
    }

    public void setPoolSql_1(sqlServer10_1 poolSql_1) {
        this.poolSql_1 = poolSql_1;
    }

    public sqlServer10_19 getPoolSql_19() {
        return poolSql_19;
    }

    public void setPoolSql_19(sqlServer10_19 poolSql_19) {
        this.poolSql_19 = poolSql_19;
    }

    public ConecionOracle getPoolOracle() {
        return poolOracle;
    }

    public void setPoolOracle(ConecionOracle poolOracle) {
        this.poolOracle = poolOracle;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNameCodigo() {
        return NameCodigo;
    }

    public void setNameCodigo(String NameCodigo) {
        this.NameCodigo = NameCodigo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

}
