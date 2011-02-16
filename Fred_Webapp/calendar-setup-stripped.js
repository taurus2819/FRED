/*  Modified from the original by Mihai Bazon, 2002, 2003  |  http://dynarch.com/mishoo/ */
Calendar.setup = function (params) {
function param_default(pname, def) { if (typeof params[pname] == "undefined") { params[pname] = def; } };

param_default("inputField",     null);
param_default("displayArea",    null);
param_default("button",         null);
param_default("eventName",      "click");
param_default("ifFormat",       "%Y/%m/%d");
param_default("daFormat",       "%Y/%m/%d");
param_default("singleClick",    true);
param_default("disableFunc",    null);
param_default("dateStatusFunc", params["disableFunc"]);// takes precedence if both are defined
param_default("dateText",       null);
param_default("firstDay",       null);
param_default("align",          "Br");
param_default("range",          [1900, 2999]);
param_default("weekNumbers",    true);
param_default("flat",           null);
param_default("flatCallback",   null);
param_default("onSelect",       null);
param_default("onClose",        null);
param_default("onUpdate",       null);
param_default("date",           null);
param_default("showsTime",      false);
param_default("timeFormat",     "24");
param_default("electric",       true);
param_default("step",           1);
param_default("position",       null);
param_default("cache",          false);
param_default("showOthers",     false);
param_default("multiple",       null);

var tmp = ["inputField", "displayArea", "button"];
for (var i in tmp) {
if (typeof params[tmp[i]] == "string") {
params[tmp[i]] = document.getElementById(params[tmp[i]]);
}
}
if (!(params.flat || params.multiple || params.inputField || params.displayArea || params.button)) {
alert("Calendar.setup:\n  Nothing to setup (no fields found).  Please check your code");
return false;
}

function onSelect(cal) {
var p = cal.params;
var dFormat = "%d/%m/%Y";
if (cal.rounding == Calendar._TT["ROUNDING_MONTH"])
dFormat = "%m/%Y";
else if (cal.rounding == Calendar._TT["ROUNDING_YEAR"])
dFormat = "%Y";
cal.dateAsStr = cal.date.print(dFormat);
var update = (cal.dateClicked || p.electric);
if (update && p.inputField) {
p.inputField.value = cal.date.print(dFormat);
if (typeof p.inputField.onchange == "function")
p.inputField.onchange();
}
if (update && p.displayArea)
p.displayArea.innerHTML = cal.date.print(dFormat);
if (update && typeof p.onUpdate == "function")
p.onUpdate(cal);
if (update && p.flat) {
if (typeof p.flatCallback == "function")
p.flatCallback(cal);
}
if (update && p.singleClick && cal.dateClicked)
cal.callCloseHandler();
};

var triggerEl = params.button || params.displayArea || params.inputField;
triggerEl["on" + params.eventName] = function() {
var dateEl = params.inputField || params.displayArea;
var dateFmt = params.inputField ? params.ifFormat : params.daFormat;
var mustCreate = false;
var cal = window.calendar;
if (dateEl) {
var dateStr = dateEl.value || dateEl.innerHTML;
params.dateStr = dateStr;
if (dateStr.length == 4)
dateStr = "01/01/" + dateStr;
else if (dateStr.length == 7)
dateStr = "01/" + dateStr;
params.date = Date.parseDate(dateStr, "%d/%m/%Y");
}
if (!(cal && params.cache)) {
window.calendar = cal = new Calendar(params.firstDay,
     params.date,
     params.dateStr,
     params.onSelect || onSelect,
     params.onClose || function(cal) { cal.hide(); });
cal.showsTime = params.showsTime;
cal.time24 = (params.timeFormat == "24");
cal.weekNumbers = params.weekNumbers;
mustCreate = true;
} else {
if (params.date)
cal.setDate(params.date);
cal.hide();
}
if (params.multiple) {
cal.multiple = {};
for (var i = params.multiple.length; --i >= 0;) {
var d = params.multiple[i];
var ds = d.print("%Y%m%d");
cal.multiple[ds] = d;
}
}
cal.showsOtherMonths = params.showOthers;
cal.yearStep = params.step;
cal.setRange(params.range[0], params.range[1]);
cal.params = params;
cal.setDateStatusHandler(params.dateStatusFunc);
cal.getDateText = params.dateText;
cal.setDateFormat(dateFmt);
if (mustCreate)
cal.create();
cal.refresh();
if (!params.position)
cal.showAtElement(params.button || params.displayArea || params.inputField, params.align);
else
cal.showAt(params.position[0], params.position[1]);
return false;
};

return this;
};
