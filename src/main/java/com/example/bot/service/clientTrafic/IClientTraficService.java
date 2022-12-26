package com.example.bot.service.clientTrafic;

import com.example.bot.model.ClientTraffics;
import com.example.bot.model.Inbounds;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IClientTraficService extends CrudRepository<ClientTraffics, Integer> {

}
