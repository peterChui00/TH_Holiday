/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb;

import entity.Passenger;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author chuik
 */
@Stateless
public class PassengerManager {

    @PersistenceContext
    private EntityManager em;

    // Business Methods

    public void add(Passenger passenger) {
        em.persist(passenger);
    }

    public Passenger update(Passenger passenger) {
        return em.merge(passenger);
    }

    public void delete(Passenger passenger) {
        em.remove(em.merge(passenger));
    }

    public List<Passenger> getAll() {
        return em.createNamedQuery("findAllPassenger", Passenger.class).getResultList();
    }

    public List<Passenger> getTripPassengers(Long tripID) {
        return em.createNamedQuery("findTripPassenger", Passenger.class).setParameter("tripID", tripID).getResultList();
    }
}
