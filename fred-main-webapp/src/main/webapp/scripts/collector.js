//Write the text field
document.write('<input id="collectorField" name="Coll" maxlength="64" onblur="hideCollectorDropout()" onkeyup="processCollectorKeyStroke(this, event)">');

//Write the div
document.write('<div id="collectorHint" style="z-index: 10; border: solid 1pt black; visibility: hidden; position: absolute"></div>');

var isIE = navigator.appName.indexOf("Internet Expl") > -1;
if (isIE)
	document.write('<iframe id="collectorFrame" style="z-index: 9; position:absolute; visibility: hidden"></iframe>');
	
var collectorHintVisible = false;
var selectedCollectorHint = -1;
var maximumCollectorHint = 0;

function processCollectorKeyStroke(collectorBox, event) {
	var keyCode = (document.layers) ? keyStroke.which : event.keyCode;
	
	switch (keyCode) {
		case 27:	//Escape
			hideCollectorDropout(false);
			break;
		case 38:	//Up arrow
			collectorHighlightUp();
			break;
		case 40:	//Down arrow
			collectorHighlightDown();
			break;
		case 13:
			updateCollectorFromKeyEvent();
			break;
		default:
			processCollectorHint(collectorBox);
			break;
	}
}

function unHighlightHint(hint) {
	if (hint >= 0) {
		oldObj = document.getElementById("collectorHint" + hint);
		oldObj.style.background = ((hint % 2) == 0) ? "white" : "#eeeeee";
		oldObj.style.color = "black";
	}
}

function highlightHint(hint) {
	newObj = document.getElementById("collectorHint" + hint);
	newObj.style.background = "darkblue";
	newObj.style.color = "white";
}

function collectorHighlightDown() {
	unHighlightHint(selectedCollectorHint);

	selectedCollectorHint += 1;
	if (selectedCollectorHint >= maximumCollectorHint)
		selectedCollectorHint = 0;
		
	highlightHint(selectedCollectorHint);
}

function collectorHighlightUp() {
	unHighlightHint(selectedCollectorHint);

	selectedCollectorHint -= 1;
	if (selectedCollectorHint <0)
		selectedCollectorHint = maximumCollectorHint - 1;
		
	highlightHint(selectedCollectorHint);
}

function updateCollectorFromKeyEvent() {
	collField = document.getElementById("collectorField");
	collField.value = document.getElementById("collectorHint" + selectedCollectorHint).collectorName;
	hideCollectorDropout(true);
	collField.focus();
}

function processCollectorHint(collectorBox) {
	var val = collectorBox.value;
	if (val.length == 0)
		hideCollectorDropout();
	else
		callAJAX("ajaxSupport.xml?type=Person&start=" + val + "&q=" + Math.random(), showCollectorHint);
}

function showCollectorHint() {
	if (xmlHttp.readyState == 4 || xmlHttp.readyState == "complete") {
		var xml = xmlHttp.responseXML;
		var thediv = document.getElementById("collectorHint");
		thediv.innerHTML = "";
		createAndShowTable(thediv, xml);
	}
}

function createAndShowTable(thediv, xml) {
	var names = xml.getElementsByTagName("person");
	if (names.length == 0) {
		hideCollectorDropout(true);
		return;
	}
	for (var i=0; i<names.length; i++) {
		var personName = names[i].getElementsByTagName("name")[0].firstChild.data;
		var personId = names[i].getElementsByTagName("id")[0].firstChild.data;
		var cell = document.createElement("div");
		if (i % 2 == 0)
			cell.style.background = "white";
		else
			cell.style.background = "#eeeeee";
		cell.id = "collectorHint" + i;
		cell.collectorId = personId;
		cell.collectorName = personName;
		cell.innerHTML = personName;
		cell.style.paddingRight = "5px";
		cell.style.paddingLeft = "5px";
		cell.style.whiteSpace = "nowrap";
		cell.style.cursor = "default";
		//eval("cell.onclick = function() {updateCollector('" + personName + "');}");
                cell[onclick] = function() {updateCollector('" + personName + "');};
		thediv.appendChild(cell);
	}
	//Reset the selected settings
	selectedCollectorHint = -1;
	maximumCollectorHint = names.length;
	
	if (!collectorHintVisible)
		showCollectorDropout();
	else if (isIE) 
		showIEFix(thediv);
}

function updateCollector(value) {
	var collField = document.getElementById("collectorField");
	collField.value = value;
	collField.focus();
}

function showCollectorDropout() {
	thediv = document.getElementById("collectorHint");
	thetxt = document.getElementById("collectorField");

	thediv.style.left = getXPos(thetxt) + "px";
	thediv.style.top = (getYPos(thetxt) + thetxt.offsetHeight) + "px";

	if (isIE) {
		showIEFix(thediv);
	}

	fadeIn("collectorHint", 0);
	thediv.style.visibility = "visible";
	collectorHintVisible = true;
}

function showIEFix(thediv) {
	thefrm = document.getElementById("collectorFrame");
	thefrm.style.visibility = "visible";
	thefrm.style.left = thediv.style.left;
	thefrm.style.top = thediv.style.top;
	thefrm.style.width = thediv.offsetWidth + "px";
	thefrm.style.height = thediv.offsetHeight + "px";
}

function hideCollectorDropout(immediate) {
	if (immediate) {
		document.getElementById("collectorHint").style.visibility = "hidden";
	} else {
		fadeOut("collectorHint", 90);
	}
	collectorHintVisible = false;
	if (isIE) 
		document.getElementById("collectorFrame").style.visibility = "hidden";
}