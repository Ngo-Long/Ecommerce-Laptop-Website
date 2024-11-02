package vn.ecommerce.laptopshop.repository;

import org.springframework.stereotype.Repository;

import vn.ecommerce.laptopshop.domain.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    Product save(Product data);

    void deleteById(Long id);

    Product findById(long id);

    Page<Product> findAll(Pageable page);

    Page<Product> findAll(Specification<Product> spec, Pageable page);
}
