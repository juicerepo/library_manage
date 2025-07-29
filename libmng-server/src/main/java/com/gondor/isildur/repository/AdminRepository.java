package com.gondor.isildur.repository;

import com.gondor.isildur.entity.Admin;

import org.springframework.data.repository.PagingAndSortingRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface AdminRepository extends PagingAndSortingRepository<Admin, Long> {
    Page<Admin> findByAdminNameContaining(String adminName, Pageable pageable);
}