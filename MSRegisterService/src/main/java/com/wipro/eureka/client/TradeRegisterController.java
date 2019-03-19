package com.wipro.eureka.client;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

@Controller
public class TradeRegisterController {

	private Map<String, User> users = new HashMap<>();

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private DiscoveryClient discoveryClient;

	@Value("${pivotal.tradeservice.name}")
	protected String tradeService;

	public TradeRegisterController() {
		User user = new User("arnab", "passw0rd", "xyz.abc@pqr.com");
		users.put("arnab", user);
	}

	@RequestMapping(value = "/countries/all", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public String displayCountries() {
		Map<String, Object> countries = new HashMap<String, Object>();

		ResponseEntity<String> result = restTemplate.getForEntity("https://restcountries.eu/rest/v2/all", String.class,
				countries);
		if (result.getStatusCode() == HttpStatus.OK) {
			return result.getBody().toString();
		} else {
			return "";
		}
	}

	public String defaultCountries() {

		return "India:New Delhi:Asia";
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
			HttpServletRequest request, HttpServletResponse response) {
		System.out.println("Inside loginUser...");
		User user = users.get(userId);
		request.getSession().setAttribute("user", user);

		if (user != null) {
			if (user.getPassword().equals(password)) {

				List<ServiceInstance> instances = discoveryClient.getInstances(tradeService);
				URI uri = instances.get(0).getUri();

				System.out.println("Register-Service.loginUser .URI=========" + uri);
				String url = uri.toString() + "/trade.html";
				System.out.println("========================================");
				System.out.println("Register-Service.loginUser .URI=========" + url);

				try {
					// request.getRequestDispatcher(url).include(request, response);
					// restTemplate.
					response.sendRedirect(url);
				} catch (Exception e) {
					System.out.println("Error in dispatching");
				}

				// ResponseEntity result = restTemplate.getForEntity(uri.toString()
				// + "/users/logincheck");

				return "";

			} else {
				return "invalidPassword";
			}
		} else {
			return "invalidUser";
		}
	}
}
