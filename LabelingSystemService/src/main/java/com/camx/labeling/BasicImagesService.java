package com.camx.labeling;

import com.camx.labeling.entity.constant.ImageQuality;
import com.camx.labeling.exception.ImageExistException;
import com.camx.labeling.exception.ImageNotExistException;
import com.camx.labeling.exception.DaoNotConsistentException;

import java.io.IOException;

public interface BasicImagesService {

   /**
    * Create a new image file with {@param buffer} on the server's disk.
    *
    * @param imageName The file name associated with the image to be created
    * @param buffer    Binary of the image
    * @throws IOException
    *         When the image cannot be created on the disk, an instance of IOException
    *         will be thrown
    * @throws ImageExistException
    *         If there is an image with {@param imageName} found on disk,
    *         it cannot be overridden and the new image cannot be created.
    * */
   void uploadImage(String imageName, byte[] buffer) throws IOException, ImageExistException, DaoNotConsistentException;

   /**
    * Randomly retrieve an unlabeled image.
    *
    * @return Return an two elements of Object constant. The first element
    *         is of constant ImageMeta. The second element is a bytes array of
    *         the image contents
    * @throws ImageNotExistException
    *         Thrown If there are no unlabeled image exist in the directory
    * @throws DaoNotConsistentException
    *
    * */
   Object[] retrieveUnlabeledImageRandomly() throws ImageNotExistException, DaoNotConsistentException;

   /**
    * Randomly retrieve {@param num} images without duplicate. If there are not adequate images available,
    * return all images available in the database.
    *
    * @throws ImageNotExistException
    *         Thrown if there are no unlabeled image exist in the directory
    * @throws DaoNotConsistentException
    *
    * */
   Object[] retrieveUnlabeledImageRandomly(int num) throws ImageNotExistException, DaoNotConsistentException;

   /**
    *
    * @throws ImageNotExistException
    * @throws DaoNotConsistentException
    * */
   byte[] retrieveUnlabeledImageByName(String name) throws ImageNotExistException, DaoNotConsistentException;

   /**
    * Remove all image metas stored in the database
    */
   void removeAllImages();

   /**
    * Count number of unlabeled images in the database
    *
    * */
   int countUnlabeledImages();

   /**
    * Count number of labeled images, regardless of quality, in the database
    *
    * */
   int countLabeledImages();


   /**
    * Label the image with {@param imageName} as {@param quality} in the database
    *
    * @throws IOException
    *
    * @throws ImageExistException
    *
    * @throws ImageNotExistException
    *
    * @throws DaoNotConsistentException
    * */
   void labelImage(String imageName, ImageQuality quality) throws IOException, ImageExistException, ImageNotExistException, DaoNotConsistentException;
}
