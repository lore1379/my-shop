package com.mycompany.myshop.view.swing;

import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

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
}
