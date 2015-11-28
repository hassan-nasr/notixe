package com.noktiz.domain.entity.rate;

import com.noktiz.domain.Utils.SolrUtils;
import com.noktiz.domain.model.BaseManager;
import com.noktiz.domain.model.ResultWithObject;
import com.noktiz.domain.system.SystemConfigManager;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hassan on 3/30/2015.
 */
public class PredefinedQuestionManager extends BaseManager{
    static List<QuestionLanguage> currentLanguages;

    public List<QuestionLanguage> getAvailableLanguages(){
        if(currentLanguages==null)
            currentLanguages= getCurrentSession().getNamedQuery("loadAvailableQuestionLanguages").list();
        return currentLanguages;
    }

    static Map<QuestionLanguage,Map<String,List<PredefinedQuestion>>> questionsMap;

    public static Map<QuestionLanguage, Map<String, List<PredefinedQuestion>>> getQuestionsMap() {
        return questionsMap;
    }

    public static void setQuestionsMap(Map<QuestionLanguage, Map<String, List<PredefinedQuestion>>> questionsMap) {
        PredefinedQuestionManager.questionsMap = questionsMap;
    }

    public void cacheAll(){
        questionsMap = new HashMap<>();
        List<PredefinedQuestion> loadedList = getCurrentSession().getNamedQuery("loadActivePredefinedQuestions").list();
        for (PredefinedQuestion predefinedQuestion : loadedList) {
            QuestionLanguage language = predefinedQuestion.getLanguage();
            String category = predefinedQuestion.getCategory();
            Map<String, List<PredefinedQuestion>> inLanguage = questionsMap.get(language);
            if(inLanguage==null){
                inLanguage = new HashMap<>();
                questionsMap.put(language,inLanguage);
            }
            List<PredefinedQuestion> predefinedQuestions = inLanguage.get(category);
            if(predefinedQuestions==null){
                predefinedQuestions = new ArrayList<>();
                inLanguage.put(category,predefinedQuestions);
            }
            predefinedQuestions.add(predefinedQuestion);
        }
    }
    public List<PredefinedQuestion> get(QuestionLanguage language,String context){
        if(questionsMap==null)
            cacheAll();
        Map<String, List<PredefinedQuestion>> inLanguage = questionsMap.get(language);
        if(inLanguage!=null){
            List<PredefinedQuestion> ret = inLanguage.get(context);
            if(ret!=null)
                return ret;
        }
        return new ArrayList<>();
    }
    public Map<String,List<PredefinedQuestion>> get(QuestionLanguage language){
        if(questionsMap==null)
            cacheAll();
        Map<String, List<PredefinedQuestion>> inLanguage = questionsMap.get(language);
        if(inLanguage!=null){
            return inLanguage;
        }
        return new HashMap<>();
    }

    public List<String> getCategories(QuestionLanguage questionLanguage) {
        Map<String, List<PredefinedQuestion>> stringListMap = getQuestionsMap().get(questionLanguage);
        List<String> ret;
        if(stringListMap!=null)
            ret = new ArrayList<String>(stringListMap.keySet());
        else
            ret = new ArrayList<>();
        return ret;
    }

    public List<PredefinedQuestion> queryQuestions(QuestionLanguage language, String category, String query, int from, int count) {
        SolrQuery solrQuery;
        solrQuery = new SolrQuery( getQueryString(language, category, query));

        solrQuery.setStart(from);
        solrQuery.setRows(count);
        SolrClient client = getSolrClient();
        List<PredefinedQuestion > ret = new ArrayList<>();
        try {
            QueryResponse response = client.query(solrQuery);
            for (SolrDocument entry : response.getResults()) {
                ret.add(load(Long.parseLong((String) entry.getFieldValue("id"))));
            }
        } catch (SolrServerException e) {
            Logger.getLogger(this.getClass()).error(e);
        }
        return ret;
    }

    public HttpSolrClient getSolrClient() {
        return new HttpSolrClient(solrServerAddress);
    }

    static String solrServerAddress;
    private static void init() {
        solrServerAddress=SystemConfigManager.getCurrentConfig().getProperty("solrServerAddress");
        if(solrServerAddress == null)
            solrServerAddress="http://localhost:8983/solr/";
        solrServerAddress = solrServerAddress+"notixePreQuest";
    }

    private PredefinedQuestion load(Long id) {
        return (PredefinedQuestion) getCurrentSession().load(PredefinedQuestion.class,id);
    }

    private String getQueryString(QuestionLanguage language, String category, String query) {
        StringBuilder ret = new StringBuilder();
        boolean needAnd=false;
        if(language!=null ){
            ret.append("language:").append(language.toString()).append(" ");
            needAnd=true;
        }

        if(category != null && !category.isEmpty()){
            if(needAnd){
                ret.append(" AND ");
            }
            category= SolrUtils.escapeString(category);
            ret.append("category:(");
            for (String s : category.split(",")) {
                ret.append(" \"").append(s).append("\" ");
            }
            ret.append(")^10 ");
        }
        if(query !=null && !query.isEmpty()){
            if(needAnd){
                ret.append(" AND ");
            }
            ret.append("(");
            query= SolrUtils.escapeString(query);
            ret.append(query).append(" ");
            ret.append(")");
        }
        {
            if(needAnd){
                ret.append(" AND ");
            }
            ret.append("active:true");

        }
        return ret.toString();
    }

    static{
        init();
//        new PredefinedQuestionManager().updateAllSolrIndex();
    }
    public void updateAllSolrIndex(){
        List<PredefinedQuestion> questionsList = getCurrentSession().getNamedQuery("loadPredefinedQuestions").list();
        try {
            if(questionsList.isEmpty()){
                return;
            }
            getSolrClient().addBeans(questionsList);
            getSolrClient().commit();
        } catch (SolrServerException | IOException e) {
            Logger.getLogger(getClass()).error(e);
        }
    }

    public void updateInSolr(PredefinedQuestion question){
        try {
            if(question.isDeleted() || !question.isActive()){
                getSolrClient().deleteById(question.getId().toString());
            }
            else {
                SolrInputDocument toAdd = new SolrInputDocument();
                toAdd.addField("id", question.getId());
                toAdd.addField("category",question.getCategory());
                toAdd.addField("description",question.getDescription());
                toAdd.addField("active",question.isActive());
                toAdd.addField("publisher",question.getPublisher().getId());
                toAdd.addField("language",question.getLanguage());
                toAdd.addField("title",question.getTitle());
                toAdd.addField("createDate",question.getPublishDate());
                getSolrClient().add(toAdd);
            }
            getSolrClient().commit();
        } catch (IOException | SolrServerException e) {
            Logger.getLogger(getClass()).error(e);
        }
    }

    public ResultWithObject<Serializable> saveAsNew(PredefinedQuestion sat) {
        ResultWithObject<Serializable> result = super.saveAsNew(sat);
        if(result.result){
            sat.setId((Long) result.object);
            updateInSolr( sat);
        }
        return result;
    }

    public ResultWithObject update(PredefinedQuestion sat) {
        ResultWithObject<Serializable> result = super.update(sat);
        if(result.result){
            updateInSolr(sat);
        }
        return result;
    }

    public ResultWithObject merge(PredefinedQuestion sat) {
        ResultWithObject<Serializable> result = super.merge(sat);
        if(result.result){
            updateInSolr(sat);
        }
        return result;
    }

}
