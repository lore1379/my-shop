package com.mycompany.myshop.view.swing;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import javax.swing.DefaultListModel;

import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.mycompany.myshop.controller.ShopController;
import com.mycompany.myshop.model.Cart;
import com.mycompany.myshop.model.Product;

@RunWith(GUITestRunner.class)
public class ShopSwingViewTest extends AssertJSwingJUnitTestCase {
	
	private static final String USER_CART_ID = "10";

	private ShopSwingView shopSwingView;
	private FrameFixture window;
	
	@Mock
	private ShopController shopController;
	
	private AutoCloseable closeable;

	@Override
	protected void onSetUp() throws Exception {
		closeable = MockitoAnnotations.openMocks(this);
		GuiActionRunner.execute(() -> {
			shopSwingView = new ShopSwingView();
			shopSwingView.setShopController(shopController);
			return shopSwingView;
		});
		window = new FrameFixture(robot(), shopSwingView);
		window.show();
	}
	
	@Override
	protected void onTearDown() throws Exception {
		closeable.close();
	}

	@Test
	public void testProductListInitialStateAndLabel() {
		window.label(JLabelMatcher.withText("Shop"));
		window.list("productList");
	}
	
	@Test
	public void testAddToCartInitialState() {
		window.button(JButtonMatcher.withText("Add to Cart")).requireDisabled();
	}
	
	@Test
	public void testProductListInCartInitialStateAndLabel() {
		window.label(JLabelMatcher.withText("Cart"));
		window.list("productListInCart");
	}
	
	@Test
	public void testAddToCartButtonShouldBeEnabledOnlyWhenAProductInShopIsSelected() {
		GuiActionRunner.execute(() ->
			shopSwingView.getListShopProductModel().addElement(new Product("1", "test")));
		window.list("productList").selectItem(0);
		JButtonFixture addToCartButton =
				window.button(JButtonMatcher.withText("Add to Cart"));
		addToCartButton.requireEnabled();
		window.list("productList").clearSelection();
		addToCartButton.requireDisabled();
	}
	
	@Test
	public void testShowAllProductsShouldAddProductDescriptionsToTheList() {
		Product product1 = new Product("1", "test1");
		Product product2 = new Product("2", "test2");
		GuiActionRunner.execute(() -> 
				shopSwingView.showAllProducts(asList(product1, product2)));
		String[] productListContents = window.list("productList").contents();
		assertThat(productListContents)
			.containsExactly(product1.toString(), product2.toString());
	}
	
	@Test
	public void testShowCartShouldAddProductDescriptionsToTheCartList() {
		Cart cart = new Cart("3");
		Product product1 = new Product("1", "test1");
		Product product2 = new Product("2", "test2");
		cart.addToCart(product1);
		cart.addToCart(product2);
		GuiActionRunner.execute(() -> 
				shopSwingView.showCart(cart));
		String[] cartListContents = window.list("productListInCart").contents();
		assertThat(cartListContents)
			.containsExactly(product1.toString(), product2.toString());
	}
	
	@Test
	public void testProductAddedToCartShouldMoveTheProductFromProductListToCartList() {
		Product product1 = new Product("1", "test1");
		Product product2 = new Product("2", "test2");
		GuiActionRunner.execute(() -> {
			DefaultListModel<Product> listShopProductsModel = shopSwingView.getListShopProductModel();
			listShopProductsModel.addElement(product1);
			listShopProductsModel.addElement(product2);
		});
		GuiActionRunner.execute(() ->
			shopSwingView.productAddedToCart(new Product("1", "test1")));
		String[] productListContents = window.list("productList").contents();
		assertThat(productListContents).containsExactly(product2.toString());
		String[] cartListContents = window.list("productListInCart").contents();
		assertThat(cartListContents).containsExactly(product1.toString());
	}
	
	@Test
	public void testAddToCartButtonShouldDelegateToShopControllerAddProductToCart() {
		Product product1 = new Product("1", "test1");
		Product product2 = new Product("2", "test2");
		GuiActionRunner.execute(() -> {
			DefaultListModel<Product> listShopProductsModel = shopSwingView.getListShopProductModel();
			listShopProductsModel.addElement(product1);
			listShopProductsModel.addElement(product2);
		});
		window.list("productList").selectItem(1);
		window.button(JButtonMatcher.withText("Add to Cart")).click();
		verify(shopController).addProductToCart(USER_CART_ID, product2);
	}
	
	@Test
	public void testErrorMessageLabelInitialState() {
		window.label("errorMessageLabel").requireText(" ");
	}
	
	@Test
	public void testShowErrorProductNotFoundShouldShowTheMessageInTheErrorLabel() {
		Product product = new Product("1", "test");
		GuiActionRunner.execute(() -> 
			shopSwingView.showErrorProductNotFound("error message", product)
		);
		window.label("errorMessageLabel")
			.requireText("error message: " + product);
	}
	
	@Test
	public void testProductAddedToCartShouldAlsoResetTheErrorLabel() {
		Product nonExistingProduct = new Product("1", "nonExisting");
		Product existingProduct = new Product("1", "existing");
		GuiActionRunner.execute(() -> {
			shopSwingView.showErrorProductNotFound("error message", nonExistingProduct);
			shopSwingView.productAddedToCart(existingProduct);
		});
		window.label("errorMessageLabel")
			.requireText(" ");
	}
	
