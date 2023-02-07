package com.example.bot.service;

import com.example.bot.model.user.UserInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserInfoService extends CrudRepository<UserInfo, Integer> {
}
