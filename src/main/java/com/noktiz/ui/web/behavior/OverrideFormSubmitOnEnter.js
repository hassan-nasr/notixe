/**
 * Created by Hossein on 2/27/2015.
 */
preventSubmit(id)
{
    $('#'+id).on("keyup keypress", function (e) {
        var code = e.keyCode || e.which;
        if (code == 13) {
            e.preventDefault();
            return false;
        }
    });
};
ajaxSubmitForm(id,submitId)
{
    $('#'+id).on("keyup keypress", function (e) {
        var code = e.keyCode || e.which;
        if (code == 13) {
            document.getElementById(submitId).onclick();
            return false;
        }
    });
}