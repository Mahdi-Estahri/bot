package com.example.bot.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "inbound_client_ips", indexes = {@Index(name = "idx_inboundclientips_unq", columnList = "client_email", unique = true)})
public class InboundClientIps {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "client_email")
    private String clientEmail;

    @Column(name = "ips")
    private String ips;
}
