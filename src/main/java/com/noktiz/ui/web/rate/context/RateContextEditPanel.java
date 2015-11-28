package com.noktiz.ui.web.rate.context;

import com.noktiz.domain.entity.privacy.ComplexPersonList;
import com.noktiz.domain.entity.rate.PredefinedQuestion;
import com.noktiz.domain.entity.rate.RateContext;
import com.noktiz.domain.entity.rate.RateContextManager;
import com.noktiz.domain.entity.rate.SimplePeriod;
import com.noktiz.domain.model.Result;
import com.noktiz.domain.model.ResultException;
import com.noktiz.domain.model.ResultWithObject;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.persistance.HSF;
import com.noktiz.ui.web.BasePanel;
import com.noktiz.ui.web.behavior.Select2Behavior;
import com.noktiz.ui.web.component.AutoGrowTextArea;
import com.noktiz.ui.web.component.IndicatingAjaxSubmitLink2;
import com.noktiz.ui.web.component.TagInput;
import com.noktiz.ui.web.component.lazy.LazyInitComponent;
import com.noktiz.ui.web.privacy.ComplexPersonListEditPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.string.AppendingStringBuffer;
import org.apache.wicket.util.string.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by hasan on 2014-11-03.
 */
public class RateContextEditPanel extends BasePanel implements LazyInitComponent{
    private final IModel<RateContext> rateContext;
    private RateContextManager rateContextManager = new RateContextManager();
    private TextField title;
    private TextArea<String> description;
    DropDownChoice<PredefinedQuestion> predefinedQuestionDropDownChoice;
    WebMarkupContainer predefinedQuestionDropDownChoiceWMC;
    TagInput withEmail;

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
//        response.render(OnDomReadyHeaderItem.forScript("$('.select2').select2();"));
        response.render(new OnDomReadyHeaderItem("$('.switch').bootstrapSwitch();"));
    }

    public RateContextEditPanel(String id,final IModel<RateContext> rateContext1) {
        super(id);
        this.rateContext = rateContext1;
//        init();
    }
    @Override
    public void init() {
        setOutputMarkupId(true);
        add(feedbackPanel);
        WebMarkupContainer editPart = new WebMarkupContainer("edit");
        add(editPart);
        Form form = new Form("rateContextEditForm"){
            @Override
            protected void onSubmit() {

            }
        };
        editPart.add(form);
        title = new TextField("title",new  IModel<String>(){

            @Override
            public String getObject() {
                return rateContext.getObject().getTitle();
            }

            @Override
            public void setObject(String object) {
                rateContext.getObject().setTitle(object);
            }

            @Override
            public void detach() {

            }
        });
        title.setRequired(true);
        title.setOutputMarkupId(true);
        form.add(title);
        description = new AutoGrowTextArea("description",new  IModel<String>(){

            @Override
            public String getObject() {
                return rateContext.getObject().getDescription();
            }

            @Override
            public void setObject(String object) {
                rateContext.getObject().setDescription(object);
            }

            @Override
            public void detach() {

            }
        });
        form.add(description);
        description.setOutputMarkupId(true);


        CheckBox enableCheckBox = new CheckBox("status", new IModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return rateContext.getObject().getEnable();
            }

            @Override
            public void setObject(Boolean object) {
                rateContext.getObject().setEnable(object);
            }

            @Override
            public void detach() {

            }
        });
        form.add(enableCheckBox);

        form.add(enableCheckBox);

        final ComplexPersonListEditPanel invitee = new ComplexPersonListEditPanel("users", new IModel<ComplexPersonList>() {
            @Override
            public ComplexPersonList getObject() {
                return rateContext.getObject().getInvitedPersons();
            }

            @Override
            public void setObject(ComplexPersonList complexPersonList) {

            }

            @Override
            public void detach() {

            }
        }, new UserFacade(rateContext.getObject().getUser()));
        form.add(invitee);
        IndicatingAjaxSubmitLink2 save = new IndicatingAjaxSubmitLink2("save") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                Result result = new Result();
                try {
                    if (rateContext.getObject().getId() != null) {
                        ResultWithObject<RateContext> update = rateContextManager.merge(rateContext.getObject(), false);
                        rateContext.setObject(update.object);
                        if (!update.result) {
                            throw new ResultException(update, null);
                        }
                        result.addRequired(update);
                    } else {
                        ResultWithObject resultWithObject = rateContextManager.saveAsNew(rateContext.getObject(), false);
                        if (!resultWithObject.result)
                            throw new ResultException(resultWithObject, null);
                        result.addRequired(resultWithObject);
                        getUserInSite().refresh();
//                        HSF.get().getCurrentSession().merge(rateContext.getObject().getUser());
//                        rateContext.getObject().getUser().getRateContexts().add(0,rateContext.getObject());
//                        rateContext.getObject().getUser().save();
                    }
                    Result inviteWithEmailResult = rateContextManager.inviteWithEmail(rateContext.getObject(), withEmail.getDefaultModelObjectAsString().split("\\s+"));
                    result.addRequired(inviteWithEmailResult);

                    rateContextManager.sendInvites(rateContext.getObject());

                    target.add(invitee);
                    withEmail.setDefaultModelObject("");
                    target.add(withEmail);
//                    target.add(predefinedQuestionDropDownChoiceWMC);
                } catch (ResultException e) {
//                    e.getResult().displayInWicket(feedbackPanel);
                    result.addRequired(e.getResult());
                }
