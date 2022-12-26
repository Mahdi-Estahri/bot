package com.example.bot.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class InboundSettingDto {
    private String clients;
    private String decryption;
    private String fallbacks;

}
