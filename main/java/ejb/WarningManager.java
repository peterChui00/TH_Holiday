package ejb;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.PersistenceContext;
import entity.Warning;

import javax.persistence.EntityManager;

@Stateless
public class WarningManager {

    @PersistenceContext
    private EntityManager em;

    // Business Methods

    public void add(Warning Warning) {
        em.persist(Warning);
    }

    public Warning update(Warning Warning) {
        return em.merge(Warning);
    }

    public void delete(Warning Warning) {
        em.remove(em.merge(Warning));
    }

    public List<Warning> getAll() {
        return em.createNamedQuery("findAllWarning", Warning.class).getResultList();
    }

    public Warning findWarning(Long WarningID) {
        return em.find(Warning.class, WarningID);
    }

    public Warning findWarningByTrip(Long tripID) {
        Warning warning = null;
        try {
            warning = em.createNamedQuery("findWarningByTrip", Warning.class).setParameter("tripID", tripID)
                    .getSingleResult();
            return warning;
        } catch (Exception e) {
            return warning;
        }

    }
}
