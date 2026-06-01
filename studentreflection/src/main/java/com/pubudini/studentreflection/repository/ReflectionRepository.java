package com.pubudini.studentreflection.repository;

import com.pubudini.studentreflection.entity.Reflection;
import com.pubudini.studentreflection.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReflectionRepository extends JpaRepository<Reflection, Long> {
    List<Reflection> findByUserOrderByCreatedAtDesc(User user);
}
