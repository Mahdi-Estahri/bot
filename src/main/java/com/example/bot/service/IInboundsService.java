package com.example.bot.service;

import com.example.bot.model.Inbounds;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IInboundsService extends CrudRepository<Inbounds, Integer> {
    Inbounds getInboundsByTagEndingWith(String port);
}
