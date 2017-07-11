package org.springbootcamp.spike;

import io.vavr.*;
import io.vavr.collection.List;
import io.vavr.collection.Stream;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

@Slf4j
public class VavrSpike {

  @Test
  public void print_a_list() throws Exception {
    List<String> strings = List.of("foo", "bar");


    strings.map(String::toUpperCase).crossProduct(2).forEach(System.out::println);
  }

  @Test
  public void divide_by_zero() throws Exception {

    Supplier<Integer> divide = () -> 1/0;
    log.info(Try.ofSupplier(divide)
      .onFailure(e -> log.error(e.toString()))
      .recover(ArithmeticException.class, 1)
      .toEither().toString());
  }

  @Test
  public void tuples() throws Exception {

    Tuple3<String,String,Long> tuple = Tuple.of("foo","bar", 5L);

    log.info(tuple.toString());
    log.info("1: {}", tuple._1);
    log.info("2: {}", tuple._2);
    log.info("3: {}", tuple._3);

    log.info(tuple.map2(String::toUpperCase).toString());
    Function3<String, String, Long, String> combine = (a,b,c) ->  a + b + c*c;

    log.info(tuple.apply(combine));
    log.info(combine.tupled().apply(tuple));
  }

  @Test
  public void functions() throws Exception {
    Function2<Integer, Integer, Integer> divide = (a, b) -> a / b;
    // We use lift to turn divide into a total function that is defined for all inputs.

    Function2<Integer, Integer, Option<Integer>> safeDivide = Function2.lift(divide);

    // = None
    Option<Integer> i1 = safeDivide.apply(1, 0);

    // = Some(2)
    Option<Integer> i2 = safeDivide.apply(4, 2);

    log.info(i1.toString());
    log.info(i2.toString());
  }

  @Test
  public void partial_application() throws Exception {
    Function5<Integer, Integer, Integer, Integer, Integer, Integer> sum = (a, b, c, d, e) -> a + b + c + d + e;
    Function2<Integer, Integer, Integer> add6 = sum.apply(2, 3, 1);

    log.info(add6.apply(4, 3).toString());
  }

  @Test
  public void memoization() throws Exception {
    Function0<Double> hashCache = Function0.of(Math::random).memoized();

    double randomValue1 = hashCache.apply();
    double randomValue2 = hashCache.apply();

    log.info("{}={}", randomValue1, randomValue2);
  }

  @Test
  public void _100_rand() throws Exception {
    //Stream.continually(Math::random).take(100).map(String::valueOf).forEach(log::info);

    Stream.from(1,1)
      .take(100)
      .filter(j -> j % 2 == 0)
      .map(i -> Match(i).of(
        Case($(1), "one"),
        Case($(2), "two"),
        Case($(), "?")
      ))
      ;


    //Stream.from(1).filter(i -> i % 2 == 0).takeUntil(i -> i < 100).map(String::valueOf).forEach(log::info);
  }
}
