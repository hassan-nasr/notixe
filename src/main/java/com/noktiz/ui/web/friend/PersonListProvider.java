/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.friend;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.noktiz.ui.web.BasePanel;
import com.noktiz.ui.web.base.ListDataProvider;
import org.apache.commons.lang.NotImplementedException;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 *
 * @author Hossein
 */
public class PersonListProvider extends ListDataProvider<PersonListProvider.IPerson> {

    protected ArrayList<IPerson> persons = new ArrayList<>();
    private ArrayList<IAction> actions = new ArrayList<>();
    private ArrayList<IAction> otherActions = new ArrayList<>();
    public PersonListProvider() {
        super(true);
    }

    public List<? extends IPerson> getPersons() {
        return persons;
    }

    public void refresh() {
    }

    public void addPerson(IPerson person) {
        persons.add(person);
    }

    public void clearPerson() {
        persons.clear();
    }
    public IModel<List<IAction>> getActions(){
        return new Model(actions);
    }
    public IModel<List<IAction>> getOtherActions(){
        return new Model(otherActions);
    }

    public boolean isSelectable() {
        return true;
    }
    
    public IModel<String> getTitle(){
        return Model.of("");
    }

    public boolean isSearchEnable() {
        return true;
    }

    public static abstract class IPerson<S> implements Serializable {

        private String tileGroupClass;

        abstract  public S getObj();

        protected ArrayList<IAction> actions = new ArrayList<>();
        protected ArrayList<IAction> otherActions = new ArrayList<>();
        private boolean select = false;

        public IPerson() {
        }

        public abstract String getImageUrl();


        public abstract IModel<String> getTitle();

        public abstract IModel<String> getDesc();

        public IModel<String> getBGClass() {
            return Model.of(" bg-blue ");
        }

        public IModel<List<IAction>> getActions() {
            return new AbstractReadOnlyModel() {
                @Override
                public Object getObject() {
                    return actions;
                }
            };
        }
        public IModel<List<IAction>> getOtherActions() {
            return new AbstractReadOnlyModel() {
                @Override
                public Object getObject() {
                    return otherActions;
                }
            };
        }
        public void addAction(IAction action) {
            actions.add(action);
        }
        public void addOtherAction(IAction action) {
            otherActions.add(action);
        }

        public IModel<Boolean> getSearchInTitle() {
            return Model.of(Boolean.TRUE);
        }

        public IModel<Boolean> getSearchInDesc() {
            return Model.of(Boolean.TRUE);
        }

        public IModel<Boolean> isSelect() {
            return new IModel<Boolean>() {
                @Override
                public Boolean getObject() {
                    return select;
                }

                @Override
                public void setObject(Boolean object) {
                    select = object;
                }

                @Override
                public void detach() {
                }
            };
        }

        public IModel<String> getSubTitle() {
            return Model.of("");
        }

        public IModel<Boolean> getSearchInSubtitle() {
            return Model.of(Boolean.FALSE);
        }

        public boolean getStarsEnable() {
            return false;
        }

        public IModel<Double> getStars() {
            return Model.of(0d);
        }

        public boolean isSeparator() {
            return false;
        }

        public boolean getDescEnable() {
            return true;
        }

        public boolean getTitleEnable() {
            return true;
        }

        public Integer getInputBoxLength(){
            return null;
        }

        public IModel<String> getInputBox() {
            return Model.of("");
        }

        public boolean getInputBoxEnable() {
            return false;
        }

        public boolean getStarsEditable() {
            return false;
        }

        public String getTileGroupClass() {
            return tileGroupClass;
        }

        public void setTileGroupClass(String tileGroupClass) {
            this.tileGroupClass = tileGroupClass;
        }

        public static abstract class IAction implements Serializable {

            public abstract IModel<String> getActionTitle();

            public boolean isActionEnabled() {
                return true;
            }

            /**
             * return true if person box need to be refresh
             *
             * @param art
             * @return
             */
            public abstract boolean onAction(AjaxRequestTarget art, Component caller);

            public IModel<String> getButtonClass() {
                return Model.of(" ");
            }

            ;

            public IModel<Boolean> getSearchInAction() {
                return Model.of(Boolean.FALSE);
            }


            public boolean isActionVisible() {
                return true;
            }
        }

        public String getImageLink(){
            return getOverallLink();
        }

        public String getTitleLink(){
            return getOverallLink();
        }
        public String getDescLink(){
            return getOverallLink();
        }
        public String getSubtitleLink(){
            return getOverallLink();
        }

        public  String getOverallLink(){
            return null;
        }

    }

    public static abstract class IAction implements Serializable {

        public abstract IModel<String> getActionTitle();

        public boolean isActionEnabled() {
            return true;
        }

        /**
         * return true if person boxes need to be refresh
         *
         * @param art
         * @return
         */
        public abstract boolean onAction(AjaxRequestTarget art, Component caller);

        public IModel<String> getButtonClass() {
            return Model.of("  ");
        }
    }


    protected void addAction(IAction action) {
        actions.add(action);
    }
    protected void addOtherAction(IAction action) {
        otherActions.add(action);
    }

    public class HeaderTile extends IPerson {

        String title;
        String desc;
        private double stars=0;

        public HeaderTile(String title, String desc) {
            this.title = title;
            this.desc = desc;
        }

        @Override
        public Object getObj() {
            return null;
        }

        @Override
        public String getImageUrl() {
            return null;
        }

        @Override
        public IModel<String> getTitle() {
            return Model.of(title);
        }

        @Override
        public IModel<String> getDesc() {
            return Model.of(desc);
        }

        @Override
        public boolean isSeparator() {
            return true;
        }

        @Override
        public IModel<String> getBGClass() {
            return Model.of(" bg-darkblue ");
        }

        public void setStars(double stars) {
            this.stars = stars;
        }

        @Override
        public boolean getStarsEnable() {
            return stars>0;
        }
        @Override
        public IModel<Double> getStars() {
            return Model.of(stars);
        }
    }


    public boolean hasMore=true;

    @Override
    public boolean hasMore(){
        return hasMore;
    }
    @Override
    public List<? extends PersonListProvider.IPerson> getElements(Integer from, Integer count) {
        if(from>=getPersons().size()) {
            hasMore =false;
            return new ArrayList();
        }
        List ret = new ArrayList();
        int to=Math.min(getPersons().size(),from+count);
        if(getPersons().size()<=to){
            hasMore=false;
        }
        List<? extends IPerson> iPersons = getPersons();
        for (int i = from; i < to; i++) {
            ret.add(iPersons.get(i));
        }
        return ret;
    }

    @Override
    public List<IPerson> getElementsBefore(IPerson From, Integer count) {
        throw  new NotImplementedException();
    }


    @Override
    public boolean isStatic() {
        return true;
    }

    @Override
    public BasePanel getPanel(String id, IPerson obj) {
        return new Tile(id,obj,isSelectable());
    }
}
