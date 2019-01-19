package com.camx.labeling;

import com.camx.labeling.exception.ImageNotExistException;
import com.camx.labeling.constant.FieldName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class NameListDownloadController {

   private static final Logger LOGGER = LoggerFactory.getLogger(NameListDownloadController.class);

   private final BasicNameListService nameListService;

   @Autowired
   public NameListDownloadController (BasicNameListService basicNameListService) {
      this.nameListService = basicNameListService;
   }

   @RequestMapping(
      path = "/download/namelist/{num}",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE
   )
   @CrossOrigin(origins = "*")
   public ResponseEntity downloadNameListWithSize(@PathVariable("num") int requestNum) {
      LOGGER.info("Receive get request for downloading name list with size {}", requestNum);

      Map<String, Object> messageBody = new HashMap<>();
      HttpStatus httpStatus;

      try {
         List<String> names = nameListService.retrieveUnlabeledImageNames(requestNum);

         messageBody.put(FieldName.NAMES_FIELD, names);
         httpStatus = HttpStatus.valueOf(200);

      } catch (ImageNotExistException e) {

         messageBody.put(FieldName.ERROR_FIELD, e.getMessage());
         httpStatus = HttpStatus.valueOf(400);

      } catch (Exception e) {

         messageBody.put(FieldName.ERROR_FIELD, e.getMessage());
         httpStatus = HttpStatus.valueOf(500);

      }

      return ResponseEntity
         .status(httpStatus)
         .body(messageBody);
   }

   @RequestMapping(
      path = "/download/namelist",
      method = RequestMethod.GET
   )
   public ResponseEntity queryNameListWithRangeAndOrder(@RequestParam("start") int start,
                                                        @RequestParam("end") int end) {
      LOGGER.info("Receive GET request for download namelist with order from {} to {}", start, end);

      Map<String, Object> messageBody = new HashMap<>();
      HttpStatus httpStatus;

      try {
         List<String> names = nameListService.retrieveUnlabeledImageNamesByInsertionOrder(start, end);

         messageBody.put(FieldName.NAMES_FIELD, names);
         httpStatus = HttpStatus.valueOf(200);

      } catch (ImageNotExistException | IllegalArgumentException e) {

         messageBody.put(FieldName.ERROR_FIELD, e.getMessage());
         httpStatus = HttpStatus.valueOf(400);

      } catch (Exception e) {

         messageBody.put(FieldName.ERROR_FIELD, e.getMessage());
         httpStatus = HttpStatus.valueOf(500);

      }

      return ResponseEntity
         .status(httpStatus)
         .body(messageBody);
   }
}
