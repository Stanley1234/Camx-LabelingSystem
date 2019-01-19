package com.camx.labeling.impl;

import com.camx.labeling.BasicImagesFileDao;
import com.camx.labeling.exception.ImageExistException;
import com.camx.labeling.exception.ImageNotExistException;
import com.camx.labeling.entity.constant.ImageQuality;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;

@Repository
public class DirectoryBasedImagesDao implements BasicImagesFileDao {

   private static final Logger LOGGER = LoggerFactory.getLogger(DirectoryBasedImagesDao.class);

   private static final String BASE_DIRECTORY = DirectoryBasedImagesDao.class.getProtectionDomain().getCodeSource().getLocation().getPath();
   private static final String LABELED_DIRECTORY = BASE_DIRECTORY + "db/labeled/";
   private static final String UNLABELED_DIRECTORY = BASE_DIRECTORY + "db/unlabeled/";

   private File unlabeledDirectory;
   private File goodLabeledDirectory;
   private File badLabeledDirectory;
   private File unknownLabeledDirectory;

   public DirectoryBasedImagesDao() {
      createDirectories();
   }

   private void createDirectories() {
      boolean result;

      LOGGER.info("Base directory: {}", BASE_DIRECTORY);

      unlabeledDirectory = new File(UNLABELED_DIRECTORY);
      result = unlabeledDirectory.mkdirs();
      if (!result) {
         LOGGER.warn("Unlabeled directory already existed");
      }

      goodLabeledDirectory = new File(LABELED_DIRECTORY + ImageQuality.GOOD.getDirectoryName());
      result = goodLabeledDirectory.mkdirs();
      if (!result) {
         LOGGER.warn("good labeled directory already existed");
      }

      badLabeledDirectory = new File(LABELED_DIRECTORY + ImageQuality.BAD.getDirectoryName());
      result = badLabeledDirectory.mkdirs();
      if (!result) {
         LOGGER.warn("bad labeled directory already existed");
      }

      unknownLabeledDirectory = new File(LABELED_DIRECTORY + ImageQuality.UNKNOWN.getDirectoryName());
      result = unknownLabeledDirectory.mkdirs();
      if (!result) {
         LOGGER.warn("unknown labeled directory already existed");
      }
   }

   @Override
   public void createNewImage (String imageName, byte[] buffer) throws IOException, ImageExistException {
      if (imageName == null || buffer == null) {
         throw new IllegalArgumentException("Parameters cannot be null");
      }

      LOGGER.info("Create new image on disk: {}", imageName);

      File fileHandler = new File(UNLABELED_DIRECTORY + imageName);
      if (fileHandler.exists()) {
         throw new ImageExistException("Image exists");
      }

      FileUtils.writeByteArrayToFile(fileHandler, buffer, false);
   }

   @Override
   public void moveImage (String imageName, ImageQuality quality, boolean unlabelToLabel) throws IOException,
      ImageNotExistException, ImageExistException {
      if (imageName == null) {
         throw new IllegalArgumentException("Parameters cannot be null");
      }

      String src, dest;
      if (unlabelToLabel) {
         src = UNLABELED_DIRECTORY;
         dest = LABELED_DIRECTORY + quality.getDirectoryName();
      } else {
         src = LABELED_DIRECTORY + quality.getDirectoryName();
         dest = UNLABELED_DIRECTORY;
      }

      File srcHandler = new File(src + imageName);
      if (!srcHandler.exists()) {
         throw new ImageNotExistException(
            String.format("Cannot find source image: %s. It may have been labeled or never exists.", imageName)
         );
      }

      File destHandler = new File(dest + imageName);
      if (destHandler.exists()) {
         throw new ImageExistException(
            String.format("Same Image: %s are found in both labeled and unlabeled directories.", imageName)
         );
      }

      FileUtils.moveFile(srcHandler, destHandler);
   }

   @Override
   public byte[] getUnlabeledImageDataByName (String imageName) throws ImageNotExistException, IOException {
      if (imageName == null) {
         throw new IllegalArgumentException("Image name cannot be null");
      }

      File fileHandler = new File(UNLABELED_DIRECTORY + imageName);
      if (!fileHandler.exists()) {
         throw new ImageNotExistException(
            String.format("Image: %s does not exist", imageName)
         );
      }

      LOGGER.info("Read bytes array from file {}", imageName);

      return FileUtils.readFileToByteArray(fileHandler);
   }

   @Override
   public void removeAllImages () {

      try {
         // delete all directories
         FileUtils.deleteDirectory(unlabeledDirectory);
         FileUtils.deleteDirectory(goodLabeledDirectory);
         FileUtils.deleteDirectory(badLabeledDirectory);
         FileUtils.deleteDirectory(unknownLabeledDirectory);

      } catch (IOException e) {

         // We don't throw exception here because if such directory does not exist
         // then we will create it later on
         // For purpose of debugging, we print the stack trace
         e.printStackTrace();
      }

      // create all empty directories
      createDirectories();
   }

}
