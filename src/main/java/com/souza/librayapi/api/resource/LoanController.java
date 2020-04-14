package com.souza.librayapi.api.resource;


import com.souza.librayapi.api.dto.LoanDto;
import com.souza.librayapi.api.dto.ReturnedLoanDTO;
import com.souza.librayapi.api.model.entity.Book;
import com.souza.librayapi.api.model.entity.Loan;
import com.souza.librayapi.api.service.BookService;
import com.souza.librayapi.api.service.LoanService;
import com.sun.org.apache.regexp.internal.RE;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {
    private final LoanService service;
    private final BookService bookService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody LoanDto dto){

        Book book  = bookService
                .getBookByIsbn(dto.getIsbn())
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.BAD_REQUEST,  "Book not found for passed isbn"));



        Loan entity = Loan.builder()
                .book(book)
                .customer(dto.getCustomer())
                .loanDate(LocalDate.now())
                .build();
            entity = service.save(entity);
        return entity.getId();
    }

    @PatchMapping("{id}")
     public void returnBook(@PathVariable Long id, @RequestBody ReturnedLoanDTO dto){

        Loan loan = service.getById(id).orElseThrow( ()-> new ResponseStatusException(HttpStatus.NOT_FOUND));

        loan.setReturned(dto.isReturned());

        service.update(loan);
    }
}
