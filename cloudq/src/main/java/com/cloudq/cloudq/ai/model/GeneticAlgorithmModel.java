package com.cloudq.cloudq.ai.model;


import java.util.Arrays;
import java.util.Random;

public class GeneticAlgorithmModel implements OptimizationAlgorithmModel {
    private final int populationSize; // Size of the population
    private final int generations;      // Number of generations to run the algorithm
    private final double mutationRate;  // Mutation rate for genetic diversity

    public GeneticAlgorithmModel(int populationSize, int generations, double mutationRate) {
        this.populationSize = populationSize;
        this.generations = generations;
        this.mutationRate = mutationRate;
    }

    @Override
    public void optimize(OptimizationTask task) {
        Random random = new Random();
        double[][] population = new double[populationSize][1]; // Assuming 1-dimensional optimization

        // Initialize population within the bounds
        for (int i = 0; i < populationSize; i++) {
            population[i][0] = task.getBounds()[0] + random.nextDouble() * (task.getBounds()[1] - task.getBounds()[0]);
        }

        // Run the optimization for a number of generations
        for (int generation = 0; generation < generations; generation++) {
            // Evaluate fitness of the population
            double[] fitness = evaluateFitness(population, task.getFunction());

            // Select the fittest individuals for reproduction
            double[][] newPopulation = new double[populationSize][1];
            for (int i = 0; i < populationSize; i++) {
                int parent1Index = selectParent(fitness);
                int parent2Index = selectParent(fitness);
                newPopulation[i] = crossover(population[parent1Index], population[parent2Index]);

                // Mutate
                if (random.nextDouble() < mutationRate) {
                    mutate(newPopulation[i]);
                }
            }

            population = newPopulation;
        }

        // Print the best solution
        double[] bestSolution = findBestSolution(population, task.getFunction());
        System.out.println("Best solution: " + Arrays.toString(bestSolution));
    }

    private double[] evaluateFitness(double[][] population, String function) {
        // Simple evaluation assuming the function is f(x) = x^2
        return Arrays.stream(population)
                .mapToDouble(individual -> Math.pow(individual[0], 2)) // Replace with actual function evaluation
                .toArray();
    }

    private int selectParent(double[] fitness) {
        double totalFitness = Arrays.stream(fitness).sum();
        double randomValue = Math.random() * totalFitness;

        for (int i = 0; i < fitness.length; i++) {
            randomValue -= fitness[i];
            if (randomValue <= 0) {
                return i;
            }
        }
        return fitness.length - 1; // Fallback
    }

    private double[] crossover(double[] parent1, double[] parent2) {
        return new double[]{(parent1[0] + parent2[0]) / 2}; // Simple average crossover
    }

    private void mutate(double[] individual) {
        individual[0] += (Math.random() - 0.5); // Simple mutation
    }

    private double[] findBestSolution(double[][] population, String function) {
        // Assuming the goal is to minimize the function
        return Arrays.stream(population)
                .min((a, b) -> Double.compare(evaluateFitness(new double[][]{a}, function)[0], evaluateFitness(new double[][]{b}, function)[0]))
                .orElse(new double[]{});
    }
}
