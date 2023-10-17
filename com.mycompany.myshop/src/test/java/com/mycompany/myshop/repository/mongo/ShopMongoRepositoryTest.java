package com.mycompany.myshop.repository.mongo;

import static org.assertj.core.api.Assertions.assertThat;
import static java.util.Arrays.asList;

import java.net.InetSocketAddress;
import java.util.List;

import org.bson.Document;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mycompany.myshop.model.Cart;
import com.mycompany.myshop.model.Product;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

public class ShopMongoRepositoryTest {
	
	private static MongoServer server;
	private static InetSocketAddress serverAddress;
	
	private MongoClient mongoClient;
	private ShopMongoRepository shopRepository;
	private MongoCollection<Document> productCollection;
	private MongoCollection<Document> cartCollection;
	
	@BeforeClass
	public static void setupServer() {
		server = new MongoServer(new MemoryBackend());
		serverAddress = server.bind();		
	}
	
	@AfterClass
	public static void shutdownServer() {
		server.shutdown();
	}
	
	@Before
	public void setup() {
		mongoClient = new MongoClient(new ServerAddress(serverAddress));
		shopRepository = new ShopMongoRepository(mongoClient, "shop", "product", "cart");
		MongoDatabase database = mongoClient.getDatabase("shop");
		database.drop();
		productCollection = database.getCollection("product");
		cartCollection = database.getCollection("cart");
	}
	
	@After
	public void tearDown() {
		mongoClient.close();
	}
	
	@Test
	public void testFindAllProductsWhenDatabaseIsEmpty() {
		assertThat(shopRepository.findAllProducts()).isEmpty();
	}
	
	@Test
	public void testFindAllProductsWhenDatabaseIsNotEmpty() {
		addTestProductToDatabase("1", "test1");
		addTestProductToDatabase("2", "test2");
		assertThat(shopRepository.findAllProducts())
			.containsExactly(
					new Product("1", "test1"),
					new Product("2", "test2"));
	}
	
	@Test
	public void testFindProductByIdNotFound() {
		assertThat(shopRepository.findProductById("1"))
			.isNull();
	}
	
	@Test
	public void testFindProductByIdFound() {
		addTestProductToDatabase("1", "test1");
		addTestProductToDatabase("2", "test2");
		assertThat(shopRepository.findProductById("2"))
			.isEqualTo(new Product("2", "test2"));
		
	}
	
	@Test
	public void testFindCartNotFound() {
		assertThat(shopRepository.findCart("1"))
			.isNull();
	}
	
	@Test
	public void testFindCartFound() {
		cartCollection.insertMany(asList(
				new Document()
					.append("id", "1"),
				new Document()
					.append("id", "2")));
		assertThat(shopRepository.findCart("2"))
			.isEqualTo(new Cart("2"));
		
	}
	
	@Test
	public void testFindCartFoundShouldReturnCartWithProductList() {
		Cart cart = new Cart("1");
		cart.addToCart(new Product("2", "test2"));
		cart.addToCart(new Product("3", "test3"));
		cartCollection.insertOne(
				new Document()
				.append("id", "1")
				.append("productList", asList(
						new Document()
						.append("id", "2")
						.append("name", "test2"),
						new Document()
						.append("id", "3")
						.append("name", "test3"))));
		assertThat(shopRepository.findCart("1"))
			.isEqualTo(cart);
	}
	
	@Test
	public void testProductFoundInCartFalse() {
		assertThat(shopRepository.productFoundInCart("1", "2"))
			.isFalse();
	}
	
	@Test
	public void testProductFoundInCartTrue() {
		cartCollection.insertOne(
				new Document()
				.append("id", "1")
				.append("productList", asList(
						new Document()
						.append("id", "2")
						.append("name", "test2"),
						new Document()
						.append("id", "3")
						.append("name", "test3"))));
		assertThat(shopRepository.productFoundInCart("1", "2"))
			.isTrue();
	}
	
	@Test
	public void testDelete() {
		Cart cart = new Cart("1");
		cartCollection.insertOne(
				new Document()
				.append("id", "1")
				.append("productList", asList(
						new Document()
						.append("id", "2")
						.append("name", "test2"),
						new Document()
						.append("id", "3")
						.append("name", "test3"))));
		shopRepository.delete("1", "2");
		@SuppressWarnings("unchecked")
		List<Document> productList = (List<Document>) cartCollection
				.find(Filters.eq("id", cart.getId()))
				.first()
				.get("productList");
		assertThat(productList)
			.containsExactly(
					new Document()
					.append("id", "3")
					.append("name", "test3"));
	}
	
	
	private void addTestProductToDatabase(String id, String name) {
		productCollection.insertOne(
				new Document()
				.append("id", id)
				.append("name", name));
	}
	

}
