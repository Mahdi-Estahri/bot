package com.example.bot;

import io.github.ndanhkhoi.telegram.bot.BotAutoConfiguration;
import io.github.ndanhkhoi.telegram.bot.annotation.BotRoute;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class BotApplication {

    public static void main(String[] args) {
        SpringApplication.run(BotApplication.class, args);

       /* try {
//            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
//            telegramBotsApi.registerBot(new BotClass());
//            telegramBotsApi.registerBot( new HelloWorldBotRoute());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }*/
    }
}
