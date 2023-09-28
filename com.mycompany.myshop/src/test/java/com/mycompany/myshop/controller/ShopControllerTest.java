package com.mycompany.myshop.controller;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;

import com.mycompany.myshop.model.Product;
import com.mycompany.myshop.repository.ShopRepository;
import com.mycompany.myshop.view.ShopView;

public class ShopControllerTest {
	
	@Test
	public void testAllProducts() {
		ShopRepository shopRepository = mock(ShopRepository.class);
		ShopView shopView = mock(ShopView.class);
		List<Product> products = asList(new Product());
		when(shopRepository.findAll())
			.thenReturn(products);
		ShopController shopController = new ShopController(shopView, shopRepository);
		shopController.allProducts();
		verify(shopView).showAllProducts(products);
	}

}
