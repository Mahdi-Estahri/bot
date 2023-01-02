package com.example.bot.service.clientTrafic;

import com.example.bot.model.Inbounds;
import com.example.bot.model.Inbounds2;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IInboundsService2 extends CrudRepository<Inbounds2, Integer> {
    Inbounds2 findInbounds2ByTagContaining(String port);

    Inbounds2 getInbounds2ByTagEndingWith(String port);
}