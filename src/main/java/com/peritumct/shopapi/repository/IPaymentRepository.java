package com.peritumct.shopapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.peritumct.shopapi.model.Payment;

public interface IPaymentRepository extends JpaRepository<Payment, Long> {
}
