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
        String[] inputArray = {
                "test_instances/ORTEC_Test_01.txt",
                "test_instances/ORTEC_Test_02.txt",
                "test_instances/ORTEC_Test_03.txt",
                "test_instances/ORTEC_Test_04.txt",
                "test_instances/ORTEC_Test_05.txt",
                "test_instances/ORTEC_Test_06.txt",
                "test_instances/ORTEC_Test_07.txt",
                "test_instances/ORTEC_Test_08.txt",
                "test_instances/ORTEC_Test_09.txt",
                "test_instances/ORTEC_Test_10.txt"
        };
        String[] outputArray = {
                "daysRandom-output_01.txt",
                "daysRandom-output_02.txt",
                "daysRandom-output_03.txt",
                "daysRandom-output_04.txt",
                "daysRandom-output_05.txt",
                "daysRandom-output_06.txt",
                "daysRandom-output_07.txt",
                "daysRandom-output_08.txt",
                "daysRandom-output_09.txt",
                "daysRandom-output_10.txt",
        };
        for(int i = 0; i <10; i++){
            Path output = Paths.get(outputArray[i]);
            ProblemModel model = new ProblemModel();
            try {
                Parser.parseInput(inputArray[i], model);
                Engine engine = new Engine(model);
                engine.decideDays();
                //engine.decideDaysDFS();
                //engine.decideDaysGA();
                long costInDay = engine.run();
                //System.out.println("TOTAL COST IS:"+(costInDay+cotDays));
                Parser.writeOutput(output, engine.bestSolution);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
