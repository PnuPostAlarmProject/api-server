package com.ppap.ppap.domain.redis.repository;

import org.springframework.data.repository.CrudRepository;

import com.ppap.ppap.domain.redis.entity.BlackListToken;

public interface BlackListTokenRepository extends CrudRepository<BlackListToken, String> {

}
