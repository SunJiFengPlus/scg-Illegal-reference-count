package org.example.normalprovider.demos.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BasicController {
    
    @PostMapping("/hello")
    public String hello(@RequestBody String body) {
        return body;
    }
}
