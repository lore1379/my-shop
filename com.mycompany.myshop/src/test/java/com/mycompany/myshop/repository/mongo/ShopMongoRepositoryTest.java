package com.mycompany.myshop.repository.mongo;

import java.net.InetSocketAddress;

import org.bson.Document;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mycompany.myshop.repository.ShopRepository;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

public class ShopMongoRepositoryTest {
	
	@Test
	public void testDBSetup() {
		MongoServer server = new MongoServer(new MemoryBackend());
		InetSocketAddress serverAddress = server.bind();
		MongoClient mongoClient = new MongoClient(new ServerAddress(serverAddress));
		ShopMongoRepository productRepository = new ShopMongoRepository(mongoClient, "shop", "product");
		MongoDatabase database = mongoClient.getDatabase("shop");
		database.drop();
		MongoCollection<Document> productCollection = database.getCollection("product");
		mongoClient.close();
		server.shutdown();
	}

}