	@Test
	public void testShowErrorProductNotFoundShouldRemoveTheProductFromTheList() {
		Product product1 = new Product("1", "test1");
		Product product2 = new Product("2", "test2");
		GuiActionRunner.execute(() -> {
			DefaultListModel<Product> listShopProductsModel = shopSwingView.getListShopProductModel();
			listShopProductsModel.addElement(product1);
			listShopProductsModel.addElement(product2);
		});
		GuiActionRunner.execute(() -> 
			shopSwingView.showErrorProductNotFound("error message", product1)
		);
		assertThat(window.list("productList").contents())
			.containsExactly(product2.toString());
	}
	
	@Test
	public void testRemoveFromCartButtonInitialState() {
		window.button(JButtonMatcher.withText("Remove from Cart")).requireDisabled();
	}
	
	@Test
	public void testRemoveFromCartButtonShouldBeEnabledOnlyWhenAProductInCartIsSelected() {
		GuiActionRunner.execute(() ->
			shopSwingView.getListCartProductModel().addElement(new Product("1", "test")));
		window.list("productListInCart").selectItem(0);
		JButtonFixture removeFromCartButton =
			window.button(JButtonMatcher.withText("Remove from Cart"));
		removeFromCartButton.requireEnabled();
		window.list("productListInCart").clearSelection();
		removeFromCartButton.requireDisabled();
	}
	
	@Test
	public void testProductRemovedFromCartShouldMoveTheProductFromCartListToProductListAndResetTheErrorLabel() {
		Product product1 = new Product("1", "test1");
		Product product2 = new Product("2", "test2");
		GuiActionRunner.execute(() -> {
			DefaultListModel<Product> listCartProductsModel = shopSwingView.getListCartProductModel();
			listCartProductsModel.addElement(product1);
			listCartProductsModel.addElement(product2);
		});
		GuiActionRunner.execute(() ->
			shopSwingView.productRemovedFromCart(new Product("1", "test1")));
		assertThat(window.list("productListInCart").contents())
			.containsExactly(product2.toString());
		assertThat(window.list("productList").contents())
			.containsExactly(product1.toString());
		window.label("errorMessageLabel").requireText(" ");
	}
	
	@Test
	public void testRemoveFromCartButtonShouldDelegateToShopControllerRemoveProductFromCart() {
		Product product1 = new Product("1", "test1");
		Product product2 = new Product("2", "test2");
		GuiActionRunner.execute(() -> {
			DefaultListModel<Product> listCartProductsModel = shopSwingView.getListCartProductModel();
			listCartProductsModel.addElement(product1);
			listCartProductsModel.addElement(product2);
		});
		window.list("productListInCart").selectItem(1);
		window.button(JButtonMatcher.withText("Remove from Cart")).click();
		verify(shopController).removeProductFromCart(USER_CART_ID, product2);
	}
	
	@Test
	public void testCheckoutProductButtonInitialState() {
		window.button(JButtonMatcher.withText("Checkout Product")).requireDisabled();
	}
	
	@Test
	public void testCheckoutProductButtonShouldBeEnabledOnlyWhenAProductInCartIsSelected() {
		GuiActionRunner.execute(() ->
			shopSwingView.getListCartProductModel().addElement(new Product("1", "test")));
		window.list("productListInCart").selectItem(0);
		JButtonFixture checkoutProductButton =
			window.button(JButtonMatcher.withText("Checkout Product"));
		checkoutProductButton.requireEnabled();
		window.list("productListInCart").clearSelection();
		checkoutProductButton.requireDisabled();
	}
	
	@Test
	public void testCheckoutProductButtonShouldRemoveTheProductFromCartListAndResetTheErrorLabel() {
		Product product1 = new Product("1", "test1");
		Product product2 = new Product("2", "test2");
		GuiActionRunner.execute(() -> {
			DefaultListModel<Product> listCartProductsModel = shopSwingView.getListCartProductModel();
			listCartProductsModel.addElement(product1);
			listCartProductsModel.addElement(product2);
		});
		GuiActionRunner.execute(() ->
			shopSwingView.productPurchased(new Product("1", "test1")));
		assertThat(window.list("productListInCart").contents())
			.containsExactly(product2.toString());
		window.label("errorMessageLabel").requireText(" ");
	}
	
	@Test
	public void testCheckoutProductButtonShouldDelegateToShopControllerCheckoutProductFromCart() {
		Cart cart = new Cart("10");
		Product product1 = new Product("1", "test1");
		Product product2 = new Product("2", "test2");
		GuiActionRunner.execute(() -> {
			DefaultListModel<Product> listCartProductsModel = shopSwingView.getListCartProductModel();
			listCartProductsModel.addElement(product1);
			listCartProductsModel.addElement(product2);
		});
		window.list("productListInCart").selectItem(1);
		window.button(JButtonMatcher.withText("Checkout Product")).click();
		verify(shopController).checkoutProductFromCart(cart.getId(), product2);
	}
	
	@Test
	public void testPurchaseSuccessMessageLabelInitialState() {
		window.label("purchaseSuccessMessageLabel").requireText(" ");
	}
	
	@Test
	public void testShowPurchaseSuccessMessageShouldShowTheMessageInThePurchaseSuccessLabel() {
		Product product = new Product("1", "test");
		GuiActionRunner.execute(() -> 
			shopSwingView.showPurchaseSuccessMessage("success message", product)
		);
		window.label("purchaseSuccessMessageLabel")
			.requireText("success message: " + product);
	}
	
}
