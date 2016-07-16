package fortyrunner;

import java.time.LocalDate;
import java.util.Objects;

public final class HouseInfo {

  private final String name;

  private final LocalDate date;

  private final Double price;

  public HouseInfo(final String date, final String name, final String averagePrice) {
    this.date = LocalDate.parse(date);
    this.name = name;
    this.price = Double.parseDouble(averagePrice);
  }

  public Double getPrice() {
    return this.price;
  }

  public String getName() {
    return name;
  }

  public int getYear() {
    return this.date.getYear();
  }

  @Override
  public String toString() {
    return "HouseInfo " +
      "name='" + this.name + '\'' +
      ", date=" + this.date +
      ", price=" + this.price +
      '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HouseInfo houseInfo = (HouseInfo) o;
    return Objects.equals(name, houseInfo.name) &&
      Objects.equals(date, houseInfo.date);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, date);
  }
}
