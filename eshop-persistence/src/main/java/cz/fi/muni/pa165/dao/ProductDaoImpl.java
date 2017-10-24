package cz.fi.muni.pa165.dao;

import cz.fi.muni.pa165.entity.Category;
import cz.fi.muni.pa165.entity.Product;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class ProductDaoImpl implements ProductDao {

	@PersistenceContext
	private EntityManager em;

	@Override
	public void create(Product p) {
		em.persist(p);
	}

	@Override
	public List<Product> findAll() {
		return em.createQuery("select p from Product p", Product.class)
				.getResultList();
	}

	@Override
	public Product findById(Long id) {
		return em.find(Product.class, id);
	}

	@Override
	public void remove(Product p) {
		em.remove(em.contains(p) ? p : em.merge(p));
	}


	@Override
	public List<Product> findByName(String otherName) {
		try {
			return em
					.createQuery("select p from Product p where name = :name", Product.class)
					.setParameter("name", otherName)
					.getResultList();
		} catch (NoResultException nre) {
			return null;
		}
	}

}
