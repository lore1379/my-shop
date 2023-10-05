package com.mycompany.myshop.bdd.steps;

import org.assertj.swing.fixture.FrameFixture;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ShopSwingViewSteps {
	
	private static final String DB_NAME = "test-db";
	
	private static int mongoPort = 
			Integer.parseInt(System.getProperty("mongo.port", "27017"));
	
	private MongoClient mongoClient;
	
	private FrameFixture window;
	
	@Before
	public void setUp() {
		mongoClient = new MongoClient(
				new ServerAddress("localhost", mongoPort));
		mongoClient.getDatabase(DB_NAME).drop();
	}
	
	@After
	public void tearDown() {
		mongoClient.close();
		if (window != null)
			window.cleanUp();
	}
	
	@Given("The Shop View is shown")
	public void the_Shop_View_is_shown() {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}

	@Given("The user selects a product from the shop list")
	public void the_user_selects_a_product_from_the_shop_list() {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}

	@When("The user clicks the {string} button")
	public void the_user_clicks_the_button(String string) {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}

	@Then("The cart list contains the product")
	public void the_cart_list_contains_the_product() {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}

}
