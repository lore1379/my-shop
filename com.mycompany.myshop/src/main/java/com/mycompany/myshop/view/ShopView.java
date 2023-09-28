package com.mycompany.myshop.view;

import java.util.List;

import com.mycompany.myshop.model.Cart;
import com.mycompany.myshop.model.Product;

public interface ShopView {

	void showAllProducts(List<Product> products);

	void showCart(Cart cart);

}
