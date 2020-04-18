package com.souza.librayapi.api.model.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Book {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;

  private String author;

  private String isbn;

  @OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
  private List<Loan> loans;
}
