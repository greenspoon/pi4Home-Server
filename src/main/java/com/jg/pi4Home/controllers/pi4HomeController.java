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

    @RequestMapping("/open")
    public String openShutter() {
        return "You are now opening the shutter;";
    }

    @RequestMapping("close")
    public String closeShutter() {
        return "You are now closing the shutter!";
    }

    @RequestMapping("stop")
    public String stopMovingShutter() {
        return "You have stopped the shutter from moving further!";
    }
}
