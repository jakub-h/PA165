package cz.fi.muni.pa165.tasks;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.validation.ConstraintViolationException;

import org.aspectj.lang.annotation.Before;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import cz.fi.muni.pa165.PersistenceSampleApplicationContext;
import cz.fi.muni.pa165.entity.Category;
import cz.fi.muni.pa165.entity.Product;

import static org.assertj.core.api.Assertions.assertThat;


@ContextConfiguration(classes = PersistenceSampleApplicationContext.class)
public class Task02 extends AbstractTestNGSpringContextTests {

	private Category electro;
	private Category kitchen;
	private Product flashlight;
	private Product robot;
	private Product plate;

	@BeforeClass
	private void prepare() {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();

		electro = new Category();
		electro.setName("Electro");
		em.persist(electro);

		kitchen = new Category();
		kitchen.setName("Kitchen");
		em.persist(kitchen);

		flashlight = new Product();
		flashlight.setName("Flashlight");
		flashlight.addCategory(electro);
		electro.addProduct(flashlight);
		em.persist(flashlight);

		robot = new Product();
		robot.setName("Kitchen Robot");
		robot.addCategory(kitchen);
		kitchen.addProduct(robot);
		robot.addCategory(electro);
		electro.addProduct(robot);
		em.persist(flashlight);

		plate = new Product();
		plate.setName("Plate");
		plate.addCategory(kitchen);
		kitchen.addProduct(plate);
		em.persist(plate);

		em.getTransaction().commit();
		em.close();
	}

	@PersistenceUnit
	private EntityManagerFactory emf;

	
	private void assertContainsCategoryWithName(Set<Category> categories,
			String expectedCategoryName) {
		for(Category cat: categories){
			if (cat.getName().equals(expectedCategoryName))
				return;
		}
			
		Assert.fail("Couldn't find category "+ expectedCategoryName+ " in collection "+categories);
	}
	private void assertContainsProductWithName(Set<Product> products,
			String expectedProductName) {
		
		for(Product prod: products){
			if (prod.getName().equals(expectedProductName))
				return;
		}
			
		Assert.fail("Couldn't find product "+ expectedProductName+ " in collection "+products);
	}

	@Test
	public void kitchenTest() {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		Category foundKitchen = em.find(Category.class, kitchen.getId());
		em.getTransaction().commit();
		em.close();

		assertContainsProductWithName(foundKitchen.getProducts(), "Plate");
		assertContainsProductWithName(foundKitchen.getProducts(), "Kitchen Robot");
	}

	@Test
	public void electroTest() {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		Category foundElectro = em.find(Category.class, electro.getId());
		em.getTransaction().commit();
		em.close();

		assertContainsProductWithName(foundElectro.getProducts(), "Kitchen Robot");
		assertContainsProductWithName(foundElectro.getProducts(), "Flashlight");
	}

	@Test
	public void flashlightTest() {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		Product foundFlashlight = em.find(Product.class, flashlight.getId());
		em.getTransaction().commit();
		em.close();

		assertContainsCategoryWithName(foundFlashlight.getCategories(), "Electro");
	}

	@Test
	public void kitchenRobotTest() {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		Product foundRobot = em.find(Product.class, robot.getId());
		em.getTransaction().commit();
		em.close();

		assertContainsCategoryWithName(foundRobot.getCategories(), "Electro");
		assertContainsCategoryWithName(foundRobot.getCategories(), "Kitchen");
	}

	@Test
	public void plateTest() {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		Product foundPlate = em.find(Product.class, plate.getId());
		em.getTransaction().commit();
		em.close();

		assertContainsCategoryWithName(foundPlate.getCategories(), "Kitchen");
	}

	@Test(expectedExceptions=ConstraintViolationException.class)
	public void testDoesntSaveNullName(){
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		Product product = new Product();
		em.persist(product);
		em.getTransaction().commit();
		em.close();
	}
}
