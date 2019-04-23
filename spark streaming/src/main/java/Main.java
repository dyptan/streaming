import com.mongodb.spark.MongoSpark;
import org.apache.spark.api.java.function.VoidFunction2;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.types.StructType;

import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.from_json;
import static org.apache.spark.sql.types.DataTypes.StringType;
import static org.apache.spark.sql.types.DataTypes.TimestampType;

public class Main {

    private static StructType RSS_SCHEMA = new StructType()
            .add("link", StringType, true)
            .add("@timestamp", TimestampType, true)
            .add("model", StringType, true)
            .add("price_usd", StringType, true)
            .add("race_km", StringType, true)
            .add("year", StringType, true)
            .add("category", StringType, true)
            .add("engine_cubic_cm", StringType, true)
            .add("message", StringType, true)
            .add("title", StringType, true)
            .add("published", TimestampType, true);



    public static void main(String[] args) throws InterruptedException, StreamingQueryException {


        SparkSession spark = SparkSession
                .builder()
                .master("local[*]")
                .appName("JavaStructuredKafkaWordCount")
                .config("spark.mongodb.output.uri", "mongodb://localhost/olx.cars")
                .getOrCreate();

        spark.sparkContext().setLogLevel("ERROR");

//Create a DataSet representing the stream of input lines from Kafka
        Dataset<Row> ads = spark
                .readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", "localhost:9092")
                .option("subscribe", "olx")
                .option("startingOffsets", "latest")
                .load();

//Generate a result table
        final Dataset<Row> luxuryCars = ads.select(
                  from_json(
                            col("value").cast("string"), RSS_SCHEMA)
                          .alias("olx"))
                .where("olx.price_usd>8000")
                .select("olx.*");


//Run the query that saves result table to MongoDB

        StreamingQuery query = luxuryCars.writeStream().foreachBatch(
        new VoidFunction2<Dataset<Row>, Long>(){

            public void call(Dataset<Row> records, Long batchId) throws Exception {
                MongoSpark.save(records);
            }
        }
        ).start();

        query.awaitTermination();

    }
}
