package com.wipro.SimpleTradeApp;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TradeRegisterController {

	private Map<String, User> users = new HashMap<>();

	public TradeRegisterController() {
		User user = new User("arnab", "passw0rd", "xyz.abc@pqr.com");
		users.put("arnab", user);
	}

	@RequestMapping(value = "/users/register", method = RequestMethod.POST)
	@ResponseBody
	public String registerUser(@ModelAttribute("userId") String userId, @ModelAttribute("password") String password,
			@ModelAttribute("email") String email) {
		System.out.println("Inside registerUser...");
		User user = new User(userId, password, email);
		users.put(userId, user);
		return "<html><body bgcolor='olive'>Registered Successfully...<br /><br /><a href='http://localhost:8080/index.html'>Go to Home Page for Login</a></body></html>";
	}

	@RequestMapping(value = "/users/all", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, User> getAllUsers() {

		return users;
	}

	@RequestMapping(value = "/users/{userId}", method = RequestMethod.GET)
	@ResponseBody
	public User getUser(@PathVariable("userId") String userId) {

		return users.get(userId);
	}

	@RequestMapping(value = "/users/login", method = RequestMethod.POST)
	public String loginUser(@ModelAttribute("userId") String userId, @ModelAttribute("password") String password,
			HttpServletRequest request) {
		System.out.println("Inside loginUser...");
		User user = users.get(userId);
		request.getSession().setAttribute("user", user);

		if (user != null) {
			if (user.getPassword().equals(password)) {
				return "trade";
			} else {
				return "invalidPassword";
			}
		} else {
			return "invalidUser";
		}
	}
}
