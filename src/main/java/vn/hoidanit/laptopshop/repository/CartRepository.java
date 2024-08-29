package vn.hoidanit.laptopshop.repository;

import vn.hoidanit.laptopshop.domain.Cart;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.domain.CartDetail;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByUser(User user);

    // Cart findByCartDetail(CartDetail cartDetail);
}
