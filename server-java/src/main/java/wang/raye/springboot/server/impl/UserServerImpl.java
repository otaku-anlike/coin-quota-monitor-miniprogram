package wang.raye.springboot.server.impl;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.TickerPrice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestOperations;
import wang.raye.springboot.model.User;
import wang.raye.springboot.model.mapper.UserMapper;
import wang.raye.springboot.server.UserServer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户相关数据库操作实现类
 * @author Raye
 * @since 2016年10月11日19:29:02
 */
@Repository
public class UserServerImpl implements UserServer {
	@Autowired
	private UserMapper mapper;

	@Autowired
	private RestOperations restOperations;


	
	public boolean add(User user) {
		return mapper.insert(user) > 0;
	}

	public List<TickerPrice> findAll(){

//		BinanceApiRestClient client = new BinanceApiRestClient();
		BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance("API-KEY", "SECRET");
		BinanceApiRestClient client = factory.newRestClient();
		List<TickerPrice> allPrices = client.getAllPrices();
		System.out.println(allPrices);

//		try {
//			this.test();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

//		return mapper.selectByExample(null);
		return allPrices;
	}



	public void test() throws Exception{
		String url = "https://sc.ftqq.com/SCU20744Tcae3e700d2845416dcf4b7d48fefd4fc5a68472d8b5e0.send?text=={text}&desp={desp}";

		Map<String, Object> uriVariables = new HashMap<String, Object>();
		uriVariables.put("text", "151xxxxxxxx");
		uriVariables.put("desp", "测试短信内容");

		String result = restOperations.getForObject(url, String.class, uriVariables);
	}

}
