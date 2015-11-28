/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.resourece;

import java.io.File;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.IResource.Attributes;
import org.apache.wicket.request.resource.ResourceStreamResource;
import org.apache.wicket.util.resource.FileResourceStream;

/**
 *
 * @author Hossein
 */
public class FolderContentResource implements IResource {

    private final File rootFolder;

    public FolderContentResource(File rootFolder) {
        this.rootFolder = rootFolder;
    }

    @Override
    public void respond(Attributes attributes) {
        PageParameters parameters = attributes.getParameters();
        String fileName = parameters.get("file").toString();
        File file = new File(rootFolder, fileName);
        FileResourceStream fileResourceStream = new FileResourceStream(file);
        ResourceStreamResource resource = new ResourceStreamResource(fileResourceStream);
        resource.respond(attributes);
    }
}