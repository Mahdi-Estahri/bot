package com.example.bot.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "inbounds", indexes = {@Index(name = "idx_inbounds_tag_port_unq", columnList = "tag, port", unique = true)})
public class Inbounds {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users userId;

    @Column(name = "up")
    private Long upload;

    @Column(name = "down")
    private Long download;

    @Column(name = "total")
    private Long total;

    @Column(name = "remark")
    private String remark;

    @Column(name = "enable")
    private Boolean enable;

    @Column(name = "expiry_time")
    private Long expiryTime;

    @Column(name = "listen")
    private String listen;

    @Column(name = "port")
    private Integer port;

    @Column(name = "protocol")
    private String protocol;

    @Column(name = "settings")
    private String settings;

    @Column(name = "stream_settings")
    private String streamSettings;

    @Column(name = "tag")
    private String tag;

    @Column(name = "sniffing")
    private String sniffing;
}
