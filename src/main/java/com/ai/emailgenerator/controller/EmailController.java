package com.ai.emailgenerator.controller;

import com.ai.emailgenerator.dto.EmailRequest;
import com.ai.emailgenerator.dto.EmailResponse;
import com.ai.emailgenerator.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://mail.google.com"
})
@RestController
@RequestMapping("/api")
public class EmailController {
  @Autowired
  private EmailService emailService;
  @PostMapping("/generate")
    public EmailResponse generateReply(@RequestBody EmailRequest request){
      return emailService.generateReply(request);
  }
}
