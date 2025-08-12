package com.guarani.shopapi.repository;

import com.guarani.shopapi.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
}
