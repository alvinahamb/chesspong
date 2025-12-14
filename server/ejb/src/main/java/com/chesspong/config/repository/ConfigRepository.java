package com.chesspong.config.repository;

import com.chesspong.config.entity.Config;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@ApplicationScoped
public class ConfigRepository {
    @PersistenceContext(unitName = "ConfigPU")
    private EntityManager em;

    public void save(Config config) {
        em.persist(config);
    }

    public Config find(int id) {
        return em.find(Config.class, id);
    }

    public List<Config> findAll() {
        return em.createQuery("SELECT c FROM Config c", Config.class).getResultList();
    }

    public Config update(Config config) {
        return em.merge(config);
    }

    public boolean delete(int id) {
        Config c = em.find(Config.class, id);
        if (c != null) {
            em.remove(c);
            return true;
        }
        return false;
    }

    public Config findLast() {
        List<Config> results = em.createQuery("SELECT c FROM Config c ORDER BY c.id DESC", Config.class)
                                 .setMaxResults(1)
                                 .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
}