package com.mycompany.myshop.bdd.steps;

import static java.util.Arrays.asList;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mycompany.myshop.bdd.ShopSwingAppBDD;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;

public class DatabaseSteps {
	
	private MongoClient mongoClient;
	
	private static final String DB_NAME = "test-db";
	private static final String CART_COLLECTION_NAME = "test-cart-collection";
	private static final String PRODUCT_COLLECTION_NAME = "test-product-collection";
	
	private static final String CART_FIXTURE_10_ID = "10";
	private static final String PRODUCT_FIXTURE_1_ID = "1";
	private static final String PRODUCT_FIXTURE_1_NAME = "test1";
	private static final String PRODUCT_FIXTURE_2_ID = "2";
	private static final String PRODUCT_FIXTURE_2_NAME = "test2";
	private static final String PRODUCT_FIXTURE_3_ID = "3";
	private static final String PRODUCT_FIXTURE_3_NAME = "test3";
	private static final String PRODUCT_FIXTURE_4_ID = "4";
	private static final String PRODUCT_FIXTURE_4_NAME = "test4";
	
	@Before
	public void setUp() {
		String containerIpAddress = ShopSwingAppBDD.getContainerIpAddress();
		Integer mappedPort = ShopSwingAppBDD.getMappedPort();
		mongoClient = new MongoClient(
				new ServerAddress(
						containerIpAddress,
						mappedPort));
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
	
	@Given("The database contains a cart with a few products in it")
	public void the_database_contains_a_cart_with_a_few_products_in_it() {
		mongoClient
			.getDatabase(DB_NAME)
			.getCollection(CART_COLLECTION_NAME)
			.insertOne(
				new Document()
				.append("id", CART_FIXTURE_10_ID)
				.append("productList", asList(
						new Document()
						.append("id", PRODUCT_FIXTURE_3_ID)
						.append("name", PRODUCT_FIXTURE_3_NAME),
						new Document()
						.append("id", PRODUCT_FIXTURE_4_ID)
						.append("name", PRODUCT_FIXTURE_4_NAME))));
	}
	
	@Given("The product is in the meantime removed from the cart in the database")
	public void the_product_is_in_the_meantime_removed_from_the_cart_in_the_database() {
		Bson cartFilter = Filters.eq("id", CART_FIXTURE_10_ID);
		Bson updateFilter = Updates.pull("productList", Filters.eq("id", PRODUCT_FIXTURE_3_ID));
	    mongoClient
	    	.getDatabase(DB_NAME)
	    	.getCollection(CART_COLLECTION_NAME)
	    	.updateOne(cartFilter, updateFilter);
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
