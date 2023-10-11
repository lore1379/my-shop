package com.mycompany.myshop.controller;

import com.mycompany.myshop.model.Cart;
import com.mycompany.myshop.model.Product;
import com.mycompany.myshop.repository.ShopRepository;
import com.mycompany.myshop.view.ShopView;

public class ShopController {

	private ShopView shopView;
	private ShopRepository shopRepository;

	public ShopController(ShopView shopView, ShopRepository shopRepository) {
		this.shopView = shopView;
		this.shopRepository = shopRepository;
	}

	public void allProducts() {
		shopView.showAllProducts(shopRepository.findAllProducts());
	}

	public void getCart(String id) {
		Cart cart = shopRepository.findCart(id);
		shopView.showCart(cart);
	}

	public void addProductToCart(Product product) {
		Product productToAdd = shopRepository.findProductById(product.getId());
		if (productToAdd == null ) {
			shopView.showErrorProductNotFound("No available product with id " + product.getId(),
					product);
		}
		else {
			shopView.productAddedToCart(productToAdd);
		}
	}

}
