package com.choonsky.telegrambot.config;

import com.choonsky.telegrambot.exception.ApiException;
import com.choonsky.telegrambot.exception.WrongPortException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.telegram.telegrambots.bots.DefaultBotOptions;

@EnableWebMvc
@Configuration
public class WebConfig {

    @Value("${telegram.proxyType}")
    String proxyType;

    @Value("${telegram.proxyHost}")
    String proxyHost;

    @Value("${telegram.proxyPort}")
    String proxyPort;

    @Bean
    public DefaultBotOptions defaultBotOptions() throws ApiException {
        DefaultBotOptions options = new DefaultBotOptions();
        if (!"NO_PROXY".equals(proxyType)) {
            options.setProxyType(DefaultBotOptions.ProxyType.HTTP);
            options.setProxyHost(proxyHost);
            try {
                options.setProxyPort(Integer.parseInt(proxyPort));
            } catch (Exception e) {
                throw new WrongPortException(proxyPort);
            }
        }
        return options;
    }


}
