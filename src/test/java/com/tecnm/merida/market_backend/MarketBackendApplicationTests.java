package com.tecnm.merida.market_backend;

import com.tecnm.merida.market_backend.domain.*;
import com.tecnm.merida.market_backend.domain.dto.AuthenticationRequest;
import com.tecnm.merida.market_backend.domain.dto.AuthenticationResponse;
import com.tecnm.merida.market_backend.web.controller.*;
import com.tecnm.merida.market_backend.web.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MarketBackendApplicationTests {

	@Autowired
	private ProductController productController;

	@Autowired
	private CategoryController categoryController;

	@Autowired
	private ClientController clientController;

	@Autowired
	private PurchaseController purchaseController;

	@Autowired
	private AuthController authController;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	void contextLoads() {
		assertNotNull(productController);
		assertNotNull(categoryController);
		assertNotNull(clientController);
		assertNotNull(purchaseController);
		assertNotNull(authController);
		assertNotNull(jwtUtil);
	}

	@Test
	void testJwtLoginAndValidation() {
		// Create a client with plain password "1234" (ClientService encrypts it with BCrypt)
		Client newClient = new Client();
		newClient.setClientId("8888888");
		newClient.setName("AuthUser");
		newClient.setLastname("Test");
		newClient.setPhone(new BigDecimal("9991112233"));
		newClient.setAddress("Calle 60");
		newClient.setEmail("auth@test.com");
		newClient.setPassword("1234");
		ResponseEntity<Client> savedRes = clientController.save(newClient);
		assertEquals(HttpStatus.CREATED, savedRes.getStatusCode());
		assertTrue(passwordEncoder.matches("1234", savedRes.getBody().getPassword()));

		// Login with valid credentials
		AuthenticationRequest loginReq = new AuthenticationRequest();
		loginReq.setUsername("8888888");
		loginReq.setPassword("1234");
		ResponseEntity<AuthenticationResponse> loginRes = authController.login(loginReq);
		assertEquals(HttpStatus.OK, loginRes.getStatusCode());
		assertNotNull(loginRes.getBody());
		String token = loginRes.getBody().getJwt();
		assertNotNull(token);
		assertTrue(jwtUtil.validateToken(token));
		assertEquals("8888888", jwtUtil.extractUsername(token));

		// Login with invalid password
		AuthenticationRequest badReq = new AuthenticationRequest();
		badReq.setUsername("8888888");
		badReq.setPassword("wrong");
		ResponseEntity<AuthenticationResponse> badRes = authController.login(badReq);
		assertEquals(HttpStatus.UNAUTHORIZED, badRes.getStatusCode());
	}

	@Test
	void testCategoryCrud() {
		// GET all
		ResponseEntity<List<Category>> allRes = categoryController.getAll();
		assertEquals(HttpStatus.OK, allRes.getStatusCode());
		assertFalse(allRes.getBody().isEmpty());

		// POST create
		Category newCat = new Category();
		newCat.setCategory("Congelados Test");
		newCat.setActive(true);
		ResponseEntity<Category> saveRes = categoryController.save(newCat);
		assertEquals(HttpStatus.CREATED, saveRes.getStatusCode());
		Integer catId = saveRes.getBody().getCategoryId();
		assertNotNull(catId);

		// GET by id
		ResponseEntity<Category> getRes = categoryController.getCategory(catId);
		assertEquals(HttpStatus.OK, getRes.getStatusCode());
		assertEquals("Congelados Test", getRes.getBody().getCategory());

		// PUT update
		Category updateCat = new Category();
		updateCat.setCategory("Congelados Actualizado");
		updateCat.setActive(false);
		ResponseEntity<Category> updateRes = categoryController.update(catId, updateCat);
		assertEquals(HttpStatus.OK, updateRes.getStatusCode());
		assertEquals("Congelados Actualizado", updateRes.getBody().getCategory());
		assertFalse(updateRes.getBody().isActive());

		// DELETE
		ResponseEntity<Void> delRes = categoryController.delete(catId);
		assertEquals(HttpStatus.OK, delRes.getStatusCode());
		assertEquals(HttpStatus.NOT_FOUND, categoryController.getCategory(catId).getStatusCode());
	}

	@Test
	void testClientCrud() {
		// GET all
		ResponseEntity<List<Client>> allRes = clientController.getAll();
		assertEquals(HttpStatus.OK, allRes.getStatusCode());
		assertFalse(allRes.getBody().isEmpty());

		// POST create
		Client newClient = new Client();
		newClient.setClientId("9999999");
		newClient.setName("Juan");
		newClient.setLastname("Perez");
		newClient.setPhone(new BigDecimal("9991234567"));
		newClient.setAddress("Calle 60 #100");
		newClient.setEmail("juan@perez.com");
		newClient.setPassword("1234");
		ResponseEntity<Client> saveRes = clientController.save(newClient);
		assertEquals(HttpStatus.CREATED, saveRes.getStatusCode());

		// GET by id
		ResponseEntity<Client> getRes = clientController.getClient("9999999");
		assertEquals(HttpStatus.OK, getRes.getStatusCode());
		assertEquals("Juan", getRes.getBody().getName());

		// PUT update
		Client updateClient = new Client();
		updateClient.setName("Juan Carlos");
		updateClient.setLastname("Perez Lopez");
		updateClient.setPhone(new BigDecimal("9997654321"));
		updateClient.setAddress("Calle 62 #200");
		updateClient.setEmail("juancarlos@perez.com");
		ResponseEntity<Client> updateRes = clientController.update("9999999", updateClient);
		assertEquals(HttpStatus.OK, updateRes.getStatusCode());
		assertEquals("Juan Carlos", updateRes.getBody().getName());

		// DELETE
		ResponseEntity<Void> delRes = clientController.delete("9999999");
		assertEquals(HttpStatus.OK, delRes.getStatusCode());
		assertEquals(HttpStatus.NOT_FOUND, clientController.getClient("9999999").getStatusCode());
	}

	@Test
	void testProductCrud() {
		// GET all
		ResponseEntity<List<Product>> allRes = productController.getAll();
		assertEquals(HttpStatus.OK, allRes.getStatusCode());
		assertFalse(allRes.getBody().isEmpty());

		// POST create
		Product newProd = new Product();
		newProd.setName("Producto Test");
		newProd.setCategoryId(1);
		newProd.setPrice(1500.0);
		newProd.setStock(50);
		newProd.setActive(true);
		ResponseEntity<Product> saveRes = productController.save(newProd);
		assertEquals(HttpStatus.CREATED, saveRes.getStatusCode());
		Integer prodId = saveRes.getBody().getProductId();
		assertNotNull(prodId);

		// PUT update
		Product updateProd = new Product();
		updateProd.setName("Producto Test Actualizado");
		updateProd.setCategoryId(1);
		updateProd.setPrice(1800.0);
		updateProd.setStock(40);
		updateProd.setActive(true);
		ResponseEntity<Product> updateRes = productController.update(prodId, updateProd);
		assertEquals(HttpStatus.OK, updateRes.getStatusCode());
		assertEquals("Producto Test Actualizado", updateRes.getBody().getName());
		assertEquals(1800.0, updateRes.getBody().getPrice());

		// DELETE
		ResponseEntity<Void> delRes = productController.delete(prodId);
		assertEquals(HttpStatus.OK, delRes.getStatusCode());
		assertEquals(HttpStatus.NOT_FOUND, productController.getProduct(prodId).getStatusCode());
	}

	@Test
	void testPurchaseMasterDetailCascade() {
		// GET all
		ResponseEntity<List<Purchase>> allRes = purchaseController.getAll();
		assertEquals(HttpStatus.OK, allRes.getStatusCode());
		assertFalse(allRes.getBody().isEmpty());

		// POST Master-Detail purchase
		Purchase purchase = new Purchase();
		purchase.setClientId("4546221");
		purchase.setDate(LocalDateTime.now());
		purchase.setPaymentMethod("E");
		purchase.setComment("Compra de prueba Maestro-Detalle");
		purchase.setState("P");

		PurchaseItem item1 = new PurchaseItem();
		item1.setProductId(1);
		item1.setQuantity(2);
		item1.setTotal(600.0);
		item1.setActive(true);

		PurchaseItem item2 = new PurchaseItem();
		item2.setProductId(2);
		item2.setQuantity(1);
		item2.setTotal(2100.0);
		item2.setActive(true);

		purchase.setItems(List.of(item1, item2));

		ResponseEntity<Purchase> saveRes = purchaseController.save(purchase);
		assertEquals(HttpStatus.CREATED, saveRes.getStatusCode());
		Purchase saved = saveRes.getBody();
		assertNotNull(saved);
		assertNotNull(saved.getPurchaseId());
		assertEquals(2, saved.getItems().size());
	}
}
