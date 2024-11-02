package vn.ecommerce.laptopshop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.ecommerce.laptopshop.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User save(User data);

    void deleteById(Long id);

    User findById(long id);

    List<User> findAll();

    List<User> findOneByEmail(String email);

    boolean existsByEmail(String email);

    User findByEmail(String email);
}
