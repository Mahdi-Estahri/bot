package com.example.bot.service.clientTrafic;

import com.example.bot.model.Inbounds;
import org.springframework.data.repository.CrudRepository;

public interface IInboundsService extends CrudRepository<Inbounds, Integer> {
    Inbounds findInboundsByTagContaining(String port);

    Inbounds getInboundsByTagEndingWith(String port);
}
