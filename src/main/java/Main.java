package main.java;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    /**
     * Entry point.
     *
     * @param args input path and output path separated by one space
     */
    public static void main(String[] args) {
        Path output = Paths.get(args[1]);
        ProblemModel model = new ProblemModel();
        try {
            Parser.parseInput(args[0], model);
            Engine engine = new Engine(model);
            long cotDays = engine.decideDaysGA();
            long costInDay = engine.run();
            System.out.println("TOTAL COST IS:"+(costInDay+cotDays));
            Parser.writeOutput(output, engine.bestSolution);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
