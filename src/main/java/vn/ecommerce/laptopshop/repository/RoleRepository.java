package vn.ecommerce.laptopshop.repository;

import org.springframework.stereotype.Repository;

import vn.ecommerce.laptopshop.domain.Role;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);

}
