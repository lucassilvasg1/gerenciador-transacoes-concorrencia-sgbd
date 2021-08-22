import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrManager
{

   Map<String, List<Object>> transactions = new HashMap<>();

   LockManager lockManager = new LockManager(this);

   String active, committed, aborted;

   int time;

   public TrManager()
   {
      active = "ACTIVE";
      committed = "COMMITTED";
      aborted = "ABORTED";
      time = 1;
   }

   String isActive()
   {
      return active;
   }

   String isCommitted()
   {
      return committed;
   }

   String isAborted()
   {
      return aborted;
   }

   public void toAbort(String idTransaction)
   {
      if (this.transactions.containsKey(idTransaction))
      {
         this.transactions.get(idTransaction).set(1, this.isAborted());
         this.lockManager.U(idTransaction);
      }
      else
      {
         Main.writeFile("TRANSACAO NAO REGISTRADA!");
         System.out.println("TRANSACAO NAO REGISTRADA!");
      }
   }

   public int getTimestamp(String idTransaction)
   {
      return (int) this.transactions.get(idTransaction).get(0);
   }

   public void BT(String idTransaction)
   {

      List<Object> timeAndStatus = new ArrayList<>();
      timeAndStatus.add(time);
      timeAndStatus.add(this.isActive());
      this.transactions.put(idTransaction, timeAndStatus);
      time++;
      Main.writeFile("\n\nBegin Transaction: ");
      System.out.println("\n\nBegin Transaction: ");
      this.printTransactions();
   }

   public void R(String idTransaction, String dado)
   {
      if (this.transactions.containsKey(idTransaction))
      {
         this.transactions.get(idTransaction).set(1, this.isActive());
         lockManager.LS(idTransaction, dado);
      }
      else
      {
         Main.writeFile("Operacao invalida! Transacao nao registrada!");
         System.out.println("Operacao invalida! Transacao nao registrada!");
      }
   }

   public void W(String idTransaction, String dado)
   {
      if (this.transactions.containsKey(idTransaction))
      {
         this.transactions.get(idTransaction).set(1, this.isActive());
         lockManager.LX(idTransaction, dado);
      }
      else
      {
         Main.writeFile("Operacao invalida! Transacao nao registrada!");
         System.out.println("Operacao invalida! Transacao nao registrada!");
      }
   }

   public void CM(String idTransaction)
   {
      if (this.transactions.containsKey(idTransaction))
      {
         this.transactions.get(idTransaction).set(1, this.isCommitted());
         this.lockManager.U(idTransaction);
      }
      else
      {
         System.out.println("Operacao invalida! Transacao nao registrada!");
      }
      System.out.println("\n\nCommit: ");
      Main.writeFile("Commit:");

      this.printTransactions();

   }

   void printTransactions()
   {
      String line1 = "||  ID   |   TIMESTAMP  | STATUS  ||";
      Main.writeFile(line1);

      String line2 = "";
      System.out.println(line1);
      for (String key : this.transactions.keySet())
      {
         line2 = "||  " + key + "  |  " + this.transactions.get(key).get(0) + "  |  " + this.transactions.get(key).get(1) + "  ||";
         System.out.println(line2);

         Main.writeFile(line2);
      }

   }

}