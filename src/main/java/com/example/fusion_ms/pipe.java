package com.example.fusion_ms;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class pipe {
    @GetMapping("/pipe")
    public String getData() {return  "welcome to jenkins pipelines" ; }
}
