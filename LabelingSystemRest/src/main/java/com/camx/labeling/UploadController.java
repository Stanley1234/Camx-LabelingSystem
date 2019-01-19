package com.camx.labeling;

import com.camx.labeling.exception.ImageExistException;
import com.camx.labeling.lib.ImageUtils;
import com.camx.labeling.constant.FieldName;
import com.camx.labeling.type.RequestImageBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UploadController {

   private static final Logger LOGGER = LoggerFactory.getLogger(UploadController.class);

   private final BasicImagesService imagesService;

   @Autowired
   public UploadController (BasicImagesService imagesService) {
      this.imagesService = imagesService;
   }

   @RequestMapping(
      value = "/upload",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
   )
   @CrossOrigin(origins = "*")
   public ResponseEntity uploadImage(@RequestBody RequestImageBody imageBody) {
      LOGGER.info("Receive post request for uploading image");

      Map<String, String> messageBody = new HashMap<>();
      HttpStatus status;

      try {
         byte[] buffer = ImageUtils.convertBase64ImageToByteArray(imageBody.getEncodedImage());

         imagesService.uploadImage(imageBody.getName(), buffer);

         status = HttpStatus.valueOf(200);
         messageBody.put(FieldName.MESSAGE_FIELD, "Upload Image Successfully");

      } catch (ImageExistException e) {

         status = HttpStatus.valueOf(400);
         messageBody.put(FieldName.ERROR_FIELD, String.format("Cannot upload Image: %s", e.getMessage()));

      } catch (Exception e) {
         // This may be caused by
         // IOE exception
         // DaoNotConsistentException
         // ...

         status = HttpStatus.valueOf(500);
         messageBody.put(FieldName.ERROR_FIELD, String.format("Cannot upload Image: %s ", e.getMessage()));

      }

      return ResponseEntity
         .status(status)
         .body(messageBody);
   }

}
