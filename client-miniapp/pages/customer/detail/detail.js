var config = require('../../../config')
var app = getApp()
Page({
  data: {
    wrapTopHeight:400,
    imgUrls:['','',''],
    scrollHeight:'0',
    info:null
  },
  deleteWrapTop:function(){
      console.log(222)
      this.setData({
         wrapTopHeight:0 
      });
  },
  onLoad: function (options) {
      this.setScrollHeight();
      this.getApiDatailData(options.id);
  },
  getApiDatailData: function (id) {
    const _this = this;
    wx.request({
      url: config.service.detailUrl, //仅为示例，并非真实的接口地址
      data: {
        id: id
      },
      header: {
        'content-type': 'application/json'
      },
      success: function (res) {
        console.log(res.data)
        _this.setData({
          info: res.data
        })
      }
    })
  },
  onReady: function (e) {
    //引入文件
    var chartUtilFile = require("../../../utils/wxChart/chartUtil.js");
    //实例化
    var chartUtilObj = chartUtilFile.chartUtil.createObj();
    //配置参数
    var canvas_id = "first_canvas";
    var xaxis = [
      {
        xdata: ["07:00", "11:00", "15:00", "20:00", "00:00", "04:00"]
      }
    ];
    var ydata = [Math.round(Math.random() * 200)];
    for (var i = 1; i < 200; i++) {
      ydata.push(Math.round((Math.random() - 0.5) * 10 + ydata[i - 1]));
    }

    var candledata = [];
    for (var i = 0; i < 20; i++) {
      var candleItem = [];
      var y;
      if (i == 0) {
        y = Math.round((Math.random() - 0.5) * 80);
      } else {
        y = Math.round((Math.random() - 0.5) * 80 + candledata[i - 1][0]);
      }
      console.log(y);
      candleItem.push(y);
      var h = Math.round((Math.random()) * 30) + y;
      var l = y - Math.round((Math.random()) * 30);
      candleItem.push(h);
      candleItem.push(l);
      candleItem.push(h - Math.round((Math.random()) * 30));
      candleItem.push(l + Math.round((Math.random()) * 30));
      candledata.push(candleItem);
    }
    var yaxis = [
      {
        ydata: ydata
      }
    ];
    var options = {
      "page_obj": this,
      "chart_type": chartUtilFile.chartType.mycandlestick,
      "xaxis": xaxis,
      "yaxis": yaxis,
      "candledata": candledata,
      "line_color": chartUtilFile.chartColor.red,
      "text": "此处放标题！",
      "unit": "（元/10克）",
      "font_size": 10
    };
    //初始化
    chartUtilObj.init(canvas_id, options);
    //开始画图
    chartUtilObj.draw();
  },



  // 设置混动区域高度
  setScrollHeight:function(){
    const _this = this;
    wx.getSystemInfo({
      success: function(res) {
        const scrollHeight = res.windowHeight;
        console.log(12222)
        _this.setData({
          scrollHeight:scrollHeight
        })
  }
    })
  },
})
