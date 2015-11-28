/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.dashboard.notif;

import com.noktiz.domain.entity.Friendship;
import com.noktiz.domain.entity.UserProperties;
import com.noktiz.domain.entity.rate.*;
import com.noktiz.domain.model.Result;
import com.noktiz.domain.model.ResultWithObject;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.model.image.ImageManagement;
import com.noktiz.ui.web.BasePanel;
import com.noktiz.ui.web.behavior.IntroBehavior;
import com.noktiz.ui.web.component.NotificationFeedbackPanel;
import com.noktiz.ui.web.friend.PersonList;
import com.noktiz.ui.web.friend.PersonListProvider;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.noktiz.ui.web.profile.ProfilePage;
import com.noktiz.ui.web.rate.ReplyAction;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 * @author Hossein
 */
public class RateInviteNotificationPanel extends BasePanel {

    UserFacade sender = null;
    List<Rate> previousRates = new ArrayList<>();
    RateManager rateManager = new RateManager();
    Rate currentRate;
    final PersonList personList;
    Friendship friendship;
    PersonListProvider qa;
    public RateInviteNotificationPanel(String id, IModel<NotificationRateInvite> model) {
        super(id, model);
        final boolean intro = !UserProperties.TRUE.equals(getUserInSite().getProperty(
                UserProperties.NotificationRateInvite_INTRODUCTION_SEEN));
        getUserInSite().setProperty(UserProperties.NotificationRateInvite_INTRODUCTION_SEEN,UserProperties.TRUE);
        feedbackPanel = new NotificationFeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupId(true);
        addOrReplace(feedbackPanel);
        sender = new UserFacade(getNotificationObject().getRateContext().getUser());

        friendship = sender.getFriendship(getUserInSite());
        previousRates = rateManager.loadLastRatesDoneByUserInContext(getUserInSite(), getNotificationObject().getRateContext(), 0, 5);
        if (previousRates.size() > 0) {
            if (getNotificationObject().getRateContext().canRateHavingPreviousRating(previousRates.get(0))) {
                currentRate = new Rate(getUserInSite().getUser(), null, null, getNotificationObject().getRateContext(), null);
            } else {
                currentRate = previousRates.remove(0);
            }
        } else {
            currentRate = new Rate(getUserInSite().getUser(), null, null, getNotificationObject().getRateContext(), null);
        }
        qa = new PersonListProvider() {

            @Override
            public boolean isSearchEnable() {
                return false;
            }

            @Override
            public boolean isSelectable() {
                return false;
            }

            @Override
            public List getElements(Integer from, Integer count) {
                if (from < 2) {
                    return super.getElements(0, 2);
                } else {
                    return super.getElements(2, 10);
                }
            }

            @Override
            public IModel<String> showMoreCaption() {
                if (hasMore) {
                    return Model.of("Show previous rating");
                }
                return Model.of();
            }

            @Override
            public BasePanel getPanel(String id, IPerson obj) {
                if (obj instanceof QuestionPerson) {
                    return new QuestionTile(id, (QuestionPerson) obj,intro);
                } else {
                    return super.getPanel(id, obj);
                }
            }

            @Override
            public boolean isStatic() {
                return true;
            }
        };
        qa.hasMore = !previousRates.isEmpty();
        qa.addPerson(new QuestionPerson());
        final PersonListProvider.IPerson answer;
//        if(currentRate.getId() == null) {
            if(model.getObject().getRateContext().getEnable()) {
                answer = new AnswerPerson(model.getObject(), currentRate, true, false);
            }
            else {
                answer = new AnswerPerson(getNotificationObject(), new Rate(null, null, "responding to this question is disabled", model.getObject().getRateContext(), null), true, true);
                if(currentRate.getId()!=null){
                    previousRates.add(0,currentRate);
                }
            }
//        }
//        else
//            answer = new AnswerPerson(model.getNotificationObject(),currentRate,true,false);
        qa.addPerson(answer);
        for (Rate rate : previousRates) {
            qa.addPerson(new AnswerPerson(getNotificationObject(),rate,false,false));
        }
        personList = new PersonList("list", Model.of(qa), "", 10, "", false);
        if(qa.getPersons().size()>2 ) {
            answer.addAction(new ShowPreviousAction(personList));
        }
        add(personList);
    }

