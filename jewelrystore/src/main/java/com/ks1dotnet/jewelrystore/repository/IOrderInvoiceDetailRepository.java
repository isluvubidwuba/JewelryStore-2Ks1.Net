package com.ks1dotnet.jewelrystore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ks1dotnet.jewelrystore.entity.OrderInvoiceDetail;

@Repository
public interface IOrderInvoiceDetailRepository extends JpaRepository<OrderInvoiceDetail, Integer> {

}
