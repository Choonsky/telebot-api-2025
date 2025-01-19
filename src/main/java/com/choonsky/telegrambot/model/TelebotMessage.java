package com.choonsky.telegrambot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "TELEBOT_MESSAGE", schema = "TELEBOT_APP")
@NoArgsConstructor
@AllArgsConstructor
public class TelebotMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private int id;
    @Column(name = "severity", nullable = false)
    @JsonProperty("severity")
    private String severity;
    @Column(name = "system", nullable = false)
    @JsonProperty("system")
    private String system;
    @Column(name = "service", nullable = false)
    @JsonProperty("service")
    private String service;
    @Column(name = "content", nullable = false)
    @JsonProperty("content")
    private String content;
    @Column(name = "date_received")
    @JsonProperty("dateReceived")
    private LocalDateTime dateReceived;
    @Column(name = "date_sent")
    @JsonProperty("dateSent")
    private LocalDateTime dateSent;

    public TelebotMessage(String severity, String system, String service, String content) {
        this.severity = severity;
        this.system = system;
        this.service = service;
        this.content = content;
        this.dateReceived = LocalDateTime.now();
    }

}
