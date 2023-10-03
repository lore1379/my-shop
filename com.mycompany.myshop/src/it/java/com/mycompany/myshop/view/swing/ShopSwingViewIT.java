package com.mycompany.myshop.view.swing;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.mycompany.myshop.controller.ShopController;
import com.mycompany.myshop.repository.mongo.ShopMongoRepository;

@RunWith(GUITestRunner.class)
public class ShopSwingViewIT extends AssertJSwingJUnitTestCase{
	
	private static int mongoPort =
			Integer.parseInt(System.getProperty("mongo.port", "27017"));
	
	private MongoClient mongoClient;
	private ShopSwingView shopSwingView;

	@Override
	protected void onSetUp() throws Exception {
		mongoClient = new MongoClient(
				new ServerAddress("localhost", mongoPort));
		ShopMongoRepository shopRepository = new ShopMongoRepository(mongoClient,
				"shop", "product", "cart");
		MongoDatabase database = mongoClient.getDatabase("shop");
		database.drop();
		GuiActionRunner.execute(() -> {
			shopSwingView = new ShopSwingView();
			ShopController shopController = new ShopController(shopSwingView, shopRepository);
			shopSwingView.setShopController(shopController);
			return shopSwingView;
		});
		FrameFixture window = new FrameFixture(robot(), shopSwingView);
		window.show();
	}
	
	@Override
	protected void onTearDown() {
		mongoClient.close();
	}
	
	@Test
	public void test() {
	}

}
