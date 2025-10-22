package com.softwareengineering.team.campussecondhand.controller;

import com.softwareengineering.team.campussecondhand.entity.User;
import com.softwareengineering.team.campussecondhand.service.MessageService;
import com.softwareengineering.team.campussecondhand.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/message")
public class MessageController {
    private final MessageService messageService;
    private final UserService userService;
    
    public MessageController(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        this.userService = userService;
    }
    
    @PostMapping("/add")
    public String addMessage(
            @RequestParam Long sid,
            @RequestParam String content,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        if (authentication == null) {
            return "redirect:/login";
        }
        
        User user = userService.findByPhone(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }
        
        try {
            messageService.addMessage(user.getId(), sid, content);
            redirectAttributes.addFlashAttribute("message", "留言发送成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "留言发送失败：" + e.getMessage());
        }
        
        return "redirect:/product/" + sid;
    }
}