
function captchaEnable(cid,key) {
    gCaptchaCid=cid;
    onLoadCaptchaAPI= function (pcid) {
        if(typeof pcid==="undefined"){
            pcid=gCaptchaCid;
        }
        grecaptcha.render(pcid, {
            "data-theme": "dark",
            'sitekey': key
        });
    }
    if(typeof grecaptcha!=='undefined'){onLoadCaptchaAPI(cid);}
}

function reRenderCaptcha(cid){
    gCaptchaCid
    Recaptcha.destroy()
    jQuery(".g-recaptcha").each(function(){
//        grecaptcha.destroy();
//        $(this).empty();
        $(this).empty();
        if(typeof grecaptcha!=="undefined"){
            grecaptcha.resrt();
        }
    });
    onLoadCaptchaAPI(cid);
}
