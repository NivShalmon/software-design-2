package il.ac.technion.cs.sd.buy.test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import il.ac.technion.cs.sd.buy.app.BuyProductInitializer;
import il.ac.technion.cs.sd.buy.app.BuyProductReader;
import org.junit.Test;

import java.io.File;
import java.time.Duration;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class StaffTest {
  private final TestingLineStorageModule testingLineStorageModule = new TestingLineStorageModule();

  private static Map.Entry<String, Long> toEntry(String s, long l) {
    return new AbstractMap.SimpleEntry(s, l);
  }

  private static Map<String, Long> mapFrom(Map.Entry<String, Long>... entries) {
    Map<String, Long> $ = new HashMap<>();
    for (Map.Entry<String, Long> e : entries) {
      $.put(e.getKey(), e.getValue());
    }
    return $;
  }

  private interface MainTest {
    void run(BuyProductReader r) throws Exception;
  }

  private static class FutureTestAggregator {
    private final List<CompletableFuture<Void>> tests;

    private FutureTestAggregator(List<CompletableFuture<Void>> tests) {
      this.tests = tests;
    }

    FutureTestAggregator() {
      this(new ArrayList<>());
    }

    public FutureTestAggregator append(CompletableFuture<Void> f) {
      ArrayList<CompletableFuture<Void>> newTests = new ArrayList<>(tests);
      newTests.add(f);
      return new FutureTestAggregator(newTests);
    }

    public void get() throws Exception {
      for (CompletableFuture<Void> f : tests) {
        f.get();
      }
    }
  }

  private void test(String fileName, int timeoutInMinutes, MainTest t) throws Exception {
    Injector injector = setupAndGetInjector(fileName, timeoutInMinutes);
    mainTest(injector, t);
  }

  private static <T> CompletableFuture<Void> futureAssertEquals(CompletableFuture<T> f, T expected) throws Exception {
    StackTraceElement[] stackTrack = Thread.currentThread().getStackTrace();
    StackTraceElement[] newStackTrace = new StackTraceElement[stackTrack.length - 2];
    System.arraycopy(stackTrack, 2, newStackTrace, 0, newStackTrace.length);
    return f.thenApply(actual -> {
      try {
        assertEquals(expected, actual);
      } catch (Error e) {
        e.setStackTrace(newStackTrace);
        throw e;
      }
      return null;
    });
  }

  private static <T> T wait(Callable<T> runnable, Duration timeout) throws Exception {
    try {
      return CompletableFuture.supplyAsync(() -> {
        try {
          return runnable.call();
        } catch (Throwable e) {
          throw new RuntimeException(e);
        }
      }).get(timeout.toMillis(), TimeUnit.MILLISECONDS);
    } catch (ExecutionException e) {
      // Since we wrap all exceptions, we need to unstack twice to get the root cause
      Throwable cause = e.getCause().getCause();
      // Fookin' Java
      if (cause instanceof Error)
        throw (Error) cause;
      else if (cause instanceof Exception)
        throw (Exception) cause;
      else
        throw new RuntimeException(e);
    }
  }

  private static void mainTest(Injector i, MainTest test) throws Exception {
    wait(() -> {
      test.run(i.getInstance(BuyProductReader.class));
      return null;
    }, Duration.ofSeconds(40)); // 10 extra seconds as a safeguard
  }

  private Injector setupAndGetInjector(String fileName, int timeoutInMinutes) throws Exception {
    String fileContents =
        new Scanner(new File(StaffTest.class.getResource(fileName).getFile())).useDelimiter("\\Z").next();
    assert !fileContents.isEmpty();
    return wait(() -> {
      // The injector isn't supposed to take any time to create its classes, but just in case...
      Injector injector = Guice.createInjector(new BuyProductModule(), testingLineStorageModule);
      BuyProductInitializer initializer = injector.getInstance(BuyProductInitializer.class);
      if (fileName.endsWith("xml"))
        initializer.setupXml(fileContents).get();
      else {
        assert fileName.endsWith("json");
        initializer.setupJson(fileContents).get();
      }
      return injector;
    }, Duration.ofMinutes(timeoutInMinutes)); // 3 minutes should be enough setup time even for the large files
  }

  @Test
  public void json1() throws Exception {
    test("medium.json", 1, reader -> new FutureTestAggregator()
        .append(futureAssertEquals(reader.isValidOrderId("dw"), true))
        .append(futureAssertEquals(reader.isValidOrderId("cn"), false))
        .append(futureAssertEquals(reader.isValidOrderId("foo"), false))
        .append(futureAssertEquals(reader.isCanceledOrder("dw"), false))
        .append(futureAssertEquals(reader.isModifiedOrder("foo"), false))
        .get());
  }

  @Test
  public void xml1() throws Exception {
    test("medium.xml", 1, reader -> new FutureTestAggregator()
        .append(futureAssertEquals(reader.isCanceledOrder("kk"), true))
        .append(futureAssertEquals(reader.isModifiedOrder("kk"), true))
        .append(futureAssertEquals(reader.getNumberOfProductOrdered("foo"), OptionalInt.empty()))
        .append(futureAssertEquals(reader.getNumberOfProductOrdered("kk"), OptionalInt.of(-78)))
        .append(futureAssertEquals(reader.getNumberOfProductOrdered("bn"), OptionalInt.of(270)))
        .get());
  }

  @Test
  public void json2() throws Exception {
    test("medium.json", 1, reader -> new FutureTestAggregator()
        .append(futureAssertEquals(reader.getUsersThatPurchased("dt"), Arrays.asList("cn")))
        .append(futureAssertEquals(reader.getUsersThatPurchased("foo"), Arrays.asList()))
        .append(futureAssertEquals(reader.getOrderIdsThatPurchased("qc"), Arrays.asList("6q", "db", "o2")))
        .append(futureAssertEquals(reader.getOrderIdsThatPurchased("foo"), Arrays.asList()))
        .append(futureAssertEquals(reader.getOrderIdsThatPurchased("si"), Arrays.asList()))
        .get());
  }

  @Test
  public void xml2() throws Exception {
    test("medium.xml", 1, reader -> new FutureTestAggregator()
        .append(futureAssertEquals(reader.getHistoryOfOrder("foo"), Arrays.asList()))
        .append(futureAssertEquals(reader.getHistoryOfOrder("2x"), Arrays.asList()))
        .append(futureAssertEquals(reader.getHistoryOfOrder("kk"), Arrays.asList(869, 419, 419, 781, 682, 772, 78, -1)))
        .append(futureAssertEquals(reader.getHistoryOfOrder("7f"), Arrays.asList(308)))
        .append(futureAssertEquals(reader.getHistoryOfOrder("ug"), Arrays.asList(123, 741)))
        .get());
  }

  @Test
  public void json3() throws Exception {
    test("medium.json", 1, reader -> new FutureTestAggregator()
        .append(futureAssertEquals(reader.getCancelRatioForUser("foo"), OptionalDouble.empty()))
        .append(futureAssertEquals(reader.getCancelRatioForUser("fo"), OptionalDouble.of(0.5)))
        .append(futureAssertEquals(reader.getCancelRatioForUser("xn"), OptionalDouble.of(0)))
        .append(futureAssertEquals(reader.getModifyRatioForUser("bar"), OptionalDouble.empty()))
        .append(futureAssertEquals(reader.getModifyRatioForUser("fo"), OptionalDouble.of(0.25)))
        .get());
  }

  @Test
  public void xml3() throws Exception {
    test("medium.xml", 1, reader -> new FutureTestAggregator()
        .append(futureAssertEquals(reader.getTotalAmountSpentByUser("foo"), 0L))
        .append(futureAssertEquals(reader.getTotalAmountSpentByUser("o"), 69110768L))
        .append(futureAssertEquals(reader.getTotalAmountSpentByUser("bar"), 0L))
        .append(futureAssertEquals(reader.getOrderIdsForUser("foo"), Arrays.asList()))
        .append(futureAssertEquals(reader.getOrderIdsForUser("o"), Arrays.asList("du", "jm")))
        .get());
  }

  @Test
  public void json4() throws Exception {
    test("medium.json", 1, reader -> new FutureTestAggregator()
        .append(futureAssertEquals(reader.getTotalNumberOfItemsPurchased("foo"), OptionalLong.empty()))
        .append(futureAssertEquals(reader.getTotalNumberOfItemsPurchased("rm"), OptionalLong.of(0L)))
        .append(futureAssertEquals(reader.getTotalNumberOfItemsPurchased("qc"), OptionalLong.of(1192)))
        .append(futureAssertEquals(reader.getAverageNumberOfItemsPurchased("qc"), OptionalDouble.of(596)))
        .append(futureAssertEquals(reader.getAverageNumberOfItemsPurchased("rm"), OptionalDouble.empty()))
        .get());
  }

  @Test
  public void xml4() throws Exception {
    test("medium.xml", 1, reader -> new FutureTestAggregator()
        .append(futureAssertEquals(reader.getAllItemsPurchased("foo"), Collections.emptyMap()))
        .append(futureAssertEquals(reader.getAllItemsPurchased("nz"),
            mapFrom(toEntry("5a", 280), toEntry("ku", 835))))
        .append(futureAssertEquals(reader.getItemsPurchasedByUsers("foo"), Collections.emptyMap()))
        .append(futureAssertEquals(reader.getItemsPurchasedByUsers("5a"),
            mapFrom(toEntry("nz", 280), toEntry("ya", 116))))
        .append(futureAssertEquals(reader.getItemsPurchasedByUsers("ja"), Collections.emptyMap()))
        .get());
  }

  @Test
  public void largeJson() throws Exception {
    test("large.json", 3, reader -> new FutureTestAggregator()
        .append(futureAssertEquals(reader.getNumberOfProductOrdered("ok2"), OptionalInt.of(-403)))
        .append(futureAssertEquals(reader.getOrderIdsForUser("vb3"), Arrays.asList("ok2", "vjs")))
        .append(futureAssertEquals(reader.getCancelRatioForUser("vb3"), OptionalDouble.of(0.5)))
        .append(futureAssertEquals(reader.getTotalAmountSpentByUser("mjd"), 7453508L))
        .append(futureAssertEquals(reader.getAllItemsPurchased("mjd"),
            mapFrom(toEntry("djv", 61), toEntry("ury", 432))))
        .get());
  }

  @Test
  public void largeXml() throws Exception {
    test("large.xml", 3, reader -> new FutureTestAggregator()
        .append(futureAssertEquals(reader.isValidOrderId("bar"), true))
        .append(futureAssertEquals(reader.isModifiedOrder("bar"), true))
        .append(futureAssertEquals(reader.getHistoryOfOrder("bar"), Arrays.asList(817, 515, -1)))
        .append(futureAssertEquals(reader.getTotalNumberOfItemsPurchased("hox"), OptionalLong.of(1867)))
        .append(futureAssertEquals(reader.getItemsPurchasedByUsers("hox"),
            mapFrom(toEntry("dna", 847), toEntry("iiw", 451), toEntry("e89", 355), toEntry("dce", 214))))
        .get());
  }
}
