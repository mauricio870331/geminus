package Beans;

import Coneciones.ConecionOracle;
import Coneciones.sqlServer10_1;
import Control.Email;
import Control.procesoRepInconsistencias;
import Model.Marcaciones;
import Model.Novedades;
import Model.correosXagencia;
import java.io.IOException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.primefaces.component.growl.Growl;
import org.primefaces.event.SelectEvent;

/**
 * Clase que se utiliza para gestionar las noveades ó inconsistencias
 * presentadas en las agencias.
 */
@Named(value = "geminusReporteNovedades")
@SessionScoped
public class geminusReporteNovedades implements Serializable {

    ArrayList<Novedades> list_novedades = new ArrayList();
    ArrayList<Novedades> list_acciones = new ArrayList();
    ArrayList<Marcaciones> list_marcaciones = new ArrayList();
    ArrayList<Novedades> list_horasextras = new ArrayList();
    ArrayList<correosXagencia> listAgencias = new ArrayList();

    private Date FechaInicial;
    private Date Fechafinal;
    private String f1;
    private String f2;
    private int cantMarcaciones;
    private int cantNovedades;
    private int cantHorasExtras;
    Growl growl = new Growl();
    ConecionOracle poolOracle = new ConecionOracle();
    sqlServer10_1 poolSql_1 = new sqlServer10_1();

    /**
     * Metodo constructor
     *
     */
    @PostConstruct
    public void init() {
        growl.setLife(5000);

    }

    public geminusReporteNovedades() {
    }

    /**
     * Metodo para castear una fecha , del framework de primefaces.
     *
     */
    public void onDateSelect(SelectEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd/MMM/yyyy");
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Date Selected", format.format(event.getObject())));
    }

    /**
     * Metodo , para castear las fechas del calendar de primefaces , y
     * identificar si el mes , consta de 31 dias o menos.
     *
     */
    public void RecuperarFechas() {
        SimpleDateFormat format2 = new SimpleDateFormat("dd/MMM/yyyy");
        this.f1 = format2.format(FechaInicial);
        this.f2 = format2.format(Fechafinal);
    }

    /**
     * Metodo para pasar a un informe excel , una lista de informacion.
     *
     * @param document : documento o tabla , que se encuentra cargada en la
     * vista web , se requiere para pasar esa informacion a excel
     */
    public void postProcessXLS(Object document) throws IOException {
        if (list_novedades.size() > 0) {
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
     * Metodo para para cargar las novedades de las agencias , en un rango de
     * fechas selecionado.
     *
     */
    public void cargarNovedades() throws SQLException, ClassNotFoundException {
        RecuperarFechas();
        list_novedades.clear();
        procesoRepInconsistencias proceso = new procesoRepInconsistencias();
        list_novedades = (ArrayList) proceso.cargarNovedades(f1, f2);
        cantNovedades = list_novedades.size();
        cantMarcaciones = proceso.CantMarcaciones(f1, f2);
        ordenar();
        cargaragencias();
    }

    /**
     * Metodo para ordenar las novedades
     *
     */
    public void ordenar() {
        Collections.sort(list_novedades, (Novedades a, Novedades b) -> {
            int resultado = a.getCosto().compareTo(b.getCosto());
            if (resultado != 0) {
                return resultado;
            }
            resultado = a.getNombre().compareTo(b.getNombre());
            if (resultado != 0) {
                return resultado;
            }
            return resultado;
        });
    }

    /**
     * Metodo seleccionar la opcion de eliminar novedad , o de autorizar novedad
     *
     * @param novedad : Novedad en la cual se esta precionando el evento
     * * @param condicion : Para saber si es para eliminar o para autorizar
     * 1:autorizar 2:eliminar
     */
    public void inabilitarcampo(Novedades novedad, int condicion) {
        String mns = "";
        for (Novedades list_novedade : list_novedades) {
            if (list_novedade.getNum() == novedad.getNum()) {
                if (condicion == 1) {
                    if (list_novedade.isEliminar() == false) {
                        if (list_novedade.isActivar()) {
                            list_novedade.setActivar(true);
                        } else {
                            list_novedade.setActivar(false);
                        }
                    } else if (list_novedade.isActivar()) {
                        list_novedade.setActivar(false);
                        mns = "Ya tiene seleccionado el campo de eliminar";
                    }

                } else if (condicion == 2) {
                    if (list_novedade.isActivar() == false) {
                        if (list_novedade.isEliminar()) {
                            list_novedade.setEliminar(true);
                        } else {
                            list_novedade.setEliminar(false);
                        }
                    } else if (list_novedade.isEliminar()) {
                        list_novedade.setEliminar(false);
                        mns = "Ya tiene seleccionado el campo de activar";
                    }
                }
            }
        }
        if (mns.length() > 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, mns, "Liquidacion Exitosa"));
        }
    }

