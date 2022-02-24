package controller.save;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static controller.BoardController.MAX_LEVEL;

//uses special save data file
public class SaveDataHandler {

  private final static byte[] KEY = "<P^z{}{Jr!mAe+X@`U2v>b(wEpJzs$4{".getBytes(StandardCharsets.UTF_8);

  private static File saveFile;
  private static int[] backUpData;

  static {
    try {
      saveFile = new File(new File(
              SaveDataHandler.class.getProtectionDomain().getCodeSource().getLocation().toURI())
              .getParentFile().getAbsolutePath() + "/kft.save");
      // this should actually get the source of the code we're running
      //  so this should be accurate to the location of the jar file itself
      backUpData = getLevelsBeaten();
    } catch (URISyntaxException e) {
      System.out.println(e.getMessage());
    }
  }

  //returns whether new file was created
  //throws error on bad data
  private static boolean refreshFile(int[] backUp) {
    if (saveFile.exists()) {
      if (!validateData(saveFile)) {
        if (CorruptedSaveAlert.display().equalsIgnoreCase("try again")) {
          refreshFile(backUpData);
        } else {
          System.exit(0);
        }
      }
      return false;
    }
    // generate file
    FileOutputStream out;
    try {
      out = new FileOutputStream(saveFile); // I think this constructor starts at beginning of file
      out.write(KEY);
      //now two bytes for each
      out.write(new byte[MAX_LEVEL * 2]); //these bytes all should start 0
      out.write(checksum3(KEY.length + MAX_LEVEL * 2));

      out.close();
    } catch (IOException e) {
      System.out.println("error writing save file");
      return false;
    }

    if (backUp != null) {
      Map<Integer, Byte> rewriteThese = new HashMap<>();
      for (int i = 1; i <= MAX_LEVEL; i++) {
        byte[] b = intToTwoBytes(backUpData[i]);
        rewriteThese.put((i - 1) * 2, b[0]);
        rewriteThese.put((i - 1) * 2 + 1, b[1]);
      }
      rewriteSaveData(rewriteThese);
    }

    return true;
  }

  private static byte[] checksum3(int i) {
    byte[] sumThis = new byte[i + 3 - i%3]; //excess bytes at the end should stay 0
    try {
      FileInputStream fis = new FileInputStream(saveFile);
      fis.read(sumThis, 0, i);
    } catch (FileNotFoundException e) {
      System.out.println(e.getMessage());
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }

    byte[] sum = new byte[3];

    for (int n = 0; n < sumThis.length; n += 3) {
      sum[0] ^= sumThis[n];
      sum[1] ^= sumThis[n + 1];
      sum[2] ^= sumThis[n + 2];
    }

    return sum;
  }

  private static boolean validateData(File f) {
    byte[] validateThis = new byte[KEY.length + MAX_LEVEL*2 + 3];
    try {
      FileInputStream fis = new FileInputStream(f);
      fis.read(validateThis);
    } catch (FileNotFoundException e) {
      System.out.println("FNF " + e.getMessage());
    } catch (IOException e) {
      System.out.println("IO " + e.getMessage());
    }

    for (int i = 0; i < KEY.length; i++) {
      if (validateThis[i] != KEY[i]) {
        System.out.println("key invalid");
        return false;
      }
    }

    byte[] checksum = checksum3(KEY.length + MAX_LEVEL*2);

    boolean checksumValid = checksum[0] == validateThis[validateThis.length - 3] &&
            checksum[1] == validateThis[validateThis.length - 2] &&
            checksum[2] == validateThis[validateThis.length - 1];
    if (!checksumValid) { System.out.println("checksum invalid"); }
    return checksumValid;
  }

  public static int[] getLevelsBeaten() {
    refreshFile(null);

    int[] moveTotals = new int[MAX_LEVEL + 1];

    FileInputStream fis;
    try {
      fis = new FileInputStream(saveFile);
    } catch (FileNotFoundException fnfE) {
      System.out.println("didn't find ktf.save");
      return new int[MAX_LEVEL + 1];
    } catch (NullPointerException e) {
      System.out.println(e.getMessage());
      return new int[MAX_LEVEL + 1];
    }

    try {
      fis.skip(KEY.length);
      moveTotals[0] = -1;
      for (int i = 1; i <= MAX_LEVEL; i++) {
        byte[] b = new byte[2];
        fis.read(b);
        moveTotals[i] = bytesToInt(b);
      }
      fis.close();
    } catch (IOException ioE) {
      System.out.println("error");
      return new int[MAX_LEVEL + 1];
    }

    return moveTotals;
  }

  public static void recordMoveTotal(int level, int moves) {
    byte[] b = intToTwoBytes(moves);
    backUpData[level] = moves;
    Map<Integer, Byte> m = new HashMap<>();
    m.put((level - 1) * 2, b[0]);
    m.put((level - 1) * 2 + 1, b[1]);
    rewriteSaveData(m);
  }

  public static void updateMoveTotalIfNewRecord(int level, int moves) {
    int prevRecord = getLevelsBeaten()[level];
    if (prevRecord == 0 || prevRecord > moves) {
      recordMoveTotal(level, moves);
    }
  }

  public static void resetSaveData() {
    Map<Integer, Byte> btw = new HashMap<>();
    backUpData = new int[1 + MAX_LEVEL];
    backUpData[0] = 1;
    for (int i = 0; i < MAX_LEVEL; i++) {
      btw.put(2 * i, (byte)0);
      btw.put(2 * i + 1, (byte)0);
    }
    rewriteSaveData(btw);
  }

  //integer represents lines from end of key
  private static void rewriteSaveData(Map<Integer, Byte> bytesToWrite) {
    refreshFile(backUpData);
    try {
      FileInputStream fis = new FileInputStream(saveFile);
      byte[] bytes = new byte[KEY.length + MAX_LEVEL*2];
      fis.read(bytes);
      for (int i : bytesToWrite.keySet()) {
        bytes[KEY.length + i] = bytesToWrite.get(i);
      }
      FileOutputStream fos = new FileOutputStream(saveFile);
      fos.write(bytes);
      fos.write(checksum3(KEY.length + MAX_LEVEL*2));
      fis.close();
      fos.close();
    } catch (IOException ioE){//| URISyntaxException ioE) {
      System.out.println("error reading or writing");
      System.out.println(ioE.getMessage());
    }
  }

  //reads positive integer into two bytes
  public static byte[] intToTwoBytes(int i) {
    if (i < 0) { return new byte[2]; }
    byte[] result = new byte[2];

    result[1] = (byte) i;
    result[0] = (byte) (i >> 8);

    return result;
  }

  //reads first two bytes into a positive integer
  public static int bytesToInt(byte[] b) {
    return (Byte.toUnsignedInt(b[0]) << 8) | Byte.toUnsignedInt(b[1]);
  }

}
