package com.souza.librayapi.api.service.impl;

import com.souza.librayapi.api.exception.BusinessException;
import com.souza.librayapi.api.model.entity.Loan;
import com.souza.librayapi.api.model.repository.LoanRepository;
import com.souza.librayapi.api.service.LoanService;

import java.util.Optional;

public class LoanServiceImpl implements LoanService {

    private LoanRepository repository;

    public LoanServiceImpl(LoanRepository repository) {
        this.repository = repository;
    }

    @Override
    public Loan save(Loan loan) {
        if(repository.existsByBookAndNotReturned(loan.getBook())){
            throw new BusinessException("Book already loaned");
        }
        return repository.save(loan);
    }

    @Override
    public Optional<Loan> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Loan update(Loan loan) {
        return null;
    }
}
