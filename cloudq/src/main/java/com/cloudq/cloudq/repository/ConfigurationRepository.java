package com.cloudq.cloudq.repository;

import com.cloudq.cloudq.model.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, Long> {
    Optional<Configuration> findByKey(String key);
    void deleteByKey(String key);
}