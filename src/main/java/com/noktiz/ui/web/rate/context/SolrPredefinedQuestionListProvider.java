package com.noktiz.ui.web.rate.context;

import com.noktiz.domain.entity.rate.PredefinedQuestion;
import com.noktiz.domain.entity.rate.PredefinedQuestionManager;
import com.noktiz.domain.entity.rate.QuestionLanguage;
import com.noktiz.ui.web.friend.PersonListProvider;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.SolrParams;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by hassan on 4/8/2015.
 */
abstract public class SolrPredefinedQuestionListProvider extends PredefinedQuestionListProvider {
    private String query;
    PredefinedQuestionManager predefinedQuestionManager = new PredefinedQuestionManager();


    protected void add(String category,List<PredefinedQuestion> questions) {

        addPerson(new HeaderTile(category,null));

        HeaderTile lastHeader = null;
        Long rateCount=0l;
        Long rateSum =0l;
        for (PredefinedQuestion question : questions) {
            addPerson(new PredefinedQuestionTile(this, question));
        }

    }


    public SolrPredefinedQuestionListProvider(QuestionLanguage language, String query) {
        super(language,null);
        this.query=query;
        setQuery(query);
    }

    @Override
    public void init() {
    }



    @Override
    public List<PredefinedQuestionTile> getElements(Integer from, Integer count) {
        List<PredefinedQuestion> predefinedQuestions = predefinedQuestionManager.queryQuestions(language, category, query, from, count);
        List<PredefinedQuestionTile> ret = predefinedQuestions.stream().map(question -> new PredefinedQuestionTile(this, question)).collect(Collectors.toList());
        if(ret.size()<count)
            hasMore=false;
        return ret;
    }

    public void setQuery(String query) {
        if(query==null){
            this.query="";
            this.category="";
            return;
        }
        StringBuilder categoryBuilder = new StringBuilder();
        StringBuilder queryBuilder = new StringBuilder();
        for (String s : query.split("\\s+")) {
            if(s.startsWith("#"))
            {
                String withOutSharp = s.substring(1);
                queryBuilder.append(withOutSharp);
                categoryBuilder.append(withOutSharp);
            }
            else
                queryBuilder.append(s);
        }
        query=queryBuilder.toString();
        category=categoryBuilder.toString();
    }
}
