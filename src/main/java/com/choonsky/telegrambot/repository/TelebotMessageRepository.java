package com.choonsky.telegrambot.repository;

import com.choonsky.telegrambot.model.TelebotGroup;
import com.choonsky.telegrambot.model.TelebotMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TelebotMessageRepository extends JpaRepository<TelebotMessage, Integer> {
    Optional<TelebotMessage> findById(int id);
}
