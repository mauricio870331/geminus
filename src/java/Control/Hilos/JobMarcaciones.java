package Control.Hilos;

import Control.Email;
import static Control.procesoMarcacionesBiometricoGeminus.InicioProceso;
import java.util.Date;
import java.util.Locale;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Esta clase actua como trabajo del sistema , para realizar el proceso de
 * Traspaso de Marcaciones al sistema Geminus
 *
 * @author: Juan David Castrillon
 * @version: 11/12/2017
 */
public class JobMarcaciones implements Job {

    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        System.out.printf(new Locale("es", "MX"), "%tc Ejecutando tarea...%n", new java.util.Date());
        InicioProceso(new Date());
        Email mail = new Email();
        mail.getMailNovedades(" Se corrio la tarea de Traspaso marcaciones " + new Date());
    }
}
