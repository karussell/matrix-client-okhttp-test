import com.graphhopper.api.GHMRequest;
import com.graphhopper.api.GHMatrixSyncRequester;
import com.graphhopper.api.GraphHopperMatrixWeb;
import com.graphhopper.api.MatrixResponse;
import com.graphhopper.util.shapes.GHPoint;
import okhttp3.OkHttpClient;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Test {

    // java -jar some.jar [API_KEY] [sleep_in_ms] [enable_log] [threads] [add_gzip_interceptor]
    public static void main(String[] args) {
        boolean addGzipInterceptor = args.length > 4 ? Boolean.parseBoolean(args[4]) : false;
        OkHttpClient.Builder okClientBuilder = new OkHttpClient.Builder().
                readTimeout(10, TimeUnit.SECONDS).
                connectTimeout(10, TimeUnit.SECONDS);

        if (addGzipInterceptor) okClientBuilder.addInterceptor(new GzipRequestInterceptor());

        GraphHopperMatrixWeb matrix = new GraphHopperMatrixWeb(new GHMatrixSyncRequester().setDownloader(okClientBuilder.build()));
        if (args.length > 0)
            matrix.setKey(args[0]);
        long sleep = args.length > 1 ? Integer.parseInt(args[1]) : 1000;
        boolean infoLog = args.length > 2 ? Boolean.parseBoolean(args[2]) : false;
        int threads = args.length > 3 ? Integer.parseInt(args[3]) : 1;
        ExecutorService exec = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            final int threadIdx = i;
            exec.submit(() -> {
                        while (true) {
                            if (infoLog)
                                System.out.println("[" + threadIdx + "] " + new Date().toInstant() + " request");
                            GHMRequest req = new GHMRequest();
                            // we do not care about the actual matrix. The request and response just shouldn't be too small:
                            for (int j = 0; j < 50; j++) {
                                req.addPoint(new GHPoint(51.534377, -0.087891));
                                req.addPoint(new GHPoint(51.467697, -0.090637));
                            }
                            try {
                                MatrixResponse rsp = matrix.route(req);
                                if (!rsp.getErrors().isEmpty())
                                    System.err.println("[" + threadIdx + "] " + new Date() + " should not happen " + rsp.getErrors());
                            } catch (Exception ex) {
                                // ex.printStackTrace();
                                System.err.println("[" + threadIdx + "] " + new Date() + " exception " + ex.getMessage());
                            }
                            Thread.sleep(sleep);
                        }
                    }
            );
        }
    }
}
