package com.captain.ExpenseTrackerApp;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User,String> {
	
}
