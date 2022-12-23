package com.example.bot.service.clientTrafic;

import com.example.bot.model.ClientTraffics;
import org.springframework.data.repository.CrudRepository;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

public interface IClientTraficService extends CrudRepository<ClientTraffics,Integer> {

    ClientTraffics findClientTrafficsById(Integer userId);
}
