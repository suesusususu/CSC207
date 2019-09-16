package warehouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class LoaderBarcodeReader extends BarcodeReader {

  private List<String> scannedSkus = new ArrayList<String>();
  /** the list of skus that has already been scanned and verified */
  private static final Logger LOGGER = Logger.getLogger(LoaderBarcodeReader.class.getName());


  /**
   * Create a new LoaderBarcodeReader.
   * 
   * @param name the name of this loader
   */
  public LoaderBarcodeReader(String name) {
    super(name);
//    // set the Loggers and handlers level to ALL
//    LOGGER.setUseParentHandlers(false);
//    LOGGER.setLevel(Level.ALL);
//    try {
//      FileHandler fh = new FileHandler("log.txt");
//      fh.setFormatter(new SimpleFormatter());
//      fh.setLevel(Level.ALL);
//      ConsoleHandler ch = new ConsoleHandler();
//      ch.setFormatter(new SimpleFormatter());
//      ch.setLevel(Level.ALL);
//      LOGGER.addHandler(fh);
//      LOGGER.addHandler(ch);
//    } catch (IOException e) {
//    }
    
  }

  @Override
  /**
   * Check the whether the scanned sku is the right.
   * 
   */
  public boolean verify(String sku) {
    int num = scannedSkus.size();
    List<String> lst = ((PickingRequest) currRequest).getSkus();
    boolean flag = sku.equals(lst.get(num));
    if (flag) {
      LOGGER.log(Level.FINE, "This item is correct");
      scannedSkus.add(lst.get(num));
    } else {
      LOGGER.log(Level.SEVERE, "The item " + (num + 1) + " is incorrect!");
      scannedSkus.add(sku);
    }
    isFinished();
    return flag;
  }

  @Override
  /**
   * Rescan this picking request.
   */
  public void rescan() {
    // clear the scanned list
    scannedSkus.clear();
    PickingRequest pr = (PickingRequest) currRequest;
    pr.setIsVerified(false);
  }

  /**
   * Check whether the current request is finished
   * 
   * @return whether the current request is finished
   */
  public void isFinished() {
    // compare the length of scanned skus list and current request sku list
    boolean flag = scannedSkus.size() == ((PickingRequest) currRequest).getSkus().size();
    if (flag) {
      List<Integer> wrongItems = new ArrayList<>();
      for (int i = 0; i < scannedSkus.size(); i++) {
        if (!scannedSkus.get(i).equals(((PickingRequest) currRequest).getSkus().get(i))) {
          wrongItems.add(i + 1);
        }
      }
      if (wrongItems.isEmpty()) {
        ((PickingRequest) currRequest).setIsVerified(true);
      } else {
        LOGGER.log(Level.SEVERE,
            "Item(s) " + wrongItems.toString() + " wrong! Rescan or" + " discard!");
      }
    }
  }

  @Override
  public void clearCurrRequest() {
    super.clearCurrRequest();
    this.scannedSkus.clear();
  }

  public List<String> getScannedSkus() {
    return scannedSkus;
  }


  public String getName() {
    return name;
  }


}
