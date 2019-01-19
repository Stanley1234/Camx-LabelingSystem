package com.camx.labeling.lib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class NumberUtils {

   private static final Random RAND = new Random();

   /**
    * Generate a random number in the given range [low, high].
    * */
   public static int generateRandomNumber(int low, int high) {
      return RAND.nextInt(high - low + 1) + low;
   }

   /**
    * Generate a list of random numbers in the given range [low, high].
    * Note that the {@param size} cannot be too large, i.e. <= 100.
    * */
   public static List<Integer> generateRandomNumbers(int low, int high, int size) {
      final int range = high - low + 1;

      if (range < size) {
         throw new IllegalArgumentException("range must be greater than size");
      }

      List<Integer> randomList = new ArrayList<>();
      for (int cur = low;cur <= high;cur ++) {
         randomList.add(cur);
      }

      Collections.shuffle(randomList);

      List<Integer> result = new ArrayList<>();
      for (int i = 0;i < size;i ++) {
         result.add(randomList.get(i));
      }

      return result;
   }

}
