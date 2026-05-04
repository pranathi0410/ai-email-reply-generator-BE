package com.ai.emailgenerator.dto;

public class EmailResponse {
    private String reply;
    public EmailResponse(String reply){
        this.reply=reply;
    }
    public String getReply(){
        return reply;
    }
}
