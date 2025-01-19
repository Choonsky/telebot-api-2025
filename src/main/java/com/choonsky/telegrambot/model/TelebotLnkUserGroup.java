package com.choonsky.telegrambot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@ToString
@Table(name = "TELEBOT_LNK_USER_GROUP", schema = "TELEBOT_APP")
@NoArgsConstructor
@AllArgsConstructor
public class TelebotLnkUserGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private int id;
    @Column(name = "date_updated")
    @JsonProperty("updateDate")
    private LocalDateTime updateDate;
    @Column(name = "active")
    @JsonProperty("active")
    private boolean active;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private TelebotUser user;
    @OneToOne
    @JoinColumn(name = "group_id")
    private TelebotGroup group;

    public TelebotLnkUserGroup(TelebotUser user, TelebotGroup group) {
        this.updateDate = LocalDateTime.now();
        this.group = group;
        this.user = user;
        this.active = true;
    }
}
