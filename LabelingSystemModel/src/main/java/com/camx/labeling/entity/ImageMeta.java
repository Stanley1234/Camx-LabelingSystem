package com.camx.labeling.entity;

import com.camx.labeling.entity.constant.ImageQuality;
import com.camx.labeling.entity.constant.ImageType;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
public class ImageMeta {
   @Id
   private String imageName;
   private ImageType imageType;
   private ImageQuality imageQuality;
   @Temporal(TemporalType.TIMESTAMP)
   private Date createDate;
   private String judger;

   public ImageMeta () {
   }

   public ImageMeta (String imageName) {
      this.imageName = imageName;
   }

   public ImageMeta (String imageName, ImageType imageType, ImageQuality imageQuality, String judger) {
      this.imageName = imageName;
      this.imageType = imageType;
      this.imageQuality = imageQuality;
      this.judger = judger;
   }

   public String getImageName () {
      return imageName;
   }

   public void setImageName (String imageName) {
      this.imageName = imageName;
   }

   public ImageType getImageType () {
      return imageType;
   }

   public void setImageType (ImageType imageType) {
      this.imageType = imageType;
   }

   public ImageQuality getImageQuality () {
      return imageQuality;
   }

   public void setImageQuality (ImageQuality imageQuality) {
      this.imageQuality = imageQuality;
   }

   public Date getCreateDate () {
      return createDate;
   }

   public void setCreateDate (Date createDate) {
      this.createDate = createDate;
   }

   public String getJudger () {
      return judger;
   }

   public void setJudger (String judger) {
      this.judger = judger;
   }

   @Override
   public String toString () {
      return "ImageMeta{" +
         "imageName='" + imageName + '\'' +
         ", imageType=" + imageType +
         ", imageQuality=" + imageQuality +
         ", createDate=" + createDate +
         ", judger='" + judger + '\'' +
         '}';
   }
}
