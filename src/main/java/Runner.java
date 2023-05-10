import com.poiji.bind.Poiji;
import com.poiji.option.PoijiOptions;
import dto.MappingExcelDto;

import java.io.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Runner {
  public static void main(String[] args) {
    String rootPath = args[0];
    String excelFilePath = args[1];

    File folder = new File(rootPath);

    File excelFile = new File(excelFilePath);
    List<MappingExcelDto> mappings = getExcelFile(excelFile);

    traverse(folder, mappings);
  }

  public static List<MappingExcelDto> getExcelFile(File excelFile) {
    PoijiOptions options = PoijiOptions.PoijiOptionsBuilder.settings().build();

    return Poiji.fromExcel(excelFile, MappingExcelDto.class, options);
  }

  public static void traverse(File dir, List<MappingExcelDto> mappings) {
    if (dir.isFile() && dir.getName().endsWith("Impl.java")) {
      System.out.println(dir.getAbsolutePath());
      replaceText(dir, mappings);
    }
    File[] children = dir.listFiles();
    if (children != null) {
      for (File child : children) {
        traverse(child, mappings);
      }
    }
  }

  public static void replaceText(File file, List<MappingExcelDto> mappings) {
    String dMC = "DomainMessageCodes.";
    try {
      BufferedReader reader = new BufferedReader(new FileReader(file));

      String line = "", oldText = "";
      while ((line = reader.readLine()) != null) {
        oldText += line + "\r\n";
      }
      reader.close();

      String replacedText = oldText;
      for (MappingExcelDto mapping : mappings) {
        String oldKey =
            dMC
                + mapping
                    .getOldKey()
                    .toUpperCase()
                    .replaceAll(Pattern.quote("."), "_")
                    .replaceAll(Pattern.quote("-"), "_")
                    .trim();
        String newKey =
            dMC
                + mapping
                    .getNewKey()
                    .toUpperCase()
                    .replaceAll(Pattern.quote("."), "_")
                    .replaceAll(Pattern.quote("-"), "_")
                    .trim();

        Pattern p = Pattern.compile("\\b" + oldKey + "\\b");
        Matcher m = p.matcher(oldText);
        if (m.find()) {
          replacedText = oldText.replaceAll(p.toString(), newKey);
          oldText = replacedText;
        }
      }

      FileWriter writer = new FileWriter(file);
      writer.write(replacedText);
      writer.close();

    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }
}
