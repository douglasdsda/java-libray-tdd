package com.souza.librayapi.api.service;

import com.souza.librayapi.api.dto.LoanFilterDTO;
import com.souza.librayapi.api.exception.BusinessException;
import com.souza.librayapi.api.model.entity.Book;
import com.souza.librayapi.api.model.entity.Loan;
import com.souza.librayapi.api.model.repository.LoanRepository;
import com.souza.librayapi.api.service.impl.LoanServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {


    LoanService service;

    @MockBean
    LoanRepository repository;

    @BeforeEach
    public void setUp(){
        this.service = new LoanServiceImpl(repository);

    }

    @Test
    @DisplayName("Deve salvar um emprestimo")
    public void saveLoanTest(){
        Book book = Book.builder().id(1l).build();
        String custumer = "Fulano";
        Loan savingLoan = Loan.builder()
                .book(book)
                .customer("Fulano")
                .loanDate(LocalDate.now())
                .build();

        Loan savedLoan = Loan.builder().id(1l).book(book).loanDate(LocalDate.now()).customer(custumer).build();

        when(repository.existsByBookAndNotReturned(book)).thenReturn(false);
        when(repository.save(savingLoan)).thenReturn(savedLoan);


        Loan loan = service.save(savingLoan);

        assertThat(loan.getId()).isEqualTo(savedLoan.getId());
        assertThat(loan.getBook().getId() ).isEqualTo(savedLoan.getBook().getId());
        assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
        assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());
    }

    @Test
    @DisplayName("Deve lancar erro de negocio ao salvar um emorestimo com livro ja emprestado")
    public void loanedBookSaveTest(){
        Book book = Book.builder().id(1l).build();
        String custumer = "Fulano";
        Loan savingLoan = Loan.builder()
                .book(book)
                .customer("Fulano")
                .loanDate(LocalDate.now())
                .build();
          when(repository.existsByBookAndNotReturned(book)).thenReturn(true);


         Throwable exception = catchThrowable( () -> service.save(savingLoan));

         assertThat(exception)
                 .isInstanceOf(BusinessException.class)
                 .hasMessage("Book already loaned");

         verify(repository, never()).save(savingLoan);
    }

    @Test
    @DisplayName("Deve obter as informações de um emprestimo pelo ID")
    public void getLoanDetaisTest(){
        //cenario
        Long id = 1l;
        Loan loan = getLoan(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(loan));

        //execucao

        Optional<Loan> result = service.getById(id);

        // verifcacao
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getCustomer()).isEqualTo(loan.getCustomer());
        assertThat(result.get().getBook()).isEqualTo(loan.getBook());
        assertThat(result.get().getLoanDate()).isEqualTo(loan.getLoanDate());

        verify( repository).findById(id);
    }

    @Test
    @DisplayName("Deve atualizar um emprestimo")
    public void updateLoanTest(){
        //cenario
        Long id = 1l;
        Loan loan = getLoan(id);
        loan.setReturned(true);

        when(repository.save(loan)).thenReturn(loan);

        //execucao

        Loan loanUpdated = service.update(loan);

        // verifcacao


        assertThat(loanUpdated.getReturned()).isEqualTo(true);

        verify( repository).save(loan);
    }

    @Test
    @DisplayName("Deve filtar emprestimo pela propriedades")
    public void findLoanTest(){
        // cenario
        Loan loan = getLoan(1l);
        LoanFilterDTO dto = LoanFilterDTO.builder().custumer("custumer").isbn("001").build();
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Loan> lista =  Arrays.asList(loan);

        Page<Loan> page = new PageImpl<Loan>(lista, pageRequest, lista.size());
        when(repository.findByBookIsbnOrCustomer(Mockito.anyString(),Mockito.anyString(), Mockito.any(PageRequest.class)))
                .thenReturn(page);
        //execucao
        Page<Loan> result = service.find(dto, pageRequest );


        //verificao
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(lista);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
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
