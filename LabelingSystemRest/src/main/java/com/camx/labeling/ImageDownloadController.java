package com.camx.labeling;

import com.camx.labeling.constant.FieldName;
import com.camx.labeling.exception.ImageNotExistException;
import com.camx.labeling.entity.ImageMeta;
import com.camx.labeling.type.ResponseImageBody;
import com.camx.labeling.lib.ImageUtils;
import com.camx.labeling.exception.DaoNotConsistentException;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class ImageDownloadController {

   private static final Logger LOGGER = LoggerFactory.getLogger(ImageDownloadController.class);

   private static final int MAX_DOWNLOAD_NUM = 100;

   private final BasicImagesService imagesService;

   @Autowired
   public ImageDownloadController (BasicImagesService imagesService) {
      this.imagesService = imagesService;
   }

   @RequestMapping(
      path = "/download",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE
   )
   @CrossOrigin(origins = "*")
   public ResponseEntity downloadOneUnlabeledImage() {
      LOGGER.info("Receive get request for downloading an image");

      Map<String, Object> messageBody = new HashMap<>();
      HttpStatus httpStatus;

      try {
         Object[] image = imagesService.retrieveUnlabeledImageRandomly();

         assert image[0] instanceof ImageMeta;
         assert image[1] instanceof byte[];
         
         final String name = ((ImageMeta)image[0]).getImageName();
         final String encodedImage = ImageUtils.convertByteArrayImageToBase64((byte[]) image[1]);
         
         ResponseImageBody responseImageBody =
            new ResponseImageBody(name, encodedImage);
         
         messageBody.put(FieldName.IMAGE_FIELD, responseImageBody);
         httpStatus = HttpStatus.valueOf(200);

      } catch (ImageNotExistException e) {

         messageBody.put(FieldName.ERROR_FIELD, e.getMessage());
         httpStatus = HttpStatus.valueOf(400);

      } catch (Exception e) {

         // this catches a fatal exception 'DaoNotConsistentException'
         messageBody.put(FieldName.ERROR_FIELD, e.getMessage());
         httpStatus = HttpStatus.valueOf(500);

      }

      return ResponseEntity
         .status(httpStatus)
         .body(messageBody);
   }

   @RequestMapping(
      path = "/download/{num}",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE
   )
   @CrossOrigin(origins = "*")
   @SuppressWarnings(value = "unchecked")
   public ResponseEntity downloadAnyUnlabeledImages(@PathVariable("num") int requestNum) {
      LOGGER.info("Receive get request for downloading {} images", requestNum);

      Map<String, Object> messageBody = new HashMap<>();
      HttpStatus httpStatus;

      if (requestNum > MAX_DOWNLOAD_NUM) {
         messageBody.put(FieldName.ERROR_FIELD, "download image exceeds max allowed: 100");
         httpStatus = HttpStatus.valueOf(400);

         return ResponseEntity
            .status(httpStatus)
            .body(messageBody);
      }

      try {
         Object[] result = imagesService.retrieveUnlabeledImageRandomly(requestNum);
         
         assert result[0] instanceof List;
         assert result[1] instanceof List;

         final List<ImageMeta> imageMetas = (List<ImageMeta>) result[0];
         final List<byte[]> imageBuffers = (List<byte[]>) result[1];
         final int numberOfImages = imageBuffers.size();
         
         List<ResponseImageBody> imageBodies = new ArrayList<>();
         for (int i = 0;i < numberOfImages;i ++) {
            final String name = imageMetas.get(i).getImageName();
            final String encodedImage = ImageUtils.convertByteArrayImageToBase64(imageBuffers.get(i));
            
            imageBodies.add(new ResponseImageBody(name, encodedImage));
         }
         
         messageBody.put(FieldName.IMAGES_FIELD, imageBodies);
         httpStatus = HttpStatus.valueOf(200);
         
      } catch (ImageNotExistException e) {

         messageBody.put(FieldName.ERROR_FIELD, e.getMessage());
         httpStatus = HttpStatus.valueOf(400);
         
      } catch (DaoNotConsistentException e) {

         messageBody.put(FieldName.ERROR_FIELD, e.getMessage());
         httpStatus = HttpStatus.valueOf(500);
         
      }

      return ResponseEntity
         .status(httpStatus)
         .body(messageBody);
   }

   @RequestMapping(
      path = "/fetch/{name}",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE
   )
   @CrossOrigin(origins = "*")
   public ResponseEntity downloadUnlabeledImageByName(@PathVariable("name") String name) {
      LOGGER.info("Receive get request for retrieving image: {}", name);

      Map<String, Object> messageBody = new HashMap<>();
      HttpStatus httpStatus;

      try {
         final byte[] buffer = imagesService.retrieveUnlabeledImageByName(name);
         final byte[] compressedBuffer = ImageUtils.compress(name, buffer);
         final String encodedImage = Base64.encodeBase64String(compressedBuffer);

         ResponseImageBody imageBody = new ResponseImageBody(name, encodedImage);

         messageBody.put(FieldName.IMAGE_FIELD, imageBody);
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
         .body(messageBody.toString());
   }

   @RequestMapping(
      path = "/fetch",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
   )
   @CrossOrigin(origins = "*")
   public ResponseEntity downloadUnlabeledImagesByName(@RequestBody List<String> names) {
      LOGGER.info("Receive post request for downloading unlabeled images by name");

      Map<String, Object> messageBody = new HashMap<>();
      HttpStatus httpStatus;

      List<ResponseImageBody> imageBodies = new LinkedList<>();

      boolean error = false;
      String errorMsg = "";

      for (String name : names) {
         final byte[] buffer;
         try {
            buffer = imagesService.retrieveUnlabeledImageByName(name);

            final byte[] compressedBuffer = ImageUtils.compress(name, buffer);
            final String encodedImage = ImageUtils.convertByteArrayImageToBase64(compressedBuffer);

            ResponseImageBody imageBody = new ResponseImageBody(name, encodedImage);
            imageBodies.add(imageBody);

         } catch (DaoNotConsistentException e) {
            // this is a fatal exception
            error = true;
            errorMsg = e.getMessage();
            break;

         } catch (Exception ignored) {}
      }

      if (error) {
         httpStatus = HttpStatus.valueOf(500);
         messageBody.put(FieldName.ERROR_FIELD, errorMsg);
      } else {
         httpStatus = HttpStatus.valueOf(200);
         messageBody.put(FieldName.IMAGES_FIELD, imageBodies);
      }

      return ResponseEntity
         .status(httpStatus)
         .body(messageBody);
   }



}
