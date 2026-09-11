package com.example.commercepaymentsystem2team.domain.refund.repository;

import com.example.commercepaymentsystem2team.domain.refund.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefundRepository extends JpaRepository<Refund, Long> {
}
