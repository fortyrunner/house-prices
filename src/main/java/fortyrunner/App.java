package fortyrunner;

import com.google.common.base.Joiner;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.toList;

/**
 * Read prices from http://www.landregistry.gov.uk/market-trend-data/public-data/hpi-background
 */
public class App {

  private static final Logger logger = LoggerFactory.getLogger(App.class);

  public static Function<String, HouseInfo> mapToHousePrice = (line) -> {
    String[] values = line.split(",");
    return new HouseInfo(values[0], values[1], values[2]);
  };

  private List<String> lines;

  private List<HouseInfo> collect;

  public static void main(String[] args) throws IOException {
    App app = new App();
    app.readPrices("Average_Prices_SA.csv");

    app.fileStats();

    showAverages(app);

    showBenfordsLaw(app);

  }

  private static void showBenfordsLaw(final App app) {
    Table<Integer, Integer, Double> table = collatePercentages(app);
    printTable(table);
  }

  private static void printTable(final Table<Integer, Integer, Double> table) {
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
    String start = Integer.toString(digit);
    List<HouseInfo> filtered = collect.stream().filter(e -> e.getYear() == year).collect(Collectors.toList());
    long total = filtered.size();

    long ones = filtered
      .stream()
      .parallel()
      .filter(e -> Double.toString(e.getPrice()).startsWith(start))
      .count();

    return (ones * 100 / total);
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

  private void readPrices(final String filename) throws IOException {

    this.lines = Files.readAllLines(Paths.get(filename), StandardCharsets.UTF_8);

    this.collect = lines.stream().skip(1).map(mapToHousePrice).collect(toList());

  }


}
