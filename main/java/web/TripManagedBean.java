/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import java.util.Date;
import javax.inject.Named;
import ejb.PassengerManager;
import ejb.PaymentRecordManager;
import ejb.TimerServiceBean;
import ejb.TripManager;
import ejb.WarningManager;
import entity.Passenger;
import entity.PaymentRecord;
import entity.Trip;
import entity.Warning;
import util.JsfMessage;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import java.io.Serializable;

/**
 *
 * @author chuik
 */
@Named
@SessionScoped
public class TripManagedBean implements Serializable {

    @EJB
    private TripManager tripManager;
    @EJB
    private PassengerManager passengerManager;
    @EJB
    private PaymentRecordManager paymentRecordManager;
    @EJB
    private WarningManager warningManager;
    @EJB
    private TimerServiceBean timerServiceBean;

    private DataModel rows; // Row data of all trips
    private DataModel passengerRows; // Row data of all trip passengers
    private DataModel paymentRecordRows; // Row data of all trip payment record
    private String status; // Mode of the current page
    Trip trip; // Current trip
    Passenger tripPassenger; // The seleted passenger from the DataModel
    PaymentRecord pr; // The payment record for the current trip

    public TripManagedBean() {
        rows = null;
        passengerRows = null;
    }

    // Getters and setters

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public DataModel getRows() {
        if (rows == null) { // Data model need to be created
            rows = new ListDataModel(tripManager.getAll());
        }
        return rows;
    }

    public DataModel getPassengerRows() {
        passengerRows = new ListDataModel(passengerManager.getTripPassengers(trip.getId()));
        return passengerRows;
    }

    public DataModel getPaymentRecordRows() {
        paymentRecordRows = new ListDataModel(paymentRecordManager.getAll(trip.getId()));
        return paymentRecordRows;
    }

    public Trip getTrip() {
        // Return the current trip or the newly added trip
        if (trip == null) {
            trip = new Trip();
            status = "Addition";
        }
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public Passenger getTripPassenger() {
        tripPassenger = (Passenger) getPassengerRows().getRowData();
        return tripPassenger;
    }

    public void setTripPassenger(Passenger tripPassenger) {
        this.tripPassenger = tripPassenger;
    }

    public PaymentRecord getPr() {
        // Return the current trip or the newly added trip
        if (pr == null) {
            pr = new PaymentRecord();
        }
        return pr;
    }

    public void setPr(PaymentRecord paymentRecord) {
        this.pr = paymentRecord;
    }

    // ---- Methods for preparation and redirection ----

    public String prepareAdd() {
        trip = new Trip();
        status = "Addition";
        return "tripInput";
    }

    public String prepareList() {
        rows = null; // Force data model to be recreated
        passengerRows = null;
        status = null;
        return "tripList";
    }

    public String prepareDetail() {
        trip = (Trip) getRows().getRowData();
        status = null;
        return "tripDetail";
    }

    public String prepareEdit() {
        trip = (Trip) getRows().getRowData();
        status = null;
        return "tripInput";
    }

    public String prepareDelete() {
        status = "Deletion";
        JsfMessage.addWarningMessage("Warning: This action is irreversible.");
        trip = (Trip) getRows().getRowData();
        return "tripDetail";
    }

    public String cancelDelete() {
        status = null;
        return "tripList";
    }

    public String prepareJoinTrip() {
        rows = null;
        status = "JoinTrip";
        return "tripList";
    }

    public String preparePayment() {
        trip = (Trip) getRows().getRowData();
        return "tripPayment";
    }

    // ----- Methods for CRUD Operations -----

    // ---- Trip operations ----

    public String save() {
        if (status == "Addition") {
            // Add new trip
            long millis = System.currentTimeMillis();
            trip.setOrderDate(new Date(millis));
            tripManager.add(trip);
            JsfMessage.addSuccessMessage("Addition successful");
            // Create timers for trip
            timerServiceBean.setPaymentTimer(trip);
            return prepareAdd();
        } else {
            // Edit trip
            tripManager.update(trip);
            JsfMessage.addSuccessMessage("Update successful");
            return "tripDetail";
        }
    }

    // Delete trip
    public String deleteTrip() {
        // Remove existing warning record of the trip
        Warning warning = warningManager.findWarningByTrip(trip.getId());
        if (warning != null) {
            warningManager.delete(warning);
        }

        // Remove trip reference from passenger
        for (Passenger passenger : trip.getPassengers()) {
            passenger.setTrip(null);
            passengerManager.update(passenger);
        }
        tripManager.delete(trip);
        trip = null;
        status = null;
        JsfMessage.addSuccessMessage("Deletion successful.");
        return prepareList();
    }

    // ---- Trip-passenger operations ----

    // Add passenger to trip
    public void joinTrip(Passenger passenger) {
        trip = (Trip) getRows().getRowData();
        trip.addPassengers(passenger);
        tripManager.update(trip);
        status = null;
        JsfMessage.addSuccessMessage("Passenger successfully added to the trip.");
    };

    // Remove passenger from trip
    public void quitTrip() {
        trip.removePassengers(tripPassenger);
        tripManager.update(trip);
        JsfMessage.addSuccessMessage("Passenger successfully removed from the trip.");
    };

    // ---- Trip-payment operations ----

    public String checkFullyPaid() {
        if (trip.getDebt() <= 0) {
            rejectPayment();
        } else {
            makePayment();
            if (trip.getDebt() <= 0) {
                trip.setPaymentOverdue(false);
                tripManager.update(trip);
                warningManager.delete(warningManager.findWarningByTrip(trip.getId()));
            }
        }
        return "tripPayment";
    }

    public void rejectPayment() {
        JsfMessage.addWarningMessage("Payment failed. The trip is fully paid.");
        pr = null;
    }

    public void makePayment() {
        pr.setTrip(trip);
        paymentRecordManager.add(pr);
        JsfMessage.addSuccessMessage("Payment successfully processed.");
        trip.addPaymentRecord(pr);
        tripManager.update(trip);
        pr = null;
    }

}
