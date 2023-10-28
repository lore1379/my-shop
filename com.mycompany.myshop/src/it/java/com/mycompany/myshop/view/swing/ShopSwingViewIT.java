package com.mycompany.myshop.view.swing;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.bson.Document;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.MongoDBContainer;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mycompany.myshop.controller.ShopController;
import com.mycompany.myshop.model.Cart;
import com.mycompany.myshop.model.Product;
import com.mycompany.myshop.repository.mongo.ShopMongoRepository;

@RunWith(GUITestRunner.class)
public class ShopSwingViewIT extends AssertJSwingJUnitTestCase{
	
	private static final String USER_CART_ID = "10";
	
	@ClassRule
	public static final MongoDBContainer mongo =
		new MongoDBContainer("mongo:4.4.3");
	
	private MongoClient mongoClient;

	private ShopSwingView shopSwingView;
	private ShopMongoRepository shopRepository;
	private ShopController shopController;
	private FrameFixture window;

	private MongoCollection<Document> productCollection;

	private MongoCollection<Document> cartCollection;

	@Override
	protected void onSetUp() throws Exception {
		mongoClient = new MongoClient(
				new ServerAddress(
						mongo.getHost(),
						mongo.getFirstMappedPort()));
		shopRepository = new ShopMongoRepository(mongoClient,
				"shop", "product", "cart");
		MongoDatabase database = mongoClient.getDatabase("shop");
		database.drop();
		productCollection = database.getCollection("product");
		cartCollection = database.getCollection("cart");
		GuiActionRunner.execute(() -> {
			shopSwingView = new ShopSwingView();
			shopController = new ShopController(shopSwingView, shopRepository);
			shopSwingView.setShopController(shopController);
			return shopSwingView;
		});
		window = new FrameFixture(robot(), shopSwingView);
		window.show();
	}
	
	@Override
	protected void onTearDown() {
		mongoClient.close();
	}
	
	@Test @GUITest
	public void testAllProducts() {
		productCollection.insertMany(asList(
				new Document()
				.append("id", "1")
				.append("name", "test1"),
				new Document()
				.append("id", "2")
				.append("name", "test2")));
		GuiActionRunner.execute( () ->
			shopController.allProducts());
		assertThat(window.list("productList").contents())
			.containsExactly(new Product("1", "test1").toString(), new Product("2", "test2").toString());
	}
	
	@Test @GUITest
	public void testGetCart() {
		Cart cart = new Cart("1");
		cartCollection.insertOne(
				new Document()
				.append("id", "1")
				.append("productList", 
						asList(new Document()
						.append("id", "2")
						.append("name", "test2"))));
		GuiActionRunner.execute( () ->
			shopController.getCart(cart.getId()));
		assertThat(window.list("productListInCart").contents())
			.containsExactly(new Product("2", "test2").toString());
	}
	
	@Test @GUITest
	public void testAddToCartButtonSuccess() {
		productCollection.insertMany(asList(
				new Document()
				.append("id", "1")
				.append("name", "test1"),
				new Document()
				.append("id", "2")
				.append("name", "test2")));
		GuiActionRunner.execute( () ->
			shopController.allProducts());
		window.list("productList").selectItem(0);
		window.button(JButtonMatcher.withText("Add to Cart")).click();
		assertThat(window.list("productList").contents())
			.containsExactly(new Product("2", "test2").toString());
		assertThat(window.list("productListInCart").contents())
			.containsExactly(new Product("1", "test1").toString());
	}
	
	@Test @GUITest
	public void testAddToCartButtonError() {
		Product product = new Product("1", "nonExistent");
		GuiActionRunner.execute( () ->
			shopSwingView.getListShopProductModel().addElement(product)
		);
		window.list("productList").selectItem(0);
		window.button(JButtonMatcher.withText("Add to Cart")).click();
		window.label("errorMessageLabel")
			.requireText("No available product with id 1: " + product);
		assertThat(window.list("productList").contents())
			.isEmpty();
	}
	
	@Test @GUITest
	public void testRemoveFromCartButton() {
		Cart cart = new Cart(USER_CART_ID);
		cartCollection.insertOne(
				new Document()
				.append("id", USER_CART_ID)
				.append("productList", asList(
						new Document()
						.append("id", "2")
						.append("name", "test2"),
						new Document()
						.append("id", "3")
						.append("name", "test3"))));
		GuiActionRunner.execute( () ->
			shopController.getCart(cart.getId())
		);
		window.list("productListInCart").selectItem(0);
		window.button(JButtonMatcher.withText("Remove from Cart")).click();
		assertThat(window.list("productList").contents())
			.containsExactly(new Product("2", "test2").toString());
		assertThat(window.list("productListInCart").contents())
			.containsExactly(new Product("3", "test3").toString());
	}
	
	@Test @GUITest
	public void testCheckoutProductButtonSuccess() {
		Cart cart = new Cart(USER_CART_ID);
		cartCollection.insertOne(
				new Document()
				.append("id", USER_CART_ID)
				.append("productList", 
						asList(new Document()
						.append("id", "2")
						.append("name", "test2"))));
		GuiActionRunner.execute( () ->
			shopController.getCart(cart.getId()));
		window.list("productListInCart").selectItem(0);
		window.button(JButtonMatcher.withText("Checkout Product")).click();
		assertThat(window.list("productListInCart").contents())
			.isEmpty();
		window.label("purchaseSuccessMessageLabel")
			.requireText("Successfully purchased product with id 2: " + 
					new Product("2", "test2"));
	}
	
	@Test @GUITest
	public void testCheckoutProductButtonError() {
		Product product = new Product("1", "nonExistent");
		GuiActionRunner.execute( () ->
			shopSwingView.getListCartProductModel().addElement(product)
		);
		window.list("productListInCart").selectItem(0);
		window.button(JButtonMatcher.withText("Checkout Product")).click();
		window.label("errorMessageLabel")
			.requireText("No available product with id 1: " + product);
		assertThat(window.list("productListInCart").contents())
			.isEmpty();
	}

}
