package com.souza.librayapi.api.resource;

import com.souza.librayapi.api.dto.BookDTO;
import com.souza.librayapi.api.dto.LoanDto;
import com.souza.librayapi.api.exception.ApiErrors;
import com.souza.librayapi.api.exception.BusinessException;
import com.souza.librayapi.api.model.entity.Book;
import com.souza.librayapi.api.model.entity.Loan;
import com.souza.librayapi.api.service.BookService;
import com.souza.librayapi.api.service.LoanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Slf4j
@Api("Book API")
public class BookController {


    private final BookService service;
    private final LoanService loanService;
    private final ModelMapper modelMapper;


    /*
    com construtor
    public BookController(BookService service, ModelMapper modelMapper, LoanService loanService) {

        this.service = service;
        this.modelMapper = modelMapper;
        this.loanService = loanService;
    }
    */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("CREATE A BOOK")
    public BookDTO create(@RequestBody @Valid BookDTO dto) {
        log.info("create a book for isbn: {}", dto.getIsbn());
        Book entity = modelMapper.map(dto, Book.class);
        //  Book.builder()
        // .author(dto.getAuthor())
        // .title(dto.getTitle())
        // .isbn(dto.getIsbn())
        //  .build();
        entity = service.save(entity);
        return modelMapper.map(entity, BookDTO.class);
    }

    @GetMapping("{id}")
    public BookDTO get(@PathVariable Long id) {
        log.info("obtaining details for book id: {}", id);
        return service
                .getById(id)
                .map(book -> modelMapper.map(book, BookDTO.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses({
            @ApiResponse(code = 204, message = "Book succesfully deleted")
    })
    public void delete(@PathVariable Long id) {
        log.info("delete book by id: {}", id);
         Book book = service
                 .getById(id)
                 .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

         service.delete(book);
    }

    @PutMapping("{id}")
     public BookDTO update(@PathVariable Long id, @RequestBody BookDTO dto) {
        log.info("update for book id: {}", id);
        Book book = service
                .getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        book.setAuthor(dto.getAuthor());
        book.setTitle(dto.getAuthor());
        book = service.update(book);
        return modelMapper.map(book, BookDTO.class);
    }

    @GetMapping
    public Page<BookDTO> find(BookDTO dto, Pageable pageRequest) {
        Book filter = modelMapper.map(dto, Book.class);
        Page<Book> result = service.find(filter, pageRequest);
         List<BookDTO> list  =  result.getContent()
                    .stream()
                    .map( entity -> modelMapper.map(entity, BookDTO.class))
                    .collect(Collectors.toList());
            return new PageImpl<BookDTO>( list, pageRequest, result.getTotalElements());
    }

    // falta fazer teste
    @GetMapping("{id}/loans")
    public Page<LoanDto> loansByBook(@PathVariable Long id, Pageable pageable){
        Book book = service.getById(id).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
       Page<Loan> result =  loanService.getLoansByBook(book, pageable);
      List<LoanDto> lista =  result.getContent()
               .stream()
               .map( loan -> {
                  Book loanBook = loan.getBook();
                  BookDTO bookDTO = modelMapper.map(loanBook, BookDTO.class);
                  LoanDto loanDto = modelMapper.map(loan, LoanDto.class);
                  loanDto.setBook(bookDTO);
                  return loanDto;
               }).collect(Collectors.toList());

      return new PageImpl<LoanDto>(lista, pageable, result.getTotalElements());
    }
}
