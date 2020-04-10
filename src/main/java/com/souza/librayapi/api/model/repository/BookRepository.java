package com.souza.librayapi.api.model.repository;

import com.souza.librayapi.api.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {


    boolean existsByIsbn(String isbn);
}
