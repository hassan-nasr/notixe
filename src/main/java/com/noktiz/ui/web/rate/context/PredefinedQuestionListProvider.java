package com.noktiz.ui.web.rate.context;

import com.noktiz.domain.entity.rate.PredefinedQuestion;
import com.noktiz.domain.entity.rate.PredefinedQuestionManager;
import com.noktiz.domain.entity.rate.QuestionLanguage;
import com.noktiz.ui.web.friend.PersonListProvider;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by hassan on 4/4/2015.
 */
public abstract class PredefinedQuestionListProvider extends PersonListProvider {
    @Override
    public boolean isSelectable() {
        return false;
    }

    @Override
    public boolean isSearchEnable() {
        return false;
    }

    public abstract void selectedQuestions(PredefinedQuestion predefinedQuestion, AjaxRequestTarget art);

    PredefinedQuestionManager predefinedQuestionManager = new PredefinedQuestionManager();
    QuestionLanguage language;
    String category;




    protected void add(String category,List<PredefinedQuestion> questions) {

        addPerson(new HeaderTile(category,null));

        HeaderTile lastHeader = null;
        Long rateCount=0l;
        Long rateSum =0l;
        for (PredefinedQuestion question : questions) {
            addPerson(new PredefinedQuestionTile(this, question));
        }
    }


    public PredefinedQuestionListProvider(QuestionLanguage language, String category) {
        this.language = language;
        this.category = category;
    }

    public void init() {
        if(language==null){
            add("Please.Select.A.Language",new ArrayList<>());

        }
        else if(category == null){
            for (Map.Entry<String, List<PredefinedQuestion>> entry : predefinedQuestionManager.get(language).entrySet()) {
                add(entry.getKey(),entry.getValue());
            }
        }
        else
            add(category,predefinedQuestionManager.get(language,category));
    }


}
