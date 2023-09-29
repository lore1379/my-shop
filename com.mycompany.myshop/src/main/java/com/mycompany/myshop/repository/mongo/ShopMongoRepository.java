package com.mycompany.myshop.repository.mongo;

import java.util.Collections;
import java.util.List;

import com.mongodb.MongoClient;
import com.mycompany.myshop.model.Cart;
import com.mycompany.myshop.model.Product;
import com.mycompany.myshop.repository.ShopRepository;

public class ShopMongoRepository implements ShopRepository {

	public ShopMongoRepository(MongoClient mongoClient, String string, String string2, String string3) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Product> findAllProducts() {
		return Collections.emptyList();
	}

	@Override
	public Product findProductById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cart findCart(String string) {
		// TODO Auto-generated method stub
		return null;
	}

}
