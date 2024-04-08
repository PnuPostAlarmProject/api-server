package com.ppap.ppap.domain.subscribe.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ppap.ppap.domain.subscribe.entity.Univ;

public interface UnivJpaRepository extends JpaRepository<Univ, Long> {

	// In 쿼리는 여러 조건을 And하는 것이 힘들기에 하나의 문자열로 만들어서 처리
	@Query(value = "select u from Univ u "
		+ "where concat(u.college, '_', u.department) in (:college_department)")
	Set<Univ> findAllByInCollegeAndDepartment(@Param("college_department") List<String> concatData);
}
