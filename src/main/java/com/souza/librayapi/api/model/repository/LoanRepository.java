package com.souza.librayapi.api.model.repository;

import com.souza.librayapi.api.model.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {

}
