package com.mycompany.myshop.bdd.steps;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.model.Filters;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;

public class DatabaseSteps {
	
	private static int mongoPort =
			Integer.parseInt(System.getProperty("mongo.port", "27017"));
	
	private MongoClient mongoClient;
	
	private static final String DB_NAME = "test-db";
	private static final String PRODUCT_COLLECTION_NAME = "test-product-collection";
	
	private static final String PRODUCT_FIXTURE_1_ID = "1";
	private static final String PRODUCT_FIXTURE_1_NAME = "test1";
	private static final String PRODUCT_FIXTURE_2_ID = "2";
	private static final String PRODUCT_FIXTURE_2_NAME = "test2";
	
	@Before
	public void setUp() {
		mongoClient = new MongoClient(
				new ServerAddress("localhost", mongoPort));
		mongoClient.getDatabase(DB_NAME).drop();
	}

	@After
	public void tearDown() {
		mongoClient.close();
	}
	
	@Given("The database contains a few products")
	public void the_database_contains_a_few_products() {
	    addTestProductToDatabase(PRODUCT_FIXTURE_1_ID, PRODUCT_FIXTURE_1_NAME);
	    addTestProductToDatabase(PRODUCT_FIXTURE_2_ID, PRODUCT_FIXTURE_2_NAME);
	}
	
	@Given("The product is in the meantime removed from the database")
	public void the_product_is_in_the_meantime_removed_from_the_database() {
	    mongoClient
	    	.getDatabase(DB_NAME)
	    	.getCollection(PRODUCT_COLLECTION_NAME)
	    	.deleteOne(Filters.eq("id", PRODUCT_FIXTURE_1_ID));
	}
	
	private void addTestProductToDatabase(String id, String name) {
		mongoClient
			.getDatabase(DB_NAME)
			.getCollection(PRODUCT_COLLECTION_NAME)
			.insertOne(
					new Document()
					.append("id", id)
					.append("name", name));
	}

}
