package com.example.bot;

import com.example.bot.service.clientTrafic.IClientTraficService;
import io.github.ndanhkhoi.telegram.bot.annotation.BotRoute;
import io.github.ndanhkhoi.telegram.bot.annotation.CommandDescription;
import io.github.ndanhkhoi.telegram.bot.annotation.CommandMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;

@BotRoute
public class BotController {

    @Autowired
    IClientTraficService iClientTraficService;

    @CommandDescription("شروع")
    @CommandMapping(value = "/start", allowAllUserAccess = true)
    public String start(Update update) {
        return "به ربات Thunder VPN خوش آمدید\n" + "برای ادامه و استفاده از ربات لطفا کانفیگ خود را اینجا بفرستید.";
    }

}
