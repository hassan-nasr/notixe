/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


$.expr[":"].containsNoCase = function(el, i, m) {
    var search = m[3];
    if (!search)
        return false;
    return new RegExp(search, "i").test($(el).text());
};
function enableSearchFor(searchText, searchContent,searchElement){
    $("#" + searchText).keyup(function() {
        var search = $(this).val();
//                    $(".time-entry").show();
        if (search) {
            selected = $("."+searchContent+":containsNoCase(" + search + ")").closest("."+searchElement);
            deselected = $("."+searchElement).not(selected);
            var size=selected.length+deselected.length;
            if(size<50){
            deselected.hide({duration: 300});
            selected.show({duration: 300});
            }
            else{
                deselected.hide();
                selected.show();
            }
//                        
//                        jQuery(".time-entry").not(":containsNoCase(" + search + ")").animate({"opacity":"0"},{"queue":false,"duration":300,"complete":function(){
//                                $(this).hide(300);
//                        }});
//                        $(".time-entry:containsNoCase(" + search + ")").animate({"opacity":"1"},{"queue":false,"duration":300 });

        } else {
            var size=$("."+searchElement).length;
            if(size<50){
            $("."+searchElement).show({duration: 300});
            }
            else{
                $("."+searchElement).show();
            }
        }
    });
}