/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import ejb.PassengerManager;
import entity.Passenger;
import entity.Trip;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import util.JsfMessage;

/**
 *
 * @author chuik
 */
@Named
@SessionScoped
public class PassengerManagedBean implements Serializable {

    @EJB
    private PassengerManager passengerManager;
    private DataModel rows; // Row data of all passengers
    private String status; // Mode of the current page
    Passenger passenger; // Current passenger

    public PassengerManagedBean() {
        rows = null;
        status = null;
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
            rows = new ListDataModel(passengerManager.getAll());
        }
        return rows;
    }

    public Passenger getPassenger() {
        // Return the current passenger or the newly added passenger
        if (passenger == null) {
            passenger = new Passenger();
            status = "Addition";
        }
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    // ---- Methods for preparation and redirection ----

    public String prepareAdd() {
        passenger = new Passenger();
        status = "Addition";
        return "passengerInput";
    }

    public String prepareList() {
        rows = null;
        status = null;
        return "passengerList";
    }

    public String prepareDetail() {
        passenger = (Passenger) getRows().getRowData();
        status = null;
        return "passengerDetail";
    }

    public String prepareEdit() {
        passenger = (Passenger) getRows().getRowData();
        status = null;
        return "passengerInput";
    }

    public String prepareDelete() {
        status = "Deletion";
        JsfMessage.addWarningMessage("Warning: This action is irreversible.");
        passenger = (Passenger) getRows().getRowData();
        return "passengerDetail";
    }

    public String cancelDelete() {
        status = null;
        return "passengerList";
    }

    public void prepareJoinTrip() {
        passenger = (Passenger) getRows().getRowData();
    }

    // ----- Methods for CRUD Operations -----

    public String save() {
        if (status == "Addition") {
            // Add passenger
            passengerManager.add(passenger);
            JsfMessage.addSuccessMessage("Addition successful");
            return prepareAdd();
        } else {
            // Update passenger
            passengerManager.update(passenger);
            JsfMessage.addSuccessMessage("Update successful");
            return "passengerDetail";
        }
    }

    public String deletePassenger() {
        passengerManager.delete(passenger);
        passenger = null;
        status = null;
        JsfMessage.addSuccessMessage("Deletion successful.");
        return prepareList();
    }

    // Join / Quit trip

    public String joinTrip(Trip trip) {
        passenger.setTrip(trip);
        passengerManager.update(passenger);
        return prepareList();
    };

    public String quitTrip(Passenger tripPassenger) {
        tripPassenger.setTrip(null);
        passengerManager.update(tripPassenger);
        return "tripDetail";
    };

}
