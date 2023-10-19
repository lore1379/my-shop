package com.mycompany.myshop.repository.mongo;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mycompany.myshop.model.Cart;
import com.mycompany.myshop.model.Product;

public class ShopMongoRepositoryDockerIT {
	
	private static int mongoPort =
			Integer.parseInt(System.getProperty("mongo.port", "27017"));
	
	private MongoClient mongoClient;
	private ShopMongoRepository shopRepository;
	private MongoCollection<Document> productCollection;
	private MongoCollection<Document> cartCollection;
	
	@Before
	public void setup() {
		mongoClient = new MongoClient(
				new ServerAddress("localhost", mongoPort));
		shopRepository = new ShopMongoRepository(mongoClient,
				"shop", "product", "cart");
		MongoDatabase database = mongoClient.getDatabase("shop");
		database.drop();
		productCollection = database.getCollection("product");
		cartCollection = database.getCollection("cart");		
	}
	
	@After
	public void onTearDown() throws Exception {
		mongoClient.close();		
	}
	
	@Test
	public void testFindAllProducts() {
		addTestProductToDatabase("1", "test1");
		addTestProductToDatabase("2", "test2");
		assertThat(shopRepository.findAllProducts())
			.containsExactly(
					new Product("1", "test1"),
					new Product("2", "test2"));
	}
	
	@Test
	public void testFindProductById() {
		addTestProductToDatabase("1", "test1");
		addTestProductToDatabase("2", "test2");
		assertThat(shopRepository.findProductById("2"))
			.isEqualTo(new Product("2", "test2"));
		
	}
	
	@Test
	public void testFindCart() {
		addTestCartToDatabase("10", "1", "test1", "2", "test2");
		cartCollection.insertOne(
				new Document()
					.append("id", "20"));
		Cart cart = new Cart("10");
		cart.addToCart(new Product("1", "test1"));
		cart.addToCart(new Product("2", "test2"));
		assertThat(shopRepository.findCart("10"))
			.isEqualTo(cart);
		
	}
	
	@Test
	public void testMoveProductToCart() {
		Cart cart = new Cart("10");
		addTestProductToDatabase("1", "test1");
		addTestProductToDatabase("2", "test2");
		cartCollection.insertOne(
				new Document()
					.append("id", "10"));
		shopRepository.moveProductToCart("10", "2");
		assertThat(shopRepository.findAllProducts())
			.containsExactly(new Product("1", "test1"));
		List<Document> productList = getCartProductList(cart);
		assertThat(productList)
			.containsExactly(
					new Document()
					.append("id", "2")
					.append("name", "test2"));
	}
	
	@Test
	public void testProductFoundInCart() {
		addTestCartToDatabase("1", "2", "test2", "3", "test3");
		assertThat(shopRepository.productFoundInCart("1", "2"))
			.isTrue();
	}
	
	@Test
	public void testDelete() {
		Cart cart = new Cart("1");
		addTestCartToDatabase("1", "2", "test2", "3", "test3");
		shopRepository.delete("1", "2");
		List<Document> productList = getCartProductList(cart);
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
	
	@SuppressWarnings("unchecked")
	private List<Document> getCartProductList(Cart cart) {
		return (List<Document>) cartCollection
				.find(Filters.eq("id", cart.getId()))
				.first()
				.get("productList");
	}

}
