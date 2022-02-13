/*
 * Copyright (c) 2018. Phasmid Software
 */

package edu.neu.coe.info6205.util;

import edu.neu.coe.info6205.sort.elementary.InsertionSort;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static edu.neu.coe.info6205.util.Utilities.formatWhole;

/**
 * This class implements a simple Benchmark utility for measuring the running time of algorithms.
 * It is part of the repository for the INFO6205 class, taught by Prof. Robin Hillyard
 * <p>
 * It requires Java 8 as it uses function types, in particular, UnaryOperator&lt;T&gt; (a function of T => T),
 * Consumer&lt;T&gt; (essentially a function of T => Void) and Supplier&lt;T&gt; (essentially a function of Void => T).
 * <p>
 * In general, the benchmark class handles three phases of a "run:"
 * <ol>
 *     <li>The pre-function which prepares the input to the study function (field fPre) (may be null);</li>
 *     <li>The study function itself (field fRun) -- assumed to be a mutating function since it does not return a result;</li>
 *     <li>The post-function which cleans up and/or checks the results of the study function (field fPost) (may be null).</li>
 * </ol>
 * <p>
 * Note that the clock does not run during invocations of the pre-function and the post-function (if any).
 *
 * @param <T> The generic type T is that of the input to the function f which you will pass in to the constructor.
 */
public class Benchmark_Timer<T> implements Benchmark<T> {

    /**
     * Calculate the appropriate number of warmup runs.
     *
     * @param m the number of runs.
     * @return at least 2 and at most m/10.
     */
    static int getWarmupRuns(int m) {
        return Integer.max(2, Integer.min(10, m / 10));
    }

    /**
     * Run function f m times and return the average time in milliseconds.
     *
     * @param supplier a Supplier of a T
     * @param m        the number of times the function f will be called.
     * @return the average number of milliseconds taken for each run of function f.
     */
    @Override
    public double runFromSupplier(Supplier<T> supplier, int m) {
        logger.info("Begin run: " + description + " with " + formatWhole(m) + " runs");
        // Warmup phase
        final Function<T, T> function = t -> {
            fRun.accept(t);
            return t;
        };
        new Timer().repeat(getWarmupRuns(m), supplier, function, fPre, null);

        // Timed phase
        return new Timer().repeat(m, supplier, function, fPre, fPost);
    }

    /**
     * Constructor for a Benchmark_Timer with option of specifying all three functions.
     *
     * @param description the description of the benchmark.
     * @param fPre        a function of T => T.
     *                    Function fPre is run before each invocation of fRun (but with the clock stopped).
     *                    The result of fPre (if any) is passed to fRun.
     * @param fRun        a Consumer function (i.e. a function of T => Void).
     *                    Function fRun is the function whose timing you want to measure. For example, you might create a function which sorts an array.
     *                    When you create a lambda defining fRun, you must return "null."
     * @param fPost       a Consumer function (i.e. a function of T => Void).
     */
    public Benchmark_Timer(String description, UnaryOperator<T> fPre, Consumer<T> fRun, Consumer<T> fPost) {
        this.description = description;
        this.fPre = fPre;
        this.fRun = fRun;
        this.fPost = fPost;
    }

    /**
     * Constructor for a Benchmark_Timer with option of specifying all three functions.
     *
     * @param description the description of the benchmark.
     * @param fPre        a function of T => T.
     *                    Function fPre is run before each invocation of fRun (but with the clock stopped).
     *                    The result of fPre (if any) is passed to fRun.
     * @param fRun        a Consumer function (i.e. a function of T => Void).
     *                    Function fRun is the function whose timing you want to measure. For example, you might create a function which sorts an array.
     */
    public Benchmark_Timer(String description, UnaryOperator<T> fPre, Consumer<T> fRun) {
        this(description, fPre, fRun, null);
    }

    /**
     * Constructor for a Benchmark_Timer with only fRun and fPost Consumer parameters.
     *
     * @param description the description of the benchmark.
     * @param fRun        a Consumer function (i.e. a function of T => Void).
     *                    Function fRun is the function whose timing you want to measure. For example, you might create a function which sorts an array.
     *                    When you create a lambda defining fRun, you must return "null."
     * @param fPost       a Consumer function (i.e. a function of T => Void).
     */
    public Benchmark_Timer(String description, Consumer<T> fRun, Consumer<T> fPost) {
        this(description, null, fRun, fPost);
    }

