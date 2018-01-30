function formatTime(date) {
  var year = date.getFullYear()
  var month = date.getMonth() + 1
  var day = date.getDate()

  var hour = date.getHours()
  var minute = date.getMinutes()
  var second = date.getSeconds()


  return [year, month, day].map(formatNumber).join('/') + ' ' + [hour, minute, second].map(formatNumber).join(':')
}

function formatDateTime(timeStamp) {
  var date = new Date();
  // date.setTime(timeStamp * 1000);
  date.setTime(timeStamp);
  return this.formatTime(date)
  // var y = date.getFullYear();
  // var m = date.getMonth() + 1;
  // m = m < 10 ? ('0' + m) : m;
  // var d = date.getDate();
  // d = d < 10 ? ('0' + d) : d;
  // var h = date.getHours();
  // h = h < 10 ? ('0' + h) : h;
  // var minute = date.getMinutes();
  // var second = date.getSeconds();
  // minute = minute < 10 ? ('0' + minute) : minute;
  // second = second < 10 ? ('0' + second) : second;
  // return y + '-' + m + '-' + d + ' ' + h + ':' + minute + ':' + second;
}; 

function formatNumber(n) {
  n = n.toString()
  return n[1] ? n : '0' + n
}

function parseStatus(status) {
  if ("1" == status) {
    status = "空仓"
  } else if ("2" == status) {
    status = "金叉"
  } else if ("3" == status) {
    status = "持有"
  } else if ("4" == status) {
    status = "死叉"
  } else if ("5" == status) {
    status = "超买"
  } else if ("6" == status) {
    status = "超卖"
  }
  return status
}

module.exports = {
  formatTime: formatTime,
  formatDateTime,
  parseStatus
}
