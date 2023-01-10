package com.example.bot.m;/*
package com.example.bot;

import com.example.bot.model.Settings;
import com.example.bot.service.admin.ISettingService;
import com.example.bot.service.clientTrafic.IClientTraficService;
import com.example.bot.service.clientTrafic.IInboundsService;
import com.example.bot.service.clientTrafic.ISettingClientsService;
import com.google.zxing.WriterException;
import com.vdurmont.emoji.EmojiParser;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static java.lang.Math.toIntExact;

@Component
public class Admin extends TelegramLongPollingBot {

    @Autowired
    IInboundsService iInboundsService;

    @Autowired
    ISettingClientsService iSettingClientsService;

    @Autowired
    IClientTraficService iClientTraficService;

    @Autowired
    ISettingService iSettingService;

    @Transactional
    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            var firstname = update.getMessage().getFrom().getFirstName();
            var lastName = update.getMessage().getFrom().getLastName();
            var userName = update.getMessage().getFrom().getUserName();
            var userId = update.getMessage().getFrom().getId();
            var messageText = update.getMessage().getText();
            var chat_id = update.getMessage().getChatId();
            if (checkChannelMember(userId).equals("creator") || checkChannelMember(userId).equals("administrator")) {
                System.out.println(messageText);
                if (messageText.equals("/start")) {
                    sendMessageText(EmojiParser.parseToUnicode(":zap: ") + "hi", userId);
                    setKeyboard(chat_id);
                } else if (messageText.startsWith("/port")) {
                    var port = messageText.substring(messageText.lastIndexOf("/port ") + 6);
                    if (!port.equals("") && !port.startsWith("/port")) {
                        Settings settings = new Settings();
//                        settings = iSettingService.findByKeyEquals("port");
                        */
/*if (settings != null) {
                            settings.setKey(settings.getKey());
                        } else {
                            settings.setKey("port");
                        }*//*

                        settings.setId(1);
                        settings.setKey("port");
                        settings.setValue(port);
                        iSettingService.save(settings);
                        sendMessageText("ثبت شد", userId);
                    } else {
                        sendMessageText(" مثال: \t" + " /port 10 ", userId);
                    }

                } else if (messageText.startsWith("/qr")) {
                    var qr = messageText.substring(messageText.lastIndexOf("/qr ") + 4);
                    System.out.println(getQRCode(qr));
                    getQRCode(qr);
                }
               */
/* else if (messageText.equals("ساختن اشتراک")) {
                    SendMessage message = new SendMessage(); // Create a message object object
                    message.setChatId(chat_id);
                    message.setText("You send /start");
                    InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                    List<InlineKeyboardButton> rowInlines = new ArrayList<>();
                    InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                    inlineKeyboardButton.setText("Update message text");
                    inlineKeyboardButton.setCallbackData("update_msg_text");
                    rowInlines.add(inlineKeyboardButton);
                    // Set the keyboard to the markup
                    rowsInline.add(rowInlines);
                    // Add it to the message
                    markupInline.setKeyboard(rowsInline);
                    message.setReplyMarkup(markupInline);
                    try {
                        execute(message); // Sending our message object to user
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                } else {
                    onClosing();
                }*//*

*/
/*
            } else if (update.hasCallbackQuery()) {
                // Set variables
                String call_data = update.getCallbackQuery().getData();
                long message_id = update.getCallbackQuery().getMessage().getMessageId();

                if (call_data.equals("update_msg_text")) {
                    String answer = "Updated message text";
                    EditMessageText new_message = new EditMessageText();
                    new_message.setChatId(chat_id);
                    new_message.setMessageId(toIntExact(message_id));
                    new_message.setText(answer);
                    try {
                        execute(new_message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                } else {
                    sendMessageText("داداچ اشتباه میزنی", userId);
                    onClosing();
                }*//*

            } else {
                onClosing();
            }
        } else {
            onClosing();
        }
    }

    public String checkChannelMember(Long userId) throws TelegramApiException {
        GetChatMember getChatMember = new GetChatMember();
        getChatMember.setUserId(userId);
        getChatMember.setChatId("@thunder_fastvpn");
        return execute(getChatMember).getStatus();

    }


    public void setKeyboard(Long chat_id) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(chat_id);
        message.setText("Here is your keyboard");
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardButton = new KeyboardRow();
        keyboardButton.add("ساختن اشتراک");
       */
/* keyboardButton.add("1.2");
        keyboardButton.add("1.3");*//*

        keyboardRows.add(keyboardButton);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        message.setReplyMarkup(replyKeyboardMarkup);
        execute(message);
    }

    public String getQRCode(String config) {

        byte[] image = new byte[0];
        try {

            // Generate and Return Qr Code in Byte Array
            image = QRCodeGenerator.getQRCodeImage(config, 250, 250);

            // Generate and Save Qr Code Image in static/image folder
            QRCodeGenerator.generateQRCodeImage(config, 250, 250, "./src/main/resources/img/qrCode/QRCode.png");

        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }
        // Convert Byte Array into Base64 Encode String

        return Base64.getEncoder().encodeToString(image);
    }

    @SneakyThrows
    public void sendMessageText(String text, Long userId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(text);
        sendMessage.setChatId(String.valueOf(userId));
        execute(sendMessage);
    }

    @Override
    public String getBotToken() {
        return "5909727523:AAHxC2SxoVZ6Ev8UateQfqPirhuxuqK4kTU";
    }

    @Override
    public String getBotUsername() {
        return "gooolax_mamadbyavarBot";
    }
}
*/
