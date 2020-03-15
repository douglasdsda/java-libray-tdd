package com.souza.librayapi.api.resource;

import com.souza.librayapi.api.dto.BookDTO;
import com.souza.librayapi.api.model.Book.Book;
import com.souza.librayapi.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
public class BookController {


    private BookService service;
    private ModelMapper modelMapper;

    public BookController(BookService service, ModelMapper modelMapper){

        this.service = service;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@RequestBody BookDTO dto){
        Book entity = modelMapper.map(dto, Book.class);
              //  Book.builder()
               // .author(dto.getAuthor())
               // .title(dto.getTitle())
               // .isbn(dto.getIsbn())
              //  .build();
        entity = service.save(entity);
        return modelMapper.map(entity, BookDTO.class);
    }
}
