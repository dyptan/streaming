import com.dyptan.ModelTrainer;
import com.dyptan.StreamTransformer;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;
import static org.hamcrest.CoreMatchers.containsString;

public class TrainerTest {

    @Test
    public void getSparkProperties() throws IOException {
        ModelTrainer modelTrainer = new ModelTrainer();
        Assert.assertThat(Arrays.stream(modelTrainer.sparkConf.getAll()).collect( Collectors.toList() ).toString(), containsString("spark."));
    }

    @Test
    public void runModelTrainer() throws IOException {
        ModelTrainer modelTrainer = new ModelTrainer();
        modelTrainer.train();
        modelTrainer.save();
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
