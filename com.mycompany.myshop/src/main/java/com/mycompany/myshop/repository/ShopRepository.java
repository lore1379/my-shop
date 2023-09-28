package com.mycompany.myshop.repository;

import java.util.List;

import com.mycompany.myshop.model.Cart;
import com.mycompany.myshop.model.Product;

public interface ShopRepository {

	public List<Product> findAllProducts();

	public Product findById(String id);

	public Cart findCart(String string);

}
