package com.choonsky.telegrambot.repository;

import com.choonsky.telegrambot.model.TelebotLnkUserGroup;
import com.choonsky.telegrambot.model.TelebotUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TelebotUserRepository extends JpaRepository<TelebotUser, Integer> {
    Optional<TelebotUser> findById(int id);
    Optional<TelebotUser> findByChatId(String chatId);
    Optional<TelebotUser> findByUserId(long userId);
}
