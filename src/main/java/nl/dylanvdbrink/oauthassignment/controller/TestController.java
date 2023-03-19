package nl.dylanvdbrink.oauthassignment.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController()
@RequestMapping("/test")
public class TestController {

    @GetMapping("ping")
    public Map<String, String> pong() {
        return Map.of("result", "pong");
    }

}
