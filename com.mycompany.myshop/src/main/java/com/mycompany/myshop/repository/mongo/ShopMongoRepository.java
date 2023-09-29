package com.mycompany.myshop.repository.mongo;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mycompany.myshop.model.Cart;
import com.mycompany.myshop.model.Product;
import com.mycompany.myshop.repository.ShopRepository;

public class ShopMongoRepository implements ShopRepository {


	private MongoCollection<Document> productCollection;

	public ShopMongoRepository(MongoClient mongoClient, String databaseName, String firstCollectionName, String secondCollectionName) {
		productCollection = mongoClient
				.getDatabase(databaseName)
				.getCollection(firstCollectionName);
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
	public Cart findCart(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	private Product fromDocumentToProduct(Document d) {
		return new Product("" + d.get("id"), "" + d.get("name"));
	}
}
