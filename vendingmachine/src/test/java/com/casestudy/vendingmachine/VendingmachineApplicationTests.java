package com.casestudy.vendingmachine;

import com.casestudy.vendingmachine.model.Product;
import com.casestudy.vendingmachine.model.VendingMachine;
import com.casestudy.vendingmachine.repository.ProductRepository;
import com.casestudy.vendingmachine.repository.VendingMachineRepository;
import com.casestudy.vendingmachine.service.Product.ProductService;
import com.casestudy.vendingmachine.service.VendingMachine.VendingMachineService;
import com.casestudy.vendingmachine.utilities.UnitMoney;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertNotEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
class VendingmachineApplicationTests {

	@Autowired
	private ProductService productService;
	@Autowired
	private VendingMachineService vendingMachineService;

	@MockBean
	private ProductRepository productRepository;
	@MockBean
	private VendingMachineRepository vendingMachineRepository;

	private Product getMockProduct(int id, String name, double price, int stock){
		Product testProduct = new Product();
		testProduct.setId(id);testProduct.setName(name);testProduct.setPrice(price);testProduct.setStock(stock);
		return testProduct;
	}

	private VendingMachine getMockVendingMachine(int machineID, double currentMoneyToProcess, double totalMoneyInCase, double temperatureInside){
		VendingMachine vendingMachine = new VendingMachine();
		vendingMachine.setMachineID(machineID);
		vendingMachine.setCurrentMoneyToProcess(currentMoneyToProcess);
		vendingMachine.setTotalMoneyInCase(totalMoneyInCase);
		vendingMachine.setTemperatureInside(temperatureInside);
		return vendingMachine;
	}

    /*
	Product service tests
	*/

	@Test
	void getAllProductsTest() {
		Product p1 = getMockProduct(0, "p1", 10, 20);
		Product p2 = getMockProduct(1, "p2", 20, 30);
		when(productRepository.findAll()).thenReturn(Stream.of(
				p1, p2
		).collect(Collectors.toList()));
		assertEquals("p1",2, productService.getAllProducts().size());

	}

	@Test
	void decreaseProductStocksByIDTest() {
		Product p1 = getMockProduct(0, "p1", 5, 15);
		when(productRepository.existsById(0)).thenReturn(true);
		when(productRepository.findById(0)).thenReturn(Optional.of(p1));
		when(productRepository.save(p1)).thenReturn(p1);

		assertEquals("p1", p1, productService.decreaseProductStocksByID(p1.getId()));
	}

	@Test
	void addProductTest() {
		Product p1 = getMockProduct(0, "p1", 5, 15);
		when(productRepository.existsById(0)).thenReturn(true);
		when(productRepository.findById(0)).thenReturn(Optional.of(p1));
		when(productRepository.save(p1)).thenReturn(p1);

		assertEquals("p1", p1, productService.addProduct(p1));
	}

	@Test
	void addStocksToProductTest() {
		Product p1 = getMockProduct(0, "p1", 5, 15);
		when(productRepository.existsById(0)).thenReturn(true);
		when(productRepository.findById(0)).thenReturn(Optional.of(p1));
		when(productRepository.save(p1)).thenReturn(p1);
		Map<String, Object> stringProductMap = new HashMap<>();
		stringProductMap.put("stock", 10);

		assertEquals("p1", p1, productService.addStocksToProduct(p1.getId(), stringProductMap));
	}

	@Test
	void resetAllStocksAndPricesTest() {
		Product p1 = getMockProduct(1, "p1", 5, 15);
		when(productRepository.findAll()).thenReturn(Stream.of(
				p1
		).collect(Collectors.toList()));
		when(productRepository.save(p1)).thenReturn(p1);

		productService.resetAllStocksAndPrices();

		assertNotEquals("price", p1.getPrice(), 5);
		assertNotEquals("stock", p1.getStock(), 15);
	}

	@Test
	void changeProductPriceTest() {
		Product p1 = getMockProduct(0, "p1", 5, 15);
		when(productRepository.existsById(0)).thenReturn(true);
		when(productRepository.findById(0)).thenReturn(Optional.of(p1));
		when(productRepository.save(p1)).thenReturn(p1);
		Map<String, Object> stringProductMap = new HashMap<>();
		stringProductMap.put("price", 10);

		assertEquals("p1", 10.0, productService.changeProductPrice(p1.getId(), stringProductMap));
	}

	@Test
	void findProductByIDTest() {
		Product p1 = getMockProduct(0, "p1", 5, 15);
		when(productRepository.existsById(0)).thenReturn(true);
		when(productRepository.findById(0)).thenReturn(Optional.of(p1));

		assertEquals("p1", p1, productService.findProductByID(p1.getId()));
	}

