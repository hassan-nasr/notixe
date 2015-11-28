/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

function ChangePropertyInput(watchingId,watchingProperty,watcher){
    setInterval(function (){
        val=jQuery("#"+watchingId).attr(watchingProperty);
        if(typeof val==="undefined"){
            val="";
        }
        jQuery("#"+watcher).attr({"value":val});
    },1000);
}
