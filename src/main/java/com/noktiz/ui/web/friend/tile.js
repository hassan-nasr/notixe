/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
function enableSlimScroll() {

    $('.sls').each(function() {
        if ($(this).height() > 70) {
            $(this).slimScroll({
                height: "70px"
            });
        }
    });
}