/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import ejb.WarningManager;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import ejb.TimerServiceBean;
import ejb.TripManager;
import entity.Warning;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author chuik
 */

@Named
@SessionScoped
public class paymentWarningBean implements Serializable {

    @EJB
    private TimerServiceBean timerServiceBean;
    @EJB
    private TripManager tripManager;
    @EJB
    private WarningManager warningManager;

    private List<Warning> warnings = new ArrayList<Warning>(); // Warning messages

    public paymentWarningBean() {
    }

    // Getters and setters

    public void setwarnings(List<Warning> warnings) {
        this.warnings = warnings;
    }

    public List<Warning> getwarnings() {
        return warnings;
    }

    public long getRemainingDate(Date expiryDate) {
        LocalDateTime currentdate = LocalDateTime.now();
        LocalDateTime expiry = expiryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        long daysBetween = Duration.between(currentdate, expiry).toDays();
        return daysBetween;
    }

    // ---- Methods for preparation and redirection ----

    public String prepareWarning() {
        warnings.clear();
        warnings = warningManager.getAll();
        return "greeting";
    }

    public String prepareTripDetail() {
        return "tripDetail";
    }
}
