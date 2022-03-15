package org.acme.spring.web;

import org.springframework.stereotype.Service;

@Service
public class GreetingBean {

    public String greet(String input) {
        return "HELLO " + input.toUpperCase() + "!";
    }
}
