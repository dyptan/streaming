import com.dyptan.StreamTransformer;
import org.junit.Test;

public class TransformerTest {

      @Test
    public void runTransformer() throws InterruptedException {
        StreamTransformer transformer = new StreamTransformer();
        Thread detatchedTransformer = new Thread(transformer);
        detatchedTransformer.start();
        Thread.sleep(30000);
        transformer.query.stop();
    }
}
