/** Globale Variablen - ajax.js **/
var wfsRequest = wfsRequestObject();

/**
 * Erzeugt das XMLHttpRequestObject, das für Ajax benötigt wird.
 */
function wfsRequestObject()
{
  try
  {
    wfsRequest = new ActiveXObject("Microsoft.XMLHTTP");
  }
  catch(Error)
  {
    try
    {
      wfsRequest = new ActiveXObject("MSXML2.XMLHTTP");
    }
    catch(Error)
    {
      try 
      {
      wfsRequest = new XMLHttpRequest();
      }
      catch(Error)
      {
        alert("Erzeugung des XMLHttpRequest-Objekts ist nicht möglich");
      }
    }
  }
  return wfsRequest;
}

/**
 * Sendet die Ajax - Anfrage des Client an der Server.
 */
function sndWFSRequest(proxy, url) 
{
	wfsRequest.open('get', proxy, true);
   	wfsRequest.onreadystatechange = handleWFSResponse;
   	if(wfsRequest.overrideMimeType)
        wfsRequest.overrideMimeType("text/xml");
   	wfsRequest.send(null);
}

/**
 * Verarbeitet das Ergebnis eines sndReq - Aufrufes
 */
function handleWFSResponse()
{
  if(wfsRequest.readyState == 4 && wfsRequest.status == 200)
  {	
	var xmldoc = wfsRequest.responseXML.documentElement;
     
	var list = xmldoc.getElementsByTagName("gns:feature_id");
	if(list.length==0)	//care for Google Chrome specifics
		list = xmldoc.getElementsByTagName("feature_id");
	
	var table = document.createElement("table");
	var idList = new Array();
	
	var result = "";
	for (var i=0;i<list.length;i++)
	{
		var tableRow = document.createElement("tr");
		var tableData = document.createElement("td");
		var textNode = document.createTextNode(list.item(i).childNodes[0].nodeValue);
		tableData.appendChild(textNode);
		tableRow.appendChild(tableData);
		table.appendChild(tableRow);
		result+=list.item(i).childNodes[0].nodeValue;
		result+=',';
		//idList.push(list.item(i).childNodes[0].nodeValue);
	}
	//base.appendChild(table);
	
	document.getElementById('wfsResponse').innerHTML = result;
	returnToParent(result.substring(0, result.length-1));
  }
}
