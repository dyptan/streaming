package com.dyptan;

import com.mongodb.spark.MongoSpark;
import org.apache.spark.api.java.function.VoidFunction2;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.types.StructType;

import java.util.logging.Logger;

import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.from_json;
import static org.apache.spark.sql.types.DataTypes.*;

public class StreamTransformer {
    final static Logger logger = Logger.getLogger(StreamTransformer.class.getName());

    private StructType STREAMING_RSS_SCHEMA = null;
    private String MONGO_COLLECTION = null;
    private String KAFKA_BOOTSTRAP_SERVER = null;
    private String KAFKA_TOPIC = null;
    private String MODEL_PATH = null;
    public StreamingQuery query = null;

    public StreamTransformer() {
                STREAMING_RSS_SCHEMA = new StructType()
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

        MONGO_COLLECTION = "localhost/olx.cars";
        KAFKA_BOOTSTRAP_SERVER = "localhost:9092";
        KAFKA_TOPIC = "olx";
        MODEL_PATH = ".trainedModel";

    }

    public void readStreamAndTransform() throws StreamingQueryException {


        SparkSession spark = SparkSession
                .builder()
                .master("local[*]")
                .appName("JavaStructuredNetworkWordCount")
                .config("spark.driver.bindAddress", "localhost")
                .config("spark.mongodb.output.uri", "mongodb://"+ MONGO_COLLECTION)
                .getOrCreate();

        spark.sparkContext().setLogLevel("WARN");

        Dataset<Row> rawKafkaStream = spark
                .readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", KAFKA_BOOTSTRAP_SERVER)
                .option("subscribe", KAFKA_TOPIC)
                .option("startingOffsets", "earliest")
                .load();

        final Dataset<Row> parsedStream = rawKafkaStream.select(
                from_json(
                        col("value").cast("string"), STREAMING_RSS_SCHEMA)
                        .alias("unbounded_table"))
                .select("unbounded_table.*");

        Dataset<Row> structuredStream = parsedStream.select(
                col("year").cast(DoubleType),
                col("category"),
                col("price_usd"),
//                col("model"),
                col("engine_cubic_cm").cast(DoubleType),
                col("race_km").cast(DoubleType));

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

        query.awaitTermination();

    }
}
