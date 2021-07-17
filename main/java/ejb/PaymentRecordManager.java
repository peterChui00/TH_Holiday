/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb;

import entity.PaymentRecord;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author chuik
 */
@Stateless
public class PaymentRecordManager {

    @PersistenceContext
    private EntityManager em;

    // Business Methods

    public void add(PaymentRecord paymentRecord) {
        em.persist(paymentRecord);
    }

    public PaymentRecord update(PaymentRecord paymentRecord) {
        return em.merge(paymentRecord);
    }

    public void delete(PaymentRecord paymentRecord) {
        em.remove(em.merge(paymentRecord));
    }

    public List<PaymentRecord> getAll(Long tripID) {
        return em.createNamedQuery("findAllPaymentRecord", PaymentRecord.class).setParameter("tripID", tripID)
                .getResultList();
    }

}
