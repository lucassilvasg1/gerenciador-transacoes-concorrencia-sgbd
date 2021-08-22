import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileManager
{

   private static final String REGEX = "[\\(\\)]";

   TrManager trManager = new TrManager();
   
   static FileWriter fw;

   public List<String> readFile(String filename) throws IOException
   {
      List<String> lines = new ArrayList<>();
      String line = "";
      String path = new File("").getAbsolutePath();
      FileReader file = new FileReader(path + filename);
      BufferedReader bufferedArchive = new BufferedReader(file);

      while (line != null)
      {
         line = bufferedArchive.readLine();
         if (line != null)
         {
            lines.add(line);
         }
      }

      bufferedArchive.close();

      return lines;
   }

   public void executeLine(String line)
   {
      List<String> commands = Arrays.asList(line.split(","));

      for (String command : commands)
      {
         if (command.startsWith("BT"))
         {
            String idTransaction = command.split(REGEX)[1];
            trManager.BT(idTransaction);
         }

         else if (command.startsWith("R"))
         {
            String[] parts = command.split(REGEX);
            String idTransaction = parts[0].replace("R", "");
            String dado = parts[1];

            this.trManager.R(idTransaction, dado);
         }

         else if (command.startsWith("W"))
         {
            String[] parts = command.split(REGEX);
            String idTransaction = parts[0].replace("W", "");
            String dado = parts[1];

            this.trManager.W(idTransaction, dado);
         }

         else if (command.startsWith("C"))
         {
            String[] parts = command.split(REGEX);
            String idTransaction = parts[1];

            this.trManager.CM(idTransaction);
         }

         else
         {
            System.out.println("OPERACAO IRRECONHECIDA!");
            Main.writeFile("OPERACAO IRRECONHECIDA!");
         }
      }
   }

   public void executeLines(List<String> lines)
   {
      for (String line : lines)
      {
         this.executeLine(line);
         this.trManager = new TrManager();
      }
   }
   
}