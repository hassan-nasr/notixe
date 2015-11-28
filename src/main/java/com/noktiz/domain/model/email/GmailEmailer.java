package com.noktiz.domain.model.email;

import java.io.*;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import org.apache.log4j.Logger;
public class GmailEmailer extends Thread
{
    
    private Properties props;
    private String to;
    private String subject;
    private String body;
    private String html;
    private String from;
    private String host;
    private int id;
    
    private static Random rand= new Random();
    private static GmailEmailer singelton=null;
    /**
     * it is synchronized to get any email a random number(id);
     * @return 
     */
    public static synchronized GmailEmailer getEmailer(){
        GmailEmailer emailer = new GmailEmailer();
        emailer.id=rand.nextInt();
        return emailer;
    }
    private GmailEmailer()
    {
    }

    @Deprecated
    public void SetEmailSetting(String to,String subject, String body){
        this.to=to;
        this.subject=subject;
        this.body=body;
        this.html=null;
    }

    public void SetEmailSetting(String to, String subject, String body, String HTML){
        this.to=to;
        this.subject=subject;
        this.body=body;
        this.html=HTML;
    }

    /**
     * send message to destination (in the same thread)
     * @param to
     * @return
     * @deprecated set settings and then run call send;
     */
    @SuppressWarnings("static-access")
    @Deprecated
    public void SendAMessage(String to,String subject, String body)
    {
        this.body=body;
        this.subject=subject;
        this.to=to;
        run();
    }

    
    @Override
    public void run() {
        // create some properties and get the default Session
        Logger.getLogger(this.getClass()).trace(id+" loading properties file");
        props = new Properties();
        try {
            props.load(GmailEmailer.class.getResourceAsStream("email.properties"));
        } catch (IOException ex) {
            Logger.getLogger(GmailEmailer.class.getName()).error(id+" can not load properties file",ex);
        }
        Logger.getLogger(this.getClass()).trace(id+" Authenticating "+new Date().getTime());
        Authenticator auth = new SMTPAuthenticator();        
        Session session = Session.getInstance(props, auth);        
        session.setDebug(Boolean.parseBoolean(props.getProperty("mail.debug")));
	
        Logger.getLogger(this.getClass()).trace("3 "+new Date().getTime());
        try 
        {
            
            // create a message
            from=props.getProperty("mail.from");
            MimeMessage msg = new MimeMessage(session);

            Multipart mulpart = new MimeMultipart("alternative");
            MimeBodyPart textPart = new MimeBodyPart();
            if(this.body== null){
                this.body="";
            }
            textPart.setText(this.body);
            mulpart.addBodyPart(textPart);
            if(this.html!=null) {
                MimeBodyPart htmlPart = new MimeBodyPart();
                htmlPart.setContent(this.html, "text/html; charset=utf-8");
                mulpart.addBodyPart(htmlPart);
            }
            msg.setContent(mulpart);

            msg.setFrom(new InternetAddress(from));
            InternetAddress address = new InternetAddress(this.to);
            msg.setRecipient(Message.RecipientType.TO, address);
            msg.setSubject(this.subject, "UTF-8");
            msg.setSentDate(new Date());
            // If the desired charset is known, you can use
            // setText(text, charset)

            Logger.getLogger(this.getClass()).trace(id+" sending.... "+new Date().getTime());
            Transport.send(msg);
            Logger.getLogger(this.getClass()).trace(id+" email sent"+new Date().getTime());
            
            return;
            } 
        catch (MessagingException mex) 
        {
            Logger.getLogger(GmailEmailer.class).error(id + " can not send email"+ new Date().getTime(),mex);
            Exception ex = mex;
            do 
            {
                if (ex instanceof SendFailedException) 
                {
                    SendFailedException sfex = (SendFailedException)ex;
                    Address[] invalid = sfex.getInvalidAddresses();
                    if (invalid != null) 
                    {
                        Logger.getLogger(GmailEmailer.class).error(id+"\tInvalid Addresses");
                        if (invalid != null) 
                        {
                            for (int i = 0; i < invalid.length; i++) 
                                Logger.getLogger(GmailEmailer.class).error("\t" + invalid[i]);
                        }
                    }
                    Address[] validUnsent = sfex.getValidUnsentAddresses();
                    if (validUnsent != null) 
                    {
                        Logger.getLogger(GmailEmailer.class).error(id+"\tValidUnsent Addresses");
                        if (validUnsent != null) 
                        {
                            for (int i = 0; i < validUnsent.length; i++) {
                                Logger.getLogger(GmailEmailer.class).error(id+ "\t"+validUnsent[i]);
                            }
                        }
                    }
                    Address[] validSent = sfex.getValidSentAddresses();
                    if (validSent != null) 
                    {
                        Logger.getLogger(GmailEmailer.class).error(id+"\tValidSent Addresses");
                        if (validSent != null) 
                        {
                            for (int i = 0; i < validSent.length; i++) {
                                Logger.getLogger(GmailEmailer.class).error(id+"\t"+validSent[i]);
                            }
                        }
                    }
                };
                if (ex instanceof MessagingException) {
                    ex = ((MessagingException)ex).getNextException();
                }
                else {
                    ex = null;
                }
            } 
            while (ex != null);
        }
        return;
    }




    private class SMTPAuthenticator extends javax.mail.Authenticator 
    {
        public PasswordAuthentication getPasswordAuthentication() 
        {
            String uName = props.getProperty("mail.username");
            String passwd = props.getProperty("mail.password");
                    ;
            return new PasswordAuthentication(uName, passwd);
        }
    }
}