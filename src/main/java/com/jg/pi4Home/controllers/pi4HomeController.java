package com.jg.pi4Home.controllers;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by josefgrinspun on 14.04.18.
 */
@RestController
public class pi4HomeController {

    GpioPinDigitalOutput openPin;
    GpioPinDigitalOutput closePin;

    @RequestMapping("/")
    public String entryPoint() {
        return "You are connected my pi4Home server";
    }

    @RequestMapping("/open")
    public String openShutter() {
        try {
            initPins();
            closePin.low();
            openPin.high();

        } catch ( Exception e) {
            return e.getMessage();
        }
        return "You are now opening the shutter;";
    }

    @RequestMapping("close")
    public String closeShutter() {
        try {
            initPins();
            openPin.low();
            closePin.high();
        } catch (Exception e) {
            return e.getMessage();
        }
        return "You are now closing the shutter!";
    }

    @RequestMapping("stop")
    public String stopMovingShutter() {
        try {
            initPins();
            openPin.low();
            closePin.low();
        } catch (Exception e) {
            return e.getMessage();
        }
        return "You have stopped the shutter from moving further!";
    }

    private void initPins() {
        if (!this.arePinsInitialized()) return;
        openPin = GpioFactory.getInstance().provisionDigitalOutputPin(RaspiPin.GPIO_00, PinState.LOW);
        closePin = GpioFactory.getInstance().provisionDigitalOutputPin(RaspiPin.GPIO_01, PinState.LOW);

    }

    private Boolean arePinsInitialized() {
        return openPin != null & closePin != null;
    }


}
