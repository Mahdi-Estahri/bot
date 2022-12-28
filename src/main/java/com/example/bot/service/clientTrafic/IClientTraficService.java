package com.example.bot.service.clientTrafic;

import com.example.bot.model.ClientTraffics;
import org.springframework.data.repository.CrudRepository;

public interface IClientTraficService extends CrudRepository<ClientTraffics, Integer> {

    ClientTraffics getClientTrafficsByEmailEquals(String email);
}
