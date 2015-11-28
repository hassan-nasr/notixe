package com.noktiz.domain.system;

import com.noktiz.domain.entity.BaseObject;
import com.noktiz.domain.model.BaseManager;
import com.noktiz.domain.model.ResultWithObject;
import com.noktiz.domain.persistance.HSF;

/**
 * Created by hasan on 8/7/14.
 */
public class SystemConfigManager extends BaseManager{
    static SystemConfig currentConfig = null;
    public static SystemConfig getCurrentConfig(){
        if(currentConfig==null)
            loadCurrentConfig();
        return currentConfig;
    }

    private static void loadCurrentConfig() {
        currentConfig = (SystemConfig) HSF.get().getCurrentSession().get(SystemConfig.class,1l);
        if(currentConfig !=null)
            HSF.get().getCurrentSession().refresh(currentConfig);
    }

    public static void setCurrentConfig(SystemConfig currentConfig1) {
        currentConfig = currentConfig1;
        HSF.get().getCurrentSession().saveOrUpdate(currentConfig);
    }

    @Override
    public ResultWithObject update(BaseObject sat) {
        ResultWithObject update = super.update(sat);
        SystemConfig updated = (SystemConfig) update.object;
        if(currentConfig == null || currentConfig.getId()== null || currentConfig.getId().equals(updated.getId()))
            currentConfig=updated;
        return update;
    }

    public static void reload() {
        if(currentConfig != null){
            HSF.get().getCurrentSession().evict(currentConfig);
        }
        currentConfig=null;
    }
}
