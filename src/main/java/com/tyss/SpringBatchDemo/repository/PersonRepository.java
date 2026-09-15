package com.tyss.SpringBatchDemo.repository;

import com.tyss.SpringBatchDemo.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {
}
