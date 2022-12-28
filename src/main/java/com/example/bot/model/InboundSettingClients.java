package com.example.bot.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;

@Entity
@Immutable
@Table(name = "inbound_setting_clients")
@Getter
@Setter
public class InboundSettingClients {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_db")
    private Integer id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "client_email")
    private String clientEmail;

    @Column(name = "flow")
    private String flow;

    @Column(name = "limitIp")
    private Integer limitIp;

    @Column(name = "totalGB")
    private Long totalGB;

    @Column(name = "expiryTime")
    private Long expiryTime;

}
