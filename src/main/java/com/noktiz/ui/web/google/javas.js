(function() {
    var po = document.createElement('script');
    po.type = 'text/javascript'; po.async = true;
    po.src = 'https://plus.google.com/js/client:plusone.js';
    var s = document.getElementsByTagName('script')[0];
    s.parentNode.insertBefore(po, s);
})();
src="//ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js" 

var helper = (function() {
    var authResult = undefined;
    return {
        onSignInCallback: function(authResult) {
            if (authResult['access_token']) {
                this.authResult = authResult;
                helper.connectServer();
                gapi.client.load('plus','v1',function(){
                     var request = gapi.client.plus.people.get({
                         'userId':'me'
                     });
                     request.execute(function(response){
                        $("#userName").text(response.name.givenName);
                        $("#userFamilyName").text(response.name.familyName);
                        $("#userImage").attr('src',response.image.url);
                        $("#userGender").text(response.gender);
                     });
                });
            
                $("#gConnect").hide();
            } else if (authResult['error']) {
                console.log('There was an error: ' + authResult['error']);
                $('#gConnect').show();
            }
            console.log('authResult', authResult);
        },
        
        renderProfile: function() {
            var request = gapi.client.plus.people.get( {'userId' : 'me'} );
            request.execute( function(profile) {
                $('#profile').empty();
                if (profile.error) {
                    $('#profile').append(profile.error);
                    return;
                }
                $('#profile').append(
                        $('<p><img src=\"' + profile.image.url + '\"></p>'));
                $('#profile').append(
                        $('<p>Hello ' + profile.displayName + '!<br />Tagline: ' +
                        profile.tagline + '<br />About: ' + profile.aboutMe + '</p>'));
                if (profile.cover && profile.coverPhoto) {
                    $('#profile').append(
                            $('<p><img src=\"' + profile.cover.coverPhoto.url + '\"></p>'));
                }
            });
            $('#authOps').show('slow');
            $('#gConnect').hide();
        },
        /**
         * Calls the server endpoint to disconnect the app for the user.
         */
        disconnectServer: function() {
            // Revoke the server tokens
            $.ajax({
                type: 'POST',
                url: window.location.href + 'disconnect',
                async: false,
                success: function(result) {
                    console.log('revoke response: ' + result);
                    $('#authOps').hide();
                    $('#profile').empty();
                    $('#visiblePeople').empty();
                    $('#authResult').empty();
                    $('#gConnect').show();
                },
                error: function(e) {
                    console.log(e);
                }
            });
        },
        
        connectServer: function() {
            console.log(this.authResult.code);
            $("#bridgeText0").val(this.authResult.code);
            $("#bridgeText1").val($("#stateLabel").text());
            console.log($("#stateLabel").text());
            $("#bridgeSubmit").click();
            
//            var stat = $("#bridgeText1").val($("#stateLabel").text();
            
//            $.post(
//                "/" + 'registerCredential',
//                {
//                    client_code: this.authResult.code,
//                    service : 'google',
//                }
//            );
        },
        
        /*
        people: function() {
            $.ajax({
                type: 'GET',
                url: window.location.href + 'people',
                contentType: 'application/octet-stream; charset=utf-8',
                success: function(result) {
                    helper.appendCircled(result);
                },
                processData: false
            });
        },
        /**
         * Displays visible People retrieved from server.
         *
         * @param {Object} people A list of Google+ Person resources.
         */
        appendCircled: function(people) {
            $('#visiblePeople').empty();
            
            $('#visiblePeople').append('Number of people visible to this app: ' +
                    people.totalItems + '<br/>');
            for (var personIndex in people.items) {
                person = people.items[personIndex];
                $('#visiblePeople').append('<img src="' + person.image.url + '">');
            }
        },
    };
})();

function onSignInCallback(authResult) {
    helper.onSignInCallback(authResult);
}