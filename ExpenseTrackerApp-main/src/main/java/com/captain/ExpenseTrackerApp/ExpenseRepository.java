package com.captain.ExpenseTrackerApp;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface ExpenseRepository extends CrudRepository<Expense,Integer> {
	 
	List<Expense> findAllByMail(String mail);
	
}