    /*
	Vending machine service tests
	*/


	@Test
	void createMachineInstanceTest() {
		VendingMachine vendingMachine = getMockVendingMachine(1, 50, 50, 20);

		List<VendingMachine> vendingMachineList = new ArrayList<>();
		when(vendingMachineRepository.findAll()).thenReturn(vendingMachineList);
		when(vendingMachineRepository.save(any(VendingMachine.class))).thenReturn(vendingMachine);

		// Creates just one instance if there is not any
		assertEquals("machine", vendingMachine, vendingMachineService.createMachineInstance());
		vendingMachineList.add(vendingMachine); // Find all will return this list, as it's created
		// It must be null if you try to create another vending machine, if there already exists
		assertEquals("machine", null, vendingMachineService.createMachineInstance());
	}

	@Test
	void getAllMachinesTest() {
		VendingMachine vendingMachine = getMockVendingMachine(1, 50, 50, 20);
		when(vendingMachineRepository.findAll()).thenReturn(Stream.of(
				vendingMachine
		).collect(Collectors.toList()));

		assertEquals("size",1, vendingMachineService.getAllMachines().size());
	}

	@Test
	void getVendingMachineInstanceTest() {
		VendingMachine vendingMachine = getMockVendingMachine(1, 50, 50, 20);
		when(vendingMachineRepository.findAll()).thenReturn(Stream.of(
				vendingMachine
		).collect(Collectors.toList()));

		assertEquals("machine",vendingMachine, vendingMachineService.getVendingMachineInstance());
	}

	@Test
	void updateVendingMachineInstanceTest() {
		VendingMachine vendingMachine = getMockVendingMachine(1, 50, 50, 20);
		when(vendingMachineRepository.existsById(0)).thenReturn(true);
		when(vendingMachineRepository.findById(0)).thenReturn(Optional.of(vendingMachine));
		when(vendingMachineRepository.save(vendingMachine)).thenReturn(vendingMachine);
		when(vendingMachineRepository.findAll()).thenReturn(Stream.of(
				vendingMachine
		).collect(Collectors.toList()));

		Map<String, Object> stringProductMap = new HashMap<>();
		stringProductMap.put("totalMoneyInCase", 100);

		assertEquals("machine", vendingMachine, vendingMachineService.updateVendingMachineInstance(stringProductMap));
	}

	@Test
	void basicUpdateVendingMachineInstanceTest() {
		VendingMachine vendingMachine = getMockVendingMachine(1, 30, 20, 10);
		when(vendingMachineRepository.existsById(0)).thenReturn(true);
		when(vendingMachineRepository.findById(0)).thenReturn(Optional.of(vendingMachine));
		when(vendingMachineRepository.save(vendingMachine)).thenReturn(vendingMachine);

		assertEquals("machine",vendingMachine, vendingMachineService.updateVendingMachineInstance(vendingMachine));
	}

	// Must cool 0.1 degrees every time it's called because dummy cooling implementation is decided to work that way.
	@Test
	void coolingTest() {
		VendingMachine vendingMachine = getMockVendingMachine(1, 30, 20, 10);
		when(vendingMachineRepository.existsById(0)).thenReturn(true);
		when(vendingMachineRepository.findById(0)).thenReturn(Optional.of(vendingMachine));
		when(vendingMachineRepository.save(vendingMachine)).thenReturn(vendingMachine);
		when(vendingMachineRepository.findAll()).thenReturn(Stream.of(
				vendingMachine
		).collect(Collectors.toList()));

		assertEquals("machine", vendingMachine.getTemperatureInside() - 0.1, vendingMachineService.cooling());
	}

	@Test
	void returnProductToUserByIdTest() {
		VendingMachine vendingMachine = getMockVendingMachine(1, 30, 20, 10);
		Product p1 = getMockProduct(0, "p1", 5, 15);
		when(productRepository.existsById(0)).thenReturn(true);
		when(productRepository.findById(0)).thenReturn(Optional.of(p1));
		when(productRepository.save(p1)).thenReturn(p1);

		when(vendingMachineRepository.existsById(0)).thenReturn(true);
		when(vendingMachineRepository.findById(0)).thenReturn(Optional.of(vendingMachine));
		when(vendingMachineRepository.save(vendingMachine)).thenReturn(vendingMachine);
		when(vendingMachineRepository.findAll()).thenReturn(Stream.of(
				vendingMachine
		).collect(Collectors.toList()));

		assertEquals("machine", vendingMachine.getCurrentMoneyToProcess() - p1.getPrice(), vendingMachineService.returnProductToUserById(p1.getId()));
	}

