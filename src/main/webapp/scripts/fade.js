function setOpacity(obj, opacity) {
  opacity = (opacity == 100)?99.999:opacity;
  
  // IE/Win
  obj.style.filter = "alpha(opacity:"+opacity+")";
  
  // Safari<1.2, Konqueror
  obj.style.KHTMLOpacity = opacity/100;
  
  // Older Mozilla and Firefox
  obj.style.MozOpacity = opacity/100;
  
  // Safari 1.2, newer Firefox and Mozilla, CSS3
  obj.style.opacity = opacity/100;
}

var currentFadeIn, currentFadeOut;

function fadeIn(objId,opacity) {
  if (document.getElementById) {
    obj = document.getElementById(objId);
    if (obj.currentFadeOut)
    	clearTimeout(obj.currentFadeOut);
    if (opacity <= 100) {
      setOpacity(obj, opacity);
      opacity += 20;
      obj.currentFadeIn = window.setTimeout("fadeIn('"+objId+"',"+opacity+")", 100);
    }
  }
}

function fadeOut(objId,opacity) {
  if (document.getElementById) {
    obj = document.getElementById(objId);
    if (obj.currentFadeIn)
    	clearTimeout(obj.currentFadeIn);
    if (opacity >= 0) {
      setOpacity(obj, opacity);
      opacity -= 20;
      obj.currentFadeOut = window.setTimeout("fadeOut('"+objId+"',"+opacity+")", 100);
    } else {
    	obj.style.visibility = "hidden";
    }
  }
}
