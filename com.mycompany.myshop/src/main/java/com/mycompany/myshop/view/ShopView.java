package com.mycompany.myshop.view;

import java.util.List;

import com.mycompany.myshop.model.Cart;
import com.mycompany.myshop.model.Product;

public interface ShopView {

	void showAllProducts(List<Product> products);

	void showCart(Cart cart);

	void productAddedToCart(Product product);

	void showErrorProductNotFound(String message, Product product);

	void productRemovedFromCart(Product product);

	void productPurchased(Product product);

	void showPurchaseSuccessMessage(String message, Product product);

}
