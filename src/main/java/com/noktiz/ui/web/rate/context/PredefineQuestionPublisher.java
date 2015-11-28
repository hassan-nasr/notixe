package com.noktiz.ui.web.rate.context;

import com.noktiz.domain.entity.rate.PredefinedQuestion;
import com.noktiz.domain.entity.rate.PredefinedQuestionManager;
import com.noktiz.domain.entity.rate.QuestionLanguage;
import com.noktiz.domain.entity.rate.RateContext;
import com.noktiz.domain.model.Result;
import com.noktiz.ui.web.BasePanel;
import com.noktiz.ui.web.component.IndicatingAjaxSubmitLink2;
import com.noktiz.ui.web.component.TagInput;
import com.noktiz.ui.web.component.lazy.LazyInitComponent;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.model.IModel;

import java.util.Date;
import java.util.List;

/**
 * Created by hassan on 4/9/2015.
 */
public abstract class PredefineQuestionPublisher extends BasePanel implements LazyInitComponent{
    private final IModel<RateContext> rateContext;
    String tag;
    private Boolean anonymous = false;
    PredefinedQuestionManager predefinedQuestionManager = new PredefinedQuestionManager();
    QuestionLanguage questionLanguage;
    public PredefineQuestionPublisher(String id, IModel<RateContext> rateContext) {
        super(id);
        this.rateContext = rateContext;
    }

    String titleString;
    String descriptionString;
    @Override
    public void init(){
        titleString=rateContext.getObject().getTitle();
        descriptionString=rateContext.getObject().getDescription();
        Form form = new Form("form");
        addOrReplace(form);
        TextField title = new TextField<String>("title", new IModel<String>() {
            @Override
            public String getObject() {
                return titleString;
            }

            @Override
            public void setObject(String o) {
                titleString=o;
            }

            @Override
            public void detach() {

            }
        });
        form.add(title);
        TextArea<String> description = new TextArea<String>("description", new IModel<String>() {
            @Override
            public String getObject() {
                return descriptionString;
            }

            @Override
            public void setObject(String o) {
                descriptionString=o;
            }

            @Override
            public void detach() {

            }
        });
        form.add(description);
        TagInput tagInput= new TagInput("tag", "", new IModel<String>() {
            @Override
            public String getObject() {
                return tag;
            }

            @Override
            public void setObject(String s) {
                tag=s;
            }

            @Override
            public void detach() {

            }
        });
        form.add(tagInput);
        tagInput.setHeight(10);
        CheckBox anonymousBox = new CheckBox("anonymous", new IModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return anonymous;
            }

            @Override
            public void setObject(Boolean aBoolean) {
                anonymous=aBoolean;
            }

            @Override
            public void detach() {
            }
        });
        form.add(anonymousBox);

        List<QuestionLanguage> availableLanguages;
        availableLanguages = predefinedQuestionManager.getAvailableLanguages();
        questionLanguage = availableLanguages.get(0);
        DropDownChoice<QuestionLanguage> languageDropDown = new DropDownChoice<QuestionLanguage>("language", new IModel<QuestionLanguage>() {
            @Override
            public QuestionLanguage getObject() {
                return questionLanguage;
            }

            @Override
            public void setObject(QuestionLanguage language) {
                questionLanguage=language;
            }

            @Override
            public void detach() {
            }
        },availableLanguages, new IChoiceRenderer<QuestionLanguage>() {
            @Override
            public Object getDisplayValue(QuestionLanguage language) {
                return getString(language.getKey(), null, language.getKey());
            }

            @Override
            public String getIdValue(QuestionLanguage language, int i) {
                return String.valueOf(i);
            }
        });
        form.add(languageDropDown);

        IndicatingAjaxSubmitLink2 submit= new IndicatingAjaxSubmitLink2("submit") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                PredefinedQuestion predefinedQuestion = new PredefinedQuestion();
                predefinedQuestion.setTitle(titleString);
                predefinedQuestion.setDescription(descriptionString);
                predefinedQuestion.setAnonymous(anonymous);
                predefinedQuestion.setCategory(tag);
                predefinedQuestion.setLanguage(questionLanguage);
                predefinedQuestion.setPublisher(getUserInSite().getUser());
                predefinedQuestion.setPublishDate(new Date());
                predefinedQuestionManager.saveAsNew(predefinedQuestion);
                super.onSubmit(target, form);
                onAjaxSave(target,new Result(true));

            }
        };
        form.add(submit);
        form.setDefaultButton(submit);
    }

    @Override
    public boolean canReInit() {
        return true;
    }

    public abstract void onAjaxSave(AjaxRequestTarget target, Result result);
}
