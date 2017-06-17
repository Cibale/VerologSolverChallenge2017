package main.java.genetic_algorithm.evaluation;

import main.java.ProblemModel;
import main.java.Vehicle;
import main.java.genetic_algorithm.Chromosome;

/**
 * Created by felentovic on 16.06.17.
 */
public class StandardEvaluation extends EvaluationFunction {


    public StandardEvaluation(ProblemModel model) {
        super(model);
    }
    

    @Override
    int calculatePunishment(Chromosome chromosome) {
        //punishment for: 1.) max distance traveled
        //                2.) load of the vehicle exceeded
        return 0;
    }


}
