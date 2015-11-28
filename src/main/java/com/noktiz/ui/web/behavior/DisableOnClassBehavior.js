/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

function DisableOnClassBehavior(componentId, htmlClass, enable) {
    id=componentId+"_"+htmlClass+"_"+enable;
    if (typeof enableMap === "undefined") {
        enableMap = new Object();
    }
    if(typeof enableMap[enable]==="undefined"){
        enableMap[enable]=true;
    }
    if (typeof DisableOnClassCallId === 'undefined') {
        DisableOnClassCallId = new Object();
    }
    if(typeof DisableOnClassCallId[id]!== "undefined"){
        return;
    }
    callid=setInterval( function() {
        var classes = jQuery("#" + componentId).attr('class').split(" ");
        var found = 0;
        for (i = 0; i < classes.length; i++) {
            if (classes[i] === htmlClass) {
                found++;
            }
        }
        if (!found) {
            enableMap[enable] = true;
        }
        else {
            enableMap[enable] = false;
        }
    },100);
    DisableOnClassCallId[id] = callid;
}

