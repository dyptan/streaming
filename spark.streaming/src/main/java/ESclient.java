import com.mongodb.spark.MongoSpark;
import org.apache.spark.api.java.function.VoidFunction2;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.feature.Imputer;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.regression.LinearRegression;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.types.StructType;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.from_json;
import static org.apache.spark.sql.types.DataTypes.*;

public class ESclient {
    private static final Logger logger = Logger.getLogger(ESclient.class.getName());


    private static StructType STATIC_RSS_SCHEMA = new StructType()
            .add("link", StringType, true)
            .add("@timestamp", TimestampType, true)
            .add("model", StringType, true)
            .add("price_usd", DoubleType, true)
            .add("race_km", DoubleType, true)
            .add("year", DoubleType, true)
            .add("category", StringType, true)
            .add("engine_cubic_cm", DoubleType, true)
            .add("message", StringType, true)
            .add("title", StringType, true)
            .add("published", TimestampType, true);

    private static StructType STREAMING_RSS_SCHEMA = new StructType()
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

        Map<String, String> myconfig = new HashMap<String, String>();
        myconfig.put("es.read.field.as.array.include", "tags");

        SparkSession spark = SparkSession
                .builder()
                .master("local[*]")
                .appName("JavaStructuredNetworkWordCount")
                .config("spark.driver.bindAddress", "localhost")
                .config("spark.mongodb.output.uri", "mongodb://localhost/olx.cars")
                .getOrCreate();

        spark.sparkContext().setLogLevel("WARN");

        Dataset<Row> cars = spark.read().format("org.elasticsearch.spark.sql").options(myconfig).schema(STATIC_RSS_SCHEMA).load("cars3/cars");

        Dataset<Row> selected = cars.select("category", "model","price_usd","engine_cubic_cm","race_km","year","published");
//        selected.show();
//        System.out.println("Count is: "+selected.count());

        Dataset<Row> labelDF = selected.withColumnRenamed("price_usd", "label");

        Imputer imputer = new Imputer()
                // .setMissingValue(1.0d)
                .setInputCols(new String[] { "engine_cubic_cm", "race_km","year" })
                .setOutputCols(new String[] { "engine_cubic_cm", "race_km","year" });

        VectorAssembler assembler = new VectorAssembler()
                .setInputCols(new String[] { "engine_cubic_cm","race_km","year" })
                .setOutputCol("features");

        // Choosing a Model
        LinearRegression linearRegression = new LinearRegression();
        linearRegression.setMaxIter(1000);

        Pipeline pipeline = new Pipeline()
                .setStages(new PipelineStage[] {
                        imputer, assembler, linearRegression
                });

        Dataset<Row>[] splitDF = labelDF.randomSplit(new double[] { 0.8, 0.2 });

        Dataset<Row> trainDF = splitDF[0];
//        Dataset<Row> evaluationDF = splitDF[1];

        PipelineModel pipelineModel = pipeline.fit(trainDF);

        // Evaluation
//        Dataset<Row> predictionsDF = pipelineModel.transform(evaluationDF);
//
//        predictionsDF.show(false);

//        Dataset<Row> forEvaluationDF = predictionsDF.select("label",
//                "prediction");

//        RegressionEvaluator evaluteR2 = new RegressionEvaluator().setMetricName("r2");
//        RegressionEvaluator evaluteRMSE = new RegressionEvaluator().setMetricName("rmse");
//
//        double r2 = evaluteR2.evaluate(forEvaluationDF);
//        double rmse = evaluteRMSE.evaluate(forEvaluationDF);
//
//        logger.info("---------------------------");
//        logger.info("R2 =" + r2);
//        logger.info("RMSE =" + rmse);
//        logger.info("---------------------------");

        Dataset<Row> ads = spark
                .readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", "localhost:9092")
                .option("subscribe", "olx")
                .option("startingOffsets", "earliest")
                .load();

        final Dataset<Row> rawCarsStream = ads.select(
                from_json(
                        col("value").cast("string"), STREAMING_RSS_SCHEMA)
                        .alias("olx"))
                .select("olx.*");

        Dataset<Row> structuredCarsStream = rawCarsStream.select(col("year").cast(DoubleType),
                col("category"),
                col("engine_cubic_cm").cast(DoubleType), col("race_km").cast(DoubleType));

        Dataset<Row> streamPredictionsDF = pipelineModel.transform(structuredCarsStream);

        Dataset<Row> filteredDf = streamPredictionsDF.drop("features");

        StreamingQuery query = filteredDf.writeStream()
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
