/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 * Comment
 */
function fittocontainer(container) {
    oneColumn=[];
    if(typeof cwidth[container]=="undefined"){
        cwidth[container]=0;
    }
    if(typeof pcwidth=="undefined"){
        pcwidth=[];
    }
    pcwidth[container] =cwidth[container];

    var sugContainerSelector=".friendSuggests"
    var sugWidth = $(sugContainerSelector).first().width();
    sugWidth=sugWidth-9;
    var containerSelector = "#"+container+" .tiles";
    var width = $(containerSelector).first().width()-10;
    cwidth[container]=width;
    var noMarginTileWidth=0;
    if(pcwidth[container]===cwidth){
        return;
    }
    tilewidth = (width + 9) / 6 - 18;
    noMarginTileWidth=(width ) / 6 -8;
    if (width < 1752) {
        tilewidth = (width + 9) / 5 - 18;
        noMarginTileWidth=(width ) / 5 -8;
    }
    if (width < 1460) {
        tilewidth = (width + 9) / 4 - 18;
        noMarginTileWidth=(width ) / 4 -8;
    }
    if (width < 1168) {
        tilewidth = (width + 9) / 3 - 18;
        noMarginTileWidth=(width ) / 3 -8;
    }
    if (width < 876) {
        tilewidth = (width + 9) / 2 - 18;
        noMarginTileWidth=(width ) / 2 -8;
    }
    bigtilewidth = (width + 9) / 2 - 18;
    var noMarginBigTileWidth=(width) / 2 -8;
    oneColumn[container] = false;
    if (width < 584) {
        tilewidth = (width + 9) / 1 - 18;
        bigtilewidth = (width + 9) / 1 - 18;
        noMarginBigTileWidth=(width ) / 1 -8
        noMarginTileWidth=(width ) / 1 -8;
        oneColumn[container] = true;
    }

    $('<style>'+"#"+container+' .tile.mytile{width:' + tilewidth + 'px !important}</style>').appendTo('head');
    $('<style>'+"#"+container+' .tile.mytile.noMargin{width:' + noMarginTileWidth + 'px !important}</style>').appendTo('head');
    $('<style>'+"#"+container+' .tile.mytile.bigt.noMargin{width:' + noMarginBigTileWidth + 'px !important}</style>').appendTo('head');
    $('<style>'+"#"+container+' .tile.mytile.bigt{width:' + bigtilewidth + 'px !important}</style>').appendTo('head');
    $('<style>'+"#"+container+' .tile.headerTile{width:' + (width - 9) + 'px !important;  height:' + (30) + 'px !important;}</style>').appendTo('head');
    $('<style>'+"#"+container+' .tile.mytile.friendSuggest{width:'+sugWidth+'px !important;  height:100px !important;}'+'</style>').appendTo('head');
//    $('<style>'+' .tile.mytile.predefinedQuestion{width:240px !important; height:100px !important;}'+'</style>').appendTo('head');
//    $("#" + containerId + " .tile").each(function (){
//        this.style.setProperty('width',tilewidth+"px","important");
//    });
}
function tileResize(container) {
    cwidth=[];
    fittocontainer(container);
    enableSlimScroll(container);
}
function enableTileResize(container){
    if(typeof cwidth =="undefined") {
        cwidth = [];
    }
    $(window).resize(function() {
        fittocontainer(container);
        enableSlimScroll(container);
    });
}
function enableSlimScroll(container) {
    if(typeof oneColumn=="undefined"){
        oneColumn=[];
    }
    if (typeof oneColumn[container]!=="undefined"&&!oneColumn[container]) {
        $("#"+container+' .sls').each(function() {
//            $(this).css({height: '130px'});
            if ($(this).height() >= 70) {
                $(this).slimScroll({
                    height: "70px"
                });
            }
            var textarea=$(this).find('textarea');
            if(!(textarea===undefined)){
                $(textarea).slimScroll({
                    height: "70px",
                    width: "inherited"
                });
            }
        });
        $("#"+container+' .tile.mytile').css({height: '130px'});
    } else {
        $("#"+container+' .tile.mytile').each(function() {
            var height1 = $(this).find('.descLink').first().height();
            var $div = $(this).find('.descLink').closest('[class^="slimScrollDiv"]');
            $div.css({height:height1+'px'});
            $div = $(this).find('.descLink').closest('[class^="sls"]');
            $div.css({height:height1+'px'});
//            $(this).find('.descLink').first().parent().css({height:height1+'px'});
            var descPos = $(this).find('.descLink').first().position();
            var top1=0;
            if(!(descPos===undefined)){
                top1=descPos.top;
            }
            var height2 = $(this).find('.img').first().height();
            var imgpos=$(this).find('.img').first().position();
            var top2 =0;
            if(!(imgpos===undefined)){
                top2=imgpos.top;
            }
            var height3 = $(this).find('textarea').first().height();
            var textpos=$(this).find('textarea').first().position();
            var top3 =0;
            if(!(textpos===undefined)){
                top3=textpos.top;
            }
            var starh = $(this).find('.mystar').first().height();
            starh+=5
            var objheight = $(this).find('.tile-object').first().height();
            height1+=top1;
            height2+=top2;
            height3+=top3+10;
            
            if(height2>height1){
                height1=height2;
            }
            if(height3>height1){
                height1=height3;
            }
            if(starh>objheight){
                objheight=starh;
            }
            height1 +=objheight ;
            $(this).css({height: height1 + 'px'});
        })
    }
}