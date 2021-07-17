/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb;

import entity.Trip;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author chuik
 */
@Stateless
public class TripManager {

    @PersistenceContext
    private EntityManager em;

    // Business Methods

    public void add(Trip trip) {
        em.persist(trip);
    }

    public Trip update(Trip trip) {
        return em.merge(trip);
    }

    public void delete(Trip trip) {
        em.remove(em.merge(trip));
    }

    public List<Trip> getAll() {
        return em.createNamedQuery("findAllTrip", Trip.class).getResultList();
    }

    public Trip findTrip(Long tripID) {
        return em.find(Trip.class, tripID);
    }

}
