package com.example.bot.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "settings")
public class Settings {
    @Id
    private Integer id;

    @Column(name = "key")
    private String key;

    @Column(name = "value")
    private String value;
}
