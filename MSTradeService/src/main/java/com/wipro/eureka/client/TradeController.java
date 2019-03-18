package com.wipro.eureka.client;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
public class TradeController {

	private User user;
	private Map<String, Double> tradingOrgs = new HashMap<>();
	private Map<String, Trade> tradings = new HashMap<>();

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private DiscoveryClient discoveryClient;

	@Value("${pivotal.registerservice.name}")
	protected String registerService;

	public TradeController() {
		tradingOrgs.put("Wipro", 425.25);
		tradingOrgs.put("Tcs", 2425.25);
		tradingOrgs.put("Infosys", 1075.55);
		tradingOrgs.put("Hcl", 265.75);
	}

	@RequestMapping(value = "/trades/all", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Trade> getAllTradings() {

		return tradings;
	}

	@RequestMapping(value = "/trades/{ticker}", method = RequestMethod.GET)
	@ResponseBody
	public Trade getTrading(@PathVariable("ticker") String ticker) {

		return tradings.get(ticker);
	}

	@RequestMapping(value = "/trade/do", method = RequestMethod.POST)
	@ResponseBody
	public String tradeDo(@ModelAttribute("ticker") String ticker, @ModelAttribute("quantity") int quantity,
			HttpServletRequest request) {
		Double price = tradingOrgs.get(ticker);
		Trade trade = new Trade(ticker, price, quantity);
		double totalCost = price * quantity;
		trade.setTotalCost(totalCost);

		tradings.put(ticker, trade);

		List<ServiceInstance> instances = discoveryClient.getInstances(registerService);
		URI uri = instances.get(0).getUri();

		System.out.println("Trade-Service.tradeDo .URI=========" + uri);
		String url = uri.toString() + "/users/all";
		System.out.println("========================================");
		System.out.println("Trade-Service.tradeDo .URI=========" + url);

		Map<String, Object> users = new HashMap<String, Object>();

		ResponseEntity<String> result = restTemplate.getForEntity(url, String.class, users);

		if (result.getStatusCode() == HttpStatus.OK) {
			return result.getBody().toString();
		} else {
			return null;
		}

//		user = (User) request.getSession().getAttribute("user");
//		double remainingBalance = user.getBalance() - totalCost;
//		user.setBalance(remainingBalance);
//
//		return "<html><body bgcolor='olive'>" + user.getUserId()
//				+ "traded Successfully. Your are currently having a balance of Rs " + user.getBalance()
//				+ "<br /><br /><a href='http://localhost:8080/trade.html'>Continue Trading</a><br /><br /><a href='http://localhost:8080/index.html'>Home Page</a></body></html>";
	}
}
