/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


function openNotif(id, enable) {
    if (typeof enableMap !== "undefined")
        if (enableMap[enable] === false) {
            jQuery("#" + id).addClass("open");
        }
}
function enablescroll() {
    jQuery("#header_notification_bar").click(function() {
        setTimeout(function() {
            var h = jQuery("#notifinner").height();
            var heightThreshold = (window.innerHeight - 78);
            if (h > heightThreshold) {
                $('#notifcontainer').slimScroll({
                    height: heightThreshold
                });
            } else {
                $("#notifcontainer").slimScroll({destroy: true});
                $('#notifcontainer').css({height: 'initial'});
                $('#notifcontainer').parent().css({height: 'initial'});
                
            }
        },1);
    }
    );

}