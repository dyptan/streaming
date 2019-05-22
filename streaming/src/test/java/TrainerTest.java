import com.dyptan.ModelTrainer;
import com.dyptan.StreamTransformer;
import org.junit.Test;

import java.io.IOException;

import static java.lang.Thread.sleep;

public class TrainerTest {

    @Test
    public void runModelTrainer() throws IOException {
        ModelTrainer modelTrainer = new ModelTrainer();
        modelTrainer.train();
        modelTrainer.save();
        modelTrainer.spark.close();
    }

    @Test
    public void runTransformer() throws InterruptedException {
        StreamTransformer transformer = new StreamTransformer();
        Thread detatchedTransformer = new Thread(transformer);
        detatchedTransformer.start();
        sleep(15000);
        transformer.query.stop();
    }
}
