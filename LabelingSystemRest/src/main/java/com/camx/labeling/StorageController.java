package com.camx.labeling;

import com.camx.labeling.constant.FieldName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class StorageController {

   private static final Logger LOGGER = LoggerFactory.getLogger(StorageController.class);

   private final BasicImagesService imagesService;

   @Autowired
   public StorageController (BasicImagesService imagesService) {
      this.imagesService = imagesService;
   }

   @RequestMapping(
      path = "/clear",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE
   )
   public ResponseEntity clearAll() {
      LOGGER.info("Clear all information from daos");

      Map<String, String> messageBody = new HashMap<>();

      imagesService.removeAllImages();

      messageBody.put(FieldName.MESSAGE_FIELD, "Remove successfully");

      return ResponseEntity
         .ok()
         .body(messageBody);
   }
}
