package test.java;
import main.java.Parser;
import main.java.ProblemModel;
import main.java.Request;
import main.java.Tool;
import org.junit.*;

import java.awt.*;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Created by mmatak on 6/15/17.
 */
public class ParserTest {
    @Test
    public void input() throws IOException {
        // assert statements
        ProblemModel model = new ProblemModel();
        String file = "test_instances/ORTEC_Test_01.txt";
        Parser.parseInput(file,model);
        assertEquals(model.name, "Instance 01");
        assertEquals(model.days, 5);
        assertEquals(model.capacity, 77);
        assertEquals(model.maxTripDistance, 29446);
        assertEquals(model.depotCoordinateId, 0);
        assertEquals(model.vehicleCost, 10);
        assertEquals(model.vehicleDayCost, 100);
        assertEquals(model.distanceCost, 1);
        assertEquals(model.tools.length, 3);

        int tool1Id = 1;
        int tool1Size = 2;
        int tool1Num = 280;
        int tool1Cost = 10000;
        Tool tool1 = new Tool(tool1Id,tool1Size,tool1Num,tool1Cost);

        assertEquals(model.tools[tool1Id], tool1);

        assertEquals(model.coordinates.length, 151);

        Point point = new Point(5445,2890);
        assertEquals(model.coordinates[0],point);

        assertEquals(model.requests.length, 151);

        Request request = new Request(1,45,2,3,1,1,2, false);
        assertEquals(model.requests[1], request);




    }
}
