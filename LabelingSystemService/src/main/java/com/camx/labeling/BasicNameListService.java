package com.camx.labeling;

import com.camx.labeling.exception.ImageNotExistException;

import java.util.List;

public interface BasicNameListService {
   /**
    * Retrieve {@param num} image names from the database
    * @throws  ImageNotExistException
    * */
   List<String> retrieveUnlabeledImageNames(int num) throws ImageNotExistException;

   /**
    * Retrieve image names from the database according to insertion order
    * */
   List<String> retrieveUnlabeledImageNamesByInsertionOrder(int start, int end) throws ImageNotExistException;
}
