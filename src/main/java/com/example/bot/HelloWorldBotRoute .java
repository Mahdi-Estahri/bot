package com.example.bot;

import com.example.bot.model.ClientTraffics;
import com.example.bot.service.clientTrafic.IClientTraficService;
import io.github.ndanhkhoi.telegram.bot.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.telegram.telegrambots.meta.api.objects.Update;

@BotRoute
public class HelloWorldBotRoute {

    @Autowired
    IClientTraficService iClientTraficService;

    @CommandDescription("مشاهده حجم ترافیک")
    @CommandMapping(value = "/getusage", allowAllUserAccess = true)
    public String getUsage(Update update, @ChatId Long chatId) {
        System.out.println("chatId = " + chatId);

        ClientTraffics clientTraffics = iClientTraficService.findClientTrafficsById(2);
        String clientTraffic= " کاربر (" + update.getMessage().getChat().getFirstName() + " "+update.getMessage().getChat().getLastName() +"( \n"+
                " مقدار حجم خریداری شده: " + clientTraffics.getTotal() + "\n"+
                " مقدار حجم مصرف شده: " + clientTraffics.getDownload()+ clientTraffics.getUpload() +"\n" ;

        return clientTraffic;
    }
}
