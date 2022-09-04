package com.example.bot.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "TELE_USER", schema = "bot")
@SequenceGenerator(name = "sequence_db", schema = "bot", sequenceName = "TELE_BOT.SEQ_TELE_USER", allocationSize = 1)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_db")
    private Long id;

    @Column(name = "FIRST_NAME")
    private String name;
}
