package com.softwareengineering.team.campussecondhand.service.impl;

import com.softwareengineering.team.campussecondhand.entity.Message;
import com.softwareengineering.team.campussecondhand.repository.MessageRepository;
import com.softwareengineering.team.campussecondhand.service.MessageService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public Message addMessage(Long sid, Long uid, String content) {
        Message m = new Message();
        m.setSid(sid);
        m.setUid(uid);
        m.setContent(content);
        m.setDisplay(1);
        return messageRepository.save(m);
    }

    @Override
    public List<Message> findBySid(Long sid) {
        return messageRepository.findBySidAndDisplay(sid, 1);
    }
}