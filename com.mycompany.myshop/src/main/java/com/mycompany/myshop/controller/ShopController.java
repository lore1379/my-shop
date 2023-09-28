package com.mycompany.myshop.controller;

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

	public void getCart(String string) {
		// TODO Auto-generated method stub
		
	}

}
