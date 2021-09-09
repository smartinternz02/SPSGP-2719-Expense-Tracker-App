package com.captain.ExpenseTrackerApp;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Entity
public class Balance {
	
	
	
	@Id
	private String mail;
	private long bal;
	
	Balance(){
		this.bal = 0;
	}
	
	Balance(long bal,String mail){
		this.bal = bal;
		this.mail = mail;
	}
	
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	public long getBal() {
		return bal;
	}
	public void setBal(long bal) {
		this.bal += bal;
	}
	
	
}
