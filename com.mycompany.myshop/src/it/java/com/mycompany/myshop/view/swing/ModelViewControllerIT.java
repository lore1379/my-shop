package com.mycompany.myshop.view.swing;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.bson.Document;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mycompany.myshop.controller.ShopController;
import com.mycompany.myshop.model.Cart;
import com.mycompany.myshop.model.Product;
import com.mycompany.myshop.repository.mongo.ShopMongoRepository;

@RunWith(GUITestRunner.class)
public class ModelViewControllerIT extends AssertJSwingJUnitTestCase {
	
	private static final String USER_CART_ID = "10";

	private static int mongoPort =
			Integer.parseInt(System.getProperty("mongo.port", "27017"));
	
	private MongoClient mongoClient;

	private ShopMongoRepository shopRepository;
	private ShopController shopController;
	private FrameFixture window;
	
	private MongoCollection<Document> productCollection;
	private MongoCollection<Document> cartCollection;

	@Override
	protected void onSetUp() throws Exception {
		mongoClient = new MongoClient(
				new ServerAddress("localhost", mongoPort));
		shopRepository = new ShopMongoRepository(mongoClient,
				"shop", "product", "cart");
		MongoDatabase database = mongoClient.getDatabase("shop");
		database.drop();
		productCollection = database.getCollection("product");
		cartCollection = database.getCollection("cart");
		window = new FrameFixture(robot(), GuiActionRunner.execute(() -> {
			ShopSwingView shopSwingView = new ShopSwingView();
			shopController = new ShopController(shopSwingView, shopRepository);
			shopSwingView.setShopController(shopController);
			return shopSwingView;
		}));
		window.show();
	}
	
	@Override
	protected void onTearDown() {
		mongoClient.close();
	}

	@Test
	public void testAddToCart() {
		Cart cart = new Cart(USER_CART_ID);
		cart.addToCart(new Product("1", "test1"));
		productCollection.insertMany(asList(
				new Document()
				.append("id", "1")
				.append("name", "test1"),
				new Document()
				.append("id", "2")
				.append("name", "test2")));
		cartCollection.insertOne(
				new Document()
				.append("id", USER_CART_ID));
		GuiActionRunner.execute( () ->
			shopController.allProducts());
		window.list("productList").selectItem(0);
		window.button(JButtonMatcher.withText("Add to Cart")).click();
		assertThat(shopRepository.findCart(USER_CART_ID))
			.isEqualTo(cart);
		assertThat(shopRepository.findProductById("1"))
			.isNull();
	}
	
	@Test
	public void testRemoveFromCart() {
		addTestCartToDatabase(USER_CART_ID, "2", "test2", "3", "test3");
		GuiActionRunner.execute( () ->
			shopController.getCart(USER_CART_ID));
		window.list("productListInCart").selectItem(0);
		window.button(JButtonMatcher.withText("Remove from Cart")).click();
		assertThat(shopRepository.productFoundInCart(USER_CART_ID, "2"))
			.isFalse();
		assertThat(shopRepository.findProductById("2"))
			.isEqualTo(new Product("2", "test2"));
		
	}
	
	@Test
	public void testCheckoutProduct() {
		addTestCartToDatabase(USER_CART_ID, "2", "test2", "3", "test3");
		GuiActionRunner.execute( () ->
			shopController.getCart(USER_CART_ID));
		window.list("productListInCart").selectItem(0);
		window.button(JButtonMatcher.withText("Checkout Product")).click();
		assertThat(shopRepository.productFoundInCart(USER_CART_ID, "2"))
			.isFalse();
	}
	
	private void addTestCartToDatabase(String cartId, String firstProductId, 
			String firstProductName, String secondProductId, String secondProductName) {
		cartCollection.insertOne(
				new Document()
				.append("id", cartId)
				.append("productList", asList(
						new Document()
						.append("id", firstProductId)
						.append("name", firstProductName),
						new Document()
						.append("id", secondProductId)
						.append("name", secondProductName))));
	}
}
