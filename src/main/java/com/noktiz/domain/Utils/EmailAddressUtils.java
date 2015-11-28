package com.noktiz.domain.Utils;

import java.util.Scanner;

/**
 * Created by Hossein on 1/11/2015.
 */
public class EmailAddressUtils {
    public static String normalizeEmail(String email) {
        email = email.trim();
        String[] emailPart = email.split("@");
        if (emailPart.length != 2) {
            return email;
        }
        emailPart[1] = emailPart[1].toLowerCase();
        String domain = emailPart[1];
        boolean normalMail=false;
        if (domain.startsWith("gmail.") || domain.startsWith("googlemail.")) {
            normalMail = true;
            emailPart[1]="gmail.com";
            domain=emailPart[1];
        }
        if(domain.startsWith("google.")){
            normalMail=true;
        }
        if(normalMail){
            String[] localParts = emailPart[0].split("\\+");
            emailPart[0]=localParts[0].replaceAll("\\.","");
        }
        return emailPart[0]+'@'+emailPart[1];
    }

    public static void main(String[] args) {
        Scanner s = new Scanner (System.in);
        while(true){
            System.out.println(normalizeEmail(s.nextLine()));
        }
    }
}
