package com.noktiz.ui.rest.services.ws;

import com.noktiz.domain.entity.PersonalInfo;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.model.PasswordManager;
import com.noktiz.domain.model.Result;
import com.noktiz.domain.model.UserFactory;
import com.noktiz.ui.rest.core.auth.TokenData;
import com.noktiz.ui.rest.core.auth.TokenManager;
import com.noktiz.ui.rest.services.BaseWS;
import com.noktiz.ui.rest.services.response.AuthenticateInfo;
import com.noktiz.ui.rest.services.response.SimpleResponse;
import com.noktiz.ui.rest.services.response.UserInfo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.Date;

///**
// * Created by hassan on 02/11/2015.
// * a class for User management and registering
// */
//
//
//@Path("/userold")
//public class UserWSOld  extends BaseWS{
//    private static final int ASK_REGISTER_VALID_MINUTES = 10;
//
//    CipherUtils cipherUtils;
//    SingleAccessTokenManager singleAccessTokenManager;
//
//
//    @GET
//    @Path("/askRegister")
//    public String askForRegister() throws IOException {
//        String captcha = "captcha";
//        SingleAccessToken singleAccessToken = singleAccessTokenManager.generateAccessToken(null, SingleAccessToken.Type.Register, ASK_REGISTER_VALID_MINUTES, 1, captcha);
//        AskRegister askRegister = new AskRegister(singleAccessToken.getToken(),captcha);
//        return getJsonCreator().getJson(askRegister);
//    }
//
//    @GET
//    @Path("/register")
////    TODO:hassan authorize user for role assignment
//    public String register(@QueryParam("email") String email,
//                           @QueryParam("firstName")String firstName,
//                           @QueryParam("lastName")String lastName,
//                           @QueryParam("password")String password,
//                           @QueryParam("roles")String rolesString,
//                           @QueryParam("registerAccessToken")String registerAccessToken,
//                           @QueryParam("captchaValue")String captchaValue,
//                           @Context HttpServletResponse response) throws IOException {
////        Verifying Captcha only if not already in site
//        if(getUserInSite()==null)
//            try {
//                SingleAccessToken singleAccessToken = singleAccessTokenManager.useToken(registerAccessToken);
//                if(!Objects.equals(singleAccessToken.getData(),captchaValue)) {
//                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//                    return createSimpleResponse(Status.Failed, "Invalid Captcha Value");
//                }
//            } catch (InvalidTokenException e) {
//                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//                return createSimpleResponse(Status.Failed, "Invalid Register Access Token");
//            }
////        Check User Can Create Another User
//        if(getUserInSite()!=null){
//            if(!hasPermission(PermissionManagerImpl.CREATE_USER))
//                return createSimpleResponse(Status.Failed, "You Can Not Create Another User");
//        }
////        Creating User
//        List<Role> roles = null;
//        if(hasPermission(PermissionManagerImpl.ASSIGN_ROLE)) {
//            roles = extractRoles(rolesString);
//        }
//        User user;
//        try {
//            user = userManager.createUser(email, firstName, lastName, password, roles);
//        if(user.getId()!=null)
//            return createSimpleResponse(Status.Success,"User Created Successfully");
//        else
//            return createSimpleResponse(Status.Failed, "Error Creating User");
//        } catch (UserCreationException | InvalidParameterException e) {
//            return createSimpleResponse(Status.Failed, e.getMessage());
//        }
//    }
//// TOD:error on missing role
//    protected List<Role> extractRoles(String rolesString) {
//        List<Role> roles;
//        roles = new ArrayList<>();
//        if (rolesString != null)
//            for (String s : rolesString.split(",")) {
//                final Role e = roleDao.loadByRoleName(s);
//                if(e!=null)
//                    roles.add(e);
//            }
//        return roles;
//    }
//
//
//    @GET
//    @Path("/authenticate")
//    public String authenticateUser(@QueryParam("email")String email,
//                                   @QueryParam("service")String serviceName,
//                                   @QueryParam("password")String password) throws IOException {
//        try {
//            AuthenticateInfo authenticateInfo = userManager.authenticateUser(email, password, serviceName);
//            return getJsonCreator().getJson(authenticateInfo);
//        } catch (AuthenticationFailedException e) {
//            return createSimpleResponse(Status.Failed,e.getMessage());
//        }
//
//    }
//
//
//    @GET
//    @Path("/info")
//    public String userInfo(@QueryParam("user_id") String userId) throws IOException {
//        User user = userManager.get(Long.parseLong(userId));
//        String loggedInUserId = getUserInSite();
//        UserInfo userInfo = new UserInfo(user, !(ObjectUtils.equals(loggedInUserId,userId) || hasPermission(PermissionManagerImpl.ASSIGN_ROLE)));
//        return getJsonCreator().getJson(userInfo);
//
//    }
//
//
//    @GET
//    @Path("/manageRoles")
//    public String assignRole(@QueryParam("user_id")String userId,
//                      @QueryParam("roles") String rolesString,
//                      @QueryParam("action") String action) throws IOException {
//        if(!hasPermission(PermissionManagerImpl.ASSIGN_ROLE))
//            return createSimpleResponse(Status.Failed, "You Can Not Change Roles(Permission Denied)");
//        final User user = userManager.get(Long.valueOf(userId));
//        final List<Role> roles = extractRoles(rolesString);
//        if(user.getId().equals(1l))
//            return createSimpleResponse(Status.Failed, "You Can Not Change Admin Roles");
//        if("add".equals(action)) {
//            for (Role role : roles) {
//                user.getRoles().add(role);
//            }
//        }
//        else if("remove".equals(action)) {
//            for (Role role : roles) {
//                user.getRoles().remove(role);
//            }
//        }else{
//            return createSimpleResponse(Status.Failed, "Unknown Action");
//        }
//        return  createSimpleResponse(Status.Success,"Roles Updated");
//    }
//}


