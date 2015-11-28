/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

jQuery(this).find('input[type=checkbox]').each(function() {
    this.checked = !this.checked;
    if (this.checked) {
        jQuery(this).closest('.tile').attr({'class': 'tile double searchElement selected'});
    }
    else {
        jQuery(this).closest('.tile').attr({'class': 'tile double searchElement'});
    }
})

function SelectPersonBehavior(containerid, checkboxid) {
    jQuery("#" + containerid).on("click", function() {
        jQuery("#" + checkboxid).each(function() {
            this.checked = !this.checked;
            if (this.checked) {
                jQuery(this).closest('.tile').addClass("selected");
            }
            else {
                jQuery(this).closest('.tile').removeClass("selected");
            }
        });
    })
}