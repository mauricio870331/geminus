package Control.Hilos;

import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.JobDetailImpl;

/**
 * Esta clase se utiliza para inicializar Job del sistema , los cuales
 * requerimios que inicien en un tiempo especificado
 *
 * @author: Juan David Castrillon
 * @version: 11/12/2017
 */
public class Mihilo {

    /**
     * Metodo constructor del hilo , se utiliza para inicializar los Jobs.
     *
     * @throws java.lang.Exception
     */
    public Mihilo() throws Exception {
        StdSchedulerFactory sf = new StdSchedulerFactory();
        Scheduler s = sf.getScheduler();

        JobDetail traspaso = new JobDetailImpl("traspaso", "grupo1", JobMarcaciones.class);
        JobDetail Redondeo = new JobDetailImpl("redondeo", "grupo1", JobRedondeo.class);

        Trigger triggerTraspaso = TriggerBuilder
                .newTrigger()
                .withIdentity("traspaso", "group1")
                .withSchedule(
                        SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInHours(8).repeatForever())
                .build();

        Trigger triggerRedondeo = TriggerBuilder
                .newTrigger()
                .withIdentity("redondeo", "group1")
                .withSchedule(
                        SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInHours(10).repeatForever())
                .build();

        s.scheduleJob(traspaso, triggerTraspaso);
        s.scheduleJob(Redondeo, triggerRedondeo);
        s.start();
    }

}
