package rest.test.User;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.noktiz.ui.rest.services.response.AuthenticateInfo;
import com.noktiz.ui.rest.services.response.UserInfo;
import com.sun.jersey.api.client.WebResource;

import java.io.IOException;

//import org.codehaus.jackson.map.ObjectMapper;

/**
 * Created by hassan on 08/11/2015.
 */
public class UserWSTestFunctions {

    public static AuthenticateInfo getAuthenticateInfo(String userEmail, String userPassword, WebResource resource) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String authenticateResponse;
        AuthenticateInfo authenticateInfo;
        authenticateResponse = resource.path("/user/authenticate")
                .queryParam("email", userEmail)
                .queryParam("password", userPassword).get(String.class);

        authenticateInfo = mapper.readValue(authenticateResponse, AuthenticateInfo.class);
        return authenticateInfo;
    }

    public static UserInfo getUserInfo(String userId, String accessToken, WebResource resource) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String infoResponse;
        UserInfo userInfo;
        infoResponse = resource.path("user/info")
                .queryParam("userId", userId)
                .queryParam("accessToken", accessToken)
                .get(String.class);
        userInfo = mapper.readValue(infoResponse, UserInfo.class);
        return userInfo;
    }


}
