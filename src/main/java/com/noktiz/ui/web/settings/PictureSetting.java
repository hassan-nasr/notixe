/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.settings;

import com.noktiz.domain.model.Result;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.model.image.ImageManagement;
import com.noktiz.ui.web.Application;
import com.noktiz.ui.web.component.IndicatingAjaxSubmitLink2;
import com.noktiz.ui.web.component.NotificationFeedbackPanel;
import java.io.IOException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.head.*;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.util.lang.Bytes;
import org.jboss.logging.Logger;

/**
 *
 * @author Hossein
 */
public class PictureSetting extends Panel {

    FileUploadField newImage;


    final HiddenField<Integer> x1;
    final HiddenField<Integer> x2;
    final HiddenField<Integer> y1;
    final HiddenField<Integer> y2;

    @Override
    public void renderHead(IHeaderResponse response) {
//        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class,"./assets/plugins/jcrop/js/jquery.Jcrop.js")));
//        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class,"./assets/plugins/jcrop/js/jquery.color.js")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class,"./assets/plugins/cropper/cropper.js")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "./assets/plugins/cropper/cropper.css")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(PictureSetting.class,"PictureSetting.js")));
//        response.render(new OnDomReadyHeaderItem("imageSelectCrop()"));
        response.render(new OnDomReadyHeaderItem("initCropper()"));
        response.render(new OnDomReadyHeaderItem("registerName('"+x1.getMarkupId()+"','"+x2.getMarkupId()+"','"+y1.getMarkupId()+"','"+y2.getMarkupId()+"')"));
    }

    public PictureSetting(String id, final UserFacade uf) {
        super(id);
        Form form = new Form("form");
        add(form);
        form.setMultiPart(true);
        form.setMaxSize(Bytes.megabytes(1));
        Image image;
        image = new Image("image", ImageManagement.getUserImageResourceReferece(),
                ImageManagement.getUserImageParameter(uf, ImageManagement.ImageSize.large));
        form.add(image);
        NotificationFeedbackPanel feedback= new NotificationFeedbackPanel("feedback",30000l);
        form.add(feedback);
        newImage = new FileUploadField("newImage");
        form.setMaxSize(Bytes.megabytes(2));
        newImage.setRequired(true);
        form.add(newImage);
        x1 = new HiddenField<>("x1", Model.of(0));
        form.add(x1);
        x2 = new HiddenField<>("x2", Model.of(10000));
        form.add(x2);
        y1 = new HiddenField<>("y1", Model.of(0));
        form.add(y1);
        y2 = new HiddenField<>("y2", Model.of(10000));
        form.add(y2);

        AjaxSubmitLink submit = new IndicatingAjaxSubmitLink2("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget art, Form form) {
                try {
                    final FileUpload uploadedFile = newImage.getFileUpload();

                    String contentType = uploadedFile.getContentType();
                    if(!contentType.split("/")[0].equals("image")){
                        error(getString("selectImageErrorMessage"));
                        return;
                    }
                    try {
                        Integer x1int= (Integer.parseInt((String)x1.getDefaultModel().getObject()));
                        Integer x2int= (Integer.parseInt((String)x2.getDefaultModel().getObject()));
                        Integer y1int= (Integer.parseInt((String)y1.getDefaultModel().getObject()));
                        Integer y2int= (Integer.parseInt((String)y2.getDefaultModel().getObject()));
                        uf.setPicture(uploadedFile.getInputStream(),x1int,y1int,x2int,y2int);
                    } catch (Exception ex) {
                        error(getString("UploadFileError"));
                        Logger.getLogger(PictureSetting.class).error("error while uploading picture",ex);
                        return;
                    }
                    Result save = uf.save();
                    save.displayInWicket(this);
                    if(save.result){
                        success(getString("profilePictureChanged"));
                    }
                    form.addOrReplace(new Image("image", ImageManagement.getUserImageResourceReferece(),
                            ImageManagement.getUserImageParameter(uf, ImageManagement.ImageSize.large)));
                } finally {
                    art.add(form);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(form);
            }
            
        };
        form.add(submit);
    }
}
