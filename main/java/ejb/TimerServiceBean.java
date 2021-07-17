/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.TimerService;
import entity.Trip;
import entity.Warning;
import javax.ejb.Timer;

/**
 *
 * @author chuik
 */
@Singleton
@Startup
public class TimerServiceBean {

    @Resource
    TimerService timerService;
    @EJB
    TripManager tripManager;
    @EJB
    WarningManager warningManager;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy"); // Date format

    // Business methods

    // Create timers for each trip
    public void setPaymentTimer(Trip trip) {
        // Timer expires 30 calendar days before the Trip departure date
        GregorianCalendar expiryDate = new GregorianCalendar();
        expiryDate.setTime(trip.getDepartureDate());
        expiryDate.add(Calendar.DAY_OF_YEAR, -30);
        Date newTimer = expiryDate.getTime();
        Timer timer = timerService.createTimer(newTimer, trip.getId() + "-remind");

    }

    public String[] getActiveTimers() {
        Collection<Timer> timers = timerService.getTimers();
        String[] results = new String[timers.size()];
        int i = 0;
        for (Timer t : timers) {
            results[i++] = dateFormat.format(t.getNextTimeout());
        }
        return results;
    }

    // Handling outstanding payment on due date
    @Timeout
    public void programmaticTimeout(Timer timer) {
        // Fetch the trip of the timer
        String timerInfo = (String) timer.getInfo();
        Long timerTripId = Long.parseLong(timerInfo.substring(0, timerInfo.lastIndexOf("-")));
        Trip trip = tripManager.findTrip(timerTripId);

        // The trip is not fully paid when the timer expired
        if (trip.getDebt() > 0) {
            // Turn on the payment overdue flag of the trip
            trip.setPaymentOverdue(true);
            tripManager.update(trip);

            // Create warning record for the trip
            Warning warning = new Warning();
            warning.setTrip(trip);
            GregorianCalendar expiryDate = new GregorianCalendar();
            expiryDate.setTime(trip.getDepartureDate());
            expiryDate.add(Calendar.DAY_OF_YEAR, -21); // Getting the expiry date
            warning.setExpiryDate(expiryDate.getTime());
            warningManager.add(warning);
        }
    }
}
