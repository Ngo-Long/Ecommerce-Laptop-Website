package vn.ecommerce.laptopshop.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import vn.ecommerce.laptopshop.domain.Order;
import vn.ecommerce.laptopshop.domain.User;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
}
