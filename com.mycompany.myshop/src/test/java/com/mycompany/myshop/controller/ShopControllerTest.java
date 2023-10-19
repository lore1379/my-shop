package com.mycompany.myshop.controller;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.ignoreStubs;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.inOrder;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.mycompany.myshop.model.Cart;
import com.mycompany.myshop.model.Product;
import com.mycompany.myshop.repository.ShopRepository;
import com.mycompany.myshop.view.ShopView;

public class ShopControllerTest {
	
	private static final String USER_CART_ID = "10";
	
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
	
	@Test
	public void testAddProductToCartWhenProductExists() {
		Product productToAdd = new Product("1", "test");
		when(shopRepository.findProductById("1"))
			.thenReturn(productToAdd);
		shopController.addProductToCart(USER_CART_ID, productToAdd);
		InOrder inOrder = inOrder(shopRepository, shopView);
		inOrder.verify(shopRepository).moveProductToCart(USER_CART_ID, "1");
		inOrder.verify(shopView).productAddedToCart(productToAdd);
	}
	
	@Test
	public void testAddProductToCartWhenProductDoesNotExist() {
		Product product = new Product("1", "test");
		when(shopRepository.findProductById("1"))
			.thenReturn(null);
		shopController.addProductToCart(USER_CART_ID, product);
		verify(shopView)
			.showErrorProductNotFound("No available product with id 1", product);
		verifyNoMoreInteractions(ignoreStubs(shopRepository));
	}
	
	@Test
	public void testRemoveProductFromCart() {
		Product productToRemove = new Product("1", "test");
		shopController.removeProductFromCart(productToRemove);
		verify(shopView).productRemovedFromCart(productToRemove);
	}
	
	@Test
	public void testCheckoutProductFromCartWhenProductExists() {
		Cart cart = new Cart("1");
		Product productToCheckout = new Product("2", "test");
		when(shopRepository.productFoundInCart("1", "2"))
			.thenReturn(true);
		shopController.checkoutProductFromCart(cart.getId(), productToCheckout);
		InOrder inOrder = inOrder(shopRepository, shopView);
		inOrder.verify(shopRepository).delete("1", "2");
		inOrder.verify(shopView).productPurchased(productToCheckout);
	}

}
