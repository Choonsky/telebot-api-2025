package com.choonsky.telegrambot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@Table(name = "TELEBOT_GROUP", schema = "TELEBOT_APP")
@NoArgsConstructor
@AllArgsConstructor
public class TelebotGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private int id;
    @Column(name = "date_updated")
    @JsonProperty("updateDate")
    private LocalDateTime updateDate;
    @Column(name = "group_code")
    @JsonProperty("groupCode")
    private String groupCode;
    @Column(name = "description")
    @JsonProperty("description")
    private String description;

}
