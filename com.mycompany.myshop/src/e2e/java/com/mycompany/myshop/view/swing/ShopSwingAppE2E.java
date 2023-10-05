package com.mycompany.myshop.view.swing;

import static org.assertj.swing.launcher.ApplicationLauncher.application;
import javax.swing.JFrame;

import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

@RunWith(GUITestRunner.class)
public class ShopSwingAppE2E extends AssertJSwingJUnitTestCase {
	
	private static int mongoPort =
			Integer.parseInt(System.getProperty("mongo.port", "27017"));

	@Override
	protected void onSetUp() throws Exception {
		MongoClient mongoClient = new MongoClient(
				new ServerAddress("localhost", mongoPort));
		mongoClient.getDatabase("shop").drop();
		application("com.mycompany.myshop.app.swing.ShopSwingApp")
			.withArgs(
				"--mongo-host=" + "localhost",
				"--mongo-port=" + Integer.toString(mongoPort),
				"--db-name=" + "shop",
				"--product-collection=" + "product",
				"--cart-collection=" + "cart"
			)
			.start();
		FrameFixture window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
			@Override
			protected boolean isMatching(JFrame frame) {
				return "MyShop".equals(frame.getTitle()) && frame.isShowing();
			}
		}).using(robot());
	}

	@Test
	public void test() {
	}
}
