import com.dyptan.Trainer;
import org.junit.Test;

import java.io.IOException;

public class TrainerTest {

    @Test
    public void runModelTrainer() throws IOException {
        Trainer modelTrainer = new Trainer();
        modelTrainer.train();
        modelTrainer.save();
        modelTrainer.spark.close();
    }

}
