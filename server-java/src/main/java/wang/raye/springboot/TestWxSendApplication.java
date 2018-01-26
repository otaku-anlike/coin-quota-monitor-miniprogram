package wang.raye.springboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import wang.raye.springboot.bean.WxSendBean;
import wang.raye.springboot.utils.HttpRequestUtils;
import wang.raye.springboot.utils.WxSendUtils;

import java.util.Date;

@SpringBootApplication
@EnableTransactionManagement
public class TestWxSendApplication {

	@Autowired
	private HttpRequestUtils httpRequestUtils;
//	@Autowired
//	private WxSendUtils wxSendUtils;

	public static void main(String[] args) {
		TestWxSendApplication application = new TestWxSendApplication();
		application.sendTest(application);
	}

	public void sendTest (TestWxSendApplication application) {
		WxSendUtils wxSendUtils = new WxSendUtils();
		WxSendBean sendBean = new WxSendBean();
		sendBean.setSckey("SCU20744Tcae3e700d2845416dcf4b7d48fefd4fc5a68472d8b5e0");
		sendBean.setExchange("binance");
		sendBean.setPrice("0.00678");
		sendBean.setSymbol("BNBBTC");
		sendBean.setStatus("2");
		sendBean.setType("MACD");
		sendBean.setTime(new Date().toString());
		sendBean.setPeriod("1h");
		wxSendUtils.sendCross(sendBean);
	}
}
