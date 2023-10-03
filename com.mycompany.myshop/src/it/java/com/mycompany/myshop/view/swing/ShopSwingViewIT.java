package com.mycompany.myshop.view.swing;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.swing.annotation.GUITest;
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
import com.mycompany.myshop.model.Product;
import com.mycompany.myshop.repository.mongo.ShopMongoRepository;

@RunWith(GUITestRunner.class)
public class ShopSwingViewIT extends AssertJSwingJUnitTestCase{
	
	private static int mongoPort =
			Integer.parseInt(System.getProperty("mongo.port", "27017"));
	
	private MongoClient mongoClient;

	private ShopSwingView shopSwingView;
	private ShopMongoRepository shopRepository;
	private ShopController shopController;
	private FrameFixture window;

	private MongoCollection<Document> productCollection;

	@Override
	protected void onSetUp() throws Exception {
		mongoClient = new MongoClient(
				new ServerAddress("localhost", mongoPort));
		shopRepository = new ShopMongoRepository(mongoClient,
				"shop", "product", "cart");
		MongoDatabase database = mongoClient.getDatabase("shop");
		database.drop();
		productCollection = database.getCollection("product");
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

}
