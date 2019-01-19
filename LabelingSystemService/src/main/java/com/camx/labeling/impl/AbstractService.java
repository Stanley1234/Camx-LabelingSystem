package com.camx.labeling.impl;

import com.camx.labeling.BasicImageMetaDao;
import com.camx.labeling.BasicImagesFileDao;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class AbstractService {

   protected final BasicImagesFileDao imagesFileDao;
   protected final BasicImageMetaDao imagesMetaDao;

   protected static final ReentrantReadWriteLock globalServiceMutex = new ReentrantReadWriteLock();
   protected static final Lock readLock = globalServiceMutex.readLock();
   protected static final Lock writeLock = globalServiceMutex.writeLock();

   protected AbstractService(BasicImagesFileDao fileDao, BasicImageMetaDao metaDao) {
      imagesFileDao = fileDao;
      imagesMetaDao = metaDao;
   }
}
