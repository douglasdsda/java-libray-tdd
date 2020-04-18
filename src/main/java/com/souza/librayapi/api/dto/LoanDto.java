package com.souza.librayapi.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanDto {
    private Long id;

    @NotEmpty
    private String isbn;

    @NotEmpty
    private String email;

    @NotEmpty
    private String customer;

    private BookDTO book;
}
