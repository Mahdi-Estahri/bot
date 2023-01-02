package com.example.bot.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "client_traffics2", indexes = {@Index(name = "idx_clienttraffics_email_unq2", columnList = "email", unique = true)})
public class ClientTraffics2 {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inbound_id")
    private Inbounds inboundId;

    @Column(name = "enable")
    private Integer enable;

    @Column(name = "email")
    private String email;

    @Column(name = "up")
    private Float upload;

    @Column(name = "down")
    private Float download;

    @Column(name = "expiry_time")
    private Long expiryTime;

    @Column(name = "total")
    private Long total;

}
