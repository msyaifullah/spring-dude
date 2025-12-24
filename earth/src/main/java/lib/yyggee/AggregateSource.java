package lib.yyggee;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AggregateSource {

  @Value("${lib.yyggee.host:default-hostname}")
  private String host;

  public String getData(String aValue, String bValue) {
    return aValue + bValue + ":" + host;
  }
}
