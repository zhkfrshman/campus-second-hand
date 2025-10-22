package com.softwareengineering.team.campussecondhand.service;

import com.softwareengineering.team.campussecondhand.entity.Message;

import java.util.List;

public interface MessageService {
    List<Message> findBySid(Long sid);
    Message addMessage(Long uid, Long sid, String content);
}