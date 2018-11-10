package Control;

import Coneciones.ConecionOracle;
import Model.Marcaciones;
import Model.Novedades;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Esta clase se utiliza para obtener las novedades de inconsistencias de
 * marcacion , por cada trabajador de acuerdo a la fecha seleccionada
 *
 * @author: Juan David Castrillon
 * @version: 11/12/2017
 */
public class procesoRepInconsistencias implements Serializable {

    ConecionOracle poolOracle = new ConecionOracle();
    ArrayList<Novedades> list_novedades = new ArrayList();
    ArrayList<Novedades> list_novedadesFinal = new ArrayList();
    ArrayList<String> eliminarTemp = new ArrayList();

    /**
     * Metodo para calcular la cantidad de marcaciones , en un rango de fechas
     *
     * @param fechaInicial : Fecha inicial para realizar el filtro de las
     * marcacion
     * * @param fechaFinal : Fecha Final para realizar el filtro de las
     * marcacion
     */
    public int CantMarcaciones(String fechaInicial, String fechaFinal) {
        int rowCount = 0;
        try {
            poolOracle.con = poolOracle.dataSource.getConnection();
            ResultSet rs = poolOracle.query("select count(*) from re_marcaciones where ma_fecha between '" + fechaInicial + "' and '" + fechaFinal + "'");
            while (rs.next()) {
                rowCount = rs.getInt(1);
            }
        } catch (SQLException ex) {
            System.out.println("Error CantMarcaciones : " + ex.toString());
        } finally {
            try {
                poolOracle.con.close();
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        }
        return rowCount;
    }

    /**
     * Metodo para cargar las novedades obtenidad , de una vista que se
     * encuentra en (10.215) Oracle , el cual me retorna todas las novedades
     * encontradas en una rango de fechas por : (Marcacion erronea fuera de su
     * turno , no marcacion tanto de entrada como de salida , personal que marca
     * y no tiene tunno)
     *
     * @param fechaInicial : Fecha inicial para realizar el filtro de las
     * marcacion
     * * @param fechaFinal : Fecha Final para realizar el filtro de las
     * marcacion
     */
    public List<Novedades> cargarNovedades(String fechaInicial, String fechaFinal) throws SQLException, ClassNotFoundException {
        list_novedades.clear();
        int num = 1;

        try {
            poolOracle.con = poolOracle.dataSource.getConnection();
            System.out.println("select A.MA_FECHA,rtrim(ltrim(A.PROGRAMADA)),rtrim(ltrim(A.MARCACION)), rtrim(ltrim(A.TIPO)),rtrim(ltrim(A.CODIGO)),rtrim(ltrim(A.CEDULA)),\n"
                    + "rtrim(ltrim(A.NOMBRE)),rtrim(ltrim(A.COSTO)), rtrim(ltrim(A.TURNO)) ,A.PJ_FECHA  from reporteNovedades A where \n"
                    + "A.pj_fecha between TO_DATE('" + fechaInicial + "', 'DD/MM/YYYY') and TO_DATE('" + fechaFinal + "', 'DD/MM/YYYY') \n"
                    + "and A.codigo not in (select codigo from ExtrasLiquidacionGeminus B where B.fecha=TO_DATE(A.MA_FECHA, 'DD/MM/YYYY') and B.noveda=A.TIPO)\n"
                    + "and A.codigo not in (select codigo from ExtrasLogliqGeminus C where C.fecha=TO_DATE(A.MA_FECHA, 'DD/MM/YYYY') and (case when C.noveda='No Laboro' then 'SalidaN' else\n"
                    + "C.noveda end )=A.TIPO\n"
                    + "union \n"
                    + "select codigo from ExtrasLogliqGeminus C where C.fecha=TO_DATE(A.MA_FECHA, 'DD/MM/YYYY') and (case when C.noveda='No Laboro' then 'EntradaN' else\n"
                    + "C.noveda end)=A.TIPO) \n"
                    + "order by 8,5,1");

            ResultSet rs = poolOracle.query("select A.MA_FECHA,rtrim(ltrim(A.PROGRAMADA)),rtrim(ltrim(A.MARCACION)), rtrim(ltrim(A.TIPO)),rtrim(ltrim(A.CODIGO)),rtrim(ltrim(A.CEDULA)),\n"
                    + "rtrim(ltrim(A.NOMBRE)),rtrim(ltrim(A.COSTO)), rtrim(ltrim(A.TURNO)) ,A.PJ_FECHA  from reporteNovedades A where \n"
                    + "A.pj_fecha between TO_DATE('" + fechaInicial + "', 'DD/MM/YYYY') and TO_DATE('" + fechaFinal + "', 'DD/MM/YYYY') \n"
                    + "and A.codigo not in (select codigo from ExtrasLiquidacionGeminus B where B.fecha=TO_DATE(A.MA_FECHA, 'DD/MM/YYYY') and B.noveda=A.TIPO)\n"
                    + "and A.codigo not in (select codigo from ExtrasLogliqGeminus C where C.fecha=TO_DATE(A.MA_FECHA, 'DD/MM/YYYY') and (case when C.noveda='No Laboro' then 'SalidaN' else\n"
                    + "C.noveda end )=A.TIPO\n"
                    + "union \n"
                    + "select codigo from ExtrasLogliqGeminus C where C.fecha=TO_DATE(A.MA_FECHA, 'DD/MM/YYYY') and (case when C.noveda='No Laboro' then 'EntradaN' else\n"
                    + "C.noveda end)=A.TIPO) \n"
                    + "order by 8,5,1");

            while (rs.next()) {

                list_novedades.add(new Novedades(num, rs.getString(1), rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7),
                        rs.getString(8), rs.getString(9), false, false, rs.getString(10)));
                num++;

            }

            String[] condiciones = {"SalidaN", "SalidaTrasnocho"};
            list_novedades.stream().filter((list_novedade) -> (list_novedade.getTipo().equals(condiciones[0]) || list_novedade.getTipo().equals(condiciones[1]))).forEachOrdered((list_novedade) -> {
                if (list_novedade.getMarcacion().equals("No marco")) {
                    eliminarTemp.add(list_novedade.getNum() + "," + list_novedade.getCodigo() + "," + list_novedade.getMarcacion() + "," + list_novedade.getTurno() + "," + list_novedade.getTipo() + ",delete");
                } else {
                    eliminarTemp.add(list_novedade.getNum() + "," + list_novedade.getCodigo() + "," + list_novedade.getMarcacion() + "," + list_novedade.getTurno() + "," + list_novedade.getTipo() + ",ok");
                }
            });
            for (Iterator<String> iterator = eliminarTemp.iterator(); iterator.hasNext();) {
                String next = iterator.next();
                String[] datos = next.split(",");
                if (contarElementos(datos[1], eliminarTemp) == 1) {
                    iterator.remove();
                }
            }
            eliminarTemp.forEach((string) -> {
//                System.out.println("Strig " + string);
                String[] da = string.split(",");
                if (da[2].equals("No marco")) {
                    deleteNovedad(da[1]);
                }

            });

        } catch (SQLException ex) {
            System.out.println("Error cargarNovedades: " + ex.toString());
        } finally {
            try {
                poolOracle.con.close();
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        }
        depuracion(fechaInicial, fechaFinal);
        return list_novedadesFinal;
    }

    public void depuracion(String f1, String f2) {
        //Turno que no marco trasnocho , pero marco antes de media noche.
        ArrayList<Novedades> list_novedades3 = new ArrayList();
        ArrayList<Novedades> list_novedades2 = new ArrayList();
        ArrayList<Novedades> list_N = new ArrayList();
        ArrayList<Marcaciones> list_SalidaYentrada = new ArrayList();
        String v[] = null;
        for (Novedades list_novedade : list_novedades) {
            if (list_novedade.getTipo().equalsIgnoreCase("Salida Trasnocho")) {
                for (Novedades list_novedade1 : list_novedades) {
                    if (!list_novedade1.getMarcacion().trim().equalsIgnoreCase("No marco")) {
                        v = list_novedade.getMarcacion().trim().split(":");
                        if (list_novedade1.getCodigo().trim().equalsIgnoreCase(list_novedade.getCodigo().trim())
                                && list_novedade1.getFecha().trim().equalsIgnoreCase(list_novedade.getFecha().trim())
                                && Integer.parseInt(v[0].equalsIgnoreCase("No marco") ? "10" : v[0]) <= 8) {
                            list_N.add(list_novedade);
                        }
                    }

                }
            }

        }

        for (Novedades novedades : list_N) {
            list_novedades.remove(novedades);
        }
        String codigo = "";
        String fecha = "";
        int count = 0;
        boolean r = false;
        for (Novedades nove : list_novedades) {
            count = 0;
            r = false;
            for (Novedades nove2 : list_novedades) {
                if (nove.getCodigo().trim().equalsIgnoreCase(nove2.getCodigo().trim())
                        && nove.getFecha().trim().equalsIgnoreCase(nove2.getFecha().trim())) {
                    if (nove2.getTipo().trim().equalsIgnoreCase("SalidaN") || nove2.getTipo().trim().equalsIgnoreCase("EntradaN")) {
                        count++;
                    }
                }

            }
            if (count > 1) {
                for (Novedades insertNove : list_novedades3) {
                    if (nove.getCodigo().trim().equalsIgnoreCase(insertNove.getCodigo().trim())
                            && nove.getFecha().trim().equalsIgnoreCase(insertNove.getFecha().trim())) {
                        r = true;
                    }
                }
                if (r == false) {
                    list_novedades3.add(nove);
                } else {
                    list_novedades2.add(nove);
                }
            }
        }

        for (Novedades nove : list_novedades2) {
            for (Novedades nove2 : list_novedades) {
                if (nove.getNum() == nove2.getNum()) {
                    nove2.setTipo("-");
                }
            }
        }

        for (Novedades nove : list_novedades3) {
            for (Novedades nove2 : list_novedades) {
                if (nove.getNum() == nove2.getNum()) {
                    nove2.setTipo("No Laboro");
                }
            }
        }

        for (Novedades nove : list_novedades) {
            if (!nove.getTipo().trim().equalsIgnoreCase("-")) {
                list_novedadesFinal.add(nove);
            }
        }
        String programacion[] = null;
        for (Novedades nove : list_novedades) {
            if (!nove.getProgramacion().trim().equalsIgnoreCase("NnN")) {
                programacion = nove.getProgramacion().split("-");
                nove.setProgramacionInicial(programacion[0]);
                nove.setProgramacionFinal(programacion[1]);
            } else {
                nove.setProgramacionInicial("0");
                nove.setProgramacionFinal("0");
            }
            programacion = null;
        }
    }

    /**
     * Metodo para calcular el trabajador en que fechas tiene Novedad.
     * resumiendo solo en una sola fecha , sin importar cuantas novedades tenga
     * en ese dia
     *
     * @param list_novedades : Arreglo con las novedades ya cargadas
     * posteriormente
     */
    public ArrayList<Novedades> SoloFechas(ArrayList<Novedades> list_novedades) {
        ArrayList<Novedades> List = new ArrayList();
        boolean r = false;
        for (Novedades objecto : list_novedades) {
            r = false;
            for (Novedades objecto2 : List) {
                if (objecto.getCodigo().trim().equalsIgnoreCase(objecto2.getCodigo().trim())
                        && objecto.getFechaProgramada().trim().equalsIgnoreCase(objecto2.getFechaProgramada().trim())) {
                    r = true;
                    break;
                }
            }
            if (r == false) {
                List.add(objecto);
            }
        }
        ArrayList<Novedades> List2 = new ArrayList();
        int cant = 0;
        for (Novedades objecto : List) {
            cant = 0;
            for (Novedades objecto2 : List) {
                if (objecto.getCodigo().trim().equalsIgnoreCase(objecto2.getCodigo().trim())) {
                    cant++;
                }
            }
            objecto.setCantNovedades(cant);
        }

        for (Novedades objecto : List) {
            r = false;
            for (Novedades objecto2 : List2) {
                if (objecto.getCodigo().trim().equalsIgnoreCase(objecto2.getCodigo().trim())) {
                    r = true;
                    break;
                }
            }
            if (r == false) {
                List2.add(objecto);
            }
        }

        return List2;
    }
    
    
     private int contarElementos(String dato, ArrayList<String> eliminarTemp) {
        int aux = 0;
        aux = eliminarTemp.stream().map((string) -> string.split(",")).filter((datos) -> (datos[1].equals(dato))).map((_item) -> 1).reduce(aux, Integer::sum);
        return aux;
    }

    private void deleteNovedad(String codigo) {
        for (Iterator<Novedades> iterator = list_novedades.iterator(); iterator.hasNext();) {
            Novedades next = iterator.next();
            if (next.getCodigo().equals(codigo) && next.getMarcacion().equals("No marco")) {
                iterator.remove();
            }
        }
    }
}
