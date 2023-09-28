package com.mycompany.myshop.controller;

import java.util.List;

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
		List<Product> products = shopRepository.findAll();
		shopView.showAllProducts(products);
	}

}
