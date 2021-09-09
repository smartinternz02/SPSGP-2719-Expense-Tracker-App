package com.captain.ExpenseTrackerApp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller	
@SessionAttributes({"currentUser"})
public class PrimaryController {
	//private String currentName;
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private ExpenseRepository expRepo;
	
	@Autowired
	private BalRepository balRepo;
	
	@Autowired
	private User curruser;
	
	@Autowired
	private Expense exp;
	
	@Autowired
	//private Balance balance;
	
	@GetMapping("/")
	public String registerPage() {
		if(curruser.getMail()!=null) {
			return "redirect:/addexp";
		}
		else
			return "register";
	}
	
	@PostMapping("/regdetails")
	public String addDetails(User user,RedirectAttributes redirAttrs) {
		Optional<User> optionalUser =  userRepo.findById(user.getMail());
		if(optionalUser.isPresent()) {
			redirAttrs.addFlashAttribute("message", "User already Exists ! Please Login");
			return "redirect:/login.html";
		}
		userRepo.save(user);
		System.out.println(user.getFname());
		System.out.println(user.getLname());
		System.out.println(user.getMail());
		System.out.println(user.getPass());
		redirAttrs.addFlashAttribute("message", "Registered Successfully, Please Login");
		return "redirect:/login.html";
	}
	
	@PostMapping("/verify")
	public String verifyLogin(User user,RedirectAttributes redirAttrs) {
		System.out.println(user.getMail());
		System.out.println(user.getPass());
		Optional<User> optionalUser =  userRepo.findById(user.getMail());
		if(optionalUser.isPresent()) {
			User existingUser = optionalUser.get();
			if(user.getPass().equals(existingUser.getPass())) {
				redirAttrs.addFlashAttribute("currentUser", existingUser.getFname());
				redirAttrs.addFlashAttribute("currentMail", existingUser.getMail());
				curruser = existingUser;
				return "redirect:/addexp";
			}
			else {
				redirAttrs.addFlashAttribute("message", "Incorrect password ! Please try again");
				return "redirect:/login.html";
			}
		}
		else {
			redirAttrs.addFlashAttribute("message", "User not found ! Please Register");
			return "redirect:/";
		}
	}
	
	@GetMapping("/login.html")
	public String loginPage() {
		if(curruser.getMail()!=null) {
			return "redirect:/addexp";
		}
		else
			return "login";
	}
	
	int c = 1;
	@PostMapping("/adding")
	public String addExp(String amount,String date,String cat) {
		exp.setId(c);
		c+=1;
		exp.setAmount(amount);
		exp.setDate(date);
		exp.setCat(cat);
		exp.setMail(curruser.getMail());
		expRepo.save(exp);
		return "redirect:/AddExp";
	}
	
	@GetMapping("/AddExp")
	public String addexp() {
		return "addexp";
	}
	
	@GetMapping("/addexp")
	public String nav1(RedirectAttributes redirAttrs) {
		if(curruser.getMail()==null) {
			redirAttrs.addFlashAttribute("message", "Session Timeout ! Please Login");
			return "redirect:/login.html";
		}
		return "addexp";
	}
	
	@GetMapping("/viewexp")
	public String nav2(Model model,RedirectAttributes redirAttrs) {
		if(curruser.getMail()==null) {
			redirAttrs.addFlashAttribute("message", "Session Timeout ! Please Login");
			return "redirect:/login.html";
		}
		model.addAttribute("expenses", expRepo.findAllByMail(curruser.getMail()));
		return "viewexp";
	}
	
	@GetMapping("/delexp/{id}")
	public String delReturn(@PathVariable("id") int id,Model model) {
		Optional<Expense> exp = expRepo.findById(id);
		Expense xcurr = exp.get();
		expRepo.delete(xcurr);
		
		return "redirect:/viewexp";
	}
	
	
	@GetMapping("/addbal")
	public String nav3(RedirectAttributes redirAttrs) {
		if(curruser.getMail()==null) {
			redirAttrs.addFlashAttribute("message", "Session Timeout ! Please Login");
			return "redirect:/login.html";
		}
		return "addbal";
	}
	
	@PostMapping("/addbalance")
	public String balAdd(long bal) {
		Optional<Balance> b = balRepo.findById(curruser.getMail());
		if(b.isPresent()) {
			Balance ob = b.get();
			ob.setBal(bal);
			balRepo.save(ob);
		}
		else {
			balRepo.save(new Balance(bal,curruser.getMail()));
		}
		
		return "redirect:/addbal";
	}
	
	@GetMapping("/viewbal")
	public String nav4(RedirectAttributes redirAttrs,Model model) {
		if(curruser.getMail()==null) {
			redirAttrs.addFlashAttribute("message", "Session Timeout ! Please Login");
			return "redirect:/login.html";
		}
		
		Optional<Balance> bal = balRepo.findById(curruser.getMail());
		if(!bal.isPresent())
			model.addAttribute("balance", 0);
		else {
			Balance b = bal.get();
			model.addAttribute("balance", b.getBal());
		}
		return "viewbal";
	}
	
	@GetMapping("/analysis")
	public String nav5(RedirectAttributes redirAttrs,Model model) {
		if(curruser.getMail()==null) {
			redirAttrs.addFlashAttribute("message", "Session Timeout ! Please Login");
			return "redirect:/login.html";
		}
		Map<String,Integer> graphData = new HashMap<String,Integer>();
		List<Expense> list = expRepo.findAllByMail(curruser.getMail());
		
		for(Expense e:list) {
			if(!graphData.containsKey(e.getCat())) {
				graphData.put(e.getCat(),Integer.parseInt(e.getAmount()));
			}
			else {
				int nval = graphData.get(e.getCat());
				nval += Integer.parseInt(e.getAmount());
				graphData.put(e.getCat(),nval);
			}
		}
		//System.out.println(graphData);
		
		Set<String> keySet = graphData.keySet();
		ArrayList<String> listOfKeys = new ArrayList<String>(keySet);
		
		Collection<Integer> values = graphData.values();
		ArrayList<Integer> listOfValues = new ArrayList<>(values);
		
		model.addAttribute("listOfKeys",listOfKeys);
		model.addAttribute("listOfValues",listOfValues);
		
		System.out.println(listOfKeys);
		System.out.println(listOfValues);
		return "analysis";
	}
	
	@GetMapping("/logout")
	public String logout() {
		curruser.setMail(null);
		System.out.println("Logout");
		return "redirect:/login.html";
	}
}
