import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Main
{

   static FileWriter fw;
   static File file;

   
   public static void main(String[] args) throws IOException
   {
      file = new File("resources/saida");
      fw = new FileWriter(file);

      FileManager manager = new FileManager();
      List<String> lines = manager.readFile("/resources/fileteste");
      manager.executeLines(lines);
      
      fw.close();
   }

   
   
   public static void writeFile(String texto)
   {
      try
      {
         fw.write("\n" + texto);
      }
      catch (Exception e)
      {
      }
      
   }
}