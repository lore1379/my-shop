package com.mycompany.myshop.repository;

import java.util.List;

import com.mycompany.myshop.model.Product;

public interface ShopRepository {

	List<Product> findAll();

}
