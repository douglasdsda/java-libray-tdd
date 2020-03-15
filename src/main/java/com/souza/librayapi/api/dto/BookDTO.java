package com.souza.librayapi.api.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    private Long id;
    @NotEmpty
    private String title;
    @NotEmpty
    private String author;
    @NotEmpty
    private String isbn;

}
