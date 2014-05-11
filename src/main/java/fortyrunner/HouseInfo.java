package fortyrunner;

import java.time.LocalDate;

public final class HouseInfo {
  private String name;

  private LocalDate date;

  private Double price;

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
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    HouseInfo houseInfo = (HouseInfo) o;

    if (!date.equals(houseInfo.date)) return false;
    if (!name.equals(houseInfo.name)) return false;
    if (price != null ? !price.equals(houseInfo.price) : houseInfo.price != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = name.hashCode();
    result = 31 * result + date.hashCode();
    return result;
  }


}
