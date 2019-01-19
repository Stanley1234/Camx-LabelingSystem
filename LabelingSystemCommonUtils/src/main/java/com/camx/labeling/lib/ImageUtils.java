package com.camx.labeling.lib;

import org.apache.tomcat.util.codec.binary.Base64;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;

public class ImageUtils {

   private static final float IMAGE_QUALITY_COMPRESS_LEVEL = 0.05f;

   public static byte[] compress(String imageName, byte[] buffer) {
      final int lastIndexOfDot = imageName.lastIndexOf('.');
      if (lastIndexOfDot == -1) {
         return null;
      }
      final String formatName = imageName.substring(lastIndexOfDot + 1);

      // The input stream is a byte array
      InputStream inputStream = new ByteArrayInputStream(buffer);
      BufferedImage bufferedImage;
      try {
         bufferedImage = ImageIO.read(inputStream);
      } catch (IOException e) {
         e.printStackTrace();
         return null;
      }

      // The output stream is a byte array
      OutputStream outputStream = new ByteArrayOutputStream();

      Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(formatName);
      ImageWriter writer = writers.next();

      ImageOutputStream ios;
      try {
         ios = ImageIO.createImageOutputStream(outputStream);
      } catch (IOException e) {
         e.printStackTrace();
         return null;
      }
      writer.setOutput(ios);

      ImageWriteParam param = writer.getDefaultWriteParam();
      param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
      param.setCompressionQuality(IMAGE_QUALITY_COMPRESS_LEVEL);

      try {
         writer.write(null, new IIOImage(bufferedImage, null, null), param);
      } catch (IOException e) {
         e.printStackTrace();
         return null;
      }

      return ((ByteArrayOutputStream) outputStream).toByteArray();
   }

   public static String convertByteArrayImageToBase64(byte[] buffer) {
      return Base64.encodeBase64String(buffer);
   }

   public static byte[] convertBase64ImageToByteArray(String image) {
      return Base64.decodeBase64(image);
   }
}
