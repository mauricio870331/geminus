
package Beans;

import Coneciones.ConecionOracle;
import Coneciones.sqlServer10_1;
import Model.ProgramacionTurno;
import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author admin
 */
@Named(value = "cargaArchivo")
@SessionScoped
public class CargaArchivo implements Serializable {

    ArrayList<ProgramacionTurno> listTurnos = new ArrayList();
    ArrayList<ProgramacionTurno> listErrores = new ArrayList();

    sqlServer10_1 poolSql_1 = new sqlServer10_1();
    ConecionOracle poolOracle = new ConecionOracle();

    static CallableStatement cstmt;
    private String destination = "D:\\";
    boolean MostrarValores = false;
    private Date fecha;
    private FileUploadEvent file;

    public CargaArchivo() {

    }

    public void onDateSelect(SelectEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Date Selected", format.format(event.getObject())));
    }

    public boolean leercsv2(String ruta) throws SQLException {
        poolSql_1.con = poolSql_1.dataSource.getConnection();
        poolSql_1.getconecion().setAutoCommit(false);
        boolean errorArchivo = false;
        try {
            ArrayList<String> l = new ArrayList();
            CsvReader usuarios_import = new CsvReader(ruta);
            // usuarios_import.readHeaders();
            boolean Camposvacios = false;
            boolean Tamañoturnos = false;

            String insert = "";
            String campos = "";
            String camposExtra = "";
            int cant = 0;
            while (usuarios_import.readRecord()) {

                String[] country = usuarios_import.getRawRecord().split(",");
                System.out.println("Cc : " + country[0]);
                System.out.println("tamaño : " + country.length);
                if (country.length > 17) {
                    Tamañoturnos = true;
                    break;
                } else if (country.length < 17) {
                    System.out.println("Menor");
                    for (int i = 0; i < (17 - country.length); i++) {
                        camposExtra += ",null";
                    }

                }

                for (String campo : country) {
                    campos += "'" + campo + "',";
                }

                if (Camposvacios) {
                    break;
                }
                if (Tamañoturnos) {
                    break;
                }

                campos = campos.substring(0, campos.length() - 1);
                insert += "insert into Geminusplano values(" + campos + camposExtra + ")\n";
                cant++;
//
//                System.out.println(usuarios_import.getRawRecord());
//                System.out.println(usuarios_import.getValues());
//                System.out.println(usuarios_import.getValues().length);
//                l.add(usuarios_import.toString());

            }

            poolSql_1.transaccion("delete from Geminusplano");
            poolSql_1.transaccion(insert);
            errorArchivo = true;
        } catch (FileNotFoundException e) {
            System.out.println("error " + e);
        } catch (IOException e) {
            System.out.println("error " + e);
        } finally {
            poolSql_1.getconecion().commit();
            poolSql_1.getconecion().setAutoCommit(true);
            poolSql_1.con.close();

        }
        return errorArchivo;
    }

