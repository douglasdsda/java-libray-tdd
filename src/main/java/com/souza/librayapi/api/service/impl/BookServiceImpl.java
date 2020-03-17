package com.souza.librayapi.api.service.impl;

import com.souza.librayapi.api.exception.BusinessException;
import com.souza.librayapi.api.model.Book.Book;
import com.souza.librayapi.api.model.repository.BookRepository;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements com.souza.librayapi.api.service.BookService {

    private BookRepository repository;

    public BookServiceImpl(BookRepository repository){
        this.repository = repository;
    }


    @Override
    public Book save(Book book) {
        if(repository.existsByIsbn(book.getIsbn())){
            throw new BusinessException(("Isbn já cadastrado."));
        }
        return repository.save(book);
    }
}
