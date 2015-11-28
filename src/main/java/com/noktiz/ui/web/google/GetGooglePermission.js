var pressed = false;

function startflow(){
    var options = {
        'callback' : signinCallback,
        'clientid' : document.getElementById('clientid').getAttribute("content"),
        'requestvisibleactions' : 'http://schema.org/AddAction',
        'cookiepolicy' : 'single_host_origin',
        'scope' : document.getElementById('scopesList').getAttribute("content")
    };
    gapi.auth.signIn(options);
}

function signinCallback(authResult) {
    if (authResult['status']['signed_in'] && !pressed) {
        // Update the app to reflect a signed in user
        // Hide the sign-in button now that the user is authorized, for example:
        $('#gsignInText').text('Connecting to Google');
        $('#gindImage').css('display','inline');
        var code = authResult['code'];
        document.getElementById('bridgeText0').value = code;
        document.getElementById("bridgeSubmit").click();
        pressed = true;
    } else {
        // Update the app to reflect a signed out user
        // Possible error values:
        //   "user_signed_out" - User is signed-out
        //   "access_denied" - User denied access to your app
        //   "immediate_failed" - Could not automatically log in the user
        console.log('Sign-in state: ' + authResult['error']);
        pressed = false;
    }
}
