package com.souza.librayapi.api.service;

import org.springframework.stereotype.Service;

import java.util.List;

public interface EmailService {

    void sendMails(String message, List<String> emailList);

}
