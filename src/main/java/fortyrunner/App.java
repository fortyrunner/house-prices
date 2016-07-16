package fortyrunner;

import com.google.common.base.Joiner;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * Read prices from http://www.landregistry.gov.uk/market-trend-data/public-data/hpi-background
 */
public class App {

  private static final Logger logger = LoggerFactory.getLogger(App.class);

  private static final Function<String, HouseInfo> mapToHouseInfo = (line) -> {
    String[] values = line.split(",");
    return new HouseInfo(values[0], values[1], values[2]);
  };

  private List<String> lines;

  private List<HouseInfo> collect;

  public static void main(String[] args) throws IOException {
    App app = new App();
    app.readHouseInfo("Average_Prices_SA.csv");

    app.fileStats();

    showAverages(app);

    showBenfordsLaw(app);

  }

  private static void showBenfordsLaw(final App app) {
    Table<Integer, Integer, Double> table = collatePercentages(app);
    printTable(table);
  }

  private static void printTable(final Table<Integer, Integer, Double> table) {
    logger.info("Benfords law predicts that most numbers begin with a 1 in sets of naturally occurring numbers\n");
    logger.info("Year,1%,2%,3%,4%,5%,6%,7%,8%,9%");
    for (Integer row : table.rowKeySet()) {
      Map<Integer, Double> columns = table.row(row);
      logger.info(Joiner.on(',').join(row, columns.values()));
    }
  }

  private static Table<Integer, Integer, Double> collatePercentages(final App app) {
    Table<Integer, Integer, Double> table = TreeBasedTable.create();

    for (int year = 1995; year < 2015; year++) {
      for (int i = 1; i <= 9; i++) {
        double percent = app.applyBenfordsLaw(i, year);
        table.put(year, i, percent);
      }

    }
    return table;
  }

  private static void showAverages(final App app) {
    app.average();

    app.average("London", 1995);
    app.average("London", 2005);
    app.average("London", 2013);
    app.average("London", 2014);
  }

  private double applyBenfordsLaw(final int digit, final int year) {
    List<HouseInfo> filtered = filterByYear(year);
    long total = filtered.size();

    long ones = filterByStartingDigit(digit, filtered);

    return (ones * 100 / total);
  }

  private long filterByStartingDigit(final int digit, final List<HouseInfo> filtered) {
    String start = Integer.toString(digit);
    return filtered
      .stream()
      .parallel()
      .filter(e -> Double.toString(e.getPrice()).startsWith(start))
      .count();
  }

  private List<HouseInfo> filterByYear(final int year) {
    return collect.stream().filter(e -> e.getYear() == year).collect(Collectors.toList());
  }

  private void average() {
    OptionalDouble average = collect.stream().mapToDouble(HouseInfo::getPrice).average();
    logger.info(String.format("Average=%.2f", average.getAsDouble()));
  }

  private void average(final String region, final int year) {
    OptionalDouble average = collect
      .stream()
      .parallel()
      .filter(e -> e.getName().startsWith(region) && e.getYear() == year)
      .mapToDouble(HouseInfo::getPrice)
      .average();
    logger.info(String.format("Average for %s in %s is %.2f", region, year, average.getAsDouble()));
  }

  private void fileStats() {
    long count = lines.stream().count();
    logger.info(String.format("File is %d lines long", count));

    logger.info("header=" + lines.stream().findFirst());

    logger.info("Sample Record=" + lines.stream().skip(1).findAny());

  }

  /**
   * Read the CSV file into a collection and convert to a collection of HouseInfo objects.
   * Remember to skip the header line
   */
  private void readHouseInfo(final String filename) throws IOException {

    this.lines = Files.readAllLines(Paths.get(filename), StandardCharsets.UTF_8);

    this.collect = lines.stream().skip(1).map(mapToHouseInfo).collect(toList());

  }


}
