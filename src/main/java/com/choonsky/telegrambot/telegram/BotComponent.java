package com.choonsky.telegrambot.telegram;

import com.choonsky.telegrambot.listener.MqListener;
import jakarta.annotation.PostConstruct;
import com.choonsky.telegrambot.exception.TelebotException;
import com.choonsky.telegrambot.exception.UnknownSeverityException;
import com.choonsky.telegrambot.model.Severity;
import com.choonsky.telegrambot.model.TelebotGroup;
import com.choonsky.telegrambot.model.TelebotLnkUserGroup;
import com.choonsky.telegrambot.model.TelebotUser;
import com.choonsky.telegrambot.repository.TelebotGroupRepository;
import com.choonsky.telegrambot.repository.TelebotLnkUserGroupRepository;
import com.choonsky.telegrambot.repository.TelebotUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Slf4j
@Component
public class BotComponent extends TelegramLongPollingBot {

    private final TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
    private final TelebotUserRepository userRepo;
    private final TelebotGroupRepository groupRepo;
    private final TelebotLnkUserGroupRepository lnkRepo;

    String botName;
    String botToken;

    @PostConstruct
    public void init() {
        try {
            botsApi.registerBot(this);
            log.info("Telegram bot has been registered!");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Autowired
    BotComponent(@Value("${telegram.botToken}") String botToken,
                 @Value("${telegram.botName}") String botName, TelebotUserRepository userRepo,
                 TelebotGroupRepository groupRepo, TelebotLnkUserGroupRepository lnkRepo, DefaultBotOptions options)
            throws TelegramApiException {
        super(options, botToken);
        this.botToken = botToken;
        this.botName = botName;
        this.userRepo = userRepo;
        this.groupRepo = groupRepo;
        this.lnkRepo = lnkRepo;
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage()) {

            // analyze the message
            var msg = update.getMessage();
            var chatId = String.valueOf(msg.getChatId());
            var userName = msg.getFrom().getUserName();
            var firstName = msg.getFrom().getFirstName();
            var lastName = msg.getFrom().getLastName();
            var userId = msg.getFrom().getId();
            StringBuilder nameExt = new StringBuilder();
            if (!firstName.isEmpty()) nameExt.append(firstName);
            if (!(userName == null || userName.isEmpty())) nameExt.append(" \"").append(userName).append("\"");
            if (!(lastName == null || lastName.isEmpty())) nameExt.append(" ").append(lastName);
            var userNameExt = nameExt.toString();
            var text = msg.getText();

            // find or add a user
            TelebotUser user = resolveUser(chatId, userId, userName, userNameExt);

            // react
            try {
                resolveUpdate(user, text);
            } catch (Exception e) {
                log.error("Got an exception during resolving! " + e.getMessage());
                throw new RuntimeException(e);
            }
        } else {
            log.warn("Got an update without a message!");
        }

    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void onRegister() {
        System.out.println("-------> Bot has been registered!");
        super.onRegister();
    }

    public void sendNotification(String chatId, String msg) throws TelegramApiException {
        var response = new SendMessage(chatId, msg);
        execute(response);
    }

    private TelebotUser resolveUser(String chatId, long userId, String userName, String userNameExt) {

        TelebotUser u = userRepo.findByChatId(chatId).orElse(userRepo.findByUserId(userId).orElse(null));

        // username not exists
        if (u == null) {
            u = new TelebotUser(userId);
            u.setChatId(chatId);
            u.setUserName(userName);
            u.setUserNameExt(userNameExt);
            userRepo.save(u);
            try {
                showStart(u);
                showHelp(u, false, false);
            } catch (TelegramApiException e) {
                // TODO: process the exception;
            }
        }

        u.setChatId(chatId);
        u.setUserName(userName);
        u.setUserNameExt(userNameExt);
        userRepo.save(u);

        return u;
    }

    private void resolveUpdate(TelebotUser user, String text) throws TelegramApiException, TelebotException, UnknownSeverityException {
        var isRegistered = isRegisteredCheck(user);
        var isAdmin = isAdminCheck(user);

        switch (text) {
            case "/users" -> showUsers(user, isRegistered);
            case "/groups" -> showGroups(user, isAdmin);
            case "/start" -> showStart(user);
            case "/help" -> showHelp(user, isRegistered, isAdmin);
//            case "/topSecretCommand" -> setAdmin(user);
            default -> otherCommands(user, isAdmin, text);
        }
    }

    private void otherCommands(TelebotUser user, boolean isAdmin, String text) throws TelegramApiException, TelebotException, UnknownSeverityException {
        if (text.contains("/user")) workWithUsers(user, isAdmin, text);
        else if (text.contains("/send")) workWithMessage(user, isAdmin, text);
        else if (text.toUpperCase().contains("FUCK")) sendNotification(user.getChatId(), "I CAN FIND YOU!!!" +
                " Don't you ever say such a thing!");
        else sendNotification(user.getChatId(), "I don't understand \"" + text + "\"!");
    }

    private void showUsers(TelebotUser user, boolean isRegistered) throws TelegramApiException {
        if (isRegistered) {
            List<TelebotUser> users =
                    userRepo.findAll().stream().sorted(Comparator.comparingInt(TelebotUser::getId)).toList();
            StringBuilder sb = new StringBuilder();
            for (TelebotUser u : users) {
                sb.append(u.getId()).append(" - ").append(u.getUserNameExt()).append("\n");
            }
            sendNotification(user.getChatId(), sb.toString());
        } else {
            sendNotification(user.getChatId(), "Sorry, you have no rights to see users list!");
        }
    }

    private void setAdmin(TelebotUser user) throws TelegramApiException {
        if (user.getLnkGroups().isEmpty()) {
            TelebotGroup group = groupRepo.findByGroupCode("ADMINS").orElse(null);
            if (group == null) {
                group = new TelebotGroup(0, LocalDateTime.now(), "ADMINS", "Admin users");
                groupRepo.save(group);
                sendNotification(user.getChatId(), "Couldn't find ADMINS group, created one!");
            }
            TelebotLnkUserGroup lnk = new TelebotLnkUserGroup(user, group);
            lnk.setActive(true);
            lnkRepo.save(lnk);
            sendNotification(user.getChatId(), "You were successfully added to ADMINS group!");
        } else {
            sendNotification(user.getChatId(), "Sorry, you are already a group member!");
        }
    }

    private void showGroups(TelebotUser user, boolean isAdmin) throws TelegramApiException {
        if (isAdmin) {
            List<TelebotGroup> groups = groupRepo.findAll();
            StringBuilder sb = new StringBuilder();
            for (TelebotGroup g : groups) {
                sb.append(g.getId()).append(" - ").append(g.getGroupCode()).append("\n");
            }
            sendNotification(user.getChatId(), sb.toString());
        } else {
            sendNotification(user.getChatId(), "Sorry, you have no rights to see groups list!");
        }
    }

    private void showHelp(TelebotUser user, boolean isRegistered, boolean isAdmin) throws TelegramApiException {
        StringBuilder sb = new StringBuilder("/start - hello text\n");
        sb.append("/help - list of allowed commands\n");
        if (isRegistered) {
            sb.append("/users - show users (all registered users)\n");
            if (isAdmin) {
                sb.append("/user XX - show user id=XX groups (ADMINS only)\n");
                sb.append("/user XX add to YY - add group YY to user XX (ADMINS only)\n");
                sb.append("/user XX remove from YY - remove user XX from group YY (ADMINS only)\n");
                sb.append("/user XX delete - delete user XX (ADMINS only)\n");
                sb.append("/groups - show groups (ADMINS only)\n");
                sb.append("/send \"Some text\" to %userId% - send message to user (ADMINS only)\n");
                sb.append("/send \"Some text\" as %severity% - send message to all receivers (ADMINS only)\n");
            }
        }
        sendNotification(user.getChatId(), sb.toString());
    }

    private void showStart(TelebotUser user) throws TelegramApiException {
        String sb = "Hello fellow user " + user.getUserNameExt() + "!\n" + "This is a C-Technology (Bishkek) messages bot.\n" +
                "To receive messages you should be added to any user group by bot admin.\n\n" +
                "Have a nice day!\nFor the list of allowed commands type /help";
        sendNotification(user.getChatId(), sb);
    }

    private void workWithUsers(TelebotUser user, boolean isAdmin, String text) throws TelegramApiException {
        if (isAdmin) {
            String[] s = text.split("\\s+");
            if (!"/user".equals(s[0])) sendNotification(user.getChatId(), "I don't understand command \"" + text +
                    "\"!");
            int id;
            if (s.length >= 2) {
                try {
                    id = Integer.parseInt(s[1]);
                } catch (Exception e) {
                    sendNotification(user.getChatId(), "Invalid user ID: \"" + s[1] + "\"!");
                    return;
                }
                TelebotUser u = userRepo.findById(id).orElse(null);
                if (u == null) {
                    sendNotification(user.getChatId(), "Can't find user ID: \"" + id + "\"!");
                    return;
                }
                if (s.length >= 5) {
                    switch (s[2].toLowerCase(Locale.ROOT)) {
                        case "add" -> addUserToGroup(user, s[1], s[4]);
                        case "remove" -> removeUserFromGroup(user, s[1], s[4]);
                        case "delete" -> deleteUser(user, s[1]);
                        default -> sendNotification(user.getChatId(), "I don't understand command \"" + text + "\"!");
                    }
                }
                u = userRepo.findById(id).orElse(null);
                List<String> groups =
                        u.getLnkGroups().stream().filter(TelebotLnkUserGroup::isActive).map(g -> g.getGroup().getGroupCode()).toList();
                sendNotification(user.getChatId(), "User " + u.getUserNameExt() + " (ID: " + u.getId() + ") in " +
                        "groups: " + groups);
            }
        } else {
            sendNotification(user.getChatId(), "Sorry, you have no rights to assign users to groups!");
        }
    }

    private void workWithMessage(TelebotUser user, boolean isAdmin, String text) throws TelegramApiException, UnknownSeverityException, TelebotException {
        if (isAdmin) {
            String[] s = text.split("\"");
            String command = s[0].trim();
            if (!"/send".equals(command)) {
                sendNotification(user.getChatId(), "I don't understand command \"" + command +
                        "\"!");
                return;
            }
            if (s.length != 3) {
                sendNotification(user.getChatId(), "Command invalid (wrong \" count)!");
                return;
            }
            String[] tail = s[2].trim().split("\\s+");
            if (tail.length != 2) {
                sendNotification(user.getChatId(), "Command invalid (wrong params count)!");
                return;
            }
            if ("TO".equalsIgnoreCase(tail[0])) {
                sendNotification(getUser(user, tail[1]).getChatId(), s[1].trim());
                sendNotification(user.getChatId(), "Message was sent to user " + tail[1]);
                return;
            }
            String severity = tail[1].trim();
            Severity severityCode = Severity.safeValueOf(severity);

            List<String> groups = new ArrayList<>();
            switch (severityCode) {
                case INFO -> groups.add("DEBUGGERS");
                case WARNING, ERROR -> groups.addAll(List.of("DEBUGGERS", "TECH_STAFF", "ADMINS"));
                case FATAL, PANIC -> groups.addAll(List.of("DEBUGGERS", "TECH_STAFF", "ADMINS", "ALL"));
                default -> throw new UnknownSeverityException(severityCode.toString());
            }
            sendNotification(user.getChatId(), "Sending to groups: " + groups);
            List<TelebotUser> usersToSend = new ArrayList<>();
            for (String group : groups) {
                TelebotGroup g = groupRepo.findByGroupCode(group).orElse(null);
                var users = lnkRepo.findAllByGroup(g).stream().filter(TelebotLnkUserGroup::isActive).map(TelebotLnkUserGroup::getUser).toList();
                sendNotification(user.getChatId(),
                        "Adding group: " + g.getGroupCode() + " of users: " + users.stream().map(TelebotUser::getId).toList());
                usersToSend.addAll(users);
            }
            List<String> chatIds = usersToSend.stream().map(TelebotUser::getChatId).distinct().toList();
            for (String chatId : chatIds) {
                try {
                    sendNotification(user.getChatId(), "Sending to " + chatId);
                    sendNotification(chatId, s[1].trim());
                } catch (TelegramApiException e) {
                    sendNotification(user.getChatId(), "Exception " + e.getMessage());
                }
            }
        } else {
            sendNotification(user.getChatId(), "Sorry, you have no rights to send messages!");
        }
    }

    private void deleteUser(TelebotUser user, String userId) throws TelegramApiException {
        TelebotUser u = getUser(user, userId);
        Set<TelebotLnkUserGroup> lnks = u.getLnkGroups();
        lnkRepo.deleteAll(lnks);
        userRepo.delete(u);
        sendNotification(user.getChatId(), "Deleted user " + u.getUserNameExt()
                + " (ID: " + u.getId() + ") and its group links");
    }

    private void addUserToGroup(TelebotUser user, String userId, String groupCode) throws TelegramApiException {
        TelebotUser u = getUser(user, userId);
        TelebotGroup g = getGroup(user, groupCode);
        TelebotLnkUserGroup lnk = lnkRepo.findByUserAndGroup(u, g).orElse(null);
        if (lnk == null) {
            lnk = new TelebotLnkUserGroup(u, g);
        } else {
            lnk.setActive(true);
        }
        lnkRepo.save(lnk);
        sendNotification(user.getChatId(), "Added user " + u.getUserNameExt()
                + " (ID: " + u.getId() + ") to group " + groupCode);
    }

    private void removeUserFromGroup(TelebotUser user, String userId, String groupCode) throws TelegramApiException {
        TelebotUser u = getUser(user, userId);
        TelebotGroup g = getGroup(user, groupCode);
        TelebotLnkUserGroup lnk = lnkRepo.findByUserAndGroup(u, g).orElse(null);
        if (lnk != null) {
            lnk.setActive(false);
            lnkRepo.save(lnk);
            sendNotification(user.getChatId(), "Removed user " + u.getUserNameExt()
                    + " (ID: " + u.getId() + ") from group " + groupCode);
        } else {
            sendNotification(user.getChatId(), "User " + u.getUserNameExt()
                    + " (ID: " + u.getId() + ") wasn't in group " + groupCode);
        }
    }

    private TelebotUser getUser(TelebotUser user, String userId) throws TelegramApiException {
        int id;
        TelebotUser u;
        try {
            id = Integer.parseInt(userId);
        } catch (Exception e) {
            sendNotification(user.getChatId(), "Cannot parse user ID: " + userId);
            throw new TelegramApiException();
        }
        u = userRepo.findById(id).orElse(null);
        if (u == null) {
            sendNotification(user.getChatId(), "Cannot find user ID: " + id);
            throw new TelegramApiException();
        }
        return u;
    }

    private TelebotGroup getGroup(TelebotUser user, String groupCode) throws TelegramApiException {
        TelebotGroup g = groupRepo.findByGroupCode(groupCode).orElse(null);
        if (g == null) {
            sendNotification(user.getChatId(), "Cannot find group " + groupCode);
            throw new TelegramApiException();
        }
        return g;
    }

    boolean isRegisteredCheck(TelebotUser user) {
        List<TelebotLnkUserGroup> lnk = lnkRepo.findByUser(user);
        return !lnk.isEmpty();
    }

    boolean isAdminCheck(TelebotUser user) {
        if (user.getLnkGroups() == null) return false;
        List<String> groups =
                user.getLnkGroups().stream().filter(TelebotLnkUserGroup::isActive).map(g -> g.getGroup().getGroupCode()).toList();
        return groups.contains("ADMINS");
    }

}
