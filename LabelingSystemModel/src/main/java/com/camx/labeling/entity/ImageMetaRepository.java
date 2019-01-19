package com.camx.labeling.entity;

import com.camx.labeling.entity.constant.ImageType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ImageMetaRepository extends PagingAndSortingRepository<ImageMeta, Integer> {

   ImageMeta findImageMetaByImageName(String name);

   Slice<ImageMeta> findDistinctImageMetaByImageType(ImageType type, Pageable pageable);

   List<ImageMeta> findAllByImageType(ImageType type);

   Slice<ImageMeta> findDistinctImageMetaByImageTypeOrderByCreateDateAsc(ImageType type, Pageable pageable);

   int countByImageType(ImageType type);

}
