package com.dyptan;

import com.mongodb.spark.MongoSpark;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.VoidFunction2;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.types.StructType;
import scala.collection.JavaConversions;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.from_json;
import static org.apache.spark.sql.types.DataTypes.*;

public class StreamTransformer implements  Runnable{
    final static Logger logger = Logger.getLogger(StreamTransformer.class.getName());

    private final StructType STREAMING_RSS_SCHEMA = new StructType()
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

    private String KAFKA_BOOTSTRAP_SERVER = null;
    private String KAFKA_TOPIC = null;
    private String MODEL_PATH = null;
    private SparkSession spark = null;
    public StreamingQuery query = null;

    public StreamTransformer() {

//      Loading up Stream properties

        InputStream sparkDefaults = getClass().getClassLoader()
                .getResourceAsStream("application.properties");

        Properties STREAM_CONFIG = new Properties();
        try {
            STREAM_CONFIG.load(sparkDefaults);
        } catch (IOException e) {
            e.printStackTrace();
        }

//      Setting up Stream properties

        KAFKA_BOOTSTRAP_SERVER = STREAM_CONFIG.getProperty("source.kafka.bootstrap.server", "kafka:9092");
        KAFKA_TOPIC = STREAM_CONFIG.getProperty("source.kafka.topic", "olx");
        MODEL_PATH = STREAM_CONFIG.getProperty("model.load.path", "/tmp/trainedmodel");

//      Converting Java Properties to SparkConf

        Map<String, String> scalaProps = new HashMap<>();
        scalaProps.putAll((Map)STREAM_CONFIG);

        SparkConf sparkConf = new SparkConf();
        sparkConf.setAll(JavaConversions.mapAsScalaMap(scalaProps));

        spark = SparkSession
                .builder()
                .appName("StreamingApplication")
                .config(sparkConf)
                .getOrCreate();

        spark.sparkContext().setLogLevel("WARN");

    }

    @Override
    public void run() {
//      Start reading data from source
        Dataset<Row> rawKafkaStream = spark
                .readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", KAFKA_BOOTSTRAP_SERVER)
                .option("subscribe", KAFKA_TOPIC)
                .option("startingOffsets", "earliest")
                .load();

        Dataset<Row> parsedStream = rawKafkaStream.select(
                from_json(
                        col("value").cast("string"), STREAMING_RSS_SCHEMA)
                        .alias("unbounded_table"))
                .select("unbounded_table.*");

//      Augmenting stream data with types
        Dataset<Row> structuredStream = parsedStream.select(
                col("year").cast(DoubleType),
                col("category"),
                col("price_usd").cast(DoubleType),
                col("model"),
                col("engine_cubic_cm").cast(DoubleType),
                col("race_km").cast(DoubleType),
                col("published")
        );

        PipelineModel pipelineModel = PipelineModel.load(MODEL_PATH);

        Dataset<Row> streamPredictionsDF = pipelineModel.transform(structuredStream);

        Dataset<Row> filteredDf = streamPredictionsDF.drop("features");

        query = filteredDf.writeStream()
//                .outputMode("append").format("console")
                .foreachBatch(
                (VoidFunction2<Dataset<Row>, Long>) (records, batchId) -> {
                    logger.warning(records.showString(10, 15, false));
                    MongoSpark.save(records);
                }
        ).start();

        try {
            query.awaitTermination();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