//                target.add(feedbackPanel);
                onAjaxSave(target, result);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(feedbackPanel);
            }
        };
        form.add(save);
        form.setDefaultButton(save);

//        createPredefinedQuestionSelector(form,rateContext);
        createPredefinedQuestionSelectorModal(form,rateContext);

        createWithEmailInvite(form);
        WebMarkupContainer advancedWMC = new WebMarkupContainer("advanced");
        form.add(advancedWMC);
        CheckBox allowMoreCheckBox = new CheckBox("allowMoreRate", new IModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return rateContext.getObject().getAllowRespondMore();
            }

            @Override
            public void setObject(Boolean object) {
                rateContext.getObject().setAllowRespondMore(object);
            }

            @Override
            public void detach() {

            }
        });
        advancedWMC.add(allowMoreCheckBox);
        DropDownChoice needRate = new DropDownChoice("needRate", new IModel<RateContext.RateState>() {
            @Override
            public RateContext.RateState getObject() {
                return rateContext.getObject().getRequireRate();
            }

            @Override
            public void setObject(RateContext.RateState object) {
                rateContext.getObject().setRequireRate(object);
            }

            @Override
            public void detach() {

            }
        }, Arrays.asList(RateContext.RateState.values()),new IChoiceRenderer() {
            @Override
            public Object getDisplayValue(Object object) {
                return "RateState."+ object.toString();
            }

            @Override
            public String getIdValue(Object object, int index) {
                return String.valueOf(((RateContext.RateState) object).ordinal());
            }
        }){
            @Override
            protected boolean localizeDisplayValues() {
                return true;
            }
        };
        advancedWMC.add(needRate);
        DropDownChoice period = new DropDownChoice("period", new IModel<SimplePeriod>() {
            @Override
            public SimplePeriod getObject() {
                return rateContext.getObject().getTimeBetweenRatings();
            }

            @Override
            public void setObject(SimplePeriod object) {
                rateContext.getObject().updateTimeBetweenRatings(object);
            }

            @Override
            public void detach() {

            }
        }, Arrays.asList(SimplePeriod.values()),new IChoiceRenderer() {
            @Override
            public Object getDisplayValue(Object object) {
                return "SimplePeriod."+((SimplePeriod) object).toString();
            }

            @Override
            public String getIdValue(Object object, int index) {
                return String.valueOf(((SimplePeriod) object).ordinal());
            }
        }){
            @Override
            protected boolean localizeDisplayValues() {
                return true;
            }
        };
        advancedWMC.add(period);

        WebMarkupContainer advancedToggle = new WebMarkupContainer("advancedToggle"){
            @Override
            protected void onConfigure() {
                super.onConfigure();
                add(new AttributeModifier("onclick","$('#"+advancedWMC.getMarkupId()+"').toggle()"));
            }
        };
        form.add(advancedToggle);
    }

    private void createWithEmailInvite(Form form) {
        withEmail = new TagInput("withEmail",getString("addEmail"),new IModel<String>() {
            String data="";
            @Override
            public String getObject() {
                return data;
            }

            @Override
            public void setObject(String o) {
                data = o;
            }

            @Override
            public void detach() {

            }
        });
        form.add(withEmail);
        withEmail.setOutputMarkupId(true);
    }

    private void createPredefinedQuestionSelectorModal(Form form,final IModel<RateContext> rateContext) {

        final WebMarkupContainer modal = new WebMarkupContainer("modal");
        modal.setOutputMarkupId(true);

        WebMarkupContainer showEdit = new WebMarkupContainer("selectQuestion"){
            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                add(new AttributeModifier("href", "#" + modal.getMarkupId()));
                add(new AttributeModifier("onclick", "setTimeout(function(){tileResize('"+modal.getMarkupId()+"')},1000)"));

            }

        };
        form.add(showEdit);
        add(modal);

        PredefinedRateContextSelector selector = new PredefinedRateContextSelector("selector") {
            @Override
            public void mySelectedQuestions(PredefinedQuestion predefinedQuestion, AjaxRequestTarget art) {
                rateContext.getObject().setTitle(predefinedQuestion.getTitle());
                rateContext.getObject().setDescription(predefinedQuestion.getDescription());
                art.add(title,description);
                art.appendJavaScript("$('.closeSelector').click()");
            }
        };
        modal.add(selector);
    }
    private void createPredefinedQuestionSelector(final Form form, final IModel<RateContext> rateContext) {
//        Select predefinedQuestions = new Select("predefinedQuestions", new IModel<RateContext.RateState>() {
//            @Override
//            public RateContext.RateState getObject() {
//                return rateContext.getObject().getRequireRate();
//            }
//
//            @Override
//            public void setObject(RateContext.RateState object) {
//                rateContext.getObject().setRequireRate(object);
//            }
//
//            @Override
//            public void detach() {
//
//            }
//        });
//        ListView opetionGrousp = new ListView("groups",new ArrayList(predefinedQuestion.entrySet())) {
//            @Override
//            protected void populateItem(ListItem listItem) {
//                Map.Entry<String, List<PredefinedQuestion>> value = ((Map.Entry<String,List<PredefinedQuestion>>)listItem.getModelObject());
//                add(new AttributeAppender("label",value.getKey()));
//                ListView items = new ListView("item",value.getValue()) {
//                    @Override
//                    protected void populateItem(ListItem listItem) {
//                        PredefinedQuestion pq=  (PredefinedQuestion)listItem.getModelObject();
//                        add(new AttributeAppender("value",pq.getId()));
//
//                    }
//                }
//            }
//        }


        Map<String, List<PredefinedQuestion>> predefinedQuestion = PredefinedQuestion.loadAll();
        ArrayList<PredefinedQuestion> thingList = new ArrayList<>();
        for (List<PredefinedQuestion> questions : predefinedQuestion.values()) {
            thingList.addAll(questions);
        }
        predefinedQuestionDropDownChoice = new DropDownChoice<PredefinedQuestion>("predefinedQuestions"
                ,new PropertyModel<PredefinedQuestion>(rateContext,"predefinedQuestion"),
                thingList,new IChoiceRenderer<PredefinedQuestion>() {
            @Override
            public Object getDisplayValue(PredefinedQuestion predefinedQuestion) {
                if(predefinedQuestion == null)
                    return getString("Questions.All");
                return predefinedQuestion.getTitle();
            }

            @Override
            public String getIdValue(PredefinedQuestion predefinedQuestion, int i) {
                if(predefinedQuestion == null)
                    return getString("Questions.All");
                return predefinedQuestion.getId().toString();
            }
        }) {
            private static final long serialVersionUID = 1L;
            private PredefinedQuestion last;

            private boolean isLast(int index) {
                return index - 1 == getChoices().size();
            }

            private boolean isFirst(int index) {
                return index == 0;
            }

            private boolean isNewGroup(PredefinedQuestion current) {
                return last == null
                        || !current.getCategory().equals(last.getCategory());
            }

            private String getGroupLabel(PredefinedQuestion current) {
                return current.getCategory();
            }

            @Override
            protected void appendOptionHtml(AppendingStringBuffer buffer,
                                            PredefinedQuestion choice, int index, String selected) {
                if (isNewGroup(choice)) {
                    if (!isFirst(index)) {
                        buffer.append("</optgroup>");
                    }
                    buffer.append("<optgroup label='");
                    buffer.append(Strings.escapeMarkup(getGroupLabel(choice)));
                    buffer.append("'>");
                }
                super.appendOptionHtml(buffer, choice, index, selected);
                if (isLast(index)) {
                    buffer.append("</optgroup>");
                }
                last = choice;

            }

            @Override
            protected void onSelectionChanged(PredefinedQuestion newSelection) {
                super.onSelectionChanged(newSelection);
            }
            @Override
            protected boolean wantOnSelectionChangedNotifications() {
                return false;
            }

//            @Override
//            public void renderHead(IHeaderResponse response) {
//                super.renderHead(response);
//                response.render(OnDomReadyHeaderItem.forScript("$('#"+getMarkupId()+"').select2();"));
//            }
        };
        predefinedQuestionDropDownChoiceWMC = new WebMarkupContainer("predefinedQuestionsWMC");
        form.add(predefinedQuestionDropDownChoiceWMC);
        predefinedQuestionDropDownChoiceWMC.setOutputMarkupId(true);
        predefinedQuestionDropDownChoiceWMC.add(predefinedQuestionDropDownChoice);
        predefinedQuestionDropDownChoice.setOutputMarkupId(true);
        predefinedQuestionDropDownChoice.setNullValid(true);
        predefinedQuestionDropDownChoice.add(new Select2Behavior());
        predefinedQuestionDropDownChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {

            @Override
            protected void onUpdate(AjaxRequestTarget ajaxRequestTarget) {
                rateContext.getObject().setTitle(((PredefinedQuestion) predefinedQuestionDropDownChoice.getDefaultModelObject()).getTitle());
                ajaxRequestTarget.add(title);
            }
        });




    }


    public void onAjaxSave(AjaxRequestTarget target, Result result){
    }
}
