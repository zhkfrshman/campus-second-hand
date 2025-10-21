package com.softwareengineering.team.campussecondhand.service;

import com.softwareengineering.team.campussecondhand.entity.Message;

import java.util.List;

public interface MessageService {
    Message addMessage(Long sid, Long uid, String content);
    List<Message> findBySid(Long sid);
}
