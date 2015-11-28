/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


function SaveBehavior(componentId, enable,saveEnable ,enableStr, disableStr) {
    if (typeof enableMap === "undefined") {
        enableMap = new Object();
    }
    if(enableMap[enable]===false){
        enableMap[saveEnable]=false;
    }
    
    var intervalId=setInterval(function() {
        if(enableMap[enable]===true){
            enableMap[saveEnable]=true;
            enableMap[enable]=false;
            clearInterval(intervalId);
        }
        if (enableMap[saveEnable] === true) {
            jQuery("#" + componentId).text(enableStr);
            
        }
        else {
            jQuery("#" + componentId).text(disableStr);
        }
        
    }, 100);
}