package com.mycompany.myshop.view.swing;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

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

import com.mycompany.myshop.model.Cart;
import com.mycompany.myshop.model.Product;

@RunWith(GUITestRunner.class)
public class ShopSwingViewTest extends AssertJSwingJUnitTestCase{

	private ShopSwingView shopSwingView;
	private FrameFixture window;

	@Override
	protected void onSetUp() throws Exception {
		GuiActionRunner.execute(() -> {
			shopSwingView = new ShopSwingView();
			return shopSwingView;
		});
		window = new FrameFixture(robot(), shopSwingView);
		window.show();
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
}