    private static class ShowPreviousAction extends PersonListProvider.IPerson.IAction {
        private final PersonList personList;
        boolean enable;

        public ShowPreviousAction(PersonList personList) {
            this.personList = personList;
            enable = true;
        }

        @Override
        public IModel<String> getActionTitle() {
            return new IModel<String>() {
                @Override
                public String getObject() {
                    return new StringResourceModel("previous", null).getObject();
//                    return "previous";
                }

                @Override
                public void setObject(String s) {
                }

                @Override
                public void detach() {
                }
            };
        }

        @Override
        public boolean onAction(AjaxRequestTarget art, Component caller) {
            personList.loadMoreItems(art);
            enable = false;
            return true;
        }

        @Override
        public boolean isActionEnabled() {
            return enable;
        }

    }

    class QuestionPerson extends PersonListProvider.IPerson {

        @Override
        public Object getObj() {
            return RateInviteNotificationPanel.this.getNotificationObject();
        }

        public QuestionPerson() {
            if (!getNotificationObject().getRateContext().getTimeBetweenRatings().equals(SimplePeriod.SingleTime)) {
                addAction(new SubscribeAction());
            }
            if (!getNotificationObject().getSeen()) {
                addOtherAction(new HideAction());
            }
            addOtherAction(new UnfollowAction());
        }

        public NotificationRateInvite getNotificationRateInvite() {
            return getNotificationObject();
        }

        @Override
        public String getImageUrl() {
            return RateInviteNotificationPanel.this.urlFor(ImageManagement.getUserImageResourceReferece(),
                    ImageManagement.getUserImageParameter(sender, ImageManagement.ImageSize.large)).toString();
        }

        @Override
        public IModel<String> getTitle() {
            return Model.of(getNotificationObject().getRateContext().getTitle());
        }

        @Override
        public IModel<String> getDesc() {

            String description = getNotificationObject().getRateContext().getDescription();
            if (description == null || description.trim().isEmpty()) {
                description = "";
            }

            return Model.of(description);
        }

        @Override
        public IModel<String> getSubTitle() {
            return Model.of(sender.getName());
        }

        @Override
        public IModel<String> getBGClass() {
            return Model.of(" bg-blue bigt noMargin");
        }

        public String getNumberFlag() {
            return getNotificationObject().getMutualFriendsCount().toString();
        }

        class SubscribeAction extends IAction {

            @Override
            public IModel<String> getActionTitle() {
                return new AbstractReadOnlyModel<String>() {
                    @Override
                    public String getObject() {
                        if (getNotificationObject().isSubscribed()) {
                            return "Ignore";
                        } else {
                            return "Notify me";
                        }
                    }
                };
            }

            @Override
            public IModel<String> getButtonClass() {
                return new AbstractReadOnlyModel<String>() {
                    @Override
                    public String getObject() {
                        if (getNotificationObject().isSubscribed()) {
                            return " red ";
                        } else {
                            return " ";
                        }
                    }
                };
            }

            @Override
            public boolean onAction(AjaxRequestTarget art, Component caller) {
                getNotificationObject().setSubscribed(!getNotificationObject().isSubscribed());
                getNotificationObject().update();
                if (getNotificationObject().isSubscribed()) {
                    info("You subscribe to this question. we'll notify you to answer this question in upcoming periods");
                } else {
                    info("You un-subscribe from this question. you'll not receive any notification to participate in this question");
                }
                return true;
            }

        }

        class HideAction extends IAction {

            @Override
            public IModel<String> getActionTitle() {
                return new AbstractReadOnlyModel<String>() {
                    @Override
                    public String getObject() {
                        if (getNotificationObject().getSeen()) {
                            return "done";
                        } else {
                            return "Hide";
                        }
                    }
                };
            }

            @Override
            public IModel<String> getButtonClass() {
                return new AbstractReadOnlyModel<String>() {
                    @Override
                    public String getObject() {
                        if (getNotificationObject().getSeen()) {
                            return " green ";
                        } else {
                            return " ";
                        }
                    }
                };
            }

