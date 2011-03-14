function getXPos(obj) {
	var pos=obj.offsetLeft;
	var tempObj=obj.offsetParent;
 	while (tempObj!=null) {
		pos+=tempObj.offsetLeft;
		tempObj=tempObj.offsetParent;
	}
	return pos;
}

function getYPos(obj) {
	var pos=obj.offsetTop;
	var tempObj=obj.offsetParent;
	while (tempObj!=null) {
 		pos+=tempObj.offsetTop;
 		tempObj=tempObj.offsetParent;
 	}
	return pos;
}