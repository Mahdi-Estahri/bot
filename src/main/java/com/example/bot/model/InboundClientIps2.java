package com.example.bot.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "inbound_client_ips2", indexes = {@Index(name = "idx_inboundclientips_unq2", columnList = "client_email", unique = true)})

public class InboundClientIps2 {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_db")
    private Integer id;

    @Column(name = "client_email")
    private String clientEmail;

    @Column(name = "ips")
    private String ips;

}
