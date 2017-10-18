package cz.fi.muni.carshop;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.util.List;
import java.util.Map;

import cz.fi.muni.carshop.exceptions.RequestedCarNotFoundException;
import org.junit.Before;
import org.junit.Test;

import org.junit.rules.ExpectedException;
import org.junit.Rule;

import cz.fi.muni.carshop.entities.Car;
import cz.fi.muni.carshop.enums.CarTypes;
import cz.fi.muni.carshop.services.CarShopStorageService;
import cz.fi.muni.carshop.services.CarShopStorageServiceImpl;

public class CarShopStorageServiceTest {

	private CarShopStorageService service;

	@Before
	public void prepare() {
		service = new CarShopStorageServiceImpl();
	}

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test()
	public void testPriceCantBeNegative() {
		// JUnit 4.11
		// thrown.expect(IllegalArgumentException.class);
		// JUnit 4.12
		thrown.reportMissingExceptionWithMessage("We expect exception on negative price").expect(IllegalArgumentException.class);

		service.addCarToStorage(new Car(Color.BLACK, CarTypes.AUDI, 2016, -1));
	}

	@Test
	public void testGetCar() {
		service.addCarToStorage(new Car(Color.BLACK, CarTypes.AUDI, 2016, 899000));

		assertTrue(service.isCarAvailable(Color.BLACK, CarTypes.AUDI).isPresent());
	}

	@Test
	public void testCarShopStorage_containsTypeForExistingCar() {
		service.addCarToStorage(new Car(Color.BLACK, CarTypes.AUDI, 2016, 899000));
		Map<CarTypes, List<Car>> cars = CarShopStorage.getInstancce().getCars();

		assertThat(cars, hasKey(CarTypes.AUDI));
	}

	// expected to fail with JUnit < 4.11
	@Test
	public void testGetCheaperCars_returnsCorrectResult() {
		service.addCarToStorage(new Car(Color.BLACK, CarTypes.AUDI, 2016, 899000));
		service.addCarToStorage(new Car(Color.BLACK, CarTypes.AUDI, 2016, 889000));
		service.addCarToStorage(new Car(Color.WHITE, CarTypes.AUDI, 2016, 859000));
		service.addCarToStorage(new Car(Color.BLUE, CarTypes.AUDI, 2016, 909000));

		assertThat(service.getCheaperCarsOfSameTypeAndYear(new Car(Color.BLACK, CarTypes.AUDI, 2016, 900000)),
				hasSize(3));

	}

	@Test
	public void testSellCar_happyScenario() {
		Map<CarTypes, List<Car>> cars = CarShopStorage.getInstancce().getCars();
		Car audi = new Car(Color.BLACK, CarTypes.AUDI, 2016, 100);
		service.addCarToStorage(audi);
		assertTrue(cars.size() == 1);
		try {
			service.sellCar(audi);
		} catch (RequestedCarNotFoundException e) {
			e.printStackTrace();
		}
		assertTrue(cars.containsKey(CarTypes.AUDI));
		assertTrue(!cars.get(CarTypes.AUDI).contains(audi));
	}

	@Test
	public void testSellCar_carNotFound() throws RequestedCarNotFoundException {
		Car audi = new Car(Color.BLACK, CarTypes.AUDI, 2016, 100);
		thrown.reportMissingExceptionWithMessage("Car not found.").expect(RequestedCarNotFoundException.class);
		service.sellCar(audi);
	}
}
