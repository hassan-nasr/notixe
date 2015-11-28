/**
 * Created by hasan on 2014-11-21.
 */
var x1;
var x2;
var y1;
var y2;
var imageSelectCrop = function() {
    var jcrop_api;

    $('.imageSelect').Jcrop({
//        onChange:   showCoords,
//        onSelect:   showCoords,
//        onRelease:  clearCoords
    },function(){
        jcrop_api = this;
    });

//    $('#coords').on('change','input',function(e){
//        var x1 = $('#x1').val(),
//            x2 = $('#x2').val(),
//            y1 = $('#y1').val(),
//            y2 = $('#y2').val();
//        jcrop_api.setSelect([x1,y1,x2,y2]);
//    });

    // Simple event handler, called from onChange and onSelect
    // event handlers, as per the Jcrop invocation above
    function showCoords(c)
    {
        $('#'+x1).val(c.x);
        $('#'+y1).val(c.y);
        $('#'+x2).val(c.x2);
        $('#'+y2).val(c.y2);
    };

    function clearCoords()
    {
        $('#coords input').val('');
    };
};


registerName = function(a1,a2,b1,b2){
    x1=a1;
    x2=a2;
    y1=b1;
    y2=b2;
}


var initCropper = function(selector) {
    var jcrop_api;

    function init() {
        return $(selector).cropper({
            aspectRatio: 1,
            done: function (c) {
                $('#' + x1).val(c.x);
                $('#' + y1).val(c.y);
                $('#' + x2).val(c.x + c.width);
                $('#' + y2).val(c.y + c.height);
            }
        });
    }

    setTimeout(
    init,100);

};