    public boolean leercsv(String ruta) throws IOException, SQLException {
        poolSql_1.con = poolSql_1.dataSource.getConnection();
        poolSql_1.getconecion().setAutoCommit(false);

        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        boolean Camposvacios = false;
        boolean Tamañoturnos = false;
        boolean errorArchivo = false;
        String insert = "";
        String campos = "";
        String camposExtra = "";

        try {
            br = new BufferedReader(new FileReader(ruta));
            System.out.println("--- " + br.read());

            int cant = 0;
            while ((line = br.readLine()) != null) {
                campos = "";
                camposExtra = "";
                System.out.println("" + line.toString());
                String[] country = line.split(cvsSplitBy);
                System.out.println("Cc : " + country[0]);
                System.out.println("tamaño : " + country.length);
                if (country.length > 17) {
                    Tamañoturnos = true;
                    break;
                } else if (country.length < 17) {
                    System.out.println("Menor");
                    for (int i = 0; i < (17 - country.length); i++) {
                        camposExtra += ",null";
                    }

                }

                for (String campo : country) {
                    campos += "'" + campo + "',";
                }

                if (Camposvacios) {
                    break;
                }
                if (Tamañoturnos) {
                    break;
                }

                campos = campos.substring(0, campos.length() - 1);
                insert += "insert into Geminusplano values(" + campos + camposExtra + ")\n";
                cant++;
            }
            System.out.println("cantidad : " + cant);

            System.out.println("-- : " + insert);
            poolSql_1.transaccion("delete from Geminusplano");
            poolSql_1.transaccion(insert);

            errorArchivo = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            poolSql_1.getconecion().commit();
            poolSql_1.getconecion().setAutoCommit(true);
            poolSql_1.con.close();

            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return errorArchivo;
    }

    public void procesoBD() throws SQLException {
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println("-----------------");
        System.out.println("-" + format2.format(fecha));
        int transaccion = 0;
        try {
            poolSql_1.con = poolSql_1.dataSource.getConnection();

            System.out.println("--" + format2.format(fecha));
            cstmt = poolSql_1.con.prepareCall("{call GeminusCreaPlano (?)}");
            cstmt.setString(1, format2.format(fecha));
            ResultSet rs = cstmt.executeQuery();
            while (rs.next()) {
                transaccion = rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println("Error proceso DB : " + e.toString());
        } finally {
            poolSql_1.con.close();

        }
    }

    public void procesoBD2() throws SQLException {
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println("-----------------");
        System.out.println("-" + format2.format(fecha));
        int transaccion = 0;
        try {
            poolSql_1.con = poolSql_1.dataSource.getConnection();

            System.out.println("--" + format2.format(fecha));
            cstmt = poolSql_1.con.prepareCall("{call GeminusCreaPlano (?)}");
            cstmt.setString(1, format2.format(fecha));
            ResultSet rs = cstmt.executeQuery();
            while (rs.next()) {
                transaccion = rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println("Error proceso DB : " + e.toString());
        } finally {
            poolSql_1.con.close();

        }
    }

    public void cargarRuta(FileUploadEvent event) throws SQLException {
        System.out.println("----");
        file = event;
        System.out.println("-- " + file);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informacion", "Ruta Cargada"));
    }

    public void start() throws SQLException, Exception {
        try {
            System.out.println("-----------------");
            System.out.println("- " + fecha);
            boolean a = false;
            boolean r = subirArchivo(file.getFile().getFileName(), file.getFile().getInputstream());
            if (r) {
                a=leercsv2("D:\\" + file.getFile().getFileName());
            }
            if (a) {
                File f = new File("D:\\" + file.getFile().getFileName());
                f.delete();
                procesoBD();
                System.out.println("paso -------");
                cargarTurnos();
            }
        } catch (IOException e) {
            System.out.println("Error : " + e.toString());
        }
    }

    public boolean subirArchivo(String fileName, InputStream in) {
        boolean r = false;
        try {
            OutputStream out = new FileOutputStream(new File(destination + fileName));
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = in.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            in.close();
            out.flush();
            out.close();
            System.out.println("New file created!");
            r = true;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return r;
    }

    public void EstadoProgramacionGemius() throws SQLException {
        try {
            poolOracle.con = poolOracle.dataSource.getConnection();
            int valor = 0;
            for (ProgramacionTurno listTurno : listTurnos) {
                System.out.println("select count(*) from RE_PROGRAMACION_JORNADAS where pj_fecha=TO_DATE('" + listTurno.getFecha() + "', 'DD/MM/YYYY') and pj_empleado='" + listTurno.getCodigo() + "'");
                ResultSet rs = poolOracle.query("select count(*) from RE_PROGRAMACION_JORNADAS where pj_fecha=TO_DATE('" + listTurno.getFecha() + "', 'DD/MM/YYYY') and pj_empleado='" + listTurno.getCodigo() + "'");
                while (rs.next()) {
                    valor = rs.getInt(1);
                    if (valor > 0) {
                        listTurno.setEstado("Existe");
                    }
                }
            }
        } catch (Exception ex) {

        } finally {
            poolOracle.con.close();
        }
    }

    public void cargarTurnos() throws SQLException, IOException, Exception {
        boolean SinErrores = false;
        try {
            listTurnos.clear();
            listErrores.clear();
            poolSql_1.con = poolSql_1.dataSource.getConnection();
            ResultSet rs = poolSql_1.query("select A.cod_empleado,A.jornada,A.descripcion from GeminusError A");
            while (rs.next()) {
                listErrores.add(new ProgramacionTurno(rs.getString(1), rs.getString(2), rs.getString(3), ""));
            }
            System.out.println("tamaño : " + listTurnos.size());
            if (listTurnos.size() == 0) {
                SinErrores = true;
                System.out.println("-- entro aqui");
                ResultSet rs2 = poolSql_1.query("select A.cod_empleado,A.dependencia,CONVERT(VARCHAR(10),A.fecha,103),A.jornada from GeminusResultCSV A");
                while (rs2.next()) {
                    listTurnos.add(new ProgramacionTurno(rs2.getString(1), rs2.getString(2), rs2.getString(3), rs2.getString(4)));
                }
                EstadoProgramacionGemius();

            } else {
                System.out.println("No entro aqui ");
            }
        } catch (Exception ex) {
            System.out.println("Error en carga " + ex.toString());
        } finally {
            poolSql_1.con.close();
        }

        if (SinErrores) {
            escribePalindromos();
        }

    }

    public void guardarProgramacionGeminus() throws SQLException, IOException {
        System.out.println("entro----------------");
        String insert = "";
        String contenido = "";

        String outputFile = "D:\\ArchivoEmpleados.csv";
//        boolean alreadyExists = new File(outputFile).exists();
//
//        if (alreadyExists) {
//            File ArchivoEmpleados = new File(outputFile);
//            ArchivoEmpleados.delete();
//        }

        CsvWriter csvOutput = new CsvWriter(new FileWriter(outputFile, true), ',');
        csvOutput.write("Registro");
        csvOutput.write("Nombre");
        csvOutput.write("Telefono");
        csvOutput.write("Mail");
        csvOutput.endRecord();
//        for (Empleado emp : empleados) {
//            
//            csvOutput.write(emp.getRegistro());
//            csvOutput.write(emp.getNombre());
//            csvOutput.write(emp.getTelefono());
//            csvOutput.write(emp.getMail());
//            csvOutput.endRecord();
//        }
        csvOutput.close();
    }

    public void escribePalindromos() throws Exception {
        CsvWriter writercsv = null;
        System.out.println("comenzamos a crear csv");
        try {
            File fichero = new File("D:\\ArchivoEmpleados.csv");
            FileWriter fwriter = new FileWriter(fichero);
            // Creamos la clase que nos permite escribir en el fichero CSV.
            writercsv = new CsvWriter(fwriter, ',');
            // Escribimos las cabeceras.
//            writercsv.write("PALABRA");
//            writercsv.write("AL REVES");
//            writercsv.write("¿ ES PALÍNDROMO ?");
//            writercsv.endRecord();
            // Escribimos los resultados.
            for (ProgramacionTurno t : listTurnos) {
                System.out.println("-----");
                writercsv.write(t.getCodigo());
                writercsv.write(t.getDependencia());
                writercsv.write(t.getFecha());
                writercsv.write(t.getTurno());
                writercsv.endRecord();
            }
//            for (int i = 0; i < listaPalindromos.size(); i++) {
//                VOPalindromo palin = (VOPalindromo) listaPalindromos.get(i);
//                writercsv.write(palin.getPalabra());
//                writercsv.write(palin.getReves());
//                writercsv.write((palin.isPalindromo()) ? "SI" :
//                "NO
//                ");
//            writercsv.endRecord();
//            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (writercsv != null) {
                writercsv.close();
            }
        }
    }

    public ArrayList<ProgramacionTurno> getListTurnos() {
        return listTurnos;
    }

    public void setListTurnos(ArrayList<ProgramacionTurno> listTurnos) {
        this.listTurnos = listTurnos;
    }

    public sqlServer10_1 getPoolSql_1() {
        return poolSql_1;
    }

    public void setPoolSql_1(sqlServer10_1 poolSql_1) {
        this.poolSql_1 = poolSql_1;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public boolean isMostrarValores() {
        return MostrarValores;
    }

    public void setMostrarValores(boolean MostrarValores) {
        this.MostrarValores = MostrarValores;
    }

    public ArrayList<ProgramacionTurno> getListErrores() {
        return listErrores;
    }

    public void setListErrores(ArrayList<ProgramacionTurno> listErrores) {
        this.listErrores = listErrores;
    }

    public Date getFecha() {
        System.out.println("get");
        return fecha;
    }

    public void setFecha(Date fecha) {
        System.out.println("entro a guardar fecha : " + fecha);
        this.fecha = fecha;
    }

    public FileUploadEvent getFile() {
        return file;
    }

    public void setFile(FileUploadEvent file) {
        this.file = file;
    }

}
