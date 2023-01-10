package com.example.bot.service.clientTrafic;

import com.example.bot.model.Inbounds;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IInboundsService extends CrudRepository<Inbounds, Integer> {
//    List<Inbounds> getAllByPort(Integer port);

    Inbounds getInboundsByTagEndingWith(String port);
}
