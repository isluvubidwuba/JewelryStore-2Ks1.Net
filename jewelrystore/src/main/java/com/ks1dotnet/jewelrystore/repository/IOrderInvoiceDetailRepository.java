package com.ks1dotnet.jewelrystore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ks1dotnet.jewelrystore.entity.OrderInvoiceDetail;

public interface IOrderInvoiceDetailRepository extends JpaRepository<OrderInvoiceDetail, Integer> {

}
