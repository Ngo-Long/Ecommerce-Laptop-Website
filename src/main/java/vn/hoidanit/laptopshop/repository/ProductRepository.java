package vn.hoidanit.laptopshop.repository;

import vn.hoidanit.laptopshop.domain.Product;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Product save(Product data);

    void deleteById(Long id);

    Product findById(long id);

}
