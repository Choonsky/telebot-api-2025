package com.choonsky.telegrambot.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Headers {
    private String channelCode;
    private String username;
    private String lang;

    public Headers(String channelCode, String username,String lang) {
        this.channelCode = channelCode;
        this.username = username;
        this.lang=lang;
    }
}
