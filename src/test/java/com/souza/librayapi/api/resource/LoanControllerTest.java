package com.souza.librayapi.api.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.souza.librayapi.api.dto.LoanDto;
import com.souza.librayapi.api.dto.LoanFilterDTO;
import com.souza.librayapi.api.dto.ReturnedLoanDTO;
import com.souza.librayapi.api.exception.BusinessException;
import com.souza.librayapi.api.model.entity.Book;
import com.souza.librayapi.api.model.entity.Loan;
import com.souza.librayapi.api.service.BookService;
import com.souza.librayapi.api.service.LoanService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@WebMvcTest(controllers = LoanController.class)
public class LoanControllerTest {
    static final String LOAN_API = "/api/loans";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private LoanService loanService;
    
    @Test
    @DisplayName("Deve realizar um emprestimo")
    public void createLoanTest() throws Exception {
        LoanDto dto = LoanDto.builder().isbn("123").email("customer@mail.com").customer("Fulano").build();
        String json = new ObjectMapper().writeValueAsString(dto);
        Book book = Book.builder()
                .id(1l)
                .isbn("123")
                .build();
        BDDMockito.given(bookService.getBookByIsbn("123")).willReturn(
                Optional.of(book ));
        Loan loan = Loan.builder()
                            .id(1l)
                            .customer("Fulano")
                            .book(book)
                            .loanDate(LocalDate.now())
                            .build();
        BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willReturn(loan);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post( LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                 .content(json);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect( content().string("1"));
    }

    @Test
    @DisplayName("Deve lancar erro ao salvar um livro inexistente")
    public void invalidIsbnCretedLoadTest()  throws Exception{
        LoanDto dto = LoanDto.builder().isbn("123").customer("Fulano").build();
        String json = new ObjectMapper().writeValueAsString(dto);
        BDDMockito.given(bookService.getBookByIsbn("123")).willReturn(Optional.empty());



        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post( LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect( jsonPath("errors", Matchers.hasSize(1)))
                .andExpect( jsonPath("errors[0]").value("Book not found for passed isbn"));

    }

    @Test
    @DisplayName("Deve lancar erro ao salvar um livro emprestado")
    public void loanedBookErrorOnCreatedLoanTest()  throws Exception{
        LoanDto dto = LoanDto.builder().isbn("123").customer("Fulano").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        Book book = Book.builder()
                .id(1l)
                .isbn("123")
                .build();

        BDDMockito.given(bookService.getBookByIsbn("123")).willReturn(Optional.of(book));

        BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willThrow(new BusinessException("Book alredy loaned"));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post( LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect( jsonPath("errors", Matchers.hasSize(1)))
                .andExpect( jsonPath("errors[0]").value("Book alredy loaned"));

    }

    @Test
    @DisplayName("Deve retornar 404 quando tentar devolver um livro inexistente")
    public void returnedBookTest() throws Exception {
        // cenario
        ReturnedLoanDTO dto = ReturnedLoanDTO.builder().returned(true).build();


        BDDMockito.given(loanService.getById(Mockito.anyLong()))
                .willReturn(Optional.empty());


        String json = new ObjectMapper().writeValueAsString(dto);
        mockMvc.perform(
                patch(LOAN_API.concat("/1"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                ).andExpect(status().isNotFound());


    }

    @Test
    @DisplayName("Deve filtrar um emprestimo")
    public void findLoanTest() throws Exception {
        Long id =  1l;

        Loan loan = getLoan(id);
        BDDMockito.given( loanService.find(Mockito.any(LoanFilterDTO.class), Mockito.any(Pageable.class)))
                .willReturn( new PageImpl<Loan>(Arrays.asList(loan), PageRequest.of(0,100), 1));

        String queryString = String.format("?isbn=%s&custumer=%s&page=0&size=100",
                loan.getBook().getIsbn(),loan.getCustomer());
        MockHttpServletRequestBuilder request  = MockMvcRequestBuilders.get(LOAN_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);
        mockMvc
                .perform( request)
                .andExpect(status().isOk())
                .andExpect( jsonPath("content", Matchers.hasSize(1)))
                .andExpect( jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(100))
                .andExpect(jsonPath("pageable.pageNumber").value(0));
    }

    public Loan getLoan(Long id){
        Book book = Book.builder().id(1l).isbn("001").author("Author").title("Title").build();
        String custumer = "Fulano";
        return Loan.builder()
                .book(book)
                .id(id)
                .customer("Fulano")
                .loanDate(LocalDate.now())
                .build();

    }
}
