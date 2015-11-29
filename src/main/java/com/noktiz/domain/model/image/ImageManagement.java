/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.domain.model.image;

import com.noktiz.domain.entity.User;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.ui.web.Application;
import com.noktiz.ui.web.resourece.FolderContentResource;
import org.apache.log4j.Logger;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.request.resource.SharedResourceReference;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Hossein
 */
public class ImageManagement {

    final private static Properties settings;
    final private static File userFolder;

    static {
        settings = new Properties();
        try {
            settings.load(ImageManagement.class.getResourceAsStream("ImageManagement.properties"));
        } catch (IOException ex) {
            Logger.getLogger(ImageManagement.class).fatal("can not load ImageManagement.properties");
            throw new RuntimeException("can not load ImageManagement.properties");
        }
        String userStr = settings.getProperty("user");
        userFolder = new File(userStr);
        if (!userFolder.exists()) {
            userFolder.mkdirs();
        } else if (!userFolder.isDirectory()) {
            Logger.getLogger(ImageManagement.class).fatal(userStr + " is not directory");
            throw new RuntimeException(userStr + " is not directory");
        }
    }

    public static File loadImage(Class<?> type, String id) {
        if (type.equals(User.class)) {
            File file = new File(userFolder, id);
            if (file.exists()) {
                return file;
            }
            return null;
        }
        return null;
    }

    public static Boolean saveNewImage(Class<?> type, String id, BufferedImage[] bufferedImages,ImageSize[] imageSizes) {
        if (type.equals(User.class)) {
            for (int i=0;i< bufferedImages.length;i++) {
                BufferedImage image=bufferedImages[i];
                String idSize= getUserImageId(id, imageSizes[i]);
                File dist = new File(userFolder, idSize);
                if (dist.exists()) {
                    return false;
                }
                try {
                    dist.createNewFile();
                    FileOutputStream fout = new FileOutputStream(dist);
                    ImageIO.write(image, "jpg", fout);
                    fout.flush();
                    fout.close();
                } catch (IOException ex) {
                    Logger.getLogger(ImageManagement.class).error(ex);
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static void loadResourece(Application application) {
        application.getSharedResources().add(getImageBaseAddress(), new FolderContentResource(userFolder));
        application.mountResource("/" + getImageBaseAddress(), new SharedResourceReference(getImageBaseAddress()));
    }

    public static String getImageBaseAddress() {
        return "userImage";
    }

    public static ResourceReference getUserImageResourceReferece() {
        return new SharedResourceReference(getImageBaseAddress());
    }

    public static PageParameters getUserImageParameter(UserFacade uf,ImageSize imageSize) {
        PageParameters p = new PageParameters();
        if(uf.isAnonymous()){
            p.set("file", "friend");
        }
        else if (uf.getPicture() != null) {
            p.set("file", getUserImageId(uf.getPicture(),imageSize));
        } else {
            p.set("file", "null");
        }
        return p;
    }

    private static String getUserImageId(String id, ImageSize imageSize) {
        return id+"_"+imageSize.toString();
    }

//        ./userImage?file=aGcxEbqAZqIYfqP_large
    public static String getUserImageUrl(User uf,ImageSize imageSize){
        return "./"+getImageBaseAddress()+"?file="+getUserImageId(uf.getPictureId(),imageSize);
    }

    public enum ImageSize{
        small,medium,large
    }
}
