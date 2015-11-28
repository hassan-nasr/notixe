package com.noktiz.ui.web.rate.context;

import com.noktiz.domain.entity.rate.PredefinedQuestion;
import com.noktiz.ui.web.friend.PersonListProvider;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

/**
* Created by hassan on 4/8/2015.
*/
class PredefinedQuestionTile extends PersonListProvider.IPerson<PredefinedQuestion> {
    private PredefinedQuestionListProvider predefinedQuestionListProvider;
    private PredefinedQuestion predefinedQuestion;


    public PredefinedQuestionTile(PredefinedQuestionListProvider predefinedQuestionListProvider, PredefinedQuestion predefinedQuestion) {
        this.predefinedQuestionListProvider = predefinedQuestionListProvider;
        this.predefinedQuestion = predefinedQuestion;
        addAction(new IAction() {
            @Override
            public IModel<String> getActionTitle() {
                return new StringResourceModel("select",null,null);
            }

            @Override
            public boolean onAction(AjaxRequestTarget art, Component caller) {
                predefinedQuestionListProvider.selectedQuestions(predefinedQuestion, art);
                return true;
            }
        });
    }

    @Override
    public PredefinedQuestion getObj() {
        return predefinedQuestion;
    }

    @Override
    public String getImageUrl() {
        return null;
    }


    @Override
    public IModel<String> getBGClass() {
        return Model.of("bg-blue");
    }



    @Override
    public IModel<String> getTitle() {
//            if(rate.getComment()!=null)
        return Model.of(predefinedQuestion.getTitle());
//            return Model.of("");
    }

    @Override
    public String getTileGroupClass() {
        return "predefinedQuestion";
    }

    @Override
    public IModel<Boolean> getSearchInSubtitle() {
        return Model.of(false);
    }

    @Override
    public IModel<String> getDesc() {
        return Model.of(predefinedQuestion.getDescription());
    }

}
