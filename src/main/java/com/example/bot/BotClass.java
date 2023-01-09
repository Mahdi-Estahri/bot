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
import com.vdurmont.emoji.EmojiParser;
import ir.huri.jcal.JalaliCalendar;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class BotClass extends TelegramLongPollingBot {

    public String helloUser = "سلام به ربات رعدوبرق خوش اومدید! ";

    public String joinChannelForUserBot = " کاربر گرامی جهت استفاده از این ربات، ابتدا در کانال ما عضو شوید:\n " + EmojiParser.parseToUnicode(":zap: ") + "@thunder_fastvpn" + EmojiParser.parseToUnicode(":zap: \n") + "سپس دستور /start را مجددا ارسال نمایید ";

    public String selectOption = "برای ادامه و مشاهده جزئیات اشتراک خود کانفیگ (vmess یا vless) خود را ارسال نمایید";

    @Autowired
    IInboundsService iInboundsService;

    @Autowired
    ISettingClientsService iSettingClientsService;

    @Autowired
    IClientTraficService iClientTraficService;

    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        var firstname = update.getMessage().getFrom().getFirstName();
        var lastName = update.getMessage().getFrom().getLastName();
        var userName = update.getMessage().getFrom().getUserName();
        var userFullName = firstname != null ? firstname : "" + " " + lastName != null ? lastName : "" + " ( " + userName != null ? userName : "" + " )";
        if (update.hasMessage() && update.getMessage().hasText()) {
            var messageText = update.getMessage().getText();
            System.out.println(messageText);
            var userId = update.getMessage().getFrom().getId();
            if (messageText.equals("/start")) {
                if (checkChannelMember(update)) {
                    sendMessageText(EmojiParser.parseToUnicode(":zap: ") + helloUser + EmojiParser.parseToUnicode(":zap: \n") + selectOption, userId);
                } else {
                    sendMessageText(EmojiParser.parseToUnicode(":loudspeaker: ") + joinChannelForUserBot, userId);
                    onClosing();
                }
            }
            if (checkChannelMember(update)) {
                if (messageText.startsWith("vless")) {
                    var configId = messageText.substring(messageText.lastIndexOf("://") + 3, messageText.indexOf("@"));
                    var host = messageText.substring(messageText.lastIndexOf("@") + 1, messageText.indexOf(".mamadbyavar.ir:"));
                    var port = messageText.substring(messageText.lastIndexOf(":") + 1, messageText.indexOf("?"));
//                    if (host != null && host.equals("server2")) {
                    if (port != null && configId != null) {
                        Inbounds inbounds = iInboundsService.getInboundsByTagEndingWith(port);
                        if (inbounds != null) {
                            var inboundSetting = inbounds.getSettings();
                            inboundSetting = inboundSetting.replace("\n", "");
                            inboundSetting = inboundSetting.replace("\r", "");
                            inboundSetting = inboundSetting.replace("\t", "");
                            inboundSetting = inboundSetting.replace(" ", "");
                            inboundSetting = inboundSetting.replace("{\"clients\":", "");
                            inboundSetting = inboundSetting.replace(",\"decryption\":\"none\",\"fallbacks\":[]}", "");
                            Set<InboundSettingClientsDto> inboundSettingClientsDtos = ConvertJsonToModelWeapon(inboundSetting);
                            if (inboundSettingClientsDtos != null) {
                                inboundSettingClientsDtos.forEach(c -> {
                                    if (c.getId().equals(configId)) {
                                        ClientTraffics clientTraffics = iClientTraficService.getClientTrafficsByEmailEquals(c.getEmail());
                                        Float usage = clientTraffics.getDownload() + clientTraffics.getUpload();
                                        String clientInfo = (clientTraffics.getEnable() != 0 ? EmojiParser.parseToUnicode(":bulb: ") : EmojiParser.parseToUnicode(":black_large_square: ")) + "وضعیت: " + (clientTraffics.getEnable() != 0 ? "فعال" : "غیرفعال") + "\n";
                                        clientInfo += EmojiParser.parseToUnicode(":bust_in_silhouette: ") + "کاربر: " + userFullName + "\n";
//                                        clientInfo += "نام کاربری: " + c.getEmail() + "\n";
                                        clientInfo += EmojiParser.parseToUnicode(":floppy_disk: ") + calculateTeraffic(c.getTotalGB(), "خریداری شده") + "\n";
                                        clientInfo += EmojiParser.parseToUnicode(":arrow_down_small: ") + calculateTeraffic(usage, "مصرف شده") + "\n";
                                        if (c.getTotalGB() != 0) {
                                            var total = (c.getTotalGB() - usage);
                                            if (total > 0) {
                                                clientInfo += EmojiParser.parseToUnicode(":white_check_mark: ") + calculateTeraffic(total, "باقیمانده") + "\n";
                                            } else {
                                                clientInfo += EmojiParser.parseToUnicode(":white_check_mark: ") + "حجم شما تمام شده است." + "\n";
                                            }
                                        } else {
                                            clientInfo += EmojiParser.parseToUnicode(":white_check_mark: ") + calculateTeraffic(0F, "باقیمانده") + "\n";
                                        }
                                        clientInfo += EmojiParser.parseToUnicode(":busts_in_silhouette: ") + "تعداد کاربر مجاز: " + (c.getLimitIp() != 0 ? c.getLimitIp() : "بدون محدودیت") + "\n";
                                        if (c.getExpiryTime() != null && c.getExpiryTime() != 0) {
                                            clientInfo += EmojiParser.parseToUnicode(":date: ") + " تاریخ اتمام اشتراک: " + calculateTime(c.getExpiryTime()) + "\n";
                                        }
                                        sendMessageText(clientInfo, userId);
                                    }
                                });
                            } else {
                                sendMessageText(EmojiParser.parseToUnicode(":no_entry_sign: ") + " مشخصات شما یافت نشد لطفا به پشتیبان Thunder پیام دهید. " + EmojiParser.parseToUnicode(":no_entry_sign: "), userId);
                            }
                        } else {
                            sendMessageText(EmojiParser.parseToUnicode(":no_entry_sign: ") + " کانفیگ ارسالی اشتباه می باشد. " + EmojiParser.parseToUnicode(":no_entry_sign: "), userId);
                        }
                    } else {
                        sendMessageText(EmojiParser.parseToUnicode(":no_entry_sign: ") + " کانفیگ ارسالی اشتباه می باشد. " + EmojiParser.parseToUnicode(":no_entry_sign: "), userId);
                    }
//                    } else {
//                        sendMessageText(EmojiParser.parseToUnicode(":no_entry_sign: ") + " کانفیگ ارسالی اشتباه می باشد. " + EmojiParser.parseToUnicode(":no_entry_sign: "), userId);
//                    }
                }
                if (messageText.startsWith("vmess")) {
                    var str = messageText.substring(messageText.lastIndexOf("://") + 3);
                    byte[] decoded = Base64.getDecoder().decode(str);
                    String decodedStr = new String(decoded, StandardCharsets.UTF_8);
                    VmessDto vmessDto = ConvertJsonToModelVmess(decodedStr);
//                    if (vmessDto != null && vmessDto.getAdd().equals("server2.mamadbyavar.ir")) {
                    Inbounds inbounds = iInboundsService.getInboundsByTagEndingWith(vmessDto.getPort());
                    if (inbounds != null) {
                        var inboundSetting = inbounds.getSettings();
                        inboundSetting = inboundSetting.replace("\r", "");
                        inboundSetting = inboundSetting.replace("\t", "");
                        inboundSetting = inboundSetting.replace("\n", "");
                        inboundSetting = inboundSetting.replace(" ", "");
                        inboundSetting = inboundSetting.replace("{\"clients\":", "");
                        inboundSetting = inboundSetting.replace(",\"disableInsecureEncryption\":false}", "");
                        Set<InboundSettingClientsDto> inboundSettingClientsDtos = ConvertJsonToModelWeapon(inboundSetting);
                        if (inboundSettingClientsDtos != null) {
                            inboundSettingClientsDtos.forEach(c -> {
                                if (c.getId().equals(vmessDto.getId())) {
                                    ClientTraffics clientTraffics = iClientTraficService.getClientTrafficsByEmailEquals(c.getEmail());
                                    Float usage = clientTraffics.getDownload() + clientTraffics.getUpload();
                                    String clientInfo = (clientTraffics.getEnable() != 0 ? EmojiParser.parseToUnicode(":bulb: ") : EmojiParser.parseToUnicode(":black_large_square: ")) + "وضعیت: " + (clientTraffics.getEnable() != 0 ? "فعال" : "غیرفعال") + "\n";
                                    clientInfo += EmojiParser.parseToUnicode(":bust_in_silhouette: ") + "کاربر: " + userFullName + "\n";
//                                        clientInfo += "نام کاربری: " + c.getEmail() + "\n";
                                    clientInfo += EmojiParser.parseToUnicode(":floppy_disk: ") + calculateTeraffic(c.getTotalGB(), "خریداری شده") + "\n";
                                    clientInfo += EmojiParser.parseToUnicode(":arrow_down_small: ") + calculateTeraffic(usage, "مصرف شده") + "\n";
                                    if (c.getTotalGB() != 0) {
                                        clientInfo += EmojiParser.parseToUnicode(":white_check_mark: ") + calculateTeraffic((c.getTotalGB() - usage), "باقیمانده") + "\n";
                                    } else {
                                        clientInfo += EmojiParser.parseToUnicode(":white_check_mark: ") + calculateTeraffic(0F, "باقیمانده") + "\n";
                                    }
                                    clientInfo += EmojiParser.parseToUnicode(":busts_in_silhouette: ") + "تعداد کاربر مجاز: " + (c.getLimitIp() != 0 ? c.getLimitIp() : "بدون محدودیت") + "\n";
                                    if (c.getExpiryTime() != null && c.getExpiryTime() != 0) {
                                        clientInfo += EmojiParser.parseToUnicode(":date: ") + " تاریخ اتمام اشتراک: " + calculateTime(c.getExpiryTime()) + "\n";
                                    }
                                    sendMessageText(clientInfo, userId);
                                }
                            });
                        } else {
                            sendMessageText(EmojiParser.parseToUnicode(":no_entry_sign: ") + " مشخصات شما یافت نشد لطفا به پشتیبان Thunder پیام دهید. " + EmojiParser.parseToUnicode(":no_entry_sign: "), userId);
                        }
                    } else {
                        sendMessageText(EmojiParser.parseToUnicode(":no_entry_sign: ") + " مشخصات شما یافت نشد لطفا به پشتیبان Thunder پیام دهید. " + EmojiParser.parseToUnicode(" :no_entry_sign: "), userId);
                    }
//                    } else {
//                        sendMessageText(EmojiParser.parseToUnicode(":no_entry_sign: ") + " کانفیگ ارسالی غیر مجاز می باشد. " + EmojiParser.parseToUnicode(" :no_entry_sign: "), userId);
//                    }
                }
            } else {
                onClosing();
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

    private String calculateTeraffic(Float hajm, String text) {
        if (hajm == 0 && !text.startsWith("مصرف")) {
            return "مقدار حجم " + text + ": نامحدود \n";
        }
        if (hajm <= 1024) {
            return "مقدار حجم " + text + "(B): " + String.format("%.2f", hajm) + " بایت \n";
        }
        if (hajm <= 1024 * 1024) {
            return "مقدار حجم " + text + "(KB): " + String.format("%.2f", hajm / 1024) + " کیلوبایت \n";
        }
        if (hajm <= Float.parseFloat("1073741824")) {
            return "مقدار حجم " + text + " (MB):" + String.format("%.2f", hajm / (1024 * 1024)) + " مگابایت \n ";
        }
        if (hajm <= Float.parseFloat("1099511627776")) {
            return "مقدار حجم " + text + "(GB): " + String.format("%.2f", hajm / Float.parseFloat("1073741824")) + " گیگابایت \n";
        }
        if (hajm <= Float.parseFloat("1125899906842624")) {
            return "مقدار حجم " + text + "(TB): " + String.format("%.2f", hajm / Float.parseFloat("1099511627776")) + " ترابایت \n";
        } else {
            return "\n";
        }
    }

    private String calculateTime(Long expiryTime) {
        Date date = new Date(expiryTime);
        JalaliCalendar shamsiExpireDate = new JalaliCalendar(date);
        return shamsiExpireDate.getYear() + "/" + shamsiExpireDate.getMonth() + "/" + shamsiExpireDate.getDay();
    }

    @SneakyThrows
    public void sendMessageText(String text, Long userId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(text);
        sendMessage.setChatId(String.valueOf(userId));
        execute(sendMessage);
    }


    public Boolean checkChannelMember(Update update) throws TelegramApiException {
        var userId = update.getMessage().getFrom().getId();
        GetChatMember getChatMember = new GetChatMember();
        getChatMember.setUserId(userId);
        getChatMember.setChatId("@thunder_fastvpn");
        var userChatStatus = execute(getChatMember).getStatus();
        return !userChatStatus.equals("left");

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