package com.noktiz.ui.rest.core.auth;

//import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noktiz.domain.Utils.CipherUtils;
import org.apache.log4j.Logger;

import java.io.IOException;

//import org.codehaus.jackson.map.ObjectMapper;

/**
 * Created by hassan on 03/11/2015.
 */
public class TokenManager {

    //    @Autowired
    CipherUtils cipherUtils = new CipherUtils();

    public TokenManager() {
    }

    /**
     * creates a TokenData from an encrypted String
     *
     * @param s
     * @return
     * @throws IOException
     */
    public TokenData extractTokenFromString(String s) throws IOException {
        String tokenInfo = cipherUtils.decrypt(s);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(tokenInfo, TokenData.class);


    }

    /**
     * creates an encrypted String from a token data
     *
     * @param tokenData
     * @return
     */
    public String createTokenString(TokenData tokenData) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            final String ret = cipherUtils.encrypt(mapper.writeValueAsString(tokenData));
            return ret;
        } catch (IOException e) {
            Logger.getLogger(this.getClass()).error("error", e);
            return null;
        }
    }
}
