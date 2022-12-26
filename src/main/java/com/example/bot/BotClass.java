package com.example.bot;

import com.example.bot.model.Inbounds;
import com.example.bot.model.dto.InboundSettingClientsDto;
import com.example.bot.service.clientTrafic.IInboundsService;
import com.example.bot.service.clientTrafic.ISettingClientsService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class BotClass extends TelegramLongPollingBot {

    @Autowired
    IInboundsService iInboundsService;

    @Autowired
    ISettingClientsService iSettingClientsService;

    @Override
    public void onUpdateReceived(Update update) {
        var firstname = update.getMessage().getFrom().getFirstName();
        var lastName = update.getMessage().getFrom().getLastName();
        var userName = update.getMessage().getFrom().getUserName();
        var userFullName = firstname != null ? firstname : "" + " " + lastName != null ? lastName : "" + " ( " + userName != null ? userName : "" + " )";
        if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage msg = new SendMessage();
            var messageText = update.getMessage().getText();
            var userId = update.getMessage().getFrom().getId();
            if (messageText.startsWith("vless")) {
                var configId = messageText.substring(messageText.lastIndexOf("://") + 3, messageText.indexOf("@"));
                msg.setText(configId);
                msg.setChatId(String.valueOf(userId));
                try {
                    execute(msg);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                var port = messageText.substring(messageText.lastIndexOf(":") + 1, messageText.indexOf("?"));
                Inbounds inbounds = iInboundsService.getInboundsByTagEndingWith(port);
                var inboundSetting = inbounds.getSettings();
                inboundSetting = inboundSetting.replace("{\n  \"clients\": ", "");
                inboundSetting = inboundSetting.replace("\n", "");
                inboundSetting = inboundSetting.replace(" ", "");
                inboundSetting = inboundSetting.replace(",\"decryption\":\"none\",\"fallbacks\":[]}", "");
                Set<InboundSettingClientsDto> inboundSettingClientsDtos = ConvertJsonToModelWeapon(inboundSetting);
                if (inboundSettingClientsDtos != null) {
                    inboundSettingClientsDtos.forEach(c -> {
                        if (c.getId().equals(configId)) {
                            String clientInfo = "کاربر: " + userFullName + "\n" + "نام کاربری: " + c.getEmail() + "\n" + "مقدار حجم خریداری شده(GB): " + c.getTotalGB() + "\n";
                            sendMessageText(clientInfo, userId);
                        }
                    });
                }
            }
        }
    }


    private Set<InboundSettingClientsDto> ConvertJsonToModelWeapon(String input) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(input, new TypeReference<Set<InboundSettingClientsDto>>() {
            });
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void sendMessageText(String text, Long userId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(text);
        sendMessage.setChatId(String.valueOf(userId));
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void setKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardButton = new KeyboardRow();
        keyboardButton.add("1.1");
        keyboardButton.add("1.2");
        keyboardButton.add("1.3");
        keyboardRows.add(keyboardButton);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
    }

    @Override
    public String getBotUsername() {
        return "thunder_reportBot";
    }

    @Override
    public String getBotToken() {
        return "5604897269:AAH2igtZlOjUGMX3q72ojXDsAx8pc2_IcwQ";
    }

}