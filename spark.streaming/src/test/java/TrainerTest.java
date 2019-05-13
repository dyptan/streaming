import com.dyptan.streaming.spark.ModelTrainer;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.containsString;

public class TrainerTest {

    @Test
    public void testTrainer() throws IOException {
        ModelTrainer modelTrainer = new ModelTrainer();
        Assert.assertThat(Arrays.stream(modelTrainer.sparkConf.getAll()).collect( Collectors.toList() ).toString(), containsString("spark."));
    }
}
