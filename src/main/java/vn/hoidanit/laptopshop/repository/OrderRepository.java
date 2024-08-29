package vn.hoidanit.laptopshop.repository;

import java.util.List;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.domain.Order;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
}