            @Override
            public boolean isActionEnabled() {
                return !getNotificationObject().getSeen();
            }

            @Override
            public boolean onAction(AjaxRequestTarget art, Component caller) {
                getNotificationObject().setSeen(true);
                getNotificationObject().update();
                if (getNotificationObject().isSubscribed()) {
                }
                return true;
            }

        }
        class UnfollowAction extends IAction {

            @Override
            public IModel<String> getActionTitle() {
                return new AbstractReadOnlyModel<String>() {
                    @Override
                    public String getObject() {
                        if (friendship.getDontFollow()) {
                            return "follow "+sender.getName()+ " again";
                        } else {
                            return "stop follow "+sender.getName();
                        }
                    }
                };
            }

            @Override
            public boolean isActionVisible() {
                return friendship!=null;
            }

            @Override
            public boolean onAction(AjaxRequestTarget art, Component caller) {
                ResultWithObject<Friendship> result = getUserInSite().toggleFollow(sender);
                result.displayInWicket(caller);
                friendship=result.object;
                return true;
            }

        }
        @Override
        public String getImageLink() {
            return RequestCycle.get().urlFor(ProfilePage.class, new PageParameters().add("userid", getNotificationRateInvite().getSender().getId())).toString();
        }

        @Override
        public String getSubtitleLink() {
            return getImageLink();
        }
    }

    class AnswerPerson extends PersonListProvider.IPerson {

        private NotificationRateInvite rateInvite;
        private Rate rate;
        private boolean first;
        private boolean disableReply;

        public AnswerPerson(NotificationRateInvite rateInvite,Rate rate,boolean first,boolean disableReply) {
            this.rateInvite = rateInvite;
            this.rate = rate;
            this.first=first;
            this.disableReply = disableReply;
            init();
        }

        private void init() {

            if (rate.getId() == null&& disableReply==false) {
                addAction(new RateAction());
            }
            else {
                if(rate.getContext().getAllowRespondMore()&& disableReply ==false &&first==true){
                    addAction(new ReRateAction());
                }
                if(rate.getThread(new UserFacade(rate.getSender()))!=null){
                    addAction(new ReplyAction(rate));
                }
            }

        }

        @Override
        public Object getObj() {
            return rate;
        }

        @Override
        public String getImageUrl() {
            return null;
        }

        @Override
        public IModel<String> getTitle() {
            return new AbstractReadOnlyModel<String>() {

                @Override
                public String getObject() {
                    if (rate.getId() == null) {
                        return null;
                    } else {
                        return new StringResourceModel("respondDate",null,new Object[]{ RateInviteNotificationPanel
                                .this.getFormattedUserDate(rate.getDate())}).getObject();
                    }
                }
            };
        }

        @Override
        public IModel<String> getDesc() {
            return new AbstractReadOnlyModel<String>() {

                @Override
                public String getObject() {
                    if (rate.getId() == null&& disableReply==false) {
                        return null;
                    } else {
                        if (rate.getComment() == null) {
                            return "<No Comment>";
                        };
                        return rate.getComment();
                    }
                }
            };
        }

        @Override
        public boolean getInputBoxEnable() {
            return rate.getId() == null&&disableReply==false;
        }
        @Override
        public Integer getInputBoxLength() {
            return 255;
        }

        @Override
        public IModel<String> getInputBox() {
            return new IModel() {

                @Override
                public Object getObject() {
                    return rate.getComment();
                }

                @Override
                public void setObject(Object object) {
                    rate.setComment((String) object);
                }

                @Override
                public void detach() {
                }

            };
        }

        @Override
        public IModel<Double> getStars() {
            return new IModel<Double>() {

                @Override
                public Double getObject() {
                    final Integer rate = AnswerPerson.this.rate.getRate();
                    if (rate == null) {
                        return 0d;
                    }
                    return rate.doubleValue();
                }

                @Override
                public void setObject(Double object) {
                    rate.setRate((int) Math.round(object));
                }

                @Override
                public void detach() {
                }
            };
        }

        @Override
        public boolean getStarsEditable() {
            return rate.getId() == null&& disableReply==false;
        }

