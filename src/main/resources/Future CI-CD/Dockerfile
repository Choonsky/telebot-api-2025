FROM openjdk:17

ADD /target/TelegramBotAPI.jar  /usr/src/app/TelegramBotAPI.jar

WORKDIR /usr/src/app

EXPOSE 8099

ENTRYPOINT ["java", "-jar", "TelegramBotAPI.jar"]