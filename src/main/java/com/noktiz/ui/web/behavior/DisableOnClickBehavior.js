/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

function DisableOnClickBehavior(componentId,enable){
    if(typeof enableMap ==="undefined"){
        enableMap=new Object();
    }
    jQuery("#"+componentId).on ("click",function(){
        enableMap[enable]=false;
    });
}

