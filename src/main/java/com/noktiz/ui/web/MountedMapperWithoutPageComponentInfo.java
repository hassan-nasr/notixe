package com.noktiz.ui.web;

import org.apache.wicket.core.request.handler.BookmarkableListenerInterfaceRequestHandler;
import org.apache.wicket.core.request.handler.ListenerInterfaceRequestHandler;
import org.apache.wicket.core.request.mapper.MountedMapper;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.mapper.info.PageComponentInfo;
import org.apache.wicket.request.mapper.parameter.PageParametersEncoder;

/**
 * Created by hasan on 2014-10-14.
 */
public class MountedMapperWithoutPageComponentInfo extends MountedMapper {

    public MountedMapperWithoutPageComponentInfo(String mountPath, Class<? extends IRequestablePage> pageClass) {
        super(mountPath, pageClass, new PageParametersEncoder());
    }

    @Override
    protected void encodePageComponentInfo(Url url, PageComponentInfo info) {
        // do nothing so that component info does not get rendered in url
    }

    @Override
    public Url mapHandler(IRequestHandler requestHandler)
    {
        if (requestHandler instanceof ListenerInterfaceRequestHandler ||
                requestHandler instanceof BookmarkableListenerInterfaceRequestHandler) {
            return null;
        } else {
            return super.mapHandler(requestHandler);
        }
    }
}