/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
function getText(date1, base, enable) {
    var diff = base - date1;
    var second = 1000;
    var minute = 60 * second;
    var hour = 60 * minute;
    var day = 24 * hour;
    var month = 30 * day;
    var year = 365 * day;
    if (diff < minute) {
//        text = Math.floor(diff / second);
        text = "a few seconds ago";
    }
    else if (diff < hour) {
        text = Math.floor(diff / minute);
        if (text == 1) {
            text = text + " minute ago";
        }
        else{
            text = text + " minutes ago"
        }
    }
    else if (diff < day) {
        text = Math.floor(diff / hour);
        if (text == 1) {
        text = text + " hour ago";
        }
        else{
            text = text +" hours ago";
        }
    }
    else if (diff < month) {
        text = Math.floor(diff / day);
        if (text == 1) {
        text = text + " day ago";
        }
        else{
            text = text + " days ago";
        }
    }
    else if (diff < year) {
        text = Math.floor(diff / month);
        if (text == 1) {
        text = text + " month ago";
        }
        else{
            text = text +" months ago";
        }
    }
    else {
        text = Math.floor(diff / year);
        if (text == 1) {
        text = text + " year ago";
        }
        else{
            text = text +" years ago";
        }
    }
    return text;
}
function setElapceTime(id, period, enable, dyear, dmonth, dday, dhour, dminute, dsecond) {
    if (typeof enableMap === 'undefined') {
        enableMap = new Object();
    }

    if (typeof elapseTimeCallid !== 'undefined') {
        if (typeof elapseTimeCallid[id] !== 'undefined') {
            clearInterval(elapseTimeCallid[id]);
        }
    }
    callid = setInterval(function() {
        if (typeof enableMap[enable] !== 'undefined' && enableMap[enable] === false) {
            return;
        }
        var date1 = new Date(dyear, dmonth, dday, dhour, dminute, dsecond);
        var base = new Date();
        text = getText(date1, base);
        jQuery("#" + id).text(text);
    }, period)
    if (typeof elapseTimeCallid === 'undefined') {
        elapseTimeCallid = new Object();
    }
    elapseTimeCallid[id] = callid;
}
function setElapceTime2(id, period, enable) {
    if (typeof enableMap === 'undefined') {
        enableMap = new Object();
    }
    var date1 = new Date();
    if (typeof elapseTimeCallid !== 'undefined') {
        if (typeof elapseTimeCallid[id] !== 'undefined') {
            clearInterval(elapseTimeCallid[id]);
        }
    }
    callid = setInterval(function() {
        if (typeof enableMap[enable] !== 'undefined' && enableMap[enable] === false) {
            return;
        }
        var base = new Date();
        text = getText(date1, base);
        jQuery("#" + id).text(text);
    }, period)
    if (typeof elapseTimeCallid === 'undefined') {
        elapseTimeCallid = new Object();
    }
    elapseTimeCallid[id] = callid;
}