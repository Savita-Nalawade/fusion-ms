package com.example.fusion_ms;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class mlogs {
    @GetMapping("/mlogs")
    public String getData() {return  "Please provide maven jobs running logs and details" ; }
}
