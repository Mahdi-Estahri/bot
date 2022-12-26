package com.example.bot.service.clientTrafic;

import com.example.bot.model.InboundSettingClients;
import org.springframework.data.repository.CrudRepository;

public interface ISettingClientsService extends CrudRepository<InboundSettingClients, Integer> {
    InboundSettingClients getByUuidEquals(String id);
}
