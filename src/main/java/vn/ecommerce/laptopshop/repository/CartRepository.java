package vn.ecommerce.laptopshop.repository;

import org.springframework.stereotype.Repository;

import vn.ecommerce.laptopshop.domain.Cart;
import vn.ecommerce.laptopshop.domain.CartDetail;
import vn.ecommerce.laptopshop.domain.User;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByUser(User user);

    // Cart findByCartDetail(CartDetail cartDetail);
}
