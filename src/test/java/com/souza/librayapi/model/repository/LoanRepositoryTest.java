package com.souza.librayapi.model.repository;

import com.souza.librayapi.api.model.entity.Book;
import com.souza.librayapi.api.model.entity.Loan;
import com.souza.librayapi.api.model.repository.LoanRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {

    @Autowired
    private LoanRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("deve verificar se existe emprestimo nao devolvido para o livro")
    public void existsByBookAndNotReturnedTest(){
        // cenario
       Book book = Book.builder().author("Fulano").title("Title").author("Author").isbn("123").build();

        entityManager.persist(book);

        Loan loan = Loan.builder().book(book).customer("custumer").loanDate(LocalDate.now()).build();
        entityManager.persist(loan);


        // execucao
       boolean exists =  repository.existsByBookAndNotReturned(book);

       assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve buscar emprestimo pelo isbn do livro ou customer")
    public void findByBookIsbnOrCustomerTest(){
        // cenario
        Book book = Book.builder().author("Fulano").title("Title").author("Author").isbn("123").build();

        entityManager.persist(book);

        Loan loan = Loan.builder().book(book).customer("Custumer").loanDate(LocalDate.now()).build();
        entityManager.persist(loan);

       Page<Loan> result = repository.findByBookIsbnOrCustomer("123", "Custumer", PageRequest.of(0, 10));
       assertThat(result.getContent()).contains(loan);
       assertThat(result.getContent()).hasSize(1);
       assertThat(result.getPageable().getPageSize()).isEqualTo(10);
       assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
       assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve obter emprestimo cuja data emprestimo for menor ou igual a tres dias atras e nao retornados")
    public void findByLoanDateLessThanAndNotReturnedTest(){
        // cenario
        Book book = Book.builder().author("Fulano").title("Title").author("Author").isbn("123").build();

        entityManager.persist(book);

        Loan loan = Loan.builder().book(book).customer("Custumer").loanDate(LocalDate.now().minusDays(5)).build();
       Loan loanSaved =  entityManager.persist(loan);

     List<Loan> result =  repository.findByLoanDateLessThanNotReturned(LocalDate.now().minusDays(4));
    assertThat(result).hasSize(1).contains(loanSaved);
    }

    @Test
    @DisplayName("Deve retornar vazio quando nao houver emprestimos atrasados.")
    public void noTfindByLoanDateLessThanAndNotReturnedTest(){
        // cenario
        Book book = Book.builder().author("Fulano").title("Title").author("Author").isbn("123").build();

        entityManager.persist(book);

        Loan loan = Loan.builder().book(book).customer("Custumer").loanDate(LocalDate.now()).build();
        Loan loanSaved =  entityManager.persist(loan);

        List<Loan> result =  repository.findByLoanDateLessThanNotReturned(LocalDate.now().minusDays(4));
        assertThat(result).isEmpty();
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
