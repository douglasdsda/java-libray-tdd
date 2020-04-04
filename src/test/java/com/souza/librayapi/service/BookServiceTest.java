package com.souza.librayapi.service;

import com.souza.librayapi.api.exception.BusinessException;
import com.souza.librayapi.api.model.Book.Book;
import com.souza.librayapi.api.model.repository.BookRepository;
import com.souza.librayapi.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.anyOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService service;

    @MockBean
    BookRepository repository;

    @BeforeEach
    public void setUp() {
        this.service = new BookServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um livro s")
    public void saveBookTest() {
        // cenario
        Book book = savedBook();
        when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);
        when(repository.save(book)).thenReturn(Book.builder().id(1l).isbn("123").author("Fulano").title("As aventuras.").build());

        //execucao
        Book saveBook = service.save(book);

        //verificacao
        assertThat(saveBook.getId()).isNotNull();
        assertThat(saveBook.getIsbn()).isEqualTo("123");
        assertThat(saveBook.getTitle()).isEqualTo("As aventuras.");
        assertThat(saveBook.getAuthor()).isEqualTo("Fulano");
    }

    private Book savedBook() {
        return Book.builder().isbn("123").author("Fulano").title("As aventuras.").build();
    }

    @Test
    @DisplayName("Deve lancar erro de negocio ao tentar salvar um livro com isbn duplicado")
    public void shouldNotSaveABookWithDuplicatedISbN() {
        // cenario
        Book book = savedBook();
        when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);
        // execucao
        Throwable exception = Assertions.catchThrowable(() -> service.save(book));

        // verificacao
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn já cadastrado.")
        ;
        Mockito.verify(repository, Mockito.never()).save(book);

    }

    @Test
    @DisplayName("Deve obter um livro por id")
    public void getByIdTest(){
        // cenario
        Long id = 1l;
        Book book = savedBook();
        book.setId(id);
        when(repository.findById(id)).thenReturn(Optional.of(book));

        //execucao
        Optional<Book> foundBook = service.getById(id);
        //verificao
        assertThat(foundBook.isPresent()).isTrue();
        assertThat(foundBook.get().getId()).isEqualTo(id);
        assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
    }

    @Test
    @DisplayName("Deve retornar vazio o livro")
    public void getNotfoundBook(){
        // cenario
        Long id = 1l;

        when(repository.findById(id)).thenReturn(Optional.empty());

        //execucao
        Optional<Book> foundBook = service.getById(id);
        //verificao teste
        assertThat(foundBook.isPresent()).isFalse();

    }

    @Test
    @DisplayName("Deve verificar delete se deletou")
    public void deleteBookTest(){
        // cenario
        Long id = 1l;
        Book book = Book.builder().id(id).isbn("123").author("Fulano").title("As aventuras.").build();

        //execucao
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> service.delete(book));

        //verificao
        Mockito.verify(repository, Mockito.times(1)).delete(book);

    }

    @Test
    @DisplayName("Deve verificar delete nao deletou")
    public void deleteNotBookTest(){
        // cenario
        Book book = new Book();
        //execucao
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.delete(book));

        //verificao
        Mockito.verify(repository, Mockito.never()).delete(book);
    }

    @Test
    @DisplayName("Deve ocorrer erro ao tentar deletar um livro inexistente.")
    public void deleteInvalidBookTest(){
        Book book = new Book();

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.delete(book));

        Mockito.verify( repository, Mockito.never() ).delete(book);
    }

    @Test
    @DisplayName("Deve nao update ")
    public void notUpdateBook(){
        // cenario
        Long id = 1l;
        Book book = new Book();
        Book atual = Book.builder().id(id).isbn("123").author("Fulano").title("As aventuras.").build();

        //execucao
        org.junit.jupiter.api.Assertions.assertThrows( IllegalArgumentException.class , () -> service.update(book));

        //verificao
        Mockito.verify(repository, Mockito.never()).save(book);

    }

    @Test
    @DisplayName("Deve update ")
    public void updatebookTest(){
        // cenario
        Long id = 1l;

        Book updatingBook = Book.builder().id(id).build();

        Book updatedBook = savedBook();
        updatedBook.setId(id);
        when(repository.save(updatingBook)).thenReturn(updatedBook);

        //execucao
        Book book = service.update(updatingBook);

        //verificao
        assertThat(book.getId()).isEqualTo(updatedBook.getId());
        assertThat(book.getTitle()).isEqualTo(updatedBook.getTitle());
        assertThat(book.getIsbn()).isEqualTo(updatedBook.getIsbn());
        assertThat(book.getAuthor()).isEqualTo(updatedBook.getAuthor());

    }

    @Test
    @DisplayName("Deve filtar livros pela propriedades")
    public void findBookTest(){
        // cenario
        Book book = Book.builder().id(1l).author("Author").title("Title").isbn("001").build();
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Book> lista =  Arrays.asList(book);
        Page<Book> page = new PageImpl<Book>(lista, pageRequest, 1);
        Mockito.when(repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
                    .thenReturn(page);
         //execucao
        Page<Book> result = service.find(book, pageRequest );


        //verificao
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(lista);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }


}
