package com.example.bot.service;

import com.example.bot.model.ClientTraffics;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IClientTraficService extends CrudRepository<ClientTraffics, Integer> {
    ClientTraffics getClientTrafficsByEmailEquals(String email);
}
