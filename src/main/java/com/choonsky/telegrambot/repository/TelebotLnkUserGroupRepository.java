package com.choonsky.telegrambot.repository;

import com.choonsky.telegrambot.model.TelebotGroup;
import com.choonsky.telegrambot.model.TelebotLnkUserGroup;
import com.choonsky.telegrambot.model.TelebotUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TelebotLnkUserGroupRepository extends JpaRepository<TelebotLnkUserGroup, Integer> {
    ArrayList<TelebotLnkUserGroup> findByUser(TelebotUser user);
    Optional<TelebotLnkUserGroup> findByUserAndGroup(TelebotUser u, TelebotGroup g);
    ArrayList<TelebotLnkUserGroup> findAllByGroup (TelebotGroup group);
    ArrayList<TelebotLnkUserGroup> findAllByUser (TelebotUser user);
}
