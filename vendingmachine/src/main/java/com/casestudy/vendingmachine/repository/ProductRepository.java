package com.casestudy.vendingmachine.repository;

import com.casestudy.vendingmachine.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Create a repository to use with products
@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {}
