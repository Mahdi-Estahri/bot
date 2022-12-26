package com.example.bot.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "client_traffics", indexes = {@Index(name = "idx_clienttraffics_email_unq", columnList = "email", unique = true)})
public class ClientTraffics {
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
    private Integer upload;

    @Column(name = "down")
    private Integer download;

    @Column(name = "expiry_time")
    private Integer expiryTime;

    @Column(name = "total")
    private Integer total;

}
