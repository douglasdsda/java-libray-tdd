package com.souza.librayapi.service;


import com.souza.librayapi.api.model.Book.Book;

import java.util.Optional;


public interface BookService {

    public Book save(Book any);

    Optional<Book> getById(Long id);

    void deleteById(Long id);

    Book update(Book book);
}
