package warehouse;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FasciaOrderManager implements OrderManager {
  
  /** A traversal table of location and its stock. */
  private static ArrayList<String[]> transTable = new ArrayList<String[]>();
  /** A picking request manager for fascia in the warehouse. */
  private PickingRequestManager pickingRequestManager;
  /** A list to hold orders before these orders become a picking request. */
  private List<String> orders;
  
  /** 
   * To initialize a facia order manager in the warehouse.
   * 
   * @param prManager
   *        Picking request manager to be assigned to the fascia order manager.
   */
  public FasciaOrderManager(PickingRequestManager prManager) {
    this.pickingRequestManager = prManager;
    this.orders = new ArrayList<String>();
  }

  /** 
   * To processes order if there are enough orders in list of orders to
   * form a picking request
   * 
   * @return PickingRequest
   *        return the picking request formed.
   */
  @Override
  public PickingRequest processOrder(String order) {
    if (orders.size() == 3) {
      orders.add(order);
      List<String> skulist = this.transSku(orders);
      PickingRequest newpr = pickingRequestManager.createPickReq(orders, skulist);
      orders.clear();
      System.out.println("Picking request ID " + newpr.getId() + " created.");
      return newpr;
    } else {
      orders.add(order);
      System.out.println("Added order " + order);
      return null;
    }
  }

  
  /**
   * Read and extract order information from the translation table.
   * 
   * @param fileName
   *            The name of the file that needs to be translated.
   * @throws FileNotFoundException 
   *         File does not exist or name is not correct.
   */
  public void translateTable(String fileName) throws FileNotFoundException {
    //ArrayList<String[]> transTable = new ArrayList<String[]>();
    ArrayList<String[]> newtable = new ArrayList<String[]>();
    Scanner sc = new Scanner(new File(fileName));
    while (sc.hasNextLine()) {
      String[] nextLine = sc.nextLine().trim().split(",");
      newtable.add(nextLine);
    }
    sc.close();
    transTable = newtable;
  }
  
  /**
   * To translate orders into Skus.
   * 
   * @param orders
   *        This is the array of orders.
   * @return finalList
   *        List of Skus.
   */
  private List<String> transSku(List<String> orders) {
    String[] ordersArray = orders.toArray(new String[orders.size()]);
    // Create a new local list to store the skus being translated.
    List<String> finalList = new ArrayList<String>();
    // If the table is not empty, translate the sku's by size and colour in
    // front-back-front-back order.
    if (transTable.size() != 0) {
      List<String> lstsku = new ArrayList<String>();
      for (int i = 0; i < 4; i++) {
        String[] onevantype = ordersArray[i].trim().split(" ");
        for (int j = 0; j < transTable.size(); j++) {
          if (onevantype[0].equals(transTable.get(j)[1])) {
            if (onevantype[1].equals(transTable.get(j)[0])) {
              String frontSku = transTable.get(j)[2];
              String backSku = transTable.get(j)[3];
              lstsku.add(frontSku);
              lstsku.add(backSku);
            }
          }
        }
      }
      // Re-arrange the lstsku and add the items into
      // front-front-front-front-back-back-back-back.
      for (int k = 0; k < 4; k++) {
        finalList.add(lstsku.get(2 * k));
      }
      for (int j = 0; j < 4; j++) {
        finalList.add(lstsku.get(2 * j + 1));
      }
    // Otherwise print a notification information and return an empty list.
    } else {
      System.out.println("There is not yet a translation table.");
    }
    return finalList;
  }

  /** 
   * To add a picking request to its picking request manager as history.
   * @param picReq
   *        picking request to be added to the picking request manager.
   */
  public void addHistoryPickReq(PickingRequest pickReq) {
	  this.pickingRequestManager.addHistoryPickReq(pickReq);
  }
}
