/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


function CallPeriodically(id, time, enable, disableBeforCall, disableOnSuccess) {
    if (typeof enableMap === 'undefined') {
        enableMap = new Object();
    }

    if (typeof CallPeriodicallyCallid !== 'undefined') {
        if (typeof CallPeriodicallyCallid[id] !== 'undefined') {
            clearInterval(CallPeriodicallyCallid[id]);
        }
    }
    jQuery("#" + id).click(function() {
        enableMap[disableBeforCall] = false;
    });
    callid = setInterval(function() {
        if (typeof enableMap[enable] !== 'undefined' && enableMap[enable] === false) {
            return;
        }

        jQuery("#" + id).trigger("click");

        jQuery("#" + id).bind('ajax:complete', function() {
            if (typeof enableMap[enable] !== 'undefined' && enableMap[enable] === true) {
                if (disableOnSuccess) {
                    enableMap[enable] = false;
                }
            }
        });
    }, time);
    if (typeof CallPeriodicallyCallid === 'undefined') {
        CallPeriodicallyCallid = new Object();
    }
    CallPeriodicallyCallid[id] = callid;
}