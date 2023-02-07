package com.example.bot.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VmessDto {
    private String port;

    private Integer aid;

    private String host, path, type, add, net, sni, tls, id, ps, v, alpn, scy;
}
