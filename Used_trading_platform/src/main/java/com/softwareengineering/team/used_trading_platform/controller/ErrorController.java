package com.softwareengineering.team.used_trading_platform.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController {

    @RequestMapping(value = "/error2")
    public String error() {
        return "error";
    }

}
