package com.camx.labeling;

import com.camx.labeling.entity.constant.ImageType;
import com.camx.labeling.exception.ImageNotExistException;
import com.camx.labeling.entity.ImageMeta;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface BasicImageMetaDao {

   void insertImageMeta(ImageMeta imageMeta);

   void updateImageMeta(ImageMeta imageMeta) throws ImageNotExistException;

   void clearAllImageMeta();

   boolean checkImageExistByName(String imageName);

   int countImageByType(ImageType type);

   ImageMeta getRandomImageMetaByType(ImageType type);

   /**
    * The parameter {@param size} cannot be too large, i.e. <= 100
    * */
   List<ImageMeta> getRandomListOfImageMetaByType(ImageType type, int size);

   /**
    * @return Return a result set of size {@param size}. However, if the {@param size}
    *         exceeds the actual number of records in the database, it will return an
    *         empty set
    * */
   List<ImageMeta> getListOfImageNameByType(ImageType type, int size);

   Slice<ImageMeta> getListOfImageNameByTypeWithInsertionOrder(ImageType type, Pageable pageable);
}
