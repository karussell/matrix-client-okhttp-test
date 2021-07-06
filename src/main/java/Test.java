import com.graphhopper.api.GHMRequest;
import com.graphhopper.api.GraphHopperMatrixWeb;
import com.graphhopper.api.MatrixResponse;
import com.graphhopper.util.shapes.GHPoint;

import java.util.Date;

public class Test {
    public static void main(String[] args) throws InterruptedException {

        GraphHopperMatrixWeb matrix = new GraphHopperMatrixWeb();
        if (args.length > 0)
            matrix.setKey(args[0]);
        long sleep = args.length > 1 ? Integer.parseInt(args[1]) : 1000;
        boolean infoLog = args.length > 2 ? Boolean.parseBoolean(args[2]) : false;

        while (true) {
            if (infoLog)
                System.out.println(new Date() + " request");
            GHMRequest req = new GHMRequest();
            req.addPoint(new GHPoint(51.534377, -0.087891));
            req.addPoint(new GHPoint(51.467697, -0.090637));
            try {
                MatrixResponse rsp = matrix.route(req);
                if (!rsp.getErrors().isEmpty())
                    System.err.println(new Date() + " should not happen " + rsp.getErrors());
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println(new Date() + " exception " + ex.getMessage());
            }
            Thread.sleep(sleep);
        }
    }
}
