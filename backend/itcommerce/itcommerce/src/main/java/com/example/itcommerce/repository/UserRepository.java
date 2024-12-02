package com.example.itcommerce.repository;

import com.example.itcommerce.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}
