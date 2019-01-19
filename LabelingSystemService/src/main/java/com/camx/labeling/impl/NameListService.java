package com.camx.labeling.impl;

import com.camx.labeling.BasicImageMetaDao;
import com.camx.labeling.BasicImagesFileDao;
import com.camx.labeling.entity.constant.ImageType;
import com.camx.labeling.exception.ImageNotExistException;
import com.camx.labeling.entity.ImageMeta;
import com.camx.labeling.BasicNameListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NameListService extends AbstractService implements BasicNameListService {

   @Autowired
   public NameListService (BasicImagesFileDao imagesFileDao,
                           BasicImageMetaDao imagesMetaDao) {
      super(imagesFileDao, imagesMetaDao);
   }

   @Override
   public List<String> retrieveUnlabeledImageNames (int num) throws ImageNotExistException {
      readLock.lock();

      List<ImageMeta> imageMetas = imagesMetaDao.getListOfImageNameByType(ImageType.UNLABELED, num);
      List<String> names = new ArrayList<>();

      if (imageMetas.size() == 0) {
         readLock.unlock();
         throw new ImageNotExistException("No unlabeled images are available");
      }

      for (ImageMeta imageMeta : imageMetas) {
         names.add(imageMeta.getImageName());
      }

      readLock.unlock();
      return names;
   }

   @Override
   public List<String> retrieveUnlabeledImageNamesByInsertionOrder (int start, int end) throws ImageNotExistException {
      readLock.lock();

      if (end < start || start < 0) {
         readLock.unlock();
         throw new IllegalArgumentException("Start or end is illegal");
      }
      final int unlabeledNum = imagesMetaDao.countImageByType(ImageType.UNLABELED);

      if (unlabeledNum == 0) {
         readLock.unlock();
         throw new ImageNotExistException("No unlabeled images are available");
      }

      final int size = 200;
      final int startPageIndex = start / size;
      final int endPageIndex = Math.min((unlabeledNum - 1) / size, end / size);

      List<String> names = new ArrayList<>();
      Pageable pageable = PageRequest.of(startPageIndex, size);

      for (int i = startPageIndex;i <= endPageIndex;i ++) {
         Slice<ImageMeta> imageMetaSlice = imagesMetaDao.getListOfImageNameByTypeWithInsertionOrder(
            ImageType.UNLABELED, pageable);

         List<ImageMeta> imageMetas = imageMetaSlice.getContent();
         for (int j = start % size;j < imageMetas.size() && start <= end;j ++, start ++) {
            names.add(imageMetas.get(j).getImageName());
         }

         pageable = imageMetaSlice.nextPageable();
      }

      readLock.unlock();
      return names;
   }
}
