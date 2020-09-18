import com.dyptan.Trainer;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

public class TrainerTest {

    @Ignore
    @Test
    public void runModelTrainer() throws IOException {
        Trainer modelTrainer = new Trainer();
        modelTrainer.train();
        modelTrainer.save();
        modelTrainer.spark.close();
    }

}
