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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

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


}
