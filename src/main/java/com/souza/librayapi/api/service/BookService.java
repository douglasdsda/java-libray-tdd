package com.souza.librayapi.api.service;


import com.souza.librayapi.api.model.Book.Book;
import org.springframework.stereotype.Service;


public interface BookService {

    public Book save(Book any);

}
