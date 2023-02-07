package com.example.bot.m;

import com.example.bot.QRCodeGenerator;
import com.example.bot.model.ClientTraffics;
import com.example.bot.model.Inbounds;
import com.example.bot.model.dto.InboundSettingClientsDto;
import com.example.bot.model.dto.VmessDto;
import com.example.bot.model.user.UserInfo;
import com.example.bot.service.IClientTraficService;
import com.example.bot.service.IInboundsService;
import com.example.bot.service.IUserInfoService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.WriterException;
import com.vdurmont.emoji.EmojiParser;
import ir.huri.jcal.JalaliCalendar;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.Set;

@Service
public class UserBotClass extends TelegramLongPollingBot {

    private static final String hello_user = "سلام به ربات رعدوبرق خوش اومدید! ";

    private static final String bot_token = "5956481568:AAFctqSPDbyeNyX9xPckfxeQmDjBltlHTbc";

    private static final String bot_user_name = "Thunder_support3_bot";

    private static final String channel_user_name = "@thunder_fastvpn";

    private static final String joinChannelForUserBot = " کاربر گرامی جهت استفاده از این ربات، ابتدا در کانال ما عضو شوید:\n " + EmojiParser.parseToUnicode(":zap: ") + "@thunder_fastvpn" + EmojiParser.parseToUnicode(":zap: \n") + "سپس دستور /start را مجددا ارسال نمایید ";

    private static final String selectOption = "برای ادامه و مشاهده جزئیات اشتراک خود کانفیگ (vmess یا vless) خود را ارسال نمایید";

    @Autowired
    private IInboundsService iInboundsService;

    @Autowired
    private IUserInfoService iUserInfoService;

    @Autowired
    private IClientTraficService iClientTraficService;

