package com.noktiz.ui.web.rate.context;

import com.noktiz.domain.entity.rate.PredefinedQuestion;
import com.noktiz.domain.entity.rate.QuestionLanguage;
import com.noktiz.domain.entity.rate.PredefinedQuestionManager;
import com.noktiz.ui.web.BasePanel;
import com.noktiz.ui.web.component.IndicatingAjaxSubmitLink2;
import com.noktiz.ui.web.friend.PersonList;
import com.noktiz.ui.web.friend.PersonListProvider;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.List;

/**
 * Created by hassan on 3/30/2015.
 */
public abstract class PredefinedRateContextSelector extends BasePanel {
    WebMarkupContainer container;
    QuestionLanguage questionLanguage = null;
//    String category = null;
    PredefinedQuestionListProvider questionListProvider;
    private String query = null;

    public PredefinedRateContextSelector(String id) {
        super(id);
        PredefinedQuestionManager predefinedQuestionManager = new PredefinedQuestionManager();
        List<QuestionLanguage> availableLanguages;
        availableLanguages = predefinedQuestionManager.getAvailableLanguages();
        questionLanguage = availableLanguages.get(0);
        Form form = new Form("form");
        add(form);
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
        languageDropDown.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget ajaxRequestTarget) {
                loadQuestions();
                ajaxRequestTarget.add(container);
            }
        });
        form.add(languageDropDown);
//        TextField<String> categorySelector = new TextField<String>("category", new IModel<String>() {
//            @Override
//            public String getObject() {
//                return category;
//            }
//
//            @Override
//            public void setObject(String s) {
//                category =s;
//            }
//
//            @Override
//            public void detach() {
//
//            }
//        });
//        form.add(categorySelector);
//        categorySelector.setOutputMarkupId(true);

        TextField<String> queryInput = new TextField<String>("query", new IModel<String>() {
            @Override
            public String getObject() {
                return query;
            }

            @Override
            public void setObject(String s) {
                query =s;
            }

            @Override
            public void detach() {

            }
        });

        form.add(queryInput);
        container = new WebMarkupContainer("questionContainer");
        add(container);
        container.setOutputMarkupId(true);
        loadQuestions();
        IndicatingAjaxSubmitLink2 submit = new IndicatingAjaxSubmitLink2("submit"){
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                loadQuestions();
                target.add(container);

            }
        };
        form.add(submit);
        form.setDefaultButton(submit);
    }

    public void loadQuestions() {
        questionListProvider = new SolrPredefinedQuestionListProvider(questionLanguage,query) {
            @Override
            public void selectedQuestions(PredefinedQuestion predefinedQuestion, AjaxRequestTarget art) {
                mySelectedQuestions(predefinedQuestion,art);
            }
        };
        questionListProvider.init();
        PersonList questionList=new PersonList("questionList", new IModel<PredefinedQuestionListProvider>() {
            @Override
            public PredefinedQuestionListProvider getObject() {
                return questionListProvider;
            }

            @Override
            public void setObject(PredefinedQuestionListProvider personListProvider) {
                PredefinedRateContextSelector.this.questionListProvider = personListProvider;
            }

            @Override
            public void detach() {
            }
        },getString("nothingFound"),1000,getString("noMore"),true);
        container.addOrReplace(questionList);
    }


    public abstract void mySelectedQuestions(PredefinedQuestion predefinedQuestion, AjaxRequestTarget art);
}
