package com.souza.librayapi.api.resource;

import com.souza.librayapi.api.dto.BookDTO;
import com.souza.librayapi.api.exception.ApiErrors;
import com.souza.librayapi.api.model.Book.Book;
import com.souza.librayapi.api.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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
    public BookDTO create(@RequestBody @Valid BookDTO dto){
        Book entity = modelMapper.map(dto, Book.class);
              //  Book.builder()
               // .author(dto.getAuthor())
               // .title(dto.getTitle())
               // .isbn(dto.getIsbn())
              //  .build();
        entity = service.save(entity);
        return modelMapper.map(entity, BookDTO.class);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleValidationExceptions(MethodArgumentNotValidException ex){
        BindingResult bindResult = ex.getBindingResult();
        return new ApiErrors(bindResult);
    }
}
