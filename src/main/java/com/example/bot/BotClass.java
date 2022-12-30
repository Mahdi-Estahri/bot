package com.example.bot;

import com.example.bot.model.ClientTraffics;
import com.example.bot.model.Inbounds;
import com.example.bot.model.dto.InboundSettingClientsDto;
import com.example.bot.model.dto.VmessDto;
import com.example.bot.service.clientTrafic.IClientTraficService;
import com.example.bot.service.clientTrafic.IInboundsService;
import com.example.bot.service.clientTrafic.ISettingClientsService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.huri.jcal.JalaliCalendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMemberCount;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.GetUpdates;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberMember;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class BotClass extends TelegramLongPollingBot {


    @Autowired
    IInboundsService iInboundsService;

    @Autowired
    ISettingClientsService iSettingClientsService;

    @Autowired
    IClientTraficService iClientTraficService;

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
            if (messageText.equals("/start")) {
                checkChannelMember(update);
                sendMessageText("به ربات Thunder VPN خوش آمدید\n" + "برای ادامه و استفاده از ربات لطفا کانفیگ خود را اینجا بفرستید.", userId);
            }
            if (messageText.startsWith("vless")) {
                var configId = messageText.substring(messageText.lastIndexOf("://") + 3, messageText.indexOf("@"));
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
                            String clientInfo = "کاربر: " + userFullName + "\n" + "نام کاربری: " + c.getEmail() + "\n";
                            clientInfo += calculateTotal(c.getTotalGB());
                            clientInfo += calculateUsage(c.getEmail());
                            clientInfo += "تعداد کاربر مجاز: " + (c.getLimitIp() != 0 ? c.getLimitIp() : "بدون محدودیت") + "\n";
                            if (c.getExpiryTime() != null && c.getExpiryTime() != 0) {
                                clientInfo += " تاریخ اتمام اشتراک: " + calculateTime(c.getExpiryTime()) + "\n";
                            }
                            sendMessageText(clientInfo, userId);
                        }
                    });
                }
            }
            if (messageText.startsWith("vmess")) {
                var str = messageText.substring(messageText.lastIndexOf("://") + 3);
                byte[] bytes = new byte[0];
                byte[] decoded = Base64.getDecoder().decode(str);
                String decodedStr = new String(decoded, StandardCharsets.UTF_8);
                VmessDto vmessDto = ConvertJsonToModelVmess(decodedStr);
                Inbounds inbounds = iInboundsService.getInboundsByTagEndingWith(vmessDto.getPort());
                var inboundSetting = inbounds.getSettings();
                inboundSetting = inboundSetting.replace("{\n  \"clients\": ", "");
                inboundSetting = inboundSetting.replace("\n", "");
                inboundSetting = inboundSetting.replace(" ", "");
                inboundSetting = inboundSetting.replace(",\"disableInsecureEncryption\":false}", "");
                Set<InboundSettingClientsDto> inboundSettingClientsDtos = ConvertJsonToModelWeapon(inboundSetting);
                if (inboundSettingClientsDtos != null) {
                    inboundSettingClientsDtos.forEach(c -> {
                        if (c.getId().equals(vmessDto.getId())) {
                            String clientInfo = "کاربر: " + userFullName + "\n" + "نام کاربری: " + c.getEmail() + "\n";
                            clientInfo += calculateTotal(c.getTotalGB());
                            clientInfo += calculateUsage(c.getEmail());
                            clientInfo += "تعداد کاربر مجاز: " + (c.getLimitIp() != 0 ? c.getLimitIp() : "بدون محدودیت") + "\n";
                            if (c.getExpiryTime() != null && c.getExpiryTime() != 0) {
                                clientInfo += " تاریخ اتمام اشتراک: " + calculateTime(c.getExpiryTime()) + "\n";
                            }
                            sendMessageText(clientInfo, userId);
                        }
                    });
                }
//                sendMessageText(decodedStr, userId);
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

    private VmessDto ConvertJsonToModelVmess(String input) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(input, new TypeReference<VmessDto>() {
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

    private String calculateTotal(Long hajm) {
        if (hajm == 0) {
            return "مقدار حجم خریداری شده: نامحدود \n";
        }
        if (hajm <= 1024) {
            return "مقدار حجم خریداری شده(B): " + hajm + "بایت \n";
        }
        if (hajm <= 1024 * 1024) {
            return "مقدار حجم خریداری شده(KB): " + hajm / 1024 + "کیلوبایت \n";
        }
        if (hajm <= Long.parseLong("1073741824")) {
            return "مقدار حجم خریداری شده(MB): " + hajm / (1024 * 1024) + "مگابایت \n";
        }
        if (hajm <= Long.parseLong("1099511627776")) {
            return "مقدار حجم خریداری شده(GB): " + hajm / Long.parseLong("1073741824") + "گیگابایت \n";
        }
        if (hajm <= Long.parseLong("1125899906842624")) {
            return "مقدار حجم خریداری شده(TB): " + hajm / Long.parseLong("1099511627776") + "ترابایت \n";
        } else {
            return "\n";
        }
    }

    private String calculateTime(Long expiryTime) {
        Date date = new Date(expiryTime);
        JalaliCalendar shamsiExpireDate = new JalaliCalendar(date);
        return shamsiExpireDate.getYear() + "/" + shamsiExpireDate.getMonth() + "/" + shamsiExpireDate.getDay();
    }

    private String calculateUsage(String email) {
        ClientTraffics clientTraffics = iClientTraficService.getClientTrafficsByEmailEquals(email);
        var usage = clientTraffics.getDownload() + clientTraffics.getUpload();
        if (usage <= 1024) {
            return "مقدار حجم مصرف شده(B): " + usage + "\n";
        }
        if (usage <= 1024 * 1024) {
            return "مقدار حجم مصرف شده(KB): " + usage / 1024 + "\n";
        }
        if (usage <= Long.parseLong("1073741824")) {
            return "مقدار حجم مصرف شده(MB): " + usage / (1024 * 1024) + "\n";
        }
        if (usage <= Long.parseLong("1099511627776")) {
            return "مقدار حجم مصرف شده(GB): " + usage / Long.parseLong("1073741824") + "\n";
        }
        if (usage <= Long.parseLong("1125899906842624")) {
            return "مقدار حجم مصرف شده(TB): " + usage / Long.parseLong("1099511627776") + "\n";
        } else {
            return "\n";
        }
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

    public void checkChannelMember(Update update){
        GetUpdates getUpdates=new GetUpdates();
        List<String> a=new ArrayList<>();
        a.add("chat_member");
        GetChat getChat=new GetChat();
        Chat chat = new Chat();
        getChat.setChatId("@thunder_fastvpn");
        GetChatMember getChatMember = new GetChatMember();
        getChatMember.setUserId(update.getMessage().getFrom().getId());
        getChatMember.setChatId("@thunder_fastvpn");
        getUpdates.setAllowedUpdates(a);
//        chatMember.setUser(update.getMessage().getFrom());
        System.out.println(getUpdates.getAllowedUpdates());
        System.out.println(update);
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