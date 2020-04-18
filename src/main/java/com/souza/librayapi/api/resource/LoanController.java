package com.souza.librayapi.api.resource;


import com.souza.librayapi.api.dto.BookDTO;
import com.souza.librayapi.api.dto.LoanDto;
import com.souza.librayapi.api.dto.LoanFilterDTO;
import com.souza.librayapi.api.dto.ReturnedLoanDTO;
import com.souza.librayapi.api.model.entity.Book;
import com.souza.librayapi.api.model.entity.Loan;
import com.souza.librayapi.api.service.BookService;
import com.souza.librayapi.api.service.LoanService;
import com.sun.org.apache.regexp.internal.RE;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {
    private final LoanService service;
    private final BookService bookService;
     private final ModelMapper modelMapper;

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

    @GetMapping
    public Page<LoanDto> find(LoanFilterDTO dto, Pageable pageRequest){
        Page<Loan> result = service.find(dto, pageRequest);
          List<LoanDto> loans = result.getContent()
                    .stream()
                    .map( e ->  {
                        Book book = e.getBook();
                        BookDTO bookDTO = modelMapper.map(book, BookDTO.class);
                        LoanDto loanDTO = modelMapper.map(e, LoanDto.class);
                        loanDTO.setBook(bookDTO);
                        return loanDTO;
                    }).collect(Collectors.toList());
        return new PageImpl<LoanDto>(loans, pageRequest, result.getTotalElements());
    }
}
