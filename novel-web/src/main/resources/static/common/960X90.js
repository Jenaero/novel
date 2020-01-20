function addCookie0803(objName, objValue, objHours) { //添加cookie
	var str = objName + "=" + escape(objValue);
	if (objHours > 0) { //为0时不设定过期时间，浏览器关闭时cookie自动消失
		var date = new Date();
		var ms = objHours * 3600 * 1000;
		date.setTime(date.getTime() + ms);
		str += "; expires=" + date.toGMTString();
	}
	document.cookie = str;
}

function getCookie0803(objName) { //获取指定名称的cookie的值
	var arrStr = document.cookie.split("; ");
	for (var i = 0; i < arrStr.length; i++) {
		var temp = arrStr[i].split("=");
		if (temp[0] == objName) return unescape(temp[1]);
	}
	return 0;
}

var adClass0803 = getCookie0803("adClass0803");
if (adClass0803 == null) {
	adClass0803 = 1;
} else {
	adClass0803++;
}
addCookie0803('adClass0803', adClass0803, 24)
if (adClass0803 % 2 == 1) {
	document.write(
		'<a href="#" target="_blank"><img src="https://gg.kkcaicai.com/960-90-1.gif" width="960" height="90"></a>'
	);
} else {
	document.write(
		'<a href="#" target="_blank"><img src="https://gg.kkcaicai.com/960-90-2.gif" width="960" height="90"></a>'
	);
}
