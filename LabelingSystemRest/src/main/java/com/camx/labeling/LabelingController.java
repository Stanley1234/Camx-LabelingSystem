package com.camx.labeling;

import com.camx.labeling.exception.ImageNotExistException;
import com.camx.labeling.entity.constant.ImageQuality;
import com.camx.labeling.constant.FieldName;
import com.camx.labeling.impl.ImagesService;
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
public class LabelingController {

   private static final Logger LOGGER = LoggerFactory.getLogger(LabelingController.class);

   private final ImagesService imagesService;

   @Autowired
   public LabelingController (ImagesService imagesService) {
      this.imagesService = imagesService;
   }

   @RequestMapping(
      path = "/count/unlabeled",
      method = RequestMethod.GET
   )
   @CrossOrigin(origins = "*")
   public ResponseEntity countUnlabeled() {
      LOGGER.info("Count unlabeled");

      Map<String, Object> messageBody = new HashMap<>();
      int unlabeledNum = imagesService.countUnlabeledImages();

      LOGGER.info("Count unlabeled: {}", unlabeledNum);

      messageBody.put(FieldName.NUMBER_FIELD, imagesService.countUnlabeledImages());

      return ResponseEntity
         .ok()
         .body(messageBody);
   }

   @RequestMapping(
      path = "/count/labeled",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE
   )
   @CrossOrigin(origins = "*")
   public ResponseEntity countLabeled() {
      LOGGER.info("Count labeled");

      Map<String, Object> messageBody = new HashMap<>();
      int labeledNum = imagesService.countLabeledImages();

      LOGGER.info("Count labeled: {}", labeledNum);

      messageBody.put(FieldName.NUMBER_FIELD, labeledNum);

      return ResponseEntity
         .ok()
         .body(messageBody);
   }

   @RequestMapping(
      path = "/label/{name}/{quality}",
      method = RequestMethod.PUT,
      produces = MediaType.APPLICATION_JSON_VALUE
   )
   public ResponseEntity labelImage(@PathVariable(name = "name") String name,
                                                         @PathVariable(name = "quality") String quality) {
      LOGGER.info("Receive Get request for com.camx.labeling.");
      LOGGER.info("Label {} as {}", name, quality);

      Map<String, String> messageBody = new HashMap<>();
      HttpStatus status;

      try {
         imagesService.labelImage(name, ImageQuality.valueOf(quality));
         messageBody.put(FieldName.MESSAGE_FIELD, "Succeed");
         status = HttpStatus.valueOf(200);

      } catch (ImageNotExistException e) {

         status = HttpStatus.valueOf(400);
         messageBody.put(FieldName.ERROR_FIELD, String.format("Error when com.camx.labeling: %s", e.getMessage()));

      } catch (Exception e) {
         // This may be triggered by
         // IOException
         // ImageExistException

         status = HttpStatus.valueOf(500);
         messageBody.put(FieldName.ERROR_FIELD, String.format("Error when com.camx.labeling: %s", e.getMessage()));
      }

      return ResponseEntity
         .status(status)
         .body(messageBody);
   }
}
