package com.mycompany.myshop.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

import java.util.regex.Pattern;

import javax.swing.JFrame;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.bson.Document;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.model.Filters;

@RunWith(GUITestRunner.class)
public class ShopSwingAppE2E extends AssertJSwingJUnitTestCase {
	
	private static final String DB_NAME = "test-db";
	private static final String CART_COLLECTION_NAME = "test-cart-collection";
	private static final String PRODUCT_COLLECTION_NAME = "test-product-collection";

	private static final String PRODUCT_FIXTURE_1_ID = "1";
	private static final String PRODUCT_FIXTURE_1_NAME = "test1";
	private static final String PRODUCT_FIXTURE_2_ID = "2";
	private static final String PRODUCT_FIXTURE_2_NAME = "test2";

	private static int mongoPort =
			Integer.parseInt(System.getProperty("mongo.port", "27017"));
	
	private MongoClient mongoClient;

	private FrameFixture window;

	@Override
	protected void onSetUp() throws Exception {
		mongoClient = new MongoClient(
				new ServerAddress("localhost", mongoPort));
		mongoClient.getDatabase(DB_NAME).drop();
		addTestProductToDatabase(PRODUCT_FIXTURE_1_ID, PRODUCT_FIXTURE_1_NAME);
		addTestProductToDatabase(PRODUCT_FIXTURE_2_ID, PRODUCT_FIXTURE_2_NAME);
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
		}).using(robot());
	}
	

	@Override
	protected void onTearDown() {
		mongoClient.close();
	}

	@Test @GUITest
	public void testOnStartAllDatabaseElementsAreShown() {
		assertThat(window.list("productList").contents())
			.anySatisfy(e -> assertThat(e)
					.contains(PRODUCT_FIXTURE_1_ID, PRODUCT_FIXTURE_1_NAME))
			.anySatisfy(e -> assertThat(e)
					.contains(PRODUCT_FIXTURE_2_ID, PRODUCT_FIXTURE_2_NAME));
	}
	
	@Test @GUITest
	public void testAddToCartButtonSuccess() {
		window.list("productList")
			.selectItem(Pattern.compile(".*" + PRODUCT_FIXTURE_1_NAME + ".*"));
		window.button(JButtonMatcher.withText("Add to Cart")).click();
		assertThat(window.list("productListInCart").contents())
			.anySatisfy(e -> assertThat(e)
					.contains(PRODUCT_FIXTURE_1_ID, PRODUCT_FIXTURE_1_NAME));
	}
	
	@Test @GUITest
	public void testAddToCartButtonError() {
		window.list("productList")
			.selectItem(Pattern.compile(".*" + PRODUCT_FIXTURE_1_NAME + ".*"));
		mongoClient
			.getDatabase(DB_NAME)
			.getCollection(PRODUCT_COLLECTION_NAME)
			.deleteOne(Filters.eq("id", PRODUCT_FIXTURE_1_ID));
		window.button(JButtonMatcher.withText("Add to Cart")).click();
		assertThat(window.label("errorMessageLabel").text())
			.contains(PRODUCT_FIXTURE_1_ID, PRODUCT_FIXTURE_1_NAME);
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
