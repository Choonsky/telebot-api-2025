package com.choonsky.telegrambot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import com.choonsky.telegrambot.exception.ApiException;
import com.choonsky.telegrambot.exception.UnknownSeverityException;
import com.choonsky.telegrambot.model.Headers;
import com.choonsky.telegrambot.model.Severity;
import com.choonsky.telegrambot.model.TelebotGroup;
import com.choonsky.telegrambot.model.TelebotLnkUserGroup;
import com.choonsky.telegrambot.model.TelebotMessage;
import com.choonsky.telegrambot.model.TelebotUser;
import com.choonsky.telegrambot.repository.TelebotGroupRepository;
import com.choonsky.telegrambot.repository.TelebotLnkUserGroupRepository;
import com.choonsky.telegrambot.repository.TelebotMessageRepository;
import com.choonsky.telegrambot.telegram.BotComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.choonsky.telegrambot.model.Severity.INCORRECT_SEVERITY;

@RestController
@RequestMapping("/**")
public class TelegramBotController {

    private static final Logger logger = LoggerFactory.getLogger(TelegramBotController.class);

    private final BotComponent bot;
    private final TelebotGroupRepository groupRepo;
    private final TelebotLnkUserGroupRepository lnkRepo;
    private final TelebotMessageRepository msgRepo;

    @Autowired
    TelegramBotController(BotComponent bot, TelebotGroupRepository groupRepo,
                          TelebotLnkUserGroupRepository lnkRepo, TelebotMessageRepository msgRepo) {
        this.bot = bot;
        this.groupRepo = groupRepo;
        this.lnkRepo = lnkRepo;
        this.msgRepo = msgRepo;
    }

    @PostMapping
    @Operation(summary = "Returns a String about Telebot message")
    @ApiResponse(description = "Telegram Bot API exception: (detailed description)", responseCode = "480", content =
    @Content)
    public ResponseEntity<String> sendMessage(@ModelAttribute Headers headers, @RequestBody TelebotMessage msg)
            throws ApiException {

        if (msg.getSystem().trim().isEmpty())
            return new ResponseEntity<>("Telegram Bot API doesn't react to the message from unknown source!",
                    HttpStatus.OK);

        if (msg.getService().trim().isEmpty())
            return new ResponseEntity<>("Telegram Bot API doesn't react to the message about unknown service!",
                    HttpStatus.OK);

        if (msg.getContent().trim().isEmpty())
            return new ResponseEntity<>("Telegram Bot API can't send an empty message!",
                    HttpStatus.OK);

        Severity severityCode = Severity.safeValueOf(msg.getSeverity());

        if (severityCode == INCORRECT_SEVERITY)
            return new ResponseEntity<>("Telegram Bot API doesn't react to the message with unknown severity type!",
                    HttpStatus.OK);

        msg.setDateReceived(LocalDateTime.now());

        var groupList = getGroupList(severityCode);
        var chatIds = getChatList(groupList);

        if (chatIds.isEmpty()) {
            return new ResponseEntity<>("Telegram Bot API didn't found any user to send a message!",
                    HttpStatus.OK);
        }

        StringBuilder msgText = new StringBuilder();
        msgText.append(severityCode).append(" in ").append(msg.getService()).append(" (").append(msg.getSystem())
                .append(") : ").append(msg.getContent());

        for (String chatId : chatIds) {
            try {
                // logger.info("Sending message to chatId: {}", chatId);
                bot.sendNotification(chatId, msgText.toString());
            } catch (TelegramApiException e) {
                logger.error("Failed to send notification to chatId: {} with error: {}", chatId, e.getMessage());
            }
        }

        msg.setDateSent(LocalDateTime.now());
        msg.setContent(msg.getContent().substring(0,1000));
        msgRepo.save(msg);

        return new ResponseEntity<>("Telegram Bot API successfully sent a message \"" + msgText
                + "\" to " + groupList + " users!", HttpStatus.OK);
    }

    private List<String> getGroupList(Severity s) throws ApiException {
        List<String> groups = new ArrayList<>();
        switch (s) {
            case INFO -> groups.add("DEBUGGERS");
            case WARNING, ERROR -> groups.addAll(List.of("DEBUGGERS", "TECH_STAFF", "ADMINS"));
            case FATAL, PANIC -> groups.addAll(List.of("DEBUGGERS", "TECH_STAFF", "ADMINS", "ALL"));
            default -> throw new UnknownSeverityException(s.toString());
        }
        return groups;
    }

    private List<String> getChatList(List<String> groupList) {
        List<TelebotUser> usersToSend = new ArrayList<>();
        for (String group : groupList) {
            TelebotGroup g = groupRepo.findByGroupCode(group).orElse(null);
            var users = lnkRepo.findAllByGroup(g).stream().filter(TelebotLnkUserGroup::isActive).map(TelebotLnkUserGroup::getUser).toList();
            usersToSend.addAll(users);
        }
        return usersToSend.stream().map(TelebotUser::getChatId).distinct().toList();

    }
}