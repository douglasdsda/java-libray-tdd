package com.souza.librayapi.api.service;

import com.souza.librayapi.api.model.Book.Book;
import com.souza.librayapi.api.model.repository.BookRepository;
import com.souza.librayapi.api.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService service;

    @MockBean
    BookRepository repository;

    @BeforeEach
    public void setUp(){
        this.service = new BookServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest(){
        // cenario
        Book book = Book.builder().isbn("123").author("Fulano").title("As aventuras.").build();
        Mockito.when(repository.save(book)).thenReturn(Book.builder().id(1l).isbn("123").author("Fulano").title("As aventuras.").build());


        //execucao
        Book saveBook = service.save(book);

        //verificacao
        assertThat(saveBook.getId()).isNotNull();
        assertThat(saveBook.getIsbn()).isEqualTo("123");
        assertThat(saveBook.getTitle()).isEqualTo("As aventuras.");
        assertThat(saveBook.getAuthor()).isEqualTo("Fulano");
    }



}
