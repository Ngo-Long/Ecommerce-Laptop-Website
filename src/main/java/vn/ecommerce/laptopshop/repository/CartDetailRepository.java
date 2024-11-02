package vn.ecommerce.laptopshop.repository;

import org.springframework.stereotype.Repository;

import vn.ecommerce.laptopshop.domain.Cart;
import vn.ecommerce.laptopshop.domain.CartDetail;
import vn.ecommerce.laptopshop.domain.Product;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail, Long> {
    boolean existsByCartAndProduct(Cart cart, Product product);

    CartDetail findByCartAndProduct(Cart cart, Product product);
}
