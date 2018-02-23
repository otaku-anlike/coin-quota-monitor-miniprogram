package wang.raye.springboot.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wang.raye.springboot.model.MacdCross;
import wang.raye.springboot.model.MacdCrossHistory;
import wang.raye.springboot.server.CrossApiServer;

import java.util.List;

@Api(value="指标交叉相关的接口")
@RestController
@RequestMapping("/macd")
public class CrossApiController {
	@Autowired
	private CrossApiServer server;
	
	/**
	 * 查询币种信息
	 * @since 2016年9月22日20:32:43
	 * @return
	 */
	@RequestMapping("/list")
	@ApiOperation(notes="列出条件下的所有币种信息",value="查询所有",httpMethod="GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name="exchange",value = "交易所",defaultValue = "binance",paramType="query",dataType="string"),
			@ApiImplicitParam(name="type",value = "指标类型(MACD,KDJ,RSI,BOLL)",defaultValue = "MACD",paramType="query",dataType="string"),
			@ApiImplicitParam(name="period",value = "k线周期(1h,4h,1d)",defaultValue = "1h",paramType="query",dataType="string"),
			@ApiImplicitParam(name="status",value = "交叉状态(1空仓,2金叉,3持有,4死叉,5超买,6超卖)",defaultValue = "2",paramType="query",dataType="string")
	})
	public List<MacdCross> findList(String exchange, String type, String period, String status){
		return server.findMacdCrossList(exchange, type, period, status);
	}

	/**
	 * 根据id查询币种信息
	 * @since 2016年9月22日20:32:43
	 * @return
	 */
	@RequestMapping("/detail")
	@ApiOperation(notes="根据id查出币种信息",value="查询单个",httpMethod="GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name="id",value = "币种id",defaultValue = "1",paramType="query",dataType="int")
	})
	public MacdCross findDetailByID(int id){
		return server.findMacdCrossById(id);
	}

	/**
	 * 根据id查询币种信息
	 * @since 2016年9月22日20:32:43
	 * @return
	 */
	@RequestMapping("/listbysymbol")
	@ApiOperation(notes="根据币种名查询的列表中单个币种信息",value="查询列表中的币种",httpMethod="GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name="exchange",value = "交易所",defaultValue = "binance",paramType="query",dataType="string"),
			@ApiImplicitParam(name="type",value = "指标类型(MACD,KDJ,RSI,BOLL)",defaultValue = "MACD",paramType="query",dataType="string"),
			@ApiImplicitParam(name="period",value = "k线周期(1h,4h,1d)",defaultValue = "1h",paramType="query",dataType="string"),
			@ApiImplicitParam(name="status",value = "交叉状态(1空仓,2金叉,3持有,4死叉,5超买,6超卖)",defaultValue = "2",paramType="query",dataType="string"),
			@ApiImplicitParam(name="symbol",value = "币种名例如(BTCUSDT)",defaultValue = "BTCUSDT",paramType="query",dataType="string")
	})
	public MacdCross findDetailBySymbolLike(String exchange, String type, String period, String status, String symbol){
		return server.findMacdCrossBySymbolLike(exchange, type, period, status, symbol);
	}

	/**
	 * 根据id查询币种信息
	 * @since 2016年9月22日20:32:43
	 * @return
	 */
	@RequestMapping("/notice")
	@ApiOperation(notes="获取滚动的公告信息列表",value="公告信息列表",httpMethod="GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name="exchange",value = "交易所",defaultValue = "binance",paramType="query",dataType="string"),
			@ApiImplicitParam(name="type",value = "指标类型(MACD,KDJ,RSI,BOLL)",defaultValue = "MACD",paramType="query",dataType="string"),
			@ApiImplicitParam(name="period",value = "k线周期(1h,4h,1d)",defaultValue = "1h",paramType="query",dataType="string"),
			@ApiImplicitParam(name="status",value = "交叉状态(1空仓,2金叉,3持有,4死叉,5超买,6超卖)",defaultValue = "2",paramType="query",dataType="string"),
			@ApiImplicitParam(name="limitHour",value = "当前时间倒推N小时的信息",defaultValue = "4",paramType="query",dataType="int")
	})
	public List<String> findNoticeList(String exchange, String type, String period, String status, int limitHour){
		return server.findAlertList(exchange, type, period, status, limitHour);
	}
}
