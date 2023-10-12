package com.mycompany.myshop.view.swing;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.mycompany.myshop.controller.ShopController;
import com.mycompany.myshop.model.Cart;
import com.mycompany.myshop.model.Product;
import com.mycompany.myshop.view.ShopView;

import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JList;
import java.awt.Insets;
import javax.swing.ListSelectionModel;
import javax.swing.JScrollPane;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import java.util.List;

public class ShopSwingView extends JFrame implements ShopView {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JButton btnAddSelectedToCart;
	private JLabel lblErrorMessage;
	private JButton btnRemoveFromCart;
	
	private DefaultListModel<Product> listShopProductsModel;
	private DefaultListModel<Product> listCartProductsModel;
	
	private transient ShopController shopController;

	public void setShopController(ShopController shopController) {
		this.shopController = shopController;
	}

	/**
	 * Create the frame.
	 */
	public ShopSwingView() {
		setTitle("MyShop");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JLabel lblShop = new JLabel("Shop");
		GridBagConstraints gbc_lblShop = new GridBagConstraints();
		gbc_lblShop.insets = new Insets(0, 0, 5, 0);
		gbc_lblShop.gridx = 0;
		gbc_lblShop.gridy = 0;
		contentPane.add(lblShop, gbc_lblShop);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		contentPane.add(scrollPane, gbc_scrollPane);
		
		listShopProductsModel = new DefaultListModel<>();
		JList<Product> listShopProducts = new JList<>(listShopProductsModel);
		listShopProducts.addListSelectionListener(
				e -> btnAddSelectedToCart.setEnabled(listShopProducts.getSelectedIndex() != -1));
		scrollPane.setViewportView(listShopProducts);
		listShopProducts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listShopProducts.setName("productList");
		
		btnAddSelectedToCart = new JButton("Add to Cart");
		btnAddSelectedToCart.addActionListener(
				e -> shopController.addProductToCart(listShopProducts.getSelectedValue()));
		btnAddSelectedToCart.setEnabled(false);
		GridBagConstraints gbc_btnAddSelectedToCart = new GridBagConstraints();
		gbc_btnAddSelectedToCart.insets = new Insets(0, 0, 5, 0);
		gbc_btnAddSelectedToCart.anchor = GridBagConstraints.EAST;
		gbc_btnAddSelectedToCart.gridx = 0;
		gbc_btnAddSelectedToCart.gridy = 2;
		contentPane.add(btnAddSelectedToCart, gbc_btnAddSelectedToCart);
		
		JLabel lblCart = new JLabel("Cart");
		GridBagConstraints gbc_lblCart = new GridBagConstraints();
		gbc_lblCart.insets = new Insets(0, 0, 5, 0);
		gbc_lblCart.gridx = 0;
		gbc_lblCart.gridy = 3;
		contentPane.add(lblCart, gbc_lblCart);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.gridx = 0;
		gbc_scrollPane_1.gridy = 4;
		contentPane.add(scrollPane_1, gbc_scrollPane_1);
		
		listCartProductsModel = new DefaultListModel<>();
		JList<Product> listCartProducts = new JList<>(listCartProductsModel);
		listCartProducts.addListSelectionListener(
				e -> btnRemoveFromCart.setEnabled(listCartProducts.getSelectedIndex() != -1));
		listCartProducts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listCartProducts.setName("productListInCart");
		scrollPane_1.setViewportView(listCartProducts);
		
		btnRemoveFromCart = new JButton("Remove from Cart");
		btnRemoveFromCart.setEnabled(false);
		GridBagConstraints gbc_btnRemoveFromCart = new GridBagConstraints();
		gbc_btnRemoveFromCart.insets = new Insets(0, 0, 5, 0);
		gbc_btnRemoveFromCart.gridx = 0;
		gbc_btnRemoveFromCart.gridy = 5;
		contentPane.add(btnRemoveFromCart, gbc_btnRemoveFromCart);
		
		lblErrorMessage = new JLabel(" ");
		lblErrorMessage.setName("errorMessageLabel");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.gridx = 0;
		gbc_label.gridy = 6;
		contentPane.add(lblErrorMessage, gbc_label);
	}

	DefaultListModel<Product> getListShopProductModel() {
		return listShopProductsModel;
	}
	
	public DefaultListModel<Product> getListCartProductModel() {
		return listCartProductsModel;
	}

	@Override
	public void showAllProducts(List<Product> products) {
		products.stream().forEach(listShopProductsModel::addElement);
	}

	@Override
	public void showCart(Cart cart) {
		List<Product> productsInCart = cart.getProductList();
		productsInCart.stream().forEach(listCartProductsModel::addElement);
	}

	@Override
	public void productAddedToCart(Product product) {
		listShopProductsModel.removeElement(product);
		listCartProductsModel.addElement(product);
		lblErrorMessage.setText(" ");
	}

	@Override
	public void showErrorProductNotFound(String message, Product product) {
		lblErrorMessage.setText(message + ": " + product);
		listShopProductsModel.removeElement(product);
	}

	@Override
	public void productRemovedFromCart(Product product) {
		listCartProductsModel.removeElement(product);
		listShopProductsModel.addElement(product);
		lblErrorMessage.setText(" ");
	}



}
