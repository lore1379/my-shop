package com.mycompany.myshop.controller;

import static org.mockito.Mockito.verify;
import static java.util.Arrays.asList;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mycompany.myshop.model.Cart;
import com.mycompany.myshop.model.Product;
import com.mycompany.myshop.repository.mongo.ShopMongoRepository;
import com.mycompany.myshop.view.ShopView;

public class ShopControllerIT {
	
	private static int mongoPort =
			Integer.parseInt(System.getProperty("mongo.port", "27017"));
	
	@Mock
	private ShopView shopView;

	private ShopMongoRepository shopRepository;

	@InjectMocks
	private ShopController shopController;

	private AutoCloseable closeable;
	
	private MongoCollection<Document> productCollection;
	private MongoCollection<Document> cartCollection;
	
	@Before
	public void setup() {
		closeable = MockitoAnnotations.openMocks(this);
		MongoClient mongoClient = new MongoClient(
				new ServerAddress("localhost", mongoPort));
		shopRepository = new ShopMongoRepository(mongoClient,
				"shop", "product", "cart");
		MongoDatabase database = mongoClient.getDatabase("shop");
		database.drop();
		shopController = new ShopController(shopView, shopRepository);
		productCollection = database.getCollection("product");
		productCollection.insertOne(
				new Document()
					.append("id", "10")
					.append("name", "testProduct"));
		cartCollection = database.getCollection("cart");
		cartCollection.insertOne(
				new Document()
					.append("id", "1")
					.append("name", "testCart"));
	}
	
	@After
	public void releaseMocks() throws Exception {
		closeable.close();
	}
	
	@Test
	public void testAllProducts() {
		Product product = new Product("10", "testProduct");
		shopController.allProducts();
		verify(shopView)
			.showAllProducts(asList(product));
	}
	
	@Test
	public void testGetCart() {
		Cart cart = new Cart("1");
		shopController.getCart("1");
		verify(shopView)
			.showCart(cart);
	}
	
	@Test
	public void testaddProductToCart() {
		Product productToAdd = new Product("10", "testProduct");
		shopController.addProductToCart(productToAdd);
		verify(shopView)
			.productAddedToCart(productToAdd);
	}

}
