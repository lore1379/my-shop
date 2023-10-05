package com.mycompany.myshop.app.swing;

import java.awt.EventQueue;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mycompany.myshop.controller.ShopController;
import com.mycompany.myshop.repository.mongo.ShopMongoRepository;
import com.mycompany.myshop.view.swing.ShopSwingView;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(mixinStandardHelpOptions = true)
public class ShopSwingApp implements Callable<Void>{
	
	@Option(names = { "--mongo-host" }, description = "MongoDB host address")
	private String mongoHost = "localhost";

	@Option(names = { "--mongo-port" }, description = "MongoDB host port")
	private int mongoPort = 27017;

	@Option(names = { "--db-name" }, description = "Database name")
	private String databaseName = "shop";

	@Option(names = { "--product-collection" }, description = "Product collection name")
	private String productCollectionName = "product";
	
	@Option(names = { "--cart-collection" }, description = "Cart collection name")
	private String cartCollectionName = "cart";
	
	public static void main(String[] args) {
		new CommandLine(new ShopSwingApp()).execute(args);
	}

	@Override
	public Void call() throws Exception {
		EventQueue.invokeLater(() -> {
			try {
				ShopMongoRepository shopRepository = new ShopMongoRepository(
						new MongoClient(new ServerAddress(mongoHost, mongoPort)),
						databaseName, productCollectionName, cartCollectionName);
				ShopSwingView shopView = new ShopSwingView();
				ShopController shopController = 
						new ShopController(shopView, shopRepository);
				shopView.setShopController(shopController);
				shopView.setVisible(true);
				shopController.allProducts();
			} catch (Exception e) {
				Logger.getLogger(getClass().getName())
					.log(Level.SEVERE, "Exception", e);
			}
		});
		return null;
	}

}
