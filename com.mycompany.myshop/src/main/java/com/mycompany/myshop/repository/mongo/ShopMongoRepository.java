package com.mycompany.myshop.repository.mongo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mycompany.myshop.model.Cart;
import com.mycompany.myshop.model.Product;
import com.mycompany.myshop.repository.ShopRepository;

public class ShopMongoRepository implements ShopRepository {


	private static final String PRODUCT_LIST_IN_CART = "productList";
	
	private MongoCollection<Document> productCollection;
	private MongoCollection<Document> cartCollection;

	public ShopMongoRepository(MongoClient mongoClient, String databaseName, String firstCollectionName, String secondCollectionName) {
		productCollection = mongoClient
				.getDatabase(databaseName)
				.getCollection(firstCollectionName);
		cartCollection = mongoClient
				.getDatabase(databaseName)
				.getCollection(secondCollectionName);
	}

	@Override
	public List<Product> findAllProducts() {
		return StreamSupport
				.stream(productCollection.find().spliterator(), false)
				.map(this::fromDocumentToProduct)
				.collect(Collectors.toList());
	}

	@Override
	public Product findProductById(String id) {
		Document d = productCollection.find(Filters.eq("id", id)).first();
		if (d != null)
			return fromDocumentToProduct(d);
		return null;
	}

	@Override
	public Cart findCart(String id) {
		Document d = cartCollection.find(Filters.eq("id", id)).first();
		if (d != null) {
			Cart cart = new Cart("" + d.get("id"));
			List<Document> productList = d.getList(PRODUCT_LIST_IN_CART, Document.class);
			if (productList != null) {
				for (Document productDocument : productList) {
		            cart.addToCart(fromDocumentToProduct(productDocument));
				}
			}
			return cart;
		}		
		return null;
	}
	
	@Override
	public void moveProductToCart(String cartId, String productId) {
		Product productToAdd = fromDocumentToProduct(
				productCollection.find(Filters.eq("id", productId)).first());
		productCollection.deleteOne(Filters.eq("id", productId));
		Bson cartFilter = Filters.eq("id", cartId);
		Bson updateFilter = Updates.push(PRODUCT_LIST_IN_CART,
				new Document()
				.append("id", productToAdd.getId())
				.append("name", productToAdd.getName()));
        cartCollection.updateOne(cartFilter, updateFilter);
	}

	@Override
	public void moveProductToShop(String cartId, String productId) {
		Bson cartFilter = Filters.eq("id", cartId);
		Bson updateFilter = Updates.pull(PRODUCT_LIST_IN_CART, Filters.eq("id", productId));
		List<Document> productList = cartCollection.find(cartFilter)
				.first()
				.getList(PRODUCT_LIST_IN_CART, Document.class);
		// search the matching document and convert to product
		Optional<Product> productOptional = productList.stream()
                .filter(d -> d.get("id").equals(productId))
                .map(this::fromDocumentToProduct)
                .findFirst();
		if (productOptional.isPresent()) {
			cartCollection.updateOne(cartFilter, updateFilter);
			Product productToMove = productOptional.get();
			productCollection.insertOne(
					new Document()
					.append("id", productToMove.getId())
					.append("name", productToMove.getName()));
		}
	}
	
	@Override
	public Boolean productFoundInCart(String cartId, String productId) {
		Bson cartFilter = Filters.and(
				Filters.eq("id", cartId),
                Filters.elemMatch(PRODUCT_LIST_IN_CART, Filters.eq("id", productId)));
		return cartCollection.find(cartFilter).first() != null;
	}

	@Override
	public void delete(String cartId, String productId) {
		Bson cartFilter = Filters.eq("id", cartId);
		Bson updateFilter = Updates.pull(PRODUCT_LIST_IN_CART, Filters.eq("id", productId));
        cartCollection.updateOne(cartFilter, updateFilter);
	}
	
	private Product fromDocumentToProduct(Document d) {
		return new Product("" + d.get("id"), "" + d.get("name"));
	}

}
