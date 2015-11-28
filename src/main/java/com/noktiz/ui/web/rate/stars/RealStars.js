/**
 * Created by hasan on 2014-10-13.
 */

$.fn.stars = function() {
    return $(this).each(function() {
        // Get the value
        var s = parseFloat($(this).attr('size'));
        var val = parseFloat($(this).html());
        // Make sure that the value is in 0 - 5 range, multiply to get width
        var size = Math.max(0, (Math.min(5, val))) * s;
        // Create stars holder
        var $span = $('<span />').width(size).height(s).css('background-size',s+'px');
        // Replace the numerical value with stars
        $(this).html($span);
        $(this).css('background-position','0 '+(-s)+'px').height(s).width(5*s).css('background-size',s+'px');

    });
}

