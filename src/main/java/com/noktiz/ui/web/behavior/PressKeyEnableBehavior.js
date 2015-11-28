/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


function PressKeyEnableBehavior(componentid, enable) {
    if (typeof enableMap === 'undefined') {
        enableMap = new Object();
    }
    if(typeof enableMap[enable]==="undefined"){
        enableMap[enable] = false;
    }
    jQuery("#" + componentid).on('input',function() {
        enableMap[enable] = true;
    })
}