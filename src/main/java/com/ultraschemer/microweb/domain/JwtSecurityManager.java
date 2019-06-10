package com.ultraschemer.microweb.domain;

import com.ultraschemer.microweb.error.StandardException;
import com.ultraschemer.microweb.utils.Security;

public class JwtSecurityManager {
    public static void initializeSKey() throws StandardException {
        // Get the symmetric key:
        String key = Configuration.read("jwt-s-key");
        if(key.equals("")) {
            // It is needed to insert a random symmetric security key:
            Configuration.write("jwt-s-key", Security.randomToken());
        }
    }
}