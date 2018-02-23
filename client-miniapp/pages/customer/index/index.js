// pages/customer/index/index.js
//获取应用实例
const { aboutMe } = require('../../../components/mine-top/mine-top.js');
var arrList = require('../../../api/kaizhang/shop.js');
var searchImgText = require('../../../image/customer/index/searchImgText.js');
let { chooseRole } = require('../../../components/guide/guide');
var config = require('../../../config')
const utils = require('../../../utils/util.js');
var app = getApp()
Page({
  data: {
    // 商户
    scrollHeight:'600',
    scrollHeightMine:'600',
    scrollTop : 0,
    scrollTopMine:0,
    refreshTop:-0,//原来是50
    arrList:arrList,
    searchImgText:searchImgText,
    refresh:false,
    showMask:false,
    //根据标签搜索
    itemcon:'5公里',
    showChoiceList:false,
    concern:true,
    exchange:'binance',
    period: '1小时',
    status: '金叉',
    types: 'MACD',
    discount:false,
    distance:false,
    //status:false,
    lastClickDisOrStatus:'',

    

    //按分类搜索
    selectCategoryTitle:'果蔬花卉',

    // 我的
    motto: 'Hello World',
    userInfo: {},
    showDiscountDetail:false,
    // 公共
    tabbarAct:'shop',
    refreshText:'下拉刷新...',
    searchHeight:'240',
    noticeHeight:65,
    hasMore:true,
    autoplay: true,
    interval: 3000,
    duration: 1000,
    imgUrls:['',''],
    tabbarHeight:100,

    array: [{
      content: '500m',
    }, {
        content: '1000m'
    },
    {
      content: '1公里'
    },
    {
      content: '5公里'
    },
    {
      content: '10公里',
    }, {
      content: '15公里'
    },
    {
      content: '20公里'
    },
    {
      content: '25公里'
    }]

  },
  onPullDownRefresh:function(){
    // console.log(123456780987654)
    const that = this;
    setTimeout(function(){
      wx.stopPullDownRefresh();
      that.getApiData();
      that.setData({
        showMask:true
      })
    },200)
  },
  chooseRole,
  onLoad: function () {

    // wx.redirectTo({
    //   url: '../guide/guide',
    //   success: function(res){
    //     // success
    //   },
    //   fail: function() {
    //     // fail
    //   },
    //   complete: function() {
    //     // complete
    //     console.log('redirectTo')
    //   }
    // })

    const _this = this;
    // wx.showNavigationBarLoading()
    this.setScrollHeight();
    //调用应用实例的方法获取全局数据
    app.getUserInfo(function(userInfo){
      //更新数据
      _this.setData({
        userInfo:userInfo
      })
    })
    this.getApiData();
  },
  onShow:function(){
      //判断是否为回退切换角色
      const role = wx.getStorageSync('role');
      if (role=='merchant'){
          wx.redirectTo({
            url: '../../common/guide/guide',
            success: function(res){
              // success
            },
            fail: function() {
              // fail
            },
            complete: function() {
              // complete
            }
          })
      } else {
        this.setData({
          showContent:true
        })
      }
  },
  onHide:function(){
      this.setData({
          showContent:false
      })
  },
 //距离或会否开张选择
  tapSlideItem:function(e){
    console.log(1, e.currentTarget.dataset.itemcon, e.currentTarget.dataset.itemtype);
    var itemtype = e.currentTarget.dataset.itemtype;
    var itemcon = e.currentTarget.dataset.itemcon;
    if ('exchange' == itemtype) {
      this.data.exchange = itemcon
    } else if ('period' == itemtype) {
      this.data.period = itemcon
    } else if ('status' == itemtype) {
      this.data.status = itemcon
    } else if ('types' == itemtype) {
      this.data.types = itemcon
    } 
    //concern: true,
      const content = e.currentTarget.dataset.itemcon;
      this.setData({
          itemcon:content,
          exchange: this.data.exchange,
          period: this.data.period,
          status: this.data.status,
          types: this.data.types,
      });
      this.getApiData();
  },
  //
  checkChoiceList:function(){
      console.log('checkChoiceList');
      let showChoiceList = false;
      if (this.data.showChoiceList == false){
          showChoiceList = true;
      } else {
          showChoiceList = false;
      }
      this.setData({
        showChoiceList:showChoiceList
      });
      return showChoiceList;
  },
  closeChoiceList:function(){
    if (this.data.showChoiceList == true){
        this.setData({
          showChoiceList:false,
          lastClickDisOrStatus:''
        });
    }
  },
  searchByConcern:function(){
      this.closeChoiceList();
      this.setData({
        concern:!this.data.concern
      });
  },
  searchByDiscount:function(){
      this.closeChoiceList();
      this.setData({
        discount:!this.data.discount
      });
  },
  searchByExchange: function () {

    console.log(123, this.data.lastClickDisOrStatus)
    if (this.data.lastClickDisOrStatus != '') {  //搜索列表已经打开
      console.log(1234567)
      if (this.data.lastClickDisOrStatus === 'exchange') {
        this.closeChoiceList();
        return;
      }

    } else {
      this.checkChoiceList();
    }
    const array = [{
      content: 'binance',
    }, {
      content: 'bittrex'
    }]
    this.setData({
      array: array,
      //exchange: true,
      lastClickDisOrStatus: 'exchange'
    });

  },
  searchByDistance:function(){
      
      console.log(123,this.data.lastClickDisOrStatus)
      if (this.data.lastClickDisOrStatus!=''){  //搜索列表已经打开
          console.log(1234567)
          if (this.data.lastClickDisOrStatus === 'period'){
              this.closeChoiceList();
              return;
          }
          
      } else {
          this.checkChoiceList();
      }
      const array = [{
        content: '1小时',
      }, {
        content: '4小时'
      },
      {
        content: '1天'
      }]
      this.setData({
        array: array,
        distance:true,
        lastClickDisOrStatus:'period'
      });
      
  },
  searchByStatus:function(){
    if (this.data.lastClickDisOrStatus != '') {  //搜索列表已经打开
          if (this.data.lastClickDisOrStatus === 'status'){
              this.closeChoiceList();
              return;
          }
      } else {
          this.checkChoiceList();
      }
    const array = [{ content: '金叉' }, { content: '死叉' }, { content: '超买' }, { content: '超卖' }];
      this.setData({
        array: array,
        //status: !this.data.status,
        lastClickDisOrStatus:'status'
      });
  },
  //分类选择
  selectCategory:function(e){
      const title = e.currentTarget.dataset.title;
      console.log(11,title)
      this.setData({
          selectCategoryTitle:title,
          types: title
      });
      this.getApiData();
  },
  


  merchantDetail:function(e){
    console.log(e)
    // wx.navigateTo({
    //   url: '../detail/detail?id=' + e.currentTarget.dataset.id,
    //   success: function(res){
    //     // success
    //   },
    //   fail: function() {
    //     // fail
    //   },
    //   complete: function() {
    //     // complete
    //   }
    // })
  },

  // aboutUs:function(){
  //   wx.navigateTo({
  //     url: '../../common/about-us/about-us',
  //     success: function(res){
  //       // success
  //     },
  //     fail: function() {
  //       // fail
  //     },
  //     complete: function() {
  //       // complete
  //     }
  //   })
  // },
  aboutMe:aboutMe,




  // touchStart:function(){
  //   this.setData({
  //     showMask:false
  //   })
  // },
  // touchEnd:function(){
  //   console.log('touchEnd')
  // },
  toUpper:function(e){
    this.onPullDownRefresh();
    
  },


  toLower:function(e){
    console.log(2,e)
    this.setData({
      hasMore:false
    })
    wx.showToast({
      title:'加载中...',
      icon:'loading',
      duration:2000
    })
  },
  scroll:function(e){
      console.log(e)
      const _this = this;
      let timer1=null;
      let timer2=null;
      const scrollTop = e.detail.scrollTop;
      const refreshTop =  _this.data.refreshTop;
      console.log('scrollTop:',scrollTop);
      console.log('refreshTop',refreshTop);

      if (scrollTop > refreshTop * 0.2 && scrollTop <= refreshTop){
          timer1 = setTimeout(function(){
             _this.setData({
                scrollTop:_this.data.refreshTop,
                refreshText:'下拉刷新...'
            })
          },150000);
      } else if (scrollTop <= refreshTop* 0.2 ){
        console.log('释放刷新');
           _this.onPullDownRefresh();
           _this.setData({
                refreshText:'释放刷新...'
            })
           timer2 = setTimeout(function(){
              _this.setData({
                    scrollTop:refreshTop
                })
           },400000);
      } else {
          _this.setData({
              scrollTop:scrollTop,
              refreshText:'下拉刷新...'
          })
      }
  },






// 我的
showDiscountDetail:function(){
  this.setData({
    showDiscountDetail:true
  })
},
closeDiscountDetail:function(){
  console.log(1234567654)
  this.setData({
    showDiscountDetail:false
  })
},





  getApiData:function(){
    const _this = this;
    wx.request({
      url: config.service.listUrl, //仅为示例，并非真实的接口地址
      data: {
        exchange: this.data.exchange,
        type: utils.parseTypeCode(this.data.types),
        period: utils.parsePeriodCode(this.data.exchange, this.data.period) ,
        status: utils.parseStatusCode(this.data.status)
      },
      header: {
          'content-type': 'application/json'
      },
      success: function(res) {
        console.log(res.data)
        res.data.forEach(function (item, index) {
          item.type = utils.parseType(item.type)
          item.status = utils.parseStatus(item.status)
          item.time = utils.formatDateTime(item.time)
          item.change = utils.convertChange(item.price, item.lastPrice)
          item.changeStatus = true
          if (item.change < 0){
            item.changeStatus = false
          }
        });
        _this.setData({
          arrList:res.data
        })
      }
    })
    wx.request({
      url: config.service.symbolUrl, //仅为示例，并非真实的接口地址
      data: {
        exchange: this.data.exchange,
        type: utils.parseTypeCode(this.data.types),
        period: utils.parsePeriodCode(this.data.exchange, this.data.period),
        // status: utils.parseStatusCode(this.data.status),
        symbol: utils.parseSymbol(this.data.exchange,'USDT-BTC')
      },
      header: {
        'content-type': 'application/json'
      },
      success: function (res) {
        console.log(res.data)
        var item = res.data
          item.type = utils.parseType(item.type)
          item.status = utils.parseStatus(item.status)
          item.time = utils.formatDateTime(item.time)
          item.lastTime = utils.formatDateTime(item.lastTime)
          item.change = utils.convertChange(item.price, item.lastPrice)
          item.changeStatus = true
          if (item.change < 0) {
            item.changeStatus = false
          }
  
        _this.setData({
          symbolBtc: res.data
        })
      }
    })
    wx.request({
      url: config.service.noticeUrl, //仅为示例，并非真实的接口地址
      data: {
        exchange: this.data.exchange,
        //type: utils.parseTypeCode(this.data.types),
        //period: utils.parsePeriodCode(this.data.exchange, this.data.period),
        //status: utils.parseStatusCode(this.data.status),
        limitHour: 4
      },
      header: {
        'content-type': 'application/json'
      },
      success: function (res) {
        console.log(res.data)
        _this.setData({
          noticeList: res.data
        })
      }
    })
  },
  // 设置混动区域高度
  setScrollHeight:function(){
    const _this = this;
    const searchHeight = _this.data.searchHeight;
    const noticeHeight = _this.data.noticeHeight
    const tabbarHeight = _this.data.tabbarHeight;
    let scrollHeight = _this.data.scrollHeight
    const leftHeight = searchHeight*1 + noticeHeight + tabbarHeight;
    wx.getSystemInfo({
      success: function(res) {
        scrollHeight = res.windowHeight- (leftHeight/res.pixelRatio).toFixed(2) + 40 ;
        const scrollHeightMine = res.windowHeight- (tabbarHeight/res.pixelRatio).toFixed(2) ;
        _this.setData({
          scrollHeight:scrollHeight,
          scrollHeightMine:scrollHeightMine
        })
  }
    })
  },
  switchTabbar:function(e){
    const switchTab = e.currentTarget.id;
    const tabbarAct = this.data.tabbarAct
    const _this  = this;
    
    if (tabbarAct == switchTab){
      return;
    } else {
      this.setScrollHeight();
      if (switchTab == 'mine'){
        wx.setNavigationBarTitle({
          title: '我的'
        })
        _this.setData({
          tabbarAct:'mine'
        })
      } else {
         wx.setNavigationBarTitle({
          title: '商户'
         })
         _this.setData({
          tabbarAct:'shop'
        })
      } 
    }
  }
})
