package com.mycompany.myshop.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
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
	}
	
	@After
	public void releaseMocks() throws Exception {
		closeable.close();
	}
	
	@Test
	public void test() {
		
	}

}