    @Transactional
    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            UserInfo userInfo = new UserInfo();
            var userId = update.getMessage().getFrom().getId();
            var firstname = update.getMessage().getFrom().getFirstName();
            var lastName = update.getMessage().getFrom().getLastName();
            var userName = update.getMessage().getFrom().getUserName();
            var userFullName = firstname + " " + (lastName != null ? lastName : "");
            var messageText = update.getMessage().getText();
            var userRole = checkChannelMember(userId);
            userInfo.setUserId(userId);
            userInfo.setFirstName(firstname);
            userInfo.setLastName(lastName);
            userInfo.setUserName(userName);
            userInfo.setRole(userRole);
            System.out.println(messageText);
            if (messageText.equals("/start")) if (userRole.equals("kicked")) {
                sendMessageText("اکانت شما نامعتبر است.", userId);
                onClosing();
            } else if (!userRole.equals("left") && !userRole.equals("restricted"))
                sendMessageText(EmojiParser.parseToUnicode(":zap: ") + hello_user + EmojiParser.parseToUnicode(":zap: \n") + selectOption, userId);
            else {
                sendMessageText(EmojiParser.parseToUnicode(":loudspeaker: ") + joinChannelForUserBot, userId);
                onClosing();
            }
            if (userRole.equals("member") || userRole.equals("administrator") || userRole.equals("creator")) {
                if (messageText.startsWith("vless")) {
                    var configId = messageText.substring(messageText.lastIndexOf("://") + 3, messageText.indexOf("@"));
                    var host = messageText.substring(messageText.lastIndexOf("@") + 1, messageText.indexOf(".mamadbyavar.ir:"));
                    var port = messageText.substring(messageText.lastIndexOf(":") + 1, messageText.indexOf("?"));
                    getQRCode(messageText);
                    if (host.equals("server2")) {
                        Inbounds inbounds = iInboundsService.getInboundsByTagEndingWith(port);
                        if (inbounds != null) {
                            var inboundSetting = inbounds.getSettings().replace("\n", "").replace("\r", "").replace("\t", "").replace(" ", "").replace("{\"clients\":", "").replace(",\"decryption\":\"none\",\"fallbacks\":[]}", "");
                            Set<InboundSettingClientsDto> inboundSettingClientsDtoSets = ConvertJsonToModelWeapon(inboundSetting);
                            if (inboundSettingClientsDtoSets != null) inboundSettingClientsDtoSets.forEach(c -> {
                                if (c.getId().equals(configId)) {
                                    generateMessage(userInfo, userId, userFullName, userRole, c);
                                }
                            });
                            else
                                sendMessageText(EmojiParser.parseToUnicode(":no_entry_sign: ") + " مشخصات شما یافت نشد لطفا به پشتیبان Thunder پیام دهید. " + EmojiParser.parseToUnicode(":no_entry_sign: "), userId);
                        } else
                            sendMessageText(EmojiParser.parseToUnicode(":no_entry_sign: ") + " کانفیگ ارسالی اشتباه می باشد. " + EmojiParser.parseToUnicode(":no_entry_sign: "), userId);
                    } else
                        sendMessageText(EmojiParser.parseToUnicode(":no_entry_sign: ") + " کانفیگ ارسالی اشتباه می باشد. " + EmojiParser.parseToUnicode(":no_entry_sign: "), userId);
                }
                if (messageText.startsWith("vmess")) {
                    var str = messageText.substring(messageText.lastIndexOf("://") + 3);
                    byte[] decoded = Base64.getDecoder().decode(str);
                    String decodedStr = new String(decoded, StandardCharsets.UTF_8);
                    VmessDto vmessDto = ConvertJsonToModelVmess(decodedStr);
                    if (vmessDto != null && vmessDto.getAdd().equals("server2.mamadbyavar.ir")) {
                        Inbounds inbounds = iInboundsService.getInboundsByTagEndingWith(vmessDto.getPort());
                        if (inbounds != null) {
                            var inboundSetting = inbounds.getSettings().replace("\r", "").replace("\t", "").replace("\n", "").replace(" ", "").replace("{\"clients\":", "").replace(",\"disableInsecureEncryption\":false}", "");
                            Set<InboundSettingClientsDto> inboundSettingClientsDtos = ConvertJsonToModelWeapon(inboundSetting);
                            if (inboundSettingClientsDtos != null) {
                                final boolean[] isInDatabase = {false};
                                inboundSettingClientsDtos.forEach(c -> {
                                    getQRCode(messageText);
                                    if (c.getId().equals(vmessDto.getId())) {
                                        isInDatabase[0] = true;
                                        generateMessage(userInfo, userId, userFullName, userRole, c);
                                    }
                                });
                                if (!isInDatabase[0])
                                    sendMessageText(EmojiParser.parseToUnicode(":no_entry_sign: ") + " مشخصات شما یافت نشد لطفا به پشتیبان Thunder پیام دهید. " + EmojiParser.parseToUnicode(" :no_entry_sign: "), userId);
                            } else
                                sendMessageText(EmojiParser.parseToUnicode(":no_entry_sign: ") + " مشخصات شما یافت نشد لطفا به پشتیبان Thunder پیام دهید. " + EmojiParser.parseToUnicode(":no_entry_sign: "), userId);
                        } else
                            sendMessageText(EmojiParser.parseToUnicode(":no_entry_sign: ") + " مشخصات شما یافت نشد لطفا به پشتیبان Thunder پیام دهید. " + EmojiParser.parseToUnicode(" :no_entry_sign: "), userId);
                    } else
                        sendMessageText(EmojiParser.parseToUnicode(":no_entry_sign: ") + " کانفیگ ارسالی غیر مجاز می باشد. " + EmojiParser.parseToUnicode(" :no_entry_sign: "), userId);
                }
            } else onClosing();
        }
    }

    private void generateMessage(UserInfo userInfo, Long userId, String userFullName, String userRole, InboundSettingClientsDto c) {
        userInfo.setEmail(c.getEmail());
        ClientTraffics clientTraffics = iClientTraficService.getClientTrafficsByEmailEquals(c.getEmail());
        userInfo.setEnable(clientTraffics.getEnable());
        Float usage = clientTraffics.getDownload() + clientTraffics.getUpload();
        String clientInfo = (clientTraffics.getEnable() != 0 ? EmojiParser.parseToUnicode(":bulb: ") : EmojiParser.parseToUnicode(":black_large_square: ")) + "وضعیت: " + (clientTraffics.getEnable() != 0 ? "فعال" : "غیرفعال") + "\n" + (EmojiParser.parseToUnicode(":bust_in_silhouette: ") + "کاربر: " + userFullName + "\n");
        if (userRole.equals("creator") || userRole.equals("administrator"))
            clientInfo += "نام کاربری: " + c.getEmail() + "\n";
        clientInfo += EmojiParser.parseToUnicode(":floppy_disk: ") + calculateTraffic(c.getTotalGB(), "خریداری شده") + "\n";
        clientInfo += EmojiParser.parseToUnicode(":arrow_down_small: ") + calculateTraffic(usage, "مصرف شده") + "\n";
        if (c.getTotalGB() != 0) {
            var total = (c.getTotalGB() - usage);
            if (total > 0)
                clientInfo += EmojiParser.parseToUnicode(":white_check_mark: ") + calculateTraffic(total, "باقیمانده") + "\n";
            else clientInfo += EmojiParser.parseToUnicode(":white_check_mark: ") + "حجم شما تمام شده است." + "\n";
        } else
            clientInfo += EmojiParser.parseToUnicode(":white_check_mark: ") + calculateTraffic(0F, "باقیمانده") + "\n";
        clientInfo += EmojiParser.parseToUnicode(":busts_in_silhouette: ") + "تعداد کاربر مجاز: " + (c.getLimitIp() != 0 ? c.getLimitIp() : "بدون محدودیت") + "\n";
        if (c.getExpiryTime() != null && c.getExpiryTime() != 0)
            clientInfo += EmojiParser.parseToUnicode(":date: ") + " تاریخ اتمام اشتراک: " + calculateTime(c.getExpiryTime()) + "\n";
        iUserInfoService.save(userInfo);
        sendQrCode(clientInfo, userId);
    }

    private Set<InboundSettingClientsDto> ConvertJsonToModelWeapon(String input) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(input, new TypeReference<>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private VmessDto ConvertJsonToModelVmess(String input) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(input, new TypeReference<>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String calculateTraffic(Float volume, String text) {
        if (volume == 0 && !text.startsWith("مصرف")) return "مقدار حجم " + text + ": نامحدود \n";
        if (volume < 1024) return "مقدار حجم " + text + "(B): " + String.format("%.2f", volume) + " بایت \n";
        if (volume < 1024 * 1024)
            return "مقدار حجم " + text + "(KB): " + String.format("%.2f", volume / 1024) + " کیلوبایت \n";
        if (volume < Float.parseFloat("1073741824"))
            return "مقدار حجم " + text + " (MB):" + String.format("%.2f", volume / (1024 * 1024)) + " مگابایت \n ";
        if (volume < Float.parseFloat("1099511627776"))
            return "مقدار حجم " + text + "(GB): " + String.format("%.2f", volume / Float.parseFloat("1073741824")) + " گیگابایت \n";
        if (volume <= Float.parseFloat("1125899906842624"))
            return "مقدار حجم " + text + "(TB): " + String.format("%.2f", volume / Float.parseFloat("1099511627776")) + " ترابایت \n";
        else return "\n";
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

    @SneakyThrows
    public void sendQrCode(String text, Long userId) {
        try {
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(userId);
            sendPhoto.setCaption(text);
            sendPhoto.setPhoto(new InputFile(new File("src/main/resources/img/qrCode/QRCode.png")));
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void getQRCode(String config) {
        byte[] image = new byte[0];
        try {
            image = QRCodeGenerator.getQRCodeImage(config, 350, 350);
            QRCodeGenerator.generateQRCodeImage(config, 350, 350, "src/main/resources/img/qrCode/QRCode.png");
        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }
        Base64.getEncoder().encodeToString(image);
    }

    public String checkChannelMember(Long userId) throws TelegramApiException {
        GetChatMember getChatMember = new GetChatMember();
        getChatMember.setUserId(userId);
        getChatMember.setChatId(channel_user_name);
        return execute(getChatMember).getStatus();
    }

    @Override
    public String getBotUsername() {
        return bot_user_name;
    }

    @Override
    public String getBotToken() {
        return bot_token;
    }
}
