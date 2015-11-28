/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


function setClick(select,target){
    jQuery("#"+select).css("cursor","pointer").click(function(){
        jQuery("#"+target).trigger("click");
    });
}