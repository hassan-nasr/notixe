/**
 * Created by hasan on 9/5/14.
 */

// This is called with the results from from FB.getLoginStatus().
function statusChangeCallback(response,successContinue) {
    console.log('statusChangeCallback');
    console.log(response);
    // The response object is returned with a status field that lets the
    // app know the current login status of the person.
    // Full docs on the response object can be found in the documentation
    // for FB.getLoginStatus().
    if (response.status === 'connected') {
        // Logged into your app and Facebook.
        console.log(response.authResponse.accessToken);
        testAPI();

        registerData(response.authResponse.accessToken, successContinue)
    } else if (response.status === 'not_authorized') {
        // The person is logged into Facebook, but not your app.
        document.getElementById('status').innerHTML = 'Please log ' +
            'into this app.';
    } else {
        // The person is not logged into Facebook, so we're not sure if
        // they are logged into this app or not.
        document.getElementById('status').innerHTML = 'Please log ' +
            'into Facebook.';
        FB.login();
    }
}

// This function is called when someone finishes with the Login
// Button.  See the onlogin handler attached to it in the sample
// code below.
function checkLoginState(successContinue) {
    console.log("hi");
    FB.getLoginStatus(function(response) {
        statusChangeCallback(response,successContinue);
    });
}
var appid='768865719827766';
window.fbAsyncInit = function() {
//    FB.init({
//        appId      : appid,
////                        appId      : '1443280285948927',
//        cookie     : true,  // enable cookies to allow the server to access
//        // the session
////                        xfbml      : true,  // parse social plugins on this page
//        version    : 'v2.0' // use version 2.0
//    });

    // Now that we've initialized the JavaScript SDK, we call
    // FB.getLoginStatus().  This function gets the state of the
    // person visiting this page and can return one of three states to
    // the callback you provide.  They can be:
    //
    // 1. Logged into your app ('connected')
    // 2. Logged into Facebook, but not your app ('not_authorized')
    // 3. Not logged into Facebook and can't tell if they are logged into
    //    your app or not.
    //
    // These three cases are handled in the callback function.

//                    FB.getLoginStatus(function(response) {
//                        statusChangeCallback(response,function(){alert('other one')});
//                    });

};

// Load the SDK asynchronously
loadFB=function(d, s, id) {
    var js, fjs = d.getElementsByTagName(s)[0];
    if (d.getElementById(id)) return;
    js = d.createElement(s); js.id = id;
    js.src = "//connect.facebook.net/en_US/sdk.js";
    fjs.parentNode.insertBefore(js, fjs);
};

// Here we run a very simple test of the Graph API after login is
// successful.  See statusChangeCallback() for when this call is made.
function testAPI() {
    console.log('Welcome!  Fetching your information.... ');
    FB.api('/me', function(response) {
        console.log('Successful login for: ' + response.name);
        document.getElementById('status').innerHTML =
            'Thanks for logging in, ' + response.name + '!';
    });
}
function registerData(id,token,next) {
    $.post('/registerCredential',
        {
            service:'facebook',
            access_token:token,
            facebook_id:id
        })
        .done(next())
    console.log('Welcome!  Fetching your information.... ');
    FB.api('/me', function(response) {
        console.log('Successful login for: ' + response.name);
        document.getElementById('status').innerHTML =
            'Thanks for logging in, ' + response.name + '!';
    });
}
ready=false;
function FacebookLogin(scope,success,fail){
    if(ready==false){
        setTimeout(FacebookLogin(scope,success,fail),1000);
        return;
    }
    FB.login(function(response) {
        if (response.authResponse) {
            success(response.authResponse.userID,response.authResponse.accessToken);
        } else {
            fail();
        }
    }, {scope: scope});
}

function myFacebookLogin(scope){
    if($("#agree").length == 0 || $("#agree").is(":checked"))
        FacebookLogin(scope,
            function(id,token){
                $("#facebookId").val(id);
                $("#access_token").val(token);
                $("#submitData").click();
            },
            function(){
                $('#indImage').css('display', 'none');
            }
        )
}

function indicate(){
    if($("#agree").length == 0 || $("#agree").is(":checked")) {
        $('#indImage').css('display', 'inline');
        $("#agreeWarning").css("display","none");
    }
    else
        $("#agreeWarning").css("display","block").css('text-color','red');
}

initFB = function(appid1) {
    appid = appid1;
    loadFB(document, 'script', 'facebook-jssdk');

    window.fbAsyncInit = function () {
        FB.init({
            appId: appid,
//                        appId      : '1443280285948927',
            cookie: true,  // enable cookies to allow the server to access
            // the session
//                        xfbml      : true,  // parse social plugins on this page
            version: 'v2.0' // use version 2.0
        });
        ready=true;
    }

}

/*'email,user_likes,user_friends,read_stream,publish_actions'*/