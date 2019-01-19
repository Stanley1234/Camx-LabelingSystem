package com.camx.labeling.entity.constant;

public enum ImageQuality {
   GOOD("good/"),
   BAD("bad/"),
   UNKNOWN("unknown/");

   private String directoryName;

   ImageQuality(String name) {
      directoryName = name;
   }

   public String getDirectoryName () {
      return directoryName;
   }
}
