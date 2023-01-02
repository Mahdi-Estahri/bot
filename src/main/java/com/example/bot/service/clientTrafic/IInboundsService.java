package com.example.bot.service.clientTrafic;

import com.example.bot.model.Inbounds;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IInboundsService extends CrudRepository<Inbounds, Integer> {
    Inbounds findInboundsByTagContaining(String port);

    Inbounds getInboundsByTagEndingWith(String port);
}
