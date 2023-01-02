package com.example.bot.service.clientTrafic;

import com.example.bot.model.ClientTraffics2;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IClientTraficService2 extends CrudRepository<ClientTraffics2, Integer> {

    ClientTraffics2 getClientTraffics2ByEmailEquals(String email);
}
