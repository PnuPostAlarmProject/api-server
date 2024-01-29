package com.ppap.ppap.domain.subscribe.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ppap.ppap.domain.subscribe.entity.Univ;

public interface UnivJpaRepository extends JpaRepository<Univ, Long> {

	@Query(value = "select u from Univ u "
		+ "where concat(u.college, '_', u.department) in (:college_department)")
	Set<Univ> findAllByInCollegeAndDepartment(@Param("college_department") List<String> concatData);
}
