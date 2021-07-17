/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;

/**
 *
 * @author chuik
 */
@Entity
@NamedQuery(name = "findAllTrip", query = "SELECT t FROM Trip t")

public class Trip implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.DATE)
    private Date orderDate;

    @Temporal(TemporalType.DATE)
    @Future(message = "The date should be in future!")
    @NotNull
    private Date departureDate;

    @DecimalMin(value = "1.00", message = "Total amount payable should be larger than $1.")
    @NotNull
    private double totalAmountPayable;

    private boolean paymentOverdue;

    @OneToMany(mappedBy = "trip")
    private List<Passenger> passengers = new ArrayList<Passenger>();

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "fk_paymentRecord_id")
    private List<PaymentRecord> paymentRecords = new ArrayList<PaymentRecord>();

    @OneToOne(mappedBy = "trip")
    private Warning warning;

    // Getters & setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
    }

    public double getTotalAmountPayable() {
        return totalAmountPayable;
    }

    public void setTotalAmountPayable(double totalAmountPayable) {
        this.totalAmountPayable = totalAmountPayable;
    }

    public boolean isPaymentOverdue() {
        return paymentOverdue;
    }

    public void setPaymentOverdue(boolean paymentOverdue) {
        this.paymentOverdue = paymentOverdue;
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public List<PaymentRecord> getPaymentRecords() {
        return paymentRecords;
    }

    // Other utils
    public void addPassengers(Passenger passenger) {
        this.passengers.add(passenger);
    }

    public void removePassengers(Passenger passenger) {
        this.passengers.remove(passenger);
    }

    public void addPaymentRecord(PaymentRecord paymentRecord) {
        this.paymentRecords.add(paymentRecord);
    }

    public void removePaymentRecord(PaymentRecord paymentRecord) {
        this.paymentRecords.remove(paymentRecord);
    }

    public double getDebt() {
        double debt = totalAmountPayable;
        if (paymentRecords.size() > 0) {
            for (PaymentRecord pr : paymentRecords) {
                debt -= pr.getAmountPaid();
            }
        }
        if (debt < 0) {
            debt = 0;
        }
        return debt;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Trip)) {
            return false;
        }
        Trip other = (Trip) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Trip[ id=" + id + " ]";
    }
}