@Path("/user")
public class UserWS extends BaseWS {

    private static final long validTokenMilliseconds = 60 * 24 * 60 * 60 * 1000L;

    @Produces("application/json")
    @GET
    @Path("/info")
    public String userInfo(@QueryParam("userId") Long userId){
        User user = User.load(userId);
        if(user==null || !user.getActive()){
            return createSimpleResponse(SimpleResponse.Status.Failed,"user not found");
        }
        UserInfo ret = new UserInfo(user,getUserInSite().getUser());
        return getJsonCreator().getJson(ret);
    }

    @Produces("application/json")
    @GET
    @Path("/authenticate")
    public String getJson(@QueryParam("email") String email, @QueryParam("password") String password ){
        User user = User.loadUserWithEmailId(email,true);
        if(user==null || !user.getActive()){
            return createSimpleResponse(SimpleResponse.Status.Failed,"user and password are not match");
        }
        if(!PasswordManager.authenticate(user,password))
            return createSimpleResponse(SimpleResponse.Status.Failed,"user and password are not match");
        AuthenticateInfo ret = new AuthenticateInfo();
        ret.setUserId(user.getId().toString());
        ret.setEmail(user.getEmail());
        final Date expireDate = new Date(new Date().getTime() + validTokenMilliseconds);
        ret.setExpireDate(expireDate.getTime());
        ret.setAccessToken(new TokenManager().createTokenString(new TokenData(ret.getUserId(),null,new Date(),expireDate,null)));
        ret.setFirstName(user.getFirstName());
        ret.setLastName(user.getLastName());
        ret.setGender(user.getGender());
        return getJsonCreator().getJson(ret);
    }

    @Produces("application/json")
    @GET
    @Path("/register")
    public String register(@QueryParam("firstName") String firstName,
                           @QueryParam("lastName") String lastName,
                           @QueryParam("email") String email,
                           @QueryParam("password") String password,
                           @QueryParam("location") String location
    ) {
        User user = User.loadUserWithEmailId(email, true);
        if (user != null) {
            return createSimpleResponse(SimpleResponse.Status.Failed, "user exists");
        }
        user = new User();
        user.setLocation(location);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPersonalInfo(new PersonalInfo());
        user.setGender(User.Gender.Unknown);
        user.getPersonalInfo().setPassword(password);
        user.getPersonalInfo().setRePassword(password);

        final Result result = UserFactory.addUser(user, false, true, true);
        if(!result.result){
            return createSimpleResponse(SimpleResponse.Status.Failed, result.messages);
        }
        AuthenticateInfo ret = new AuthenticateInfo();
        ret.setUserId(user.getId().toString());
        ret.setEmail(user.getEmail());
        final Date expireDate = new Date(new Date().getTime() + validTokenMilliseconds);
        ret.setExpireDate(expireDate.getTime());
        ret.setAccessToken(new TokenManager().createTokenString(new TokenData(ret.getUserId(), null, new Date(), expireDate, null)));
        ret.setFirstName(user.getFirstName());
        ret.setLastName(user.getLastName());
        ret.setGender(user.getGender());
        return getJsonCreator().getJson(ret);
    }

}
