    /**
     * Constructor for a Benchmark_Timer where only the (timed) run function is specified.
     *
     * @param description the description of the benchmark.
     * @param f           a Consumer function (i.e. a function of T => Void).
     *                    Function f is the function whose timing you want to measure. For example, you might create a function which sorts an array.
     */
    public Benchmark_Timer(String description, Consumer<T> f) {
        this(description, null, f, null);
    }

    private final String description;
    private final UnaryOperator<T> fPre;
    private final Consumer<T> fRun;
    private final Consumer<T> fPost;

    final static LazyLogger logger = new LazyLogger(Benchmark_Timer.class);

    public static void main(String[] args) {
        InsertionSort insertionSort = new InsertionSort();
        Random random = new Random();

        for (int n = 50; n < 3500; n = n * 2) {
            //CASE: random array
            ArrayList<Integer> random_list = new ArrayList<>();
            for (int j = 0; j < n; j++) random_list.add(random.nextInt(n));

            //Converting lists to array through function list.toArrays
            Integer[] radndomArray = random_list.toArray(new Integer[0]);
            //Creating Benchmark
            Benchmark<Boolean> randomBenchmark = new Benchmark_Timer<>(
                    "Sorting Random", b -> {
                insertionSort.sort(radndomArray.clone(), 0, radndomArray.length);
            });
            //Running the benchmark
            double random_result = randomBenchmark.run(true, 10);
//***************************************************************************************************************************
            //CASE: ordered Array
            ArrayList<Integer> ordered_list = new ArrayList<Integer>();
            for (int j = 0; j < n; j++) ordered_list.add(j + 1);

            //Converting lists to array through function list.toArrays
            Integer[] ordered_Array = ordered_list.toArray(new Integer[0]);

            //Creating Benchmark
            Benchmark<Boolean> orderedBenchmark = new Benchmark_Timer<>(
                    "Sorting ordered", b -> {
                insertionSort.sort(ordered_Array.clone(), 0, ordered_Array.length);
            }
            );
            //Running the benchmark
            double ordered_result = orderedBenchmark.run(true, 10);
//***************************************************************************************************************************

            //CASE: reverse Array
            ArrayList<Integer> reverse_list = new ArrayList<Integer>();
            for (int a = 0; a < n; a++) {
                reverse_list.add(n - a);
            }

            //Converting lists to array through function list.toArrays
            Integer[] reverse_Array = reverse_list.toArray(new Integer[0]);

            //Creating Benchmark
            Benchmark<Boolean> reverseBenchmark = new Benchmark_Timer<>(
                    "Sorting reverse", b -> {
                insertionSort.sort(reverse_Array.clone(), 0, reverse_Array.length);
            }
            );
            //Running the benchmark
            double reverse_result = reverseBenchmark.run(true, 10);
//***************************************************************************************************************************

            //CASE: Partially ordered Array
            List<Integer> partially_ordered_list = new ArrayList<Integer>();
            for (int j = 0; j < n; j++) {
                if (j > n / 2) partially_ordered_list.add(random.nextInt(n));
                else partially_ordered_list.add(j);
            }

            //Converting lists to array through function list.toArrays
            Integer[] partially_ordered_Array = partially_ordered_list.toArray(new Integer[0]);

            //Creating Benchmark
            Benchmark<Boolean> partiallyOrderedBenchmark = new Benchmark_Timer<>(
                    "Sorting reverse", b -> {
                insertionSort.sort(partially_ordered_Array.clone(), 0, partially_ordered_Array.length);
            }
            );
            //Running the benchmark
            double partially_ordered_result = partiallyOrderedBenchmark.run(true, 10);

            System.out.println(n);
            System.out.println("random\t" + "ordered\t" + "reverse\t" + "partial_ordered\t");
            System.out.println(random_result + "\t" + ordered_result + "\t" + reverse_result + "\t" + partially_ordered_result);
        }
    }
}
