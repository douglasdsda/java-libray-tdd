package com.souza.librayapi.service.impl;

import com.souza.librayapi.api.exception.BusinessException;
import com.souza.librayapi.api.model.Book.Book;
import com.souza.librayapi.api.model.repository.BookRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class BookServiceImpl implements com.souza.librayapi.service.BookService {

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

    @Override
    public Optional<Book> getById(Long id) {


        return repository.findById(id);
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public Book update(Book book) {
        return null;
    }
}
