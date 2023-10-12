package com.mycompany.myshop.bdd.steps;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

import java.util.regex.Pattern;

import javax.swing.JFrame;

import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.finder.WindowFinder;
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
	private static final String CART_COLLECTION_NAME = "test-cart-collection";
	private static final String PRODUCT_COLLECTION_NAME = "test-product-collection";
	
	private static final String PRODUCT_FIXTURE_1_ID = "1";
	private static final String PRODUCT_FIXTURE_1_NAME = "test1";
	private static final String PRODUCT_FIXTURE_3_ID = "3";
	private static final String PRODUCT_FIXTURE_3_NAME = "test3";
	
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
	    application("com.mycompany.myshop.app.swing.ShopSwingApp")
	    .withArgs(
    		"--mongo-host=" + "localhost",
			"--mongo-port=" + Integer.toString(mongoPort),
			"--db-name=" + DB_NAME,
			"--product-collection=" + PRODUCT_COLLECTION_NAME,
			"--cart-collection=" + CART_COLLECTION_NAME
		)
		.start();
	    window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
			@Override
			protected boolean isMatching(JFrame frame) {
				return "MyShop".equals(frame.getTitle()) && frame.isShowing();
			}
		}).using(BasicRobot.robotWithCurrentAwtHierarchy());
	}

	@Given("The user selects a product from the shop list")
	public void the_user_selects_a_product_from_the_shop_list() {
	    window.list("productList")
	    	.selectItem(Pattern.compile(".*" + PRODUCT_FIXTURE_1_NAME + ".*"));
	}

	@When("The user clicks the {string} button")
	public void the_user_clicks_the_button(String buttonText) {
	    window.button(JButtonMatcher.withText(buttonText)).click();
	}

	@Then("The cart list contains the product")
	public void the_cart_list_contains_the_product() {
	    assertThat(window.list("productListInCart").contents())
	    	.anySatisfy(e -> assertThat(e).contains(PRODUCT_FIXTURE_1_ID, PRODUCT_FIXTURE_1_NAME));
	}
	
	@Then("An error is shown containing the name of the selected product")
	public void an_error_is_shown_containing_the_name_of_the_selected_product() {
	    assertThat(window.label("errorMessageLabel").text())
	    	.contains(PRODUCT_FIXTURE_1_NAME);
	}
	
	@Then("The product is removed from the list")
	public void the_product_is_removed_from_the_list() {
	    assertThat(window.list("productList").contents())
	    	.noneMatch(e -> e.contains(PRODUCT_FIXTURE_1_NAME));
	}
	
	@Given("The user selects a product from the cart list")
	public void the_user_selects_a_product_from_the_cart_list() {
		window.list("productListInCart")
    		.selectItem(Pattern.compile(".*" + PRODUCT_FIXTURE_3_NAME + ".*"));
	}
	
	@Then("The shop list contains the product")
	public void the_shop_list_contains_the_product() {
		assertThat(window.list("productList").contents())
    		.anySatisfy(e -> assertThat(e).contains(PRODUCT_FIXTURE_3_ID, PRODUCT_FIXTURE_3_NAME));
	}

}
