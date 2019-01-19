package com.camx.labeling;

import com.camx.labeling.entity.constant.ImageQuality;
import com.camx.labeling.exception.ImageExistException;
import com.camx.labeling.exception.ImageNotExistException;

import java.io.IOException;

public interface BasicImagesFileDao {

   void createNewImage(String imageName, byte[] buffer) throws IOException, ImageExistException;

   void moveImage(String imageName, ImageQuality quality, boolean unlabelToLabel) throws IOException, ImageExistException, ImageNotExistException;

   byte[] getUnlabeledImageDataByName (String imageName) throws ImageNotExistException, IOException;

   void removeAllImages();
}