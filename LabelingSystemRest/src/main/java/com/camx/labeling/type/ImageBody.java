package com.camx.labeling.type;

public abstract class ImageBody {
   private String name;
   private String encodedImage;

   public ImageBody() {}

   public ImageBody(String name, String encodedImage) {
      this.name = name;
      this.encodedImage = encodedImage;
   }

   public String getName () {
      return name;
   }

   public void setName (String name) {
      this.name = name;
   }

   public String getEncodedImage () {
      return encodedImage;
   }

   public void setEncodedImage (String encodedImage) {
      this.encodedImage = encodedImage;
   }
}
