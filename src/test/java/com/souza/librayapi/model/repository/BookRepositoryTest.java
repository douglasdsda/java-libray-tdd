package com.souza.librayapi.model.repository;

import com.souza.librayapi.api.model.Book.Book;
import com.souza.librayapi.api.model.repository.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.swing.text.html.Option;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository repository;

    @Test
    @DisplayName("Deve retornar verdadeiro quando existir um livro na base com o isbn informado.")
    public void returnTrueWhenIsbnExists(){
        // cenario
            String isbn = "123";
            Book book = Book.builder().title("Aventuras").author("Fulano").isbn(isbn).build();
            entityManager.persist(book);
        // execucao
      boolean exists = repository.existsByIsbn(isbn);
        // verificacao
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve retornar falso quando existir um livro na base com o isbn informado.")
    public void returnFalseWhenIsbnNotExists(){
        // cenario
        String isbn = "123";

        // execucao
        boolean exists = repository.existsByIsbn(isbn);
        // verificacao
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Deve obter um livro por id.")
    public void findByIdTest(){
        Book book = createNewBook();
        entityManager.persist(book);

        Optional<Book> foundBook = repository.findById(book.getId());

        assertThat(foundBook.isPresent()).isTrue();
    }

    public Book createNewBook(){
        return Book.builder().title("Aventuras").author("Fulano").isbn("123").build();

    }

}
