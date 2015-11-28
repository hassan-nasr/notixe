/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

package com.noktiz.domain.social.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import static com.google.api.client.util.ByteStreams.read;
import com.google.gdata.client.Query;
import com.google.gdata.client.Service;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.Link;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.data.extensions.Email;
import com.google.gdata.util.ServiceException;
import com.google.gson.Gson;
import com.noktiz.domain.Utils.EmailAddressUtils;
import com.noktiz.domain.entity.social.FacebookSocialConnection;
import com.noktiz.domain.entity.social.SocialConnection;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.entity.cred.GoogleInfo;
import com.noktiz.domain.entity.social.GoogleSocialConnection;
import com.noktiz.domain.model.Result.Message.Level;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.social.AccessDeniedException;
import com.noktiz.domain.social.FriendProvider;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.noktiz.domain.system.SystemConfigManager;
import org.apache.log4j.Logger;

/**
 *
 * @author sina
 */
public class GoogleFriendProvider implements FriendProvider {

    protected static final String CLIENT_ID = SystemConfigManager.getCurrentConfig().getProperty("GoogleClientID");
    protected static final String CLIENT_SECRET = SystemConfigManager.getCurrentConfig().getProperty("GoogleClientSecret");
    protected static final HttpTransport TRANSPORT = new NetHttpTransport();
    protected static final JacksonFactory JSON_FACTORY = new JacksonFactory();
    protected static final Gson GSON = new Gson();
    private int read;
    
    @Override
    public ArrayList<SocialConnection> getConnections(UserFacade u) throws AccessDeniedException, IOException {
        u.refresh();
        Set<SocialConnection> socialConnections = u.getSocialConnections();
        Set<SocialConnection> googleSocialConnections = new HashSet<SocialConnection>();
        for(SocialConnection sc : socialConnections){
            if(sc.getContext().equals(SocialConnection.Context.Email))
                googleSocialConnections.add(sc);
        }
        try {
            
            //****************************************************************************************
            GoogleInfo gc = u.getCredential().getGoogleInfo();
            GoogleCredential credential = new GoogleCredential.Builder()
                    .setJsonFactory(JSON_FACTORY)
                    .setTransport(TRANSPORT)
                    .setClientSecrets(CLIENT_ID, CLIENT_SECRET).build()
                    .setFromTokenResponse(JSON_FACTORY.fromString(
                            gc.getGoogle_access_token(), GoogleTokenResponse.class));
            credential.refreshToken();
            ContactsService cService = new ContactsService("Noktiz-tester-1");
            cService.setOAuth2Credentials(credential);
            URL feedUrl = new URL("https://www.google.com/m8/feeds/contacts/default/full");
            Query query = new Query(feedUrl);;
            query.setMaxResults(Integer.MAX_VALUE);
            ContactFeed cf = cService.query(query, ContactFeed.class);
            
            for(ContactEntry entry:cf.getEntries()){
                if(entry.hasEmailAddresses()){
                    for(Email email: entry.getEmailAddresses()){
                        if(email.getAddress().equals(u.getEmail()))
                            continue;
                        SocialConnection entrySocialConnection;
                        if(entry.hasName()){
                            entrySocialConnection =
                                    new GoogleSocialConnection(u.getUser(),SocialConnection.Context.Email,email.getAddress(),false,entry.getName().getFullName().getValue());
                        }
                        else{
                            entrySocialConnection =
                                    new GoogleSocialConnection(u.getUser(),SocialConnection.Context.Email,email.getAddress(),false,email.getAddress());
                        }
                        
//                        if(entry.getContactPhotoLink()!=null && entry.getEtag()!=null)
//                        {
////                            entrySocialConnection.setPictureUrl(entry.getContactPhotoLink().getHref());
//                            Link photoLink = entry.getContactPhotoLink();
//                            if(photoLink!=null){
//                                Service.GDataRequest clq = cService.createLinkQueryRequest(photoLink);
//                                clq.execute();
//                                InputStream in = clq.getResponseStream();
//                                ByteArrayOutputStream out = new ByteArrayOutputStream();
//                                RandomAccessFile file = new RandomAccessFile("~/contancts/"+email+".jpg", "rw");
//                                byte[] buffer = new byte[4096];
//                                while (true) {
//                                    if ((read = in.read(buffer)) != -1) {
//                                        out.write(buffer, 0, read);
//                                    } else {
//                                        break;
//                                    }
//                                }
//                            }
//                        }
                        if(email!=null && email.getAddress() !=null) {
                            User registeredUser = User.loadUserWithEmailId(email.getAddress(), true);
                            if (registeredUser != null) {
                                entrySocialConnection.setRegisteredUser(registeredUser);
                                //if (new UserFacade(registeredUser).doIOwnerTheFriendshipOf(u)) {
                                if(u.doIOwnerTheFriendshipOf(new UserFacade(registeredUser))){
                                    entrySocialConnection.setInvited(true);
                                }
                            }
                        }
                        googleSocialConnections.add(entrySocialConnection);
                    }
                }
            }
        }
        catch (ServiceException ex) {
            Logger.getLogger(this.getClass()).error(ex);
        }catch(Exception e){
            Logger.getLogger(this.getClass()).error(e);
            e.printStackTrace();
            return new ArrayList<>();
        }
        return new ArrayList<SocialConnection>(googleSocialConnections);
    }

    @Override
    public SocialConnection getReverse(SocialConnection socialConnection) {
         //Sina implement this
        if(socialConnection.getRegisteredUser()==null || socialConnection.getOwner().getCredential().getGoogleInfo() == null || socialConnection.getOwner().getCredential().getGoogleInfo().getGoogle_id() == null)
            return null;
        GoogleSocialConnection ret = new GoogleSocialConnection(socialConnection.getRegisteredUser(), socialConnection.getContext(), socialConnection.getOwner().getCredential().getGoogleInfo().getPrimary_email(), false, socialConnection.getOwner().getName());
        ret.setRegisteredUser(socialConnection.getOwner());
        return ret;
    }



}