	@Test
	void takeRefundTest() {
		VendingMachine vendingMachine = getMockVendingMachine(1, 30, 20, 10);

		when(vendingMachineRepository.existsById(0)).thenReturn(true);
		when(vendingMachineRepository.findById(0)).thenReturn(Optional.of(vendingMachine));
		when(vendingMachineRepository.save(vendingMachine)).thenReturn(vendingMachine);
		when(vendingMachineRepository.findAll()).thenReturn(Stream.of(
				vendingMachine
		).collect(Collectors.toList()));

		assertEquals("machine", vendingMachine.getCurrentMoneyToProcess(), vendingMachineService.takeRefund());
	}

	@Test
	void acceptMoneyFromUserTest() {
		VendingMachine vendingMachine = getMockVendingMachine(1, 30, 20, 10);

		when(vendingMachineRepository.existsById(0)).thenReturn(true);
		when(vendingMachineRepository.findById(0)).thenReturn(Optional.of(vendingMachine));
		when(vendingMachineRepository.save(vendingMachine)).thenReturn(vendingMachine);
		when(vendingMachineRepository.findAll()).thenReturn(Stream.of(
				vendingMachine
		).collect(Collectors.toList()));

		assertEquals("machine", vendingMachine.getCurrentMoneyToProcess() + 5, vendingMachineService.acceptMoneyFromUser(UnitMoney.FIVE));
	}

	@Test
	void putMoneyTest() {
		VendingMachine vendingMachine = getMockVendingMachine(1, 30, 20, 10);

		when(vendingMachineRepository.existsById(0)).thenReturn(true);
		when(vendingMachineRepository.findById(0)).thenReturn(Optional.of(vendingMachine));
		when(vendingMachineRepository.save(vendingMachine)).thenReturn(vendingMachine);
		when(vendingMachineRepository.findAll()).thenReturn(Stream.of(
				vendingMachine
		).collect(Collectors.toList()));

		assertEquals("machine", vendingMachine.getCurrentMoneyToProcess() + 10, vendingMachineService.putMoney(10));
	}

	@Test
	void resetVendingMachineTest() {
		VendingMachine vendingMachine = getMockVendingMachine(1, 30, 20, 10);

		when(vendingMachineRepository.existsById(0)).thenReturn(true);
		when(vendingMachineRepository.findById(0)).thenReturn(Optional.of(vendingMachine));
		when(vendingMachineRepository.save(vendingMachine)).thenReturn(vendingMachine);
		when(vendingMachineRepository.findAll()).thenReturn(Stream.of(
				vendingMachine
		).collect(Collectors.toList()));

		vendingMachineService.resetVendingMachine();

		assertEquals("machine", 0.0, vendingMachineService.getVendingMachineInstance().getCurrentMoneyToProcess());
		assertEquals("machine", 0.0, vendingMachineService.getVendingMachineInstance().getTotalMoneyInCase());
	}

	@Test
	void collectMoneyTest() {
		VendingMachine vendingMachine = getMockVendingMachine(1, 30, 20, 10);

		when(vendingMachineRepository.existsById(0)).thenReturn(true);
		when(vendingMachineRepository.findById(0)).thenReturn(Optional.of(vendingMachine));
		when(vendingMachineRepository.save(vendingMachine)).thenReturn(vendingMachine);
		when(vendingMachineRepository.findAll()).thenReturn(Stream.of(
				vendingMachine
		).collect(Collectors.toList()));

		assertEquals("machine", vendingMachine.getTotalMoneyInCase(), vendingMachineService.collectMoney());
	}

	@Test
	void requestProductByIDTest() {
		VendingMachine vendingMachine = getMockVendingMachine(1, 30, 20, 10);
		Product p1 = getMockProduct(0, "p1", 5, 15);
		when(productRepository.existsById(0)).thenReturn(true);
		when(productRepository.findById(0)).thenReturn(Optional.of(p1));
		when(productRepository.save(p1)).thenReturn(p1);

		when(vendingMachineRepository.existsById(0)).thenReturn(true);
		when(vendingMachineRepository.findById(0)).thenReturn(Optional.of(vendingMachine));
		when(vendingMachineRepository.save(vendingMachine)).thenReturn(vendingMachine);
		when(vendingMachineRepository.findAll()).thenReturn(Stream.of(
				vendingMachine
		).collect(Collectors.toList()));

		assertEquals("machine", vendingMachine.getCurrentMoneyToProcess() - p1.getPrice(), vendingMachineService.requestProductByID(p1.getId()));
	}



}
