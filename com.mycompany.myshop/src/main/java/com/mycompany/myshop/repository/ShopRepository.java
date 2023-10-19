package com.mycompany.myshop.repository;

import java.util.List;

import com.mycompany.myshop.model.Cart;
import com.mycompany.myshop.model.Product;

public interface ShopRepository {

	public List<Product> findAllProducts();

	public Product findProductById(String id);

	public Cart findCart(String string);

	public Boolean productFoundInCart(String cartId, String productId);

	public void delete(String cartId, String productId);

	public void moveProductToCart(String cartId, String productId);

	public void moveProductToShop(String cartId, String productId);

}
