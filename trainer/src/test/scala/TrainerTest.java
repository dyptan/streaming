import com.dyptan.ModelTrainer;
import org.junit.Test;

import java.io.IOException;

public class TrainerTest {

    @Test
    public void runModelTrainer() throws IOException {
        ModelTrainer modelTrainer = new ModelTrainer();
        modelTrainer.train();
        modelTrainer.save();
        modelTrainer.spark.close();
    }

}
