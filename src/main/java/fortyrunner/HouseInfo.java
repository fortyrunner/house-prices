package fortyrunner;

import java.time.LocalDate;

public class HouseInfo {
  private String name;

  private LocalDate date;

  private Double price;

  public HouseInfo(final String date, final String name, final String averagePrice) {
    this.date = LocalDate.parse(date);
    this.name = name;
    this.price = Double.parseDouble(averagePrice);
  }

  public Double getPrice() {
    return price;
  }

  @Override
  public String toString() {
    return "HouseInfo " +
      "name='" + name + '\'' +
      ", date=" + date +
      ", price=" + price +
      '}';
  }

  public String getName() {
    return name;
  }

  public int getYear() {
    return this.date.getYear();
  }
}
