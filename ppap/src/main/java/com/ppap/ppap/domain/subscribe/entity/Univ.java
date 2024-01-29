package com.ppap.ppap.domain.subscribe.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "univ_tb", uniqueConstraints = {
	@UniqueConstraint(name = "uk_univ_college_department", columnNames = {"college", "department"})
})
public class Univ {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "univ_id")
	private Long id;

	@Column(nullable = false, length = 100)
	private String college;
	@Column(nullable = false, length = 100)
	private String department;

	@Builder
	public Univ(Long id, String college, String department) {
		this.id = id;
		this.college = college;
		this.department = department;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Univ univ = (Univ)o;
		return Objects.equals(getCollege(), univ.getCollege()) && Objects.equals(getDepartment(),
			univ.getDepartment());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getCollege(), getDepartment());
	}
}
