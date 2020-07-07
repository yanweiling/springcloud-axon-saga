package com.ywl.study.user.query;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerEntityRepository extends JpaRepository<CustomerEntity,String> {
}
