package com.mycompany.myshop.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Cart {
	
	private String id;
	private List<Product> productList;
	
	public Cart() {}

	public Cart(String id) {
		this.id = id;
		this.productList = new ArrayList<>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Product> getProductList() {
		return productList;
	}

	public void addToCart(Product product) {
		productList.add(product);
	}
	
	public void removeFromCart(Product product) {
		productList.remove(product);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, productList);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cart other = (Cart) obj;
		return Objects.equals(id, other.id) && Objects.equals(productList, other.productList);
	}

	@Override
	public String toString() {
		return "Cart [id=" + id + ", productList=" + productList + "]";
	}


}
