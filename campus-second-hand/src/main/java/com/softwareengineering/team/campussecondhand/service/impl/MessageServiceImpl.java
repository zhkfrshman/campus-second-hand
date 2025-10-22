package com.softwareengineering.team.campussecondhand.service.impl;

import com.softwareengineering.team.campussecondhand.entity.Message;
import com.softwareengineering.team.campussecondhand.repository.MessageRepository;
import com.softwareengineering.team.campussecondhand.service.MessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    
    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }
    
    @Override
    public List<Message> findBySid(Long sid) {
        return messageRepository.findBySidOrderByCreatedAtDesc(sid);
    }
    
    @Override
    @Transactional
    public Message addMessage(Long uid, Long sid, String content) {
        Message message = new Message();
        message.setUid(uid);
        message.setSid(sid);
        message.setContent(content);
        return messageRepository.save(message);
    }
}