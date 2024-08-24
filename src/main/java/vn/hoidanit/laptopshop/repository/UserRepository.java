package vn.hoidanit.laptopshop.repository;

import java.util.List;
import vn.hoidanit.laptopshop.domain.User;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User save(User data);

    void deleteById(Long id);

    User findById(long id);

    List<User> findAll();

    List<User> findByEmail(String email);
}
