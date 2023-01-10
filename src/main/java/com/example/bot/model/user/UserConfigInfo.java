package com.example.bot.model.user;

import com.example.bot.model.ClientTraffics;
import com.example.bot.model.InboundClientIps;
import com.example.bot.model.Inbounds;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "user_config_info")
public class UserConfigInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_user_config_info")
    private Integer id;

    @Column(name = "uuid")
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inbounds_id")
    private Inbounds inbounds;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_traffics_id")
    private ClientTraffics clientTraffics;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_info_id")
    private UserInfo userInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inbound_client_ips")
    private InboundClientIps inboundClientIps;

    @Column(name = "config_text")
    private String configText;

    @Column(name = "qr_code")
    private String qrCode;


}
