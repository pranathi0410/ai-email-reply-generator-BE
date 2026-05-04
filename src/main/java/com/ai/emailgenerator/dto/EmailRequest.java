package com.ai.emailgenerator.dto;

public class EmailRequest {
    private String emailContent;
    private String tone;

    //getters & setters
    public String getEmailContent(){
        return emailContent;
    }
    public void setEmailContent(String emailContent){
        this.emailContent= emailContent;
    }

    public String getTone(){
        return tone;
    }
    public void setTone(String tone){
        this.tone=tone;
    }
}
