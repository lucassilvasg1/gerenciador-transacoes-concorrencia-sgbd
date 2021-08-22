import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;

public class WaitQ
{

   Map<String, String> queue = new LinkedHashMap<>(0, 20, false);

   PrintStream p = System.out;

   void addToQueue(String idTransaction, String typeLock)
   {
      queue.put(idTransaction, typeLock);
      p.println("\n\nAdd to Queue: ");
      Main.writeFile("\n\nAdd to Queue: ");
      this.printQueue();
   }

   Map<String, String> getQueue()
   {
      return this.queue;
   }

   void removeFromQueue(String idTransaction)
   {
      queue.remove(idTransaction);
      Main.writeFile("\n\nRemove from Queue: ");
      p.println("\n\nRemove from Queue: ");
      this.printQueue();
   }

   String getTypeLock(String idTransaction)
   {
      return queue.get(idTransaction);
   }

   void printQueue()
   {
      Main.writeFile("||  ID TRANSACTION | TYPE LOCK  ||");
      p.println("||  ID TRANSACTION | TYPE LOCK  ||");
      for (String key : this.queue.keySet())
      {
         p.println("||  " + key + "  |  " + this.queue.get(key) + "  ||");
         Main.writeFile("||  " + key + "  |  " + this.queue.get(key) + "  ||");
      }
   }
}