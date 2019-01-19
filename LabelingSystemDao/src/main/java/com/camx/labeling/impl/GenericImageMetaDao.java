package com.camx.labeling.impl;

import com.camx.labeling.BasicImageMetaDao;
import com.camx.labeling.entity.constant.ImageType;
import com.camx.labeling.exception.ImageNotExistException;
import com.camx.labeling.entity.ImageMeta;
import com.camx.labeling.entity.ImageMetaRepository;
import com.camx.labeling.lib.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class GenericImageMetaDao implements BasicImageMetaDao {

   private static final Logger LOGGER = LoggerFactory.getLogger(GenericImageMetaDao.class);

   private final ImageMetaRepository repository;

   @Autowired
   public GenericImageMetaDao (ImageMetaRepository repository) {
      this.repository = repository;
   }

   @Override
   public void insertImageMeta (ImageMeta imageMeta) {
      if (imageMeta == null) {
         throw new IllegalArgumentException("Parameter cannot be null");
      }

      LOGGER.info("Insert {} at time {}", imageMeta.getImageName(), imageMeta.getCreateDate());
      repository.save(imageMeta);
   }

   @Override
   public void updateImageMeta (ImageMeta newImageMeta) throws ImageNotExistException {
      if (newImageMeta == null) {
         throw new IllegalArgumentException("Parameter cannot be null");
      }

      LOGGER.info("Update {}", newImageMeta.getImageName());
      if (!checkImageExistByName(newImageMeta.getImageName())) {
         throw new ImageNotExistException("Cannot update an image that does not exist");
      }

      repository.save(newImageMeta);
      LOGGER.info("Update completes: unlabeled {}, labeled {}",
         countImageByType(ImageType.UNLABELED),
         countImageByType(ImageType.LABELED));
   }

   @Override
   public void clearAllImageMeta () {
      repository.deleteAll();
   }

   @Override
   public boolean checkImageExistByName (String imageName) {
      if (imageName == null) {
         throw new IllegalArgumentException("Parameter cannot be null");
      }

      LOGGER.info("Check existence of {}", imageName);
     
      return repository.findImageMetaByImageName(imageName) != null;
   }

   @Override
   public int countImageByType(ImageType imageType) {
      return repository.countByImageType(imageType);
   }

   @Override
   public ImageMeta getRandomImageMetaByType (ImageType type) {
      if (type == null) {
         throw new IllegalArgumentException("Parameter cannot be null");
      }

      final int num = countImageByType(type);
      if (num == 0) {
         return null;
      }

      final int randIndex = NumberUtils.generateRandomNumber(0, num - 1);
      Slice<ImageMeta> imageMetaSlice = repository.findDistinctImageMetaByImageType(type,
         PageRequest.of(randIndex, 1));

      if (!imageMetaSlice.hasContent()) {
         return null;
      }

      return imageMetaSlice.getContent().get(0);
   }

   @Override
   public List<ImageMeta> getRandomListOfImageMetaByType (ImageType type, int size) {
      if (type == null) {
         throw new IllegalArgumentException("Parameter cannot be null");
      }

      final int num = countImageByType(type);
      List<ImageMeta> result = new ArrayList<>();

      if (num == 0) {
         return result;
      }

      if (num <= size) {
         result.addAll(repository.findAllByImageType(type));
         return result;
      }

      // generate random indices
      int low, high;
      if (num <= 100) {
         low = 0;
         high = num - 1;
      } else {
         low = NumberUtils.generateRandomNumber(0, num - 100);
         high = low + 100;
      }
      List<Integer> randIndices = NumberUtils.generateRandomNumbers(low, high, size);

      // fetch the corresponding imageMeta from the database
      for (int index : randIndices) {
         Slice<ImageMeta> imageMetaSlice =
            repository.findDistinctImageMetaByImageType(type, PageRequest.of(index, 1));

         if (imageMetaSlice.hasContent()) {
            result.add(imageMetaSlice.getContent().get(0));
         }
      }

      return result;
   }

   @Override
   public List<ImageMeta> getListOfImageNameByType (ImageType type, int size) {
      if (type == null) {
         throw new IllegalArgumentException("Parameter cannot be null");
      }

      Slice<ImageMeta> imageMetaSlice =
         repository.findDistinctImageMetaByImageType(type, PageRequest.of(0, size));
      return imageMetaSlice.getContent();
   }

   @Override
   public Slice<ImageMeta> getListOfImageNameByTypeWithInsertionOrder (ImageType type, Pageable pageable) {
      if (type == null || pageable == null) {
         throw new IllegalArgumentException("Parameter cannot be null");
      }
      return repository.findDistinctImageMetaByImageTypeOrderByCreateDateAsc(type, pageable);
   }
}
