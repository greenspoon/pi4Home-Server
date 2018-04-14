package com.jg.pi4Home.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by josefgrinspun on 14.04.18.
 */
@RestController
public class pi4HomeController {

    @RequestMapping("/")
    public String entryPoint() {
        return "You are on my pi4Home server";
    }
}
