package com.tyss.restdemo.repository;

import com.tyss.restdemo.entity.Leave;
import com.tyss.restdemo.entity.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Integer>, JpaSpecificationExecutor<Leave> {

    Page<Leave> findByStatus(Status status, Pageable pageable);
}
