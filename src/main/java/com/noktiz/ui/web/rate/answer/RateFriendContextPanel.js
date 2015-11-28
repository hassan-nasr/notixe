/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

function enableChangeWidth(){
    setWidth();
    $(window).resize(function() {
        setWidth();
    });
}
function setWidth(){
//    $(".row-fluid.answer").each(function(){
//       var left=$(this).find(".star").width();
//       var right=$(this).find(".submit").width();
//       var all=$(this).parent().width();
//       var newWidth=all-left-right;
//       $(this).find(".comment").css({width:newWidth+"px"});
//    });
}