    /**
     * Metodo para validar las horas extras , autorizadas o eliminadas , que
     * previamente se han seleccioando
     *
     */
    public String validacionHE() throws IOException {
        list_acciones.clear();
        boolean r = false;
        for (Novedades list_novedade2 : list_novedades) {
            if (list_novedade2.isActivar() || list_novedade2.isEliminar()) {
                r = true;
                break;
            }
        }
        if (r) {
            for (Novedades list_novedade2 : list_novedades) {
                if (list_novedade2.isActivar() || list_novedade2.isEliminar()) {
                    list_acciones.add(list_novedade2);
                }
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "No tiene Seleccionado ninguna Hora Extra", ""));
        }
        return r == true ? "/Disciplinario/validacionHorasExtras" : "";
    }

    /**
     * Metodo para guardar las novedades de eliminar o autorizar horas extras ,
     * en el sistema de oracle (Geminus 10.215)
     *
     */
    public void guardarNovedades() throws ClassNotFoundException, SQLException, IOException {
        SimpleDateFormat format2 = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");

        Date fecha = new Date();
        String formato = "";
        String formato2 = "";

        if (fecha.getHours() > 12) {
            formato = "PM";
            formato2 = "pm";
        } else {
            formato = "AM";
            formato2 = "am";
        }
        String fechaMod = "to_date('" + format2.format(fecha) + " " + formato + "','DD/MM/YYYY HH:MI:SS " + formato2 + "')";
        int r = -1;
        try {
            poolOracle.con = poolOracle.dataSource.getConnection();
            poolOracle.getconecion().setAutoCommit(false);
            for (Novedades novedad : list_acciones) {
                if (novedad.isActivar()) {
                    r = poolOracle.transaccion("insert into ExtrasLiquidacionGeminus values(horasExtras.NextVal," + novedad.getCodigo() + ",'" + novedad.getCedula() + "',"
                            + "TO_DATE('" + novedad.getFecha() + "', 'DD/MM/YYYY')" + ",'" + novedad.getTipo() + "'," + fechaMod + ",'ADM')");
                } else if (novedad.isEliminar()) {
                    r = poolOracle.transaccion("insert into ExtrasLogliqGeminus values(horasExtrasLog.NextVal," + novedad.getCodigo() + ",'" + novedad.getCedula() + "',"
                            + "TO_DATE('" + novedad.getFecha() + "', 'DD/MM/YYYY')" + ",'" + novedad.getTipo() + "'," + fechaMod + ",'ADM')");
                }
            }
        } catch (SQLException ex) {
            System.out.println("error :  " + ex.toString());
            r = -5;
        } finally {
            poolOracle.getconecion().commit();
            poolOracle.getconecion().setAutoCommit(true);
            poolOracle.con.close();
        }
        if (r == 1) {
            list_novedades.clear();
            FacesContext.getCurrentInstance().getExternalContext().redirect("ReporteInconsistencias.xhtml");
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al cargar las novedades", ""));
        }
    }

    /**
     * Metodo para cargar horas extras autorizadas
     *
     */
    public void cargarHorasExtras() {
        RecuperarFechas();
        list_horasextras.clear();
        try {
            poolOracle.con = poolOracle.dataSource.getConnection();
            ResultSet rs = poolOracle.query("select consecutivo,fecha,noveda,codigo,cedula,usuario,fechaoperacion "
                    + " from ExtrasLiquidacionGeminus where fecha between '" + f1 + "' and '" + f2 + "' order by consecutivo");
            while (rs.next()) {
                list_horasextras.add(new Novedades(rs.getInt(1), rs.getString(2).substring(0, 10), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7)));
            }
            cantHorasExtras = list_horasextras.size();
        } catch (SQLException ex) {
            System.out.println("error cargarHorasExtras  " + ex.toString());
        } finally {
            try {
                poolOracle.con.close();
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        }
    }

    /**
     * Metodo para quitar las novedades
     *
     */
    public String procesoQuitarNovedades() {
        list_acciones.clear();
        boolean r = false;
        for (Novedades list_novedade2 : list_horasextras) {
            if (list_novedade2.isActivar()) {
                r = true;
                break;
            }
        }
        if (r) {
            for (Novedades list_novedade2 : list_horasextras) {
                if (list_novedade2.isActivar()) {
                    list_acciones.add(list_novedade2);
                }
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "No tiene Seleccionado ninguna Hora Extra", ""));
        }
        return r == true ? "/Disciplinario/operacionHE" : "";
    }

    /**
     * Metodo para eliminar las novedades , que se han registrado previamente en
     * el sistema
     *
     */
    public String eliminarNovedades() throws SQLException, IOException {
        int r = -1;
        try {
            poolOracle.con = poolOracle.dataSource.getConnection();
            poolOracle.getconecion().setAutoCommit(false);
            for (Novedades novedad : list_acciones) {
                if (novedad.isActivar()) {
                    r = poolOracle.transaccion("delete from ExtrasLiquidacionGeminus where consecutivo=" + novedad.getNum());
                }
            }
        } catch (SQLException ex) {
            System.out.println("error :  " + ex.toString());
            r = -5;
        } finally {
            poolOracle.getconecion().commit();
            poolOracle.getconecion().setAutoCommit(true);
            poolOracle.con.close();
        }

        if (r == 1) {
            list_novedades.clear();
            FacesContext.getCurrentInstance().getExternalContext().redirect("ReporteHorasExtras.xhtml");
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Error al eliminar las novedades", ""));
        }
        return null;
    }

    /**
     * Metodo para enviar las novedades de inconsistencias para la agencia. por
     * medio de correo electronico
     *
     */
    public void email() throws SQLException, IOException {
        String insert = "";
        int num = 1;
        int transacion = -1;
        FacesMessage mns = null;
        String v[] = null;
        boolean r = false;
        if (list_novedades.size() > 0) {

            for (Novedades n : list_novedades) {
                if (!n.getProgramacion().trim().equalsIgnoreCase("NnN")) {

                    v = n.getProgramacion().trim().split("-");
                    insert = insert + "insert into EmailNovedadesGeminus values(" + num + ",'" + n.getFechaProgramada().trim().substring(0, 10) + "','" + v[0] + "','" + v[1]
                            + "','" + n.getMarcacion().trim() + "','" + n.getTipo().trim() + "'," + Integer.parseInt(n.getCodigo()) + ",'"
                            + n.getCedula().trim() + "','" + n.getNombre() + "','" + n.getCosto().trim() + "','" + n.getTurno().trim() + "',null);\n";
                } else {
                    insert = insert + "insert into EmailNovedadesGeminus values(" + num + ",'" + n.getFechaProgramada().trim().substring(0, 10) + "','0','0','" + n.getMarcacion().trim() + "','" + n.getTipo().trim() + "'," + Integer.parseInt(n.getCodigo()) + ",'"
                            + n.getCedula().trim() + "','" + n.getNombre() + "','" + n.getCosto().trim() + "','N',null);\n";
                }
                num++;
            }
            try {
                poolSql_1.con = poolSql_1.dataSource.getConnection();
                poolSql_1.getconecion().setAutoCommit(false);
                poolSql_1.transaccion("delete from EmailNovedadesGeminus");
                poolSql_1.transaccion(insert);
                r = true;
            } catch (SQLException ex) {
                System.out.println("Error SQL : " + ex.toString());
                r = false;
            } finally {
                poolSql_1.getconecion().commit();
                poolSql_1.getconecion().setAutoCommit(true);
                poolSql_1.con.close();
            }

            if (r) {
                Email mail = new Email();
                transacion = mail.getMail();

                switch (transacion) {
                    case -1:
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe primero cargar el reporte de novedades", ""));
                        break;
                    case 0:
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe primero cargar el reporte de novedades", ""));
                        break;
                    case 1:
                        list_novedades.clear();
                        FacesContext.getCurrentInstance().getExternalContext().redirect("Mensaje.xhtml");
                        break;
                    default:
                        break;
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Proceso de Enviar correos no se pudo Realizar , comuniquese con soporte", ""));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe primero cargar el reporte de novedades", ""));
        }
    }

    /**
     * Metodo para cargar las agencias , que existen en el sistema
     *
     */
    public void cargaragencias() throws SQLException {
        try {
            listAgencias.clear();
            poolSql_1.con = poolSql_1.dataSource.getConnection();
            boolean campo = false;
            ResultSet rs = poolSql_1.query("select a.nombre,a.estado from ct_sucursalesgeminus A where A.nombre<>'ADM' order by a.nombre");
            while (rs.next()) {
                correosXagencia correo = new correosXagencia();
                correo.setAgencia(rs.getString(1));
                campo = rs.getInt(2) == 0 ? true : false;
                correo.setEstado(campo);
                listAgencias.add(correo);
            }

        } catch (SQLException e) {
            System.out.println("Error proceso DB : " + e.toString());
        } finally {
            poolSql_1.con.close();

        }
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

    public ArrayList<Novedades> getList_novedades() {
        return list_novedades;
    }

    public void setList_novedades(ArrayList<Novedades> list_novedades) {
        this.list_novedades = list_novedades;
    }

    public ArrayList<Marcaciones> getList_marcaciones() {
        return list_marcaciones;
    }

    public void setList_marcaciones(ArrayList<Marcaciones> list_marcaciones) {
        this.list_marcaciones = list_marcaciones;
    }

    public Growl getGrowl() {
        return growl;
    }

    public void setGrowl(Growl growl) {
        this.growl = growl;
    }

    public ArrayList<Novedades> getList_acciones() {
        return list_acciones;
    }

    public void setList_acciones(ArrayList<Novedades> list_acciones) {
        this.list_acciones = list_acciones;
    }

    public ArrayList<Novedades> getList_horasextras() {
        return list_horasextras;
    }

    public void setList_horasextras(ArrayList<Novedades> list_horasextras) {
        this.list_horasextras = list_horasextras;
    }

    public int getCantMarcaciones() {
        return cantMarcaciones;
    }

    public void setCantMarcaciones(int cantMarcaciones) {
        this.cantMarcaciones = cantMarcaciones;
    }

    public int getCantNovedades() {
        return cantNovedades;
    }

    public void setCantNovedades(int cantNovedades) {
        this.cantNovedades = cantNovedades;
    }

    public int getCantHorasExtras() {
        return cantHorasExtras;
    }

    public void setCantHorasExtras(int cantHorasExtras) {
        this.cantHorasExtras = cantHorasExtras;
    }

    public ArrayList<correosXagencia> getListAgencias() {
        return listAgencias;
    }

    public void setListAgencias(ArrayList<correosXagencia> listAgencias) {
        this.listAgencias = listAgencias;
    }

}
