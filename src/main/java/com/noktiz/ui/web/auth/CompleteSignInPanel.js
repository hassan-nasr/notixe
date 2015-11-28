function readyCaptcha() {
    $("#forget-password").click(function () {
        $("#captchaPanel").show();
    })
    $("#register-btn").click(function () {
        $("#captchaPanel").show();
    })
    $("#back-btn").click(function () {
        $("#captchaPanel").hide();
    })
    $("#register-back-btn").click(function () {
        $("#captchaPanel").hide();
    })
}
