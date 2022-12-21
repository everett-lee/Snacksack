package com.snacksack.snacksack;


import com.snacksack.snacksack.dp.Solver;
import com.snacksack.snacksack.dp.bottomUp.BottomUpSolver;
import com.snacksack.snacksack.dp.bottomUp.BottomUpSolverThreaded;
import com.snacksack.snacksack.model.NormalisedProduct;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class DpBenchmark {
    Set<NormalisedProduct> products;


    public static void main(String[] args) throws RunnerException {
        Options opt = (new OptionsBuilder()).include(DpBenchmark.class.getSimpleName()).warmupIterations(2).warmupForks(1).measurementIterations(2).forks(1).mode(Mode.AverageTime).jvmArgs(new String[]{"-Xms8g", "-Xmx20g"}).build();
        (new Runner(opt)).run();
    }

    @Setup(Level.Trial)
    public void setUp() {
        Random random = new Random();
        int min = 100;
        int max = 5000;
        int n = 2000;
        this.products = IntStream.range(0, n).mapToObj((i) -> {
            int calories = random.nextInt(max - min + 1) + min;
            int price = random.nextInt(max - min + 1) + min;
            return new NormalisedProduct(String.valueOf(i), calories, price);
        }).collect(Collectors.toSet());
    }

    @Benchmark
    public void testBottomUp() {
        Solver solver = new BottomUpSolver();
        solver.solve(100_000, this.products);
    }

    @Benchmark
    public void testBottomUpThreaded() {
        Solver solver = new BottomUpSolverThreaded(5_000);
        solver.solve(100_000, this.products);
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // total money = 500,000, threshold = 5_000
    // single thread = 3432ms, multi thread = 1590ms
    //
    // total money = 250,000, threshold = 5_000
    // single thread = 1472ms, multi thread = 1026ms
    //
}

