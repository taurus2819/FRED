/*
 * From http://sanketvasa.blogspot.com/2008/09/javascript-creating-textbox-watermarks.html
 */

function clearTextbox(objId,text)
      {
      try
      {
      var obj = document.getElementById(objId);
      //Clear the textbox only if the textbox contains the original default value
      if (obj != null && obj.value.toString().toLowerCase() == text.toLowerCase())
      {
      obj.value = "";
      }
      }
      catch (e)
      {
      alert(e.message);
      }
      }

    function showDefaultText(objId,text)
      {
      try
      {
      var obj = document.getElementById(objId);
      if (obj != null && obj.value == "")
      {
      obj.value = text;
      }
      }
      catch (e)
      {
      alert(e.message);
      }
      }