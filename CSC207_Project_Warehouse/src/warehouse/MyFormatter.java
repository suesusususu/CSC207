package warehouse;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class MyFormatter extends Formatter {

  public MyFormatter() {

  }

  @Override
  public String format(LogRecord record) {
    return record.getLevel() + ":" + record.getMessage()+"\n";
  }

}
