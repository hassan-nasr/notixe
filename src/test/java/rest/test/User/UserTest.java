package rest.test.User;

import com.noktiz.ui.rest.services.response.AuthenticateInfo;
import com.noktiz.ui.rest.services.response.UserInfo;
import org.junit.Test;
import rest.JerseyTest.AbstractJerseyTest;
import rest.test.Vars;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by hassan on 08/11/2015.
 */
public class UserTest extends AbstractJerseyTest {
    @Test
    public void authenticate() throws IOException {
        final AuthenticateInfo authenticateInfo = UserWSTestFunctions.getAuthenticateInfo(Vars.adminEmail, Vars.adminPassword, resource());
        assertNotNull(authenticateInfo);
        assertEquals(Vars.adminEmail,authenticateInfo.getEmail());
        assertNotNull(authenticateInfo.getAccessToken());
    }

    @Test
    public void authenticateAndGetInfo() throws IOException {

        final AuthenticateInfo authenticateInfo = UserWSTestFunctions.getAuthenticateInfo(Vars.adminEmail, Vars.adminPassword, resource());
        assertNotNull(authenticateInfo);
        assertEquals(Vars.adminEmail,authenticateInfo.getEmail());
        assertNotNull(authenticateInfo.getAccessToken());

        final UserInfo userInfo = UserWSTestFunctions.getUserInfo(authenticateInfo.getUserId(), authenticateInfo.getAccessToken(), resource());
        assertNotNull(userInfo);
        assertEquals(Vars.adminFirstName, userInfo.getFirstName());
        assertEquals(Vars.adminGender, userInfo.getGender());
    }

}
