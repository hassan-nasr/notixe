/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


function filltimezone(id){
    var d=new Date();
    var timez=d.getTimezoneOffset();
    timez=timez*60*1000;
    $("#"+id).val(timez);
}