/**
 * 小程序配置文件
 */

// 此处主机域名修改成腾讯云解决方案分配的域名
var host = 'https://www.natiac.cn';

var config = {

  // 下面的地址配合云端 Demo 工作
  service: {
    host,

    // 登录地址，用于建立会话
    loginUrl: `${host}/weapp/login`,

    // 指标列表地址，用于获取币种的指标
    listUrl: `${host}/macd/list`,
    // 指标详情地址，用于获取币种的指标
    detailUrl: `${host}/macd/detail`
  }
};

module.exports = config;