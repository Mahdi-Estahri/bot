package com.example.bot.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InboundSettingClientsDto {

    private String id;
    private String flow;
    private String email;
    private Integer limitIp;
    private Integer alterId;
    private Float totalGB;
    private Long expiryTime;
}
