package com.mycompany.myshop.repository.mongo;

import org.bson.Document;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class ShopMongoRepositoryDockerIT {
	
	private static int mongoPort =
			Integer.parseInt(System.getProperty("mongo.port", "27017"));
	
	@Test
	public void test() {
		MongoClient mongoClient = new MongoClient(
				new ServerAddress("localhost", mongoPort));
		ShopMongoRepository shopRepository = new ShopMongoRepository(mongoClient,
				"shop", "product", "cart");
		MongoDatabase database = mongoClient.getDatabase("shop");
		database.drop();
		MongoCollection<Document> productCollection = database.getCollection("product");
		MongoCollection<Document> cartCollection = database.getCollection("cart");
		mongoClient.close();
	}

}
