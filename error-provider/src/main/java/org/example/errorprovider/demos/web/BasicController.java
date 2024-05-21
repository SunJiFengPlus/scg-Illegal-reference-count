package org.example.errorprovider.demos.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BasicController {
    
    @PostMapping("/err")
    public String error(@RequestBody String body) throws InterruptedException {
        Thread.sleep(1000);
        return body;
    }
}
