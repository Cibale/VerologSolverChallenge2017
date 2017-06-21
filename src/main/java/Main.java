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
                "daysGA-output_01.txt",
                "daysGA-output_02.txt",
                "daysGA-output_03.txt",
                "daysGA-output_04.txt",
                "daysGA-output_05.txt",
                "daysGA-output_06.txt",
                "daysGA-output_07.txt",
                "daysGA-output_08.txt",
                "daysGA-output_09.txt",
                "daysGA-output_10.txt",
        };
        for(int i = 0; i <10; i++){
            Path output = Paths.get(outputArray[i]);
            ProblemModel model = new ProblemModel();
            try {
                Parser.parseInput(inputArray[i], model);
                Engine engine = new Engine(model);
                //engine.decideDays();
                //engine.decideDaysDFS();
                engine.decideDaysGA();
                long costInDay = engine.run();
                //System.out.println("TOTAL COST IS:"+(costInDay+cotDays));
                Parser.writeOutput(output, engine.bestSolution);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
