package com.camx.labeling.lib;

import java.sql.Timestamp;

public class TimeUtils {

   public static Timestamp getCurrentTimestamp() {
      return new Timestamp(System.currentTimeMillis());
   }
}
