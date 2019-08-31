package com.jg.pi4Home.controllers;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListener;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.gpio.trigger.GpioCallbackTrigger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by josefgrinspun on 14.04.18.
 */
@RestController
public class pi4HomeController {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    GpioPinDigitalOutput openPin;
    GpioPinDigitalOutput closePin;
    GpioPinDigitalInput switchClosePin;
    GpioPinDigitalInput switchOpenPin;

    public pi4HomeController() {
        logInfo("Controller is beeing initialied");
        this.initPinsIfNeeded();
    }

    @RequestMapping("/")
    public String entryPoint() {
        logInfo("entryPoint was called");
        return "You are connected to my pi4Home server";
    }

    @RequestMapping("/open")
    public String openShutter() {
        logInfo("openShutter was called");
        try {
            initPinsIfNeeded();
            setPinLow(closePin);
            Thread.sleep(100);
            setPinHigh(openPin);
        } catch (Exception e) {
            logInfo("error:" + e.toString());
            return e.getMessage();
        }
        return "You are now opening the shutter;";
    }

    @RequestMapping("close")
    public String closeShutter() {
        try {
            initPinsIfNeeded();
            setPinLow(openPin);
            Thread.sleep(100);
            setPinHigh(closePin);
        } catch (Exception e) {
            logInfo("error:" + e.toString());
            return e.getMessage();
        }
        return "You are now closing the shutter!";
    }

    @RequestMapping("stop")
    public String stopMovingShutter() {
        try {
            initPinsIfNeeded();
            setPinLow(closePin);
            Thread.sleep(100);
            setPinLow(openPin);
        } catch (Exception e) {
            return e.getMessage();
        }
        return "You have stopped the shutter from moving further!";
    }

    private void initPinsIfNeeded() {
        if (this.arePinsInitialized()) {
            logInfo("pins are ready");
            return;
        }
        try {
            logInfo("pins needs to get initialized");
            openPin = GpioFactory.getInstance().provisionDigitalOutputPin(RaspiPin.GPIO_01, "openPin", PinState.LOW);
            openPin.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
            if (openPin != null) {
                logInfo("openPin init success");
            }
            closePin = GpioFactory.getInstance().provisionDigitalOutputPin(RaspiPin.GPIO_02, "closePin", PinState.LOW);
            closePin.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
            if (closePin != null) {
                logInfo("closePin init success");
            }

            switchClosePin = GpioFactory.getInstance().provisionDigitalInputPin(RaspiPin.GPIO_04, "switchClosePin", PinPullResistance.PULL_UP);
            switchClosePin.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
            switchClosePin.addListener((GpioPinListenerDigital) gpioPinDigitalStateChangeEvent -> {
                logInfo(gpioPinDigitalStateChangeEvent.toString());
                if (gpioPinDigitalStateChangeEvent.getState() == PinState.HIGH) {
                    pi4HomeController.this.closeShutter();
                } else {
                    pi4HomeController.this.stopMovingShutter();
                }
            });

            switchOpenPin = GpioFactory.getInstance().provisionDigitalInputPin(RaspiPin.GPIO_05, "switchOpenPin", PinPullResistance.PULL_UP);
            switchOpenPin.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);

            switchOpenPin.addListener((GpioPinListenerDigital) gpioPinDigitalStateChangeEvent -> {
                logInfo("edge: " + gpioPinDigitalStateChangeEvent.getEdge().getName() + "value: " + gpioPinDigitalStateChangeEvent.getEventType());
                if (gpioPinDigitalStateChangeEvent.getState() == PinState.HIGH) {
                    pi4HomeController.this.openShutter();
                } else {
                    pi4HomeController.this.stopMovingShutter();
                }
            });

            switchOpenPin.addTrigger(new GpioCallbackTrigger(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    if (switchOpenPin.isHigh()) {
                        pi4HomeController.this.openShutter();
                    } else if (switchOpenPin.isLow()) {
                        pi4HomeController.this.stopMovingShutter();
                    }

                    return null;
                }
            }));
        } catch (Exception e) {
            logInfo("error while init pins: " + e.toString());
        }

    }


    private Boolean arePinsInitialized() {
        boolean arePinsInitialized = openPin != null &
                                     closePin != null &
                                     switchClosePin != null &
                                     switchOpenPin != null;
        logInfo("are pins initialized: " + arePinsInitialized);
        return arePinsInitialized;
    }

    private void logInfo(String msg) {
        logger.log(Level.INFO, msg);
    }

    private void setPinLow(GpioPinDigitalOutput pin) {
        pin.low();

        if (pin.isLow()) {
            logInfo(pin.getName() + " is off");
        } else {
            logInfo(pin.getName() + " is still on!");
        }
    }

    private void setPinHigh(GpioPinDigitalOutput pin) {
        pin.high();
        if (pin.isHigh()) {
            logInfo(pin.getName() + " is on");
        } else {
            logInfo(pin.getName() + " is still off!");
        }
    }


}
