package com.choonsky.telegrambot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Entity
@ToString
@Table(name = "TELEBOT_USER", schema = "TELEBOT_APP")
@NoArgsConstructor
@AllArgsConstructor
public class TelebotUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private int id;
    @Column(name = "date_created")
    @JsonProperty("createDate")
    private LocalDateTime createDate;
    @Column(name = "date_updated")
    @JsonProperty("updateDate")
    private LocalDateTime updateDate;
    @Column(name = "chat_id", unique = true, nullable = false)
    @JsonProperty("chatId")
    private String chatId;
    @Column(name = "user_id", unique = true, nullable = false)
    @JsonProperty("userId")
    private long userId;
    // User name like @Choonsky
    @Column(name = "user_name", nullable = false)
    @JsonProperty("userName")
    private String userName;
    // Full complex user name like Stanislav "Choonsky" Nemirovsky
    @Column(name = "user_name_ext", nullable = false)
    @JsonProperty("userNameExt")
    private String userNameExt;
    @OneToMany(mappedBy="user")
    @Fetch(FetchMode.SELECT)
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<TelebotLnkUserGroup> lnkGroups;

    public TelebotUser (long userId) {
        this.createDate = LocalDateTime.now();
        this.updateDate = LocalDateTime.now();
        this.userId = userId;
    }

}
