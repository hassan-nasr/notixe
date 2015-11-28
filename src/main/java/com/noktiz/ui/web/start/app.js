function inViewport($el) {
    var H = $(window).height(),
        r = $el[0].getBoundingClientRect();
    t = r.top;
    b=r.bottom;
    return Math.max(0, t>0? H-t : (b<H?b:H));  
};

$(window).on("scroll resize", function(){
	if ($("body").scrollTop()>100) {
		if(!$("#top").hasClass("navbar-inverse")){
			$("#top").addClass("navbar-inverse")
		}
	}else if($("body").scrollTop()<50){
		if($("#top").hasClass("navbar-inverse")){
			$("#top").removeClass("navbar-inverse")
		}
	}
    // if ($("body").scrollTop()>500) {
    //     if(!$('.notixe-first-img').hasClass("big-width")){
    //         $('.notixe-first-img').addClass("big-width")
    //     }
    // }else if($("body").scrollTop()<50){
    //     if($('.notixe-first-img').hasClass("big-width")){
    //         $('.notixe-first-img').removeClass("big-width")
    //     }
    // }
});

$(".sign-up-link").click(function(){
	EPPZScrollTo.scrollVerticalToElementById('sign-up-link', 100);
});
$(".learn-more-link").click(function(){
	EPPZScrollTo.scrollVerticalToElementById('learn-more-link', 30);
});
// $(".img1").hover(
// function()
// {
//     $("#img1").attr("src", "images/giff2.gif");
// },
// function()
// {
//     $("#img1").attr("src", "images/dna.png");
// });

var EPPZScrollTo =
{
    documentVerticalScrollPosition: function()
    {
        if (self.pageYOffset) return self.pageYOffset;
        if (document.documentElement && document.documentElement.scrollTop) return document.documentElement.scrollTop; 
        if (document.body.scrollTop) return document.body.scrollTop;
        return 0;
    },

    viewportHeight: function()
    { return (document.compatMode === "CSS1Compat") ? document.documentElement.clientHeight : document.body.clientHeight; },

    documentHeight: function()
    { return (document.height !== undefined) ? document.height : document.body.offsetHeight; },

    documentMaximumScrollPosition: function()
    { return this.documentHeight() - this.viewportHeight(); },

    elementVerticalClientPositionById: function(id)
    {
        var element = document.getElementById(id);
        var rectangle = element.getBoundingClientRect();
        return rectangle.top;
    },
    scrollVerticalTickToPosition: function(currentPosition, targetPosition)
    {
        var filter = 0.2;
        var fps = 60;
        var difference = parseFloat(targetPosition) - parseFloat(currentPosition);
        var arrived = (Math.abs(difference) <= 0.5);
        if (arrived)
        {
            scrollTo(10.0, targetPosition);
            return;
        }
        currentPosition = (parseFloat(currentPosition) * (1.0 - filter)) + (parseFloat(targetPosition) * filter);
        scrollTo(10.0, Math.round(currentPosition));
        setTimeout("EPPZScrollTo.scrollVerticalTickToPosition("+currentPosition+", "+targetPosition+")", (1000 / fps));
    },
    scrollVerticalToElementById: function(id, padding)
    {
        var element = document.getElementById(id);
        if (element == null)
        {
            console.warn('Cannot find element with id \''+id+'\'.');
            return;
        }
        var targetPosition = this.documentVerticalScrollPosition() + this.elementVerticalClientPositionById(id) - padding;
        var currentPosition = this.documentVerticalScrollPosition();
        var maximumScrollPosition = this.documentMaximumScrollPosition();
        if (targetPosition > maximumScrollPosition) targetPosition = maximumScrollPosition;
        this.scrollVerticalTickToPosition(currentPosition, targetPosition);
    }
};