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
