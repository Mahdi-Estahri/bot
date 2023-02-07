package com.example.bot.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InboundSettingClientsDto {
    private String id, flow, email;

    private Integer limitIp, alterId;

    private Float totalGB;

    private Long expiryTime;
}
