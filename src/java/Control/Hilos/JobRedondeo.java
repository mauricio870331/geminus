package Control.Hilos;

import Control.Email;
import Control.procesoRedondeo;
import java.sql.SQLException;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Esta clase actua como trabajo del sistema , para realizar el proceso de
 * redondeo de Marcaciones.
 *
 * @author: Juan David Castrillon
 * @version: 11/12/2017
 */
public class JobRedondeo implements Job {

    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        try {
            System.out.printf(new Locale("es", "MX"), "%tc Ejecutando tarea redondeo...%n", new java.util.Date());
            procesoRedondeo p = new procesoRedondeo();
            p.RedondiarEntrada(new java.util.Date());
            p.RedondiarSalida(new java.util.Date());
            p.redondiarSalidaTrasnocho(new java.util.Date());
            Email mail = new Email();
            mail.getMailNovedades(" Se corrio la tarea de redondeo de marcacions " + new Date());
        } catch (SQLException ex) {
            Logger.getLogger(JobRedondeo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
