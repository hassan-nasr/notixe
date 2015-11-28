function align(classStr){
    var bp1 = 0, bp2 = 0, bp3 = 0;
    jQuery("." + classStr).each(function () {
        jQuery(this).find(".part1").css({height: 'initial'});
        jQuery(this).find(".part2").css({height: 'initial'});
        jQuery(this).find(".part3").css({height: 'initial'});
        var p1 = jQuery(this).find(".part1").height();
        var p2 = jQuery(this).find(".part2").height();
        var p3 = jQuery(this).find(".part3").height();
        if (bp1 < p1) {
            bp1 = p1;
        }
        if (bp2 < p2) {
            bp2 = p2;
        }
        if (bp3 < p3) {
            bp3 = p3;
        }
    })
    jQuery("." + classStr).each(function () {
        if(bp1>0) {
            jQuery(this).find(".part1").css({height: bp1});
        }
        jQuery(this).find(".part2").css({height: bp2});
        jQuery(this).find(".part3").css({height: bp3});
    })
}

function enableAlign(classStr){
    align(classStr)
    $(window).resize(function() {
        align(classStr);
    })
}