        @Override
        public boolean getStarsEnable() {
            if(disableReply)return false;
            if(rate.getId()== null)
                return !RateContext.RateState.Unavailable.equals(rateInvite.getRateContext().getRequireRate());
            return rate.getRate()!=null && rate.getRate()>0;

        }

        @Override
        public IModel<String> getBGClass() {
            return new AbstractReadOnlyModel<String>() {

                @Override
                public String getObject() {
                    if(rate.getId()==null){
                        return " bg-green bigt noMargin ";
                    }
                       return " bg-purple bigt noMargin ";
                }
            };
        }

        @Override
        public boolean getDescEnable() {
            return disableReply==true||rate.getId() != null;
        }

        public class RateAction extends IAction {

            @Override
            public IModel<String> getActionTitle() {
                return new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        if (rate.getId()!= null) {
                            return getString("Done");
                        } else {
                            return getString("Respond");
                        }
                    }
                };
            }

            @Override
            public boolean isActionEnabled() {
                return rate.getId() == null;

            }

            @Override
            public IModel<String> getButtonClass() {
                return Model.of(" green ");
            }

            @Override
            public boolean onAction(AjaxRequestTarget art, Component caller) {
                //TODO باید چک کنیم که الان که داره ارسال میکنه زمان یک پاسخ تمام نشده باشه
                //@todo           و اگه شده بهش ارور بدیم و به عنوان امتیاز جدید ححصاب کنیم

                rate.setDate(new Date());
                Result result = rateManager.rateFriend(rate, rateInvite);
//                Result result = new Result(new Result.Message("Your response saved successfully", Result.Message.Level.success), true);
                result.displayInWicket(feedbackPanel);
                return result.result;
            }

        }

        private class ReRateAction extends IAction{
            @Override
            public IModel<String> getActionTitle() {
                return Model.of("Respond again");
            }

            @Override
            public boolean onAction(AjaxRequestTarget art, Component caller) {
                previousRates.add(0, rate);
                AnswerPerson last = new AnswerPerson(rateInvite,rate,false,false);
                rate = new Rate(getUserInSite().getUser(), null, null, getNotificationObject().getRateContext(), null);
                ((List)(getActions().getObject())).clear();
                addAction(new ShowPreviousAction(personList));
                init();
                ((List)qa.getPersons()).add(2, last);
                return true;
            }
        }
    }

//    class AnsweredPerson extends PersonListProvider.IPerson {
//
//        Rate rate;
//
//        public AnsweredPerson(Rate rate) {
//            if (rate.getThread(new UserFacade(rate.getSender())) != null) {
//                addAction(new ReplyAction(rate));
//            }
//            this.rate = rate;
//        }
//
//        @Override
//        public String getImageUrl() {
//            return null;
//        }
//
//        @Override
//        public IModel<String> getDesc() {
//            String comment = rate.getComment();
//            if (comment == null || comment.trim().isEmpty()) {
//                comment = "<No Comment>";
//            }
//            return Model.of(comment);
//        }
//
//        @Override
//        public IModel<Double> getStars() {
//            return new AbstractReadOnlyModel<Double>() {
//
//                @Override
//                public Double getNotificationObject() {
//                    final Integer r = rate.getRate();
//                    if (r == null) {
//                        return 0d;
//                    }
//                    return r.doubleValue();
//                }
//
//            };
//        }
//
//        @Override
//        public boolean getStarsEditable() {
//            return false;
//        }
//
//        @Override
//        public boolean getStarsEnable() {
//            return rate.getRate()!= null && rate.getRate()>0;
//        }
//
//        @Override
//        public IModel<String> getBGClass() {
//            return Model.of(" bg-purple ");
//        }
//
//        @Override
//        public IModel<String> getTitle() {
//            if(rate.getDate() == null)
//                return Model.of("");
//            return Model.of("Respond on " + RateInviteNotificationPanel.this.getFormattedUserDate(rate.getDate()));
//        }
//
//    }

    private NotificationRateInvite getNotificationObject() {
        return (NotificationRateInvite) getDefaultModelObject();
    }

}
