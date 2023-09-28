package com.mycompany.myshop.controller;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.mycompany.myshop.model.Cart;
import com.mycompany.myshop.model.Product;
import com.mycompany.myshop.repository.ShopRepository;
import com.mycompany.myshop.view.ShopView;

public class ShopControllerTest {
	
	@Mock
	private ShopView shopView;
	
	@Mock
	private ShopRepository shopRepository;

	@InjectMocks
	private ShopController shopController;

	private AutoCloseable closeable;
	
	@Before
	public void setup() {
		closeable = MockitoAnnotations.openMocks(this);
	}
	
	@After
	public void releaseMocks() throws Exception {
		closeable.close();
	}

	@Test
	public void testAllProducts() {
		List<Product> products = asList(new Product());
		when(shopRepository.findAllProducts())
			.thenReturn(products);
		shopController.allProducts();
		verify(shopView).showAllProducts(products);
	}
	
	@Test
	public void testGetCart() {
		Cart cart = new Cart("1");
		when(shopRepository.findCart("1"))
			.thenReturn(cart);
		shopController.getCart("1");
		verify(shopView).showCart(cart);
	}

}
