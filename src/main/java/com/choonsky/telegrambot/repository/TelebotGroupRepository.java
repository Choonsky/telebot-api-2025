package com.choonsky.telegrambot.repository;

import com.choonsky.telegrambot.model.TelebotGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TelebotGroupRepository extends JpaRepository<TelebotGroup, Integer> {
    Optional<TelebotGroup> findById(int id);
    Optional<TelebotGroup> findByGroupCode(String code);
}
