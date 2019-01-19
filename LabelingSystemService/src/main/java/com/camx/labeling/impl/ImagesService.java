package com.camx.labeling.impl;

import com.camx.labeling.BasicImagesService;
import com.camx.labeling.BasicImageMetaDao;
import com.camx.labeling.BasicImagesFileDao;
import com.camx.labeling.entity.constant.ImageQuality;
import com.camx.labeling.entity.constant.ImageType;
import com.camx.labeling.exception.ImageExistException;
import com.camx.labeling.exception.ImageNotExistException;
import com.camx.labeling.entity.ImageMeta;
import com.camx.labeling.exception.DaoNotConsistentException;
import com.camx.labeling.lib.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImagesService extends AbstractService implements BasicImagesService {

   @Autowired
   public ImagesService (BasicImagesFileDao imagesFileDao,
                         BasicImageMetaDao imagesMetaDao) {
      super(imagesFileDao, imagesMetaDao);
   }

   @Override
   public void uploadImage (String imageName, byte[] buffer) throws ImageExistException, DaoNotConsistentException {
      writeLock.lock();

      if (imageName == null || buffer == null) {
         writeLock.unlock();
         throw new IllegalArgumentException("Parameters cannot be null");
      }

      ImageMeta imageMeta = new ImageMeta();
      imageMeta.setImageName(imageName);
      imageMeta.setImageType(ImageType.UNLABELED);
      imageMeta.setCreateDate(TimeUtils.getCurrentTimestamp());

      if (imagesMetaDao.checkImageExistByName(imageName)) {
         writeLock.unlock();
         throw new ImageExistException("Image exists already");
      }

      try {
         imagesMetaDao.insertImageMeta(imageMeta);
         imagesFileDao.createNewImage(imageName, buffer);
      } catch (Exception e) {
         writeLock.unlock();
         throw new DaoNotConsistentException(String.format("Daos are not consistent: %s", e.getMessage()));
      }

      writeLock.unlock();
   }

   @Override
   public Object[] retrieveUnlabeledImageRandomly () throws ImageNotExistException, DaoNotConsistentException {
      readLock.lock();

      checkIfUnlabeledImagesAreAvailable();

      byte[] image;
      ImageMeta imageMeta;

      try {
         imageMeta = imagesMetaDao.getRandomImageMetaByType(ImageType.UNLABELED);
         image = imagesFileDao.getUnlabeledImageDataByName(imageMeta.getImageName());

      } catch (Exception e) {
         readLock.unlock();
         throw new DaoNotConsistentException(String.format("Daos are not consistent: %s", e.getMessage()));
      }

      readLock.unlock();
      return new Object[] {imageMeta, image};
   }

   @Override
   public Object[] retrieveUnlabeledImageRandomly (int size) throws ImageNotExistException, DaoNotConsistentException {
      readLock.lock();

      checkIfUnlabeledImagesAreAvailable();

      List<ImageMeta> imageMetas;
      List<byte[]> imageBuffers = new ArrayList<>();

      imageMetas = imagesMetaDao.getRandomListOfImageMetaByType(ImageType.UNLABELED, size);
      if (imageMetas.size() == 0) {
         readLock.unlock();
         throw new ImageNotExistException("No unlabeled images are available");
      }

      try {

         for (ImageMeta imageMeta : imageMetas) {
            String name = imageMeta.getImageName();
            imageBuffers.add(imagesFileDao.getUnlabeledImageDataByName(name));
         }

      } catch (Exception e) {
         readLock.unlock();
         throw new DaoNotConsistentException(String.format("Daos are not consistent: %s", e.getMessage()));
      }

      readLock.unlock();
      return new Object[] {imageMetas, imageBuffers};
   }

   @Override
   public byte[] retrieveUnlabeledImageByName (String name) throws ImageNotExistException, DaoNotConsistentException {
      readLock.lock();

      if (!imagesMetaDao.checkImageExistByName(name)) {
         readLock.unlock();
         throw new ImageNotExistException(String.format("Image %s does not exist", name));
      }

      byte[] buffer;

      try {
         buffer = imagesFileDao.getUnlabeledImageDataByName(name);
      } catch (IOException e) {
         readLock.unlock();
         throw new DaoNotConsistentException(e.getMessage());
      }

      if (buffer == null) {
         readLock.unlock();
         throw new DaoNotConsistentException("Buffer is null");
      }

      readLock.unlock();
      return buffer;
   }
   
   @Override
   public void removeAllImages () {
      writeLock.lock();

      imagesMetaDao.clearAllImageMeta();
      imagesFileDao.removeAllImages();

      writeLock.unlock();
   }

   @Override
   public int countUnlabeledImages () {
      readLock.lock();

      try{
         return imagesMetaDao.countImageByType(ImageType.UNLABELED);
      } finally {
         readLock.unlock();
      }
   }

   @Override
   public int countLabeledImages () {
      readLock.lock();

      try {
         return imagesMetaDao.countImageByType(ImageType.LABELED);
      } finally {
         readLock.unlock();
      }
   }

   @Override
   public void labelImage (String imageName, ImageQuality quality) throws IOException, ImageExistException, DaoNotConsistentException, ImageNotExistException {
      writeLock.lock();

      if (imageName == null || quality == null) {
         writeLock.unlock();
         throw new IllegalArgumentException("Parameters cannot be null");
      }

      ImageMeta imageMeta = new ImageMeta();
      imageMeta.setImageName(imageName);
      imageMeta.setImageType(ImageType.LABELED);
      imageMeta.setImageQuality(quality);

      imagesMetaDao.updateImageMeta(imageMeta);

      try {
         imagesFileDao.moveImage(imageName, quality, true);
      } catch (ImageNotExistException e) {
         throw new DaoNotConsistentException(e.getMessage());
      }


      writeLock.unlock();
   }


   /**
    * Throw exceptions when there are no unlabeled images
    * */
   private void checkIfUnlabeledImagesAreAvailable() throws ImageNotExistException {
      final int num = imagesMetaDao.countImageByType(ImageType.UNLABELED);

      if (num == 0) {
         throw new ImageNotExistException("No unlabeled image is available");
      }
   }

}
