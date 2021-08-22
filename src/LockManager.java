import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class LockManager
{

   Map<String, List<String>> lockTable = new HashMap<>();

   Map<String, WaitQ> dadoQueue = new HashMap<String, WaitQ>();

   TrManager trManager;

   String shared, exclusive;

   File file = new File("resources/saida");

   public LockManager(TrManager trManager)
   {
      shared = "S";
      exclusive = "X";
      this.trManager = trManager;
   }

   String isShared()
   {
      return shared;
   }

   String isExclusive()
   {
      return exclusive;
   }

   public void LS(String idTransaction2, String dado)
   {

      // Criar logo a fila do dado
      if (!this.dadoQueue.containsKey(dado))
      {
         this.dadoQueue.put(dado, new WaitQ());
      }
      // Percorrendo tabela pra saber se existe algum tipo de lock sobre o
      // dado desejado
      String idTransaction1;
      boolean processed = false; // Indica se a operacao foi já abortada ou pra
                                 // fila
      Iterator<String> ids = this.lockTable.keySet().iterator();
      while (ids.hasNext() && !processed)
      {
         idTransaction1 = ids.next();
         List<String> dadoAndLock = this.lockTable.get(idTransaction1);
         if (dadoAndLock.get(0).equals(dado))
         {
            // Caso em que há outra transação com lock sobre o dado e se não são
            // duas shareds
            if (!idTransaction1.equals(idTransaction2) && !dadoAndLock.get(1).equals(this.isShared()))
            {
               this.WaitDie(idTransaction1, idTransaction2, dado, this.isShared());
            }
            // Caso em que a transação já tem lock sobre o dado
            else
            {
               List<String> newdadoAndLock = new ArrayList<>();
               newdadoAndLock.add(dado);
               newdadoAndLock.add(this.isShared());
               this.lockTable.put(idTransaction2, newdadoAndLock);
            }
            this.getElementsFromQueue(dadoAndLock.get(0));
            this.getElementsFromQueue(dado);
            processed = true;
         }
      }
      // Caso em que não há transacao acessando o dado
      if (!processed)
      {
         List<String> dadoAndLock = new ArrayList<>();
         dadoAndLock.add(dado);
         dadoAndLock.add(this.isShared());
         this.lockTable.put(idTransaction2, dadoAndLock);
      }
      System.out.println("\n\nLS: ");
      Main.writeFile("\n\nLS: ");
      this.printLockTable();

      this.getFromAllQueues();
   }

   public void LX(String idTransaction2, String dado)
   {

      if (!this.dadoQueue.containsKey(dado))
      {
         this.dadoQueue.put(dado, new WaitQ());
      }
      // Percorrendo tabela pra saber se existe algum tipo de lock sobre o
      // dado desejado
      String idTransaction1;
      boolean processed = false; // Indica se a operacao foi já abortada ou pra
                                 // fila
      Iterator<String> ids = this.lockTable.keySet().iterator();
      while (ids.hasNext() && !processed)
      {
         idTransaction1 = ids.next();
         List<String> dadoAndLock = this.lockTable.get(idTransaction1);
         if (dadoAndLock.get(0).equals(dado))
         {
            // Caso em que há outra transação com lock sobre o dado
            if (!idTransaction1.equals(idTransaction2))
            {
               this.WaitDie(idTransaction1, idTransaction2, dado, this.isExclusive());
            }
            // Caso em que a transação já tem lock sobre o dado
            else
            {
               List<String> newdadoAndLock = new ArrayList<>();
               newdadoAndLock.add(dado);
               newdadoAndLock.add(this.isExclusive());
               this.lockTable.put(idTransaction2, newdadoAndLock);
            }
            processed = true;
         }
      }

      // Caso em que não há transacao acessando o dado
      if (!processed)
      {
         List<String> dadoAndLock = new ArrayList<>();
         dadoAndLock.add(dado);
         dadoAndLock.add(this.isExclusive());
         this.lockTable.put(idTransaction2, dadoAndLock);
      }
      System.out.println("\n\nLX: ");
      Main.writeFile("\n\nLX: ");
      this.printLockTable();

      this.getFromAllQueues();
   }

   public void U(String idTransaction)
   {
      // Apaga o bloqueio da transacao sobre o item dado na Lock Table.
      String dado;
      if (this.lockTable.containsKey(idTransaction))
      {
         dado = this.lockTable.get(idTransaction).get(0);
         this.lockTable.remove(idTransaction);
         this.getElementsFromQueue(dado);
      }
      // Aqui deve-se pegar a lista do dado e pegar a proxima transacao
      // System.out.println("\n\nU: ");
      // this.printLockTable();
   }

   void getFromAllQueues()
   {
      for (String dado : this.dadoQueue.keySet())
      {
         this.getElementsFromQueue(dado);
      }
   }

   public void getElementsFromQueue(String dado)
   {

      WaitQ waitQ = this.dadoQueue.get(dado);
      if (waitQ != null)
      {
         Map<String, String> queue = waitQ.getQueue();
         boolean mustContinue = false;
         Map.Entry<String, String> transaction;
         String idTransaction;
         Iterator<Map.Entry<String, String>> transactions = queue.entrySet().iterator();
         while (transactions.hasNext() && !mustContinue)
         {
            transaction = transactions.next();
            idTransaction = transaction.getKey();
            if (this.lockTable.containsKey(idTransaction))
            {
               this.lockTable.get(idTransaction).set(1, transaction.getValue());
            }
            else if (this.checkCanRemoveFromQueue(dado, transaction.getValue()))
            {
               waitQ.removeFromQueue(idTransaction);
               if (transaction.getValue().equals(this.isShared()))
               {
                  this.LS(idTransaction, dado);
               }
               else
               {
                  this.LX(idTransaction, dado);
               }
            }
            else
            {
               mustContinue = false;
            }
         }
      }
   }

   boolean checkCanRemoveFromQueue(String dado, String typeLock)
   {

      List<String> lockers = this.getAllLockers(dado);
      if (lockers.contains(this.isExclusive()) || (lockers.contains(this.isShared()) && typeLock.equals(this.isExclusive())))
      {
         return false;
      }
      else
      {
         return true;
      }
   }

   List<String> getAllLockers(String dado)
   {
      List<String> lockers = new ArrayList<>();
      for (List<String> locker : this.lockTable.values())
      {
         if (locker.get(0).equals(dado))
         {
            lockers.add(locker.get(1));
         }
      }
      return lockers;
   }

   void WaitDie(String idTransaction1, String idTransaction2, String dado, String lockType)
   {
      if (this.trManager.getTimestamp(idTransaction1) < this.trManager.getTimestamp(idTransaction2))
      {
         this.trManager.toAbort(idTransaction2);
         System.out.println("\n\nTransação " + idTransaction2 + " abortada!");
         Main.writeFile("\n\nTransação " + idTransaction2 + " abortada!");

      }
      else
      {
         this.dadoQueue.get(dado).addToQueue(idTransaction2, lockType);
      }
      this.U(idTransaction2);
   }

   void printLockTable()
   {

      String line1 = "||  ID TRANSACTION  |   DADO  | LOCK TYPE  ||";
      String line2 = "";
      System.out.println(line1);
      Main.writeFile(line1);
      for (String key : this.lockTable.keySet())
      {
         line2 = "||  " + key + "  |  " + this.lockTable.get(key).get(0) + "  |  " + this.lockTable.get(key).get(1) + "  ||";
         System.out.println(line2);
         Main.writeFile(line2);
      }

   }

}