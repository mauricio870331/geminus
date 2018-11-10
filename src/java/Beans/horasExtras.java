package Beans;

import Coneciones.sqlServer10_1;
import Control.Email;
import Control.procesoReporteLiq;
import Model.Horasextras;
import Model.usuario;
import java.io.IOException;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
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
 * clase que se utiliza para , cargar las horas extras generadas por el sistema
 * de Geminus (Oracle 10.215)
 *
 */
@Named(value = "horasExtras")
@SessionScoped
public class horasExtras implements Serializable {

    ArrayList<Horasextras> listExtras = new ArrayList();
    private Date FechaInicial;
    private Date Fechafinal;
    private String f1;
    private String f2;
    private String empresa;
    private int cantidad;
    Growl growl = new Growl();
    sqlServer10_1 poolSql_1 = new sqlServer10_1();
    static CallableStatement cstmt;

    public horasExtras() {
    }

    /**
     * Metodo constructor
     *
     */
    @PostConstruct
    public void init() {
        growl.setLife(5000);
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
     * Metodo , para castear las fechas del calendar de primefaces
     *
     */
    public String RecuperarFechas() {
        SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");
        this.f1 = format2.format(FechaInicial);
        this.f2 = format2.format(Fechafinal);
        return "";
    }

    /**
     * Metodopara cargar la informacion de las horas extras , con unos filtros
     * de fechas y empresa
     *
     */
    public void cargarDatos() throws SQLException, ParseException {
        RecuperarFechas();
        listExtras.clear();
        procesoReporteLiq p = new procesoReporteLiq();
        try {
            listExtras = (ArrayList) p.cargarReporte(f1, f2, empresa, 1);
        } catch (ClassNotFoundException ex) {
            System.out.println("Error CargaDatos : " + ex.toString());
        }
        cantidad = listExtras.size();
    }

    /**
     * Metodo para pasar a un informe excel , una lista de informacion.
     *
     * @param document : documento o tabla , que se encuentra cargada en la
     * vista web , se requiere para pasar esa informacion a excel
     */
    public void postProcessXLS(Object document) throws IOException {

        if (listExtras.size() > 0) {
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

    public ArrayList<Horasextras> getListExtras() {
        return listExtras;
    }

    public void setListExtras(ArrayList<Horasextras> listExtras) {
        this.listExtras = listExtras;
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

    public Growl getGrowl() {
        growl = new Growl();
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

}
