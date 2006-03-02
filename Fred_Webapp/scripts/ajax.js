var xmlHttp

function getXmlHttpObject(handler) { 
	var objXmlHttp=null

	if (navigator.userAgent.indexOf("Opera")>=0) {
		alert("Opera is not a supported browser - please use IE or a Mozilla derived browser (eg Firefox)") 
		return; 
	}
	if (navigator.userAgent.indexOf("MSIE")>=0) { 
		var strName="Msxml2.XMLHTTP"
		if (navigator.appVersion.indexOf("MSIE 5.5")>=0) {
			strName="Microsoft.XMLHTTP"
		} 
		try { 
			objXmlHttp=new ActiveXObject(strName)
			objXmlHttp.onreadystatechange=handler 
			return objXmlHttp
		} catch(e) { 
			alert("Error. Scripting for ActiveX might be disabled") 
			return;
		} 
	} 
	if (navigator.userAgent.indexOf("Mozilla")>=0) {
		objXmlHttp=new XMLHttpRequest()
		objXmlHttp.onload=handler
		objXmlHttp.onerror=handler 
		return objXmlHttp
	}
}

function callAJAX(url, listener) { 
	xmlHttp=getXmlHttpObject(listener)
	xmlHttp.open("GET", url , true)
	xmlHttp.send(null)
} 
