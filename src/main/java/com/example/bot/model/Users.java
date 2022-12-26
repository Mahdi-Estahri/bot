package com.example.bot.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_db")
    private Integer id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

}
