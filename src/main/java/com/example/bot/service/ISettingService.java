package com.example.bot.service;

import com.example.bot.model.Settings;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ISettingService extends CrudRepository<Settings, Integer> {
    Settings findByKeyEquals(String key);
}
