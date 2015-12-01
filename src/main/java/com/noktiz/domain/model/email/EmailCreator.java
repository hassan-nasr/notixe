/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.domain.model.email;

import com.noktiz.domain.entity.User;
import com.noktiz.domain.entity.notifications.*;
import com.noktiz.domain.entity.rate.NotificationRateInvite;
import com.noktiz.domain.entity.rate.SimplePeriod;
import com.noktiz.domain.entity.token.SingleAccessToken;
import com.noktiz.domain.i18n.Local;
import com.noktiz.domain.system.SystemConfig;
import com.noktiz.domain.system.SystemConfigManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author Hossein
 */
public class EmailCreator {

    private static EmailCreator emailCreator = new EmailCreator();
    private static Properties props;
    private static Map<Local,ResourceBundle> emailResources = new HashMap<>();

    private static void loadProps(){
        if(props==null) {
            props = new Properties();
            try {
                props.load(EmailCreator.class.getResourceAsStream("htmls.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private static ResourceBundle getBundle(Local local){
        ResourceBundle ret = emailResources.get(local);
        if(ret==null || true){
            ret=ResourceBundle.getBundle(EmailCreator.class.getPackage().getName()+".EmailResources",local.getLocale());
            emailResources.put(local,ret);
        }
        return ret;
    }

    private static String loadAndReplaceSimple(InputStream file, ResourceBundle bundle, String content, String title){
        loadProps();
        Scanner fis = new Scanner(file,"UTF-8");
        StringBuilder retbuilder = new StringBuilder();
        while(fis.hasNextLine()){
            retbuilder.append(fis.nextLine());
        }
        String ret = retbuilder.toString();
        ret = ret.replaceAll("LOGOLINK", props.getProperty("htmls.base").toString() + "logo.png");
        ret = ret.replaceAll("TITLE",title);
        ret = ret.replaceAll("UNSUBSCRIBELINK",props.getProperty("htmls.base").toString()+"settings#notification_form");
        ret = ret.replaceAll("CONTENT",content);
        String[] statements = new String[]{"ThisEmailIsFromNotixe", "MailUnsubscribeLinkText", "MailUnsubscribeNote2","MailUnsubscribeNote1"};
        ret = StringUtils.replaceEach(ret,statements,Arrays.stream(statements).map(s->bundle.getString(s)).collect(Collectors.toList()).toArray(new String[statements.length]));

        return ret;
    }

    private static String loadAndReplaceButton(String content, String title, String buttonContent, String buttonLink, ResourceBundle bundle){
        loadProps();
        Scanner fis = new Scanner(EmailCreator.class.getResourceAsStream(props.getProperty("htmls.buttonTemplate")),"UTF-8");
        StringBuilder retbuilder = new StringBuilder();
        while(fis.hasNextLine()){
            retbuilder.append(fis.nextLine());
        }
        String ret = retbuilder.toString();
        ret = ret.replaceAll("LOGOLINK", props.getProperty("htmls.base").toString() + "logo.png");
        ret = ret.replaceAll("TITLE",title);
        ret = ret.replaceAll("UNSUBSCRIBELINK",props.getProperty("htmls.base").toString()+"settings#notification_form");
        ret = ret.replaceAll("BUTTONCONTENT",buttonContent);
        ret = ret.replaceAll("BUTTONLINK",buttonLink);
        ret = ret.replaceAll("CONTENT",content);
        String[] statements = new String[]{"ThisEmailIsFromNotixe", "MailUnsubscribeLinkText", "MailUnsubscribeNote2","MailUnsubscribeNote1"};
        ret = StringUtils.replaceEach(ret,statements,Arrays.stream(statements).map(s->bundle.getString(s)).collect(Collectors.toList()).toArray(new String[statements.length]));
        return ret;
    }

    @Deprecated
    private static String loadAndReplace(InputStream file, Map<String,String> keys) throws IOException {
//        TODO: cache templates;
        Scanner fis = new Scanner(file,"UTF-8");
        StringBuilder retbuilder = new StringBuilder();
        while(fis.hasNextLine()){
            retbuilder.append(fis.nextLine());
        }
        String ret = retbuilder.toString();
        for(String key:keys.keySet()){
            ret = ret.replaceAll(key,keys.get(key));
        }
        return ret;
    }

    public static IEmailProvider.IEmail createWeeklyEmail(List<BaseNotification> notifications) {
        ResourceBundle bundle = getBundle(notifications.get(0).getOwner().getPersonalInfo().getLocal());
        ArrayList<NotificationNewMessage> newMessage = new ArrayList<>();
        ArrayList<NotificationAddFriend> addFriend = new ArrayList<>();
        ArrayList<NotificationRate> rate = new ArrayList<>();
        ArrayList<NotificationRateInvite> rateInvite   = new ArrayList<>();
        ArrayList<NotificationEndorse> endorse = new ArrayList<>();
        ArrayList<NotificationThanks> thanks = new ArrayList<>();

        for (BaseNotification baseNotification : notifications) {
            if (baseNotification instanceof NotificationNewMessage) {
                newMessage.add((NotificationNewMessage) baseNotification);
            }
            if (baseNotification instanceof NotificationAddFriend) {
                addFriend.add((NotificationAddFriend) baseNotification);
            }
            if (baseNotification instanceof NotificationEndorse) {
                endorse.add(((NotificationEndorse) baseNotification));
            }
            if (baseNotification instanceof NotificationRate) {
                rate.add((NotificationRate) baseNotification);
            }
            if (baseNotification instanceof NotificationRateInvite) {
                rateInvite.add((NotificationRateInvite) baseNotification);
            }
            if (baseNotification instanceof NotificationThanks) {
                thanks.add((NotificationThanks) baseNotification);
            }
        }
        StringBuilder content = new StringBuilder();
        StringBuilder html = new StringBuilder();
        content.append(bundle.getString("here.is.your.weekly.update.in.0"));
        html.append(bundle.getString("here.is.your.weekly.update.in.0"));
        content.append("\n");
        html.append("<br/>");
        if (!newMessage.isEmpty()) {
            int count = 0;
            boolean found=false;
            for (NotificationNewMessage nnm : newMessage) {
                count++;
                if(nnm.getMessage().isReciverRead()){
                    continue;
                }
                if(!found){
                    content.append(MessageFormat.format(bundle.getString("you.have.0.new.messages.n"), newMessage.size()));
                    html.append(MessageFormat.format(bundle.getString("you.have.0.new.messages.n"), newMessage.size()));
                    found=true;
                }
                if (nnm.isAnnonymous()) {
                    content.append("your friend wrote: ");
                    html.append("your friend wrote: ");
                } else {
                    content.append(MessageFormat.format(bundle.getString("0.wrote"), nnm.getMessage().getSender().getName()));
                    html.append(MessageFormat.format(bundle.getString("0.wrote"), nnm.getMessage().getSender().getName()));
                }
                final String messageContent = nnm.getMessage().getContent();
                content.append(messageContent.substring(0, Math.min(50, messageContent.length())).replaceAll("\\s+", " "));
                html.append(messageContent.substring(0, Math.min(50, messageContent.length())).replaceAll("\\s+", " "));
                if (messageContent.length() > 50) {
                    content.append("...");
                    html.append("...");
                }
                content.append("\n");
                html.append("<br/>");
                if (count > 9) {
                    break;
                }
            }
            if (newMessage.size() > 10) {
                content.append(MessageFormat.format(bundle.getString("and.0.more"), newMessage.size() - 10));
                html.append(MessageFormat.format(bundle.getString("and.0.more"), newMessage.size() - 10));
            }
            content.append("\n");
            html.append("<br/>");
        }
        if (!addFriend.isEmpty()) {
            content.append(MessageFormat.format(bundle.getString("0.person.added.you.as.friend.n"), addFriend.size()));
            html.append(MessageFormat.format(bundle.getString("0.person.added.you.as.friend.n"), addFriend.size()));
            content.append("    ");
            html.append("    ");
            int i=0;
            for (NotificationAddFriend af : addFriend) {
                i++;
                content.append(af.getFriend().getName());
                html.append(af.getFriend().getName());
                if(i != addFriend.size()) {
                    content.append(" ,");
                    html.append(" ,");
                }
            }
            content.append("\n");
            html.append("<br/>");
        }
        if (!rate.isEmpty()) {
            content.append(MessageFormat.format(bundle.getString("you.ve.got.0.answers.to.your.questions"), rate.size()));
            html.append(MessageFormat.format(bundle.getString("you.ve.got.0.answers.to.your.questions"), rate.size()));
            content.append("\n");
            html.append("<br/>");
        }
        if (!rateInvite.isEmpty()) {
            Set<User> rateInviteSenders = new HashSet<>();
            for (NotificationRateInvite notificationRateInvite : rateInvite) {
                rateInviteSenders.add(notificationRateInvite.getOwner());
            }
            content.append(MessageFormat.format(bundle.getString("you.ve.got.0.invitations.to.help.your.friends.by"), rateInvite.size()));
            html.append(MessageFormat.format(bundle.getString("you.ve.got.0.invitations.to.help.your.friends.by"), rateInvite.size()));
            content.append("\n");
            html.append("<br/>");
            content.append("   ");
            html.append("   ");
            int i=0;
            for (User rateInviteSender :  rateInviteSenders) {
                i++;
                content.append(rateInviteSender.getName());
                html.append(rateInviteSender.getName());
                if(i!=rateInviteSenders.size()) {
                    content.append(", ");
                    html.append(", ");
                }
            }
            content.append("\n");
            html.append("<br/>");
        }
        if (!thanks.isEmpty()) {
            content.append(MessageFormat.format(bundle.getString("you.ve.got.0.thanks.from.your.friends"), thanks.size()));
            html.append(MessageFormat.format(bundle.getString("you.ve.got.0.thanks.from.your.friends"), thanks.size()));
            content.append("\n");
            html.append("<br/>");

        }
        if (!endorse.isEmpty()) {
            Set<String> endorseStrings = new HashSet<>();
            for (NotificationEndorse notificationEndorse : endorse) {
                endorseStrings.add(notificationEndorse.getEndorse().getContext());
            }
            content.append(bundle.getString("you.ve.got.endorsed.for"));
            html.append(bundle.getString("you.ve.got.endorsed.for"));
            int i=0;
            for (String endorseString : endorseStrings) {
               i++;
                content.append(endorseString);
                html.append(endorseString);
                if(i!=endorseStrings.size()) {
                    html.append(", ");
                }
            }
            content.append(bundle.getString("which.you.can.add.to.you.profile"));
            html.append(bundle.getString("which.you.can.add.to.you.profile"));
            content.append("\n");
            html.append("<br/>");

        }
        String siteUrl = "www."+SystemConfigManager.getCurrentConfig().getAppName().toLowerCase()+".com";
        content.append(MessageFormat.format(bundle.getString("please.continue.to.0.for.more.details.and.possible.actions.you.want.to.do"), siteUrl));
        html.append(MessageFormat.format(bundle.getString("please.continue.to.0.for.more.details.and.possible.actions.you.want.to.do"), siteUrl));
        IEmailProvider.IEmail iEmail = new IEmailProvider.IEmail();
        iEmail.content = content.toString();
        iEmail.title = bundle.getString("you.weekly.updates.from.APPNAME");
        iEmail.html = loadAndReplaceSimple(EmailCreator.class.getResourceAsStream("templates/simpleTemplate.html"), bundle, iEmail.content, iEmail.title);
        return iEmail;
    }

    public static IEmailProvider.IEmail createAddFriendEmail(NotificationAddFriend naf) {
        ResourceBundle bundle = getBundle(naf.getOwner().getPersonalInfo().getLocal());
        StringBuilder content = new StringBuilder();
        content.append(MessageFormat.format(bundle.getString("addFriendMessage"), naf.getFriend().getName()));
        IEmailProvider.IEmail iEmail = new IEmailProvider.IEmail();
        iEmail.content = content.toString();
        iEmail.title = bundle.getString("addFriendTitle");
        iEmail.html = loadAndReplaceSimple(EmailCreator.class.getResourceAsStream("templates/simpleTemplate.html"),bundle, iEmail.content, iEmail.title);
        return iEmail;
    }

    public static IEmailProvider.IEmail createNewMessageEmail(NotificationNewMessage nnm) {
        ResourceBundle bundle = getBundle(nnm.getOwner().getPersonalInfo().getLocal());
        StringBuilder content = new StringBuilder();
        StringBuilder html = new StringBuilder();
        content.append(bundle.getString("you.have.a.new.message") ).append("\n");
        if (nnm.isAnnonymous()) {
            content.append(bundle.getString("your.friend.wrote"));
            html.append(" <strong> "+bundle.getString("you.have.a.new.message") +" </strong> <br/>");
        } else {
            content.append(nnm.getMessage().getSender().getName() + " wrote: ");
            html.append("<strong> " + bundle.getString("0.wrote")+" </strong> <br/>");
        }
        final String messageContent = nnm.getMessage().getContent();
        content.append(messageContent.substring(0, Math.min(50, messageContent.length())).replaceAll("\\s+", " "));
        html.append(messageContent.substring(0, Math.min(50, messageContent.length())).replaceAll("\\s+", " "));
        if (messageContent.length() > 50) {
            content.append("...");
            html.append("...");
        }
        content.append("\n");
        html.append("<br/>");
        IEmailProvider.IEmail iEmail = new IEmailProvider.IEmail();
        iEmail.content=content.toString();
        iEmail.title= bundle.getString("newMessageTitle");

        iEmail.html = loadAndReplaceSimple(EmailCreator.class.getResourceAsStream("templates/simpleTemplate.html"),bundle, html.toString(), iEmail.title);
        return iEmail;
    }

    public static IEmailProvider.IEmail createActivationEmail(User user) {
        ResourceBundle bundle = getBundle(user.getPersonalInfo().getLocal());
        loadProps();
        StringBuilder content = new StringBuilder();
        PageParameters p = new PageParameters();
        p.add("code", user.getPersonalInfo().getActivate());
        final String offset = "/activate?code="+user.getPersonalInfo().getActivate();
        URL url= null;
        try {
            url = new URL(SystemConfigManager.getCurrentConfig().getProperty("hosturl"));
            url= new URL(url,offset);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        content.append((bundle.getString("thanks.and.activation.message")));
        content.append("\n "+ url.toString());
        IEmailProvider.IEmail iEmail = new IEmailProvider.IEmail();
        iEmail.content=content.toString();
        iEmail.title= bundle.getString("active account message title");
        String html = bundle.getString("thanks.and.activation.message")+"<br>";
        iEmail.html = loadAndReplaceButton(html,iEmail.title,"Activate",url.toString(),bundle);
        return iEmail;
    }

    public static IEmailProvider.IEmail createForgetPasswordEmail(User user,SingleAccessToken singleAccessToken) {
        ResourceBundle bundle = getBundle(user.getPersonalInfo().getLocal());
        StringBuilder content = new StringBuilder();
        StringBuilder html = new StringBuilder();
        final String offset = "ssi?code="+singleAccessToken.getToken();
        URL url= null;
        try {
            url = new URL(SystemConfigManager.getCurrentConfig().getProperty("hosturl"));
            url= new URL(url, offset);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        content.append(bundle.getString("resetPasswordMessage")).append("\n" + url.toString());
        html.append(bundle.getString("resetPasswordMessage")).append(" <br/>").append(url.toString());
        IEmailProvider.IEmail iEmail = new IEmailProvider.IEmail();
        iEmail.content=content.toString();
        iEmail.title= bundle.getString("resetMessageTitle");
        //iEmail.html = loadAndReplaceSimple(EmailCreator.class.getResourceAsStream("templates/simpleTemplate.html"), html.toString(), iEmail.title);
        iEmail.html = loadAndReplaceButton(html.toString(), iEmail.title, bundle.getString("Restore.Password.ButtonTitle"), url.toString(),bundle);
        return iEmail;
    }

    public static IEmailProvider.IEmail createNewRateEmail(NotificationRate nnm) {
        ResourceBundle bundle = getBundle(nnm.getOwner().getPersonalInfo().getLocal());
        StringBuilder content = new StringBuilder();
        StringBuilder html = new StringBuilder();
//        content.append("");
        String senderName=null;
        if(nnm.isAnonymous())
            senderName=bundle.getString("yourfriend");
        else
            senderName=nnm.getRate().getSender().getName();

        String rateMessage= MessageFormat.format(bundle.getString("0.responded.to.your.question.1"),senderName, nnm.getRate().getContext().getTitle());
        if(nnm.getRate().getRate()!=null)
            rateMessage += MessageFormat.format(bundle.getString("with.0.stars"), nnm.getRate().getRate());
        content.append(rateMessage);
        html.append(rateMessage);
        String commentMessage = ((nnm.getRate().getComment()!=null && !nnm.getRate().getComment().isEmpty())? MessageFormat.format(bundle.getString("and.wrote.n.0"), nnm.getRate().getComment()) :".");
        String commentMessageHtml = ((nnm.getRate().getComment()!=null && !nnm.getRate().getComment().isEmpty())? MessageFormat.format(bundle.getString("and.wrote.br.0_html"), nnm.getRate().getComment()) :".");
        content.append(commentMessage);
        html.append(commentMessageHtml);

        content.append("\n");
        html.append("<br/>");
        IEmailProvider.IEmail iEmail = new IEmailProvider.IEmail();
        iEmail.content=content.toString();
        iEmail.title= bundle.getString("newResponseMassage");
        iEmail.html = loadAndReplaceSimple(EmailCreator.class.getResourceAsStream("templates/simpleTemplate.html"),bundle, html.toString(), iEmail.title);
        return iEmail;
    }

    public static IEmailProvider.IEmail createNewEndorseEmail(NotificationEndorse ne) {
        ResourceBundle bundle = getBundle(ne.getOwner().getPersonalInfo().getLocal());
        StringBuilder content = new StringBuilder();
        StringBuilder html = new StringBuilder();
        content.append(MessageFormat.format(bundle.getString("endorseMessage"), ne.getEndorse().getSender().getName(), ne.getEndorse().getContext()));
        html.append(MessageFormat.format(bundle.getString("endorseMessage"), ne.getEndorse().getSender().getName(), ne.getEndorse().getContext()));

        content.append("\n");
        html.append("<br/>");
        IEmailProvider.IEmail iEmail = new IEmailProvider.IEmail();
        iEmail.content=content.toString();
        iEmail.title= bundle.getString("EndorseTitle");
        iEmail.html = loadAndReplaceSimple(EmailCreator.class.getResourceAsStream("templates/simpleTemplate.html"),bundle, html.toString(), iEmail.title);
        return iEmail;
    }

    public static IEmailProvider.IEmail createRateInviteEmail(NotificationRateInvite notificationRateInvite) {
        ResourceBundle bundle = getBundle(notificationRateInvite.getOwner().getPersonalInfo().getLocal());
        if(!notificationRateInvite.getOwner().getActive()){
            return createRateInviteWithRegisterEmail(notificationRateInvite);
        }
        StringBuilder content = new StringBuilder();
        StringBuilder html = new StringBuilder();
        SimplePeriod timeBetweenRatings = notificationRateInvite.getRateContext().getTimeBetweenRatings();
        content.append(MessageFormat.format(bundle.getString("0.invited.you.to.answer.a.question.1"), notificationRateInvite.getRateContext().getUser().getName(), timeBetweenRatings.equals(SimplePeriod.SingleTime) ? "" : " " + timeBetweenRatings.getName().toLowerCase() + "."));
        html.append(MessageFormat.format(bundle.getString("0.invited.you.to.answer.a.question.1"), notificationRateInvite.getRateContext().getUser().getName(), timeBetweenRatings.equals(SimplePeriod.SingleTime) ? "" : " " + timeBetweenRatings.getName().toLowerCase() + "."));
        content.append("\n");
        html.append("<br/>");
        content.append(notificationRateInvite.getRateContext().getTitle());
        html.append(notificationRateInvite.getRateContext().getTitle());
        IEmailProvider.IEmail iEmail = new IEmailProvider.IEmail();
        iEmail.content=content.toString();
        iEmail.title= MessageFormat.format(bundle.getString("QuestionRequestTitle"), notificationRateInvite.getRateContext().getUser().getName());
        iEmail.html = loadAndReplaceSimple(EmailCreator.class.getResourceAsStream("templates/simpleTemplate.html"),bundle, html.toString(), iEmail.title);
        return iEmail;
    }

    public static EmailCreator getEmailCreator() {
        return emailCreator;
    }

    public static IEmailProvider.IEmail createInviteAsTrustedFriend(User user) {
        ResourceBundle bundle = getBundle(user.getPersonalInfo().getLocal());
        StringBuilder content = new StringBuilder();
        StringBuilder html = new StringBuilder();
        StringBuilder title = new StringBuilder();
        html.append("<b>");
        content.append(user.getName());
        html.append(user.getName());
        title.append(user.getName());
        html.append("<b/>");
        content.append(bundle.getString("inviteMessage"));
        html.append(bundle.getString("inviteMessage"));
        title .append(bundle.getString("inviteFriendTitle"));

        URL url= null;
        try {
            url = new URL(SystemConfigManager.getCurrentConfig().getProperty("hosturl"));
            url = new URL(url,"/welcome");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        content.append(SystemConfigManager.getCurrentConfig().getProperty(SystemConfig.App_Name)+"("+url+")");
        html.append("<a href=\""+url+"\"> "+SystemConfigManager.getCurrentConfig().getProperty(SystemConfig.App_Name)+" </a>");
//        SystemConfigManager.getCurrentConfig().getProperty(SystemConfig.App_Name);
        IEmailProvider.IEmail iEmail = new IEmailProvider.IEmail();
        iEmail.content=content.toString();
        iEmail.title= title.toString();
        iEmail.html = loadAndReplaceSimple(EmailCreator.class.getResourceAsStream("templates/simpleTemplate.html"),bundle, html.toString(), iEmail.title);
        return iEmail;
    }


    public static IEmailProvider.IEmail createRateInviteWithRegisterEmail(NotificationRateInvite notificationRateInvite) {
        ResourceBundle bundle = getBundle(notificationRateInvite.getOwner().getPersonalInfo().getLocal());
        StringBuilder content = new StringBuilder();
        StringBuilder html = new StringBuilder();

        URL url= null;
        URL RegisteratinUrl=null;
        try {
            url = new URL(SystemConfigManager.getCurrentConfig().getProperty("hosturl"));
            RegisteratinUrl= new URL(url,"activate?code="+notificationRateInvite.getOwner().getPersonalInfo().getActivate());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        URL privacyUrl =null;
        try {
            privacyUrl= new URL(url,"/legal/privacy");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        URL termsUrl =null;
        try {
            termsUrl= new URL(url,"/legal/terms");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        SimplePeriod timeBetweenRatings = notificationRateInvite.getRateContext().getTimeBetweenRatings();
        content.append(MessageFormat.format(bundle.getString("0.invited.you.to.answer.a.question.1"), notificationRateInvite.getRateContext().getUser().getName(), timeBetweenRatings.equals(SimplePeriod.SingleTime) ? "" : " " + timeBetweenRatings.getName().toLowerCase() + "."));
        html.append(MessageFormat.format(bundle.getString("0.invited.you.to.answer.a.question.1"), notificationRateInvite.getRateContext().getUser().getName(), timeBetweenRatings.equals(SimplePeriod.SingleTime) ? "" : " " + timeBetweenRatings.getName().toLowerCase() + "."));
        content.append("\n");
        html.append("<br/>");
        content.append(notificationRateInvite.getRateContext().getTitle());
        html.append(notificationRateInvite.getRateContext().getTitle());
        content.append("\n");
        html.append("<br/>");
        content.append(bundle.getString("createAccountAndResponse"));
        html.append(bundle.getString("createAccountAndResponse"));
        content.append(MessageFormat.format(bundle.getString("note.that.you.should.agree.to.terms.of.use.0.and.privacy.policy.1"), termsUrl, privacyUrl));
        html.append(MessageFormat.format(bundle.getString("note.that.you.should.agree.to.terms.of.use.0.and.privacy.policy.1"), "<a href=\"" + termsUrl +"\">Terms of Use</a>", "<a href=\"" + privacyUrl + "\"> Privacy Policy </a>"));
        content.append(RegisteratinUrl);
        html.append("<a href=\""+RegisteratinUrl+"\"> "+RegisteratinUrl+" </a>");
        IEmailProvider.IEmail iEmail = new IEmailProvider.IEmail();
        iEmail.content=content.toString();
        iEmail.title= MessageFormat.format(bundle.getString("QuestionRequestTitle"), notificationRateInvite.getRateContext().getUser().getName());
        iEmail.html = loadAndReplaceSimple(EmailCreator.class.getResourceAsStream("templates/simpleTemplate.html"),bundle, html.toString(), iEmail.title);
        return iEmail;
    }

    public static IEmailProvider.IEmail createRawEmail(String title, String html, String text){
        IEmailProvider.IEmail iEmail = new IEmailProvider.IEmail();
        iEmail.html = html;
        iEmail.content = text;
        iEmail.title = title;
        return iEmail;
    }
}
