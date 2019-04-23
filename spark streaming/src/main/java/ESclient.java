import com.mongodb.spark.MongoSpark;
import org.apache.spark.api.java.function.VoidFunction2;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.evaluation.RegressionEvaluator;
import org.apache.spark.ml.feature.Imputer;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.regression.LinearRegression;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.types.StructType;
import org.elasticsearch.spark.sql.api.java.JavaEsSparkSQL;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.from_json;
import static org.apache.spark.sql.types.DataTypes.*;

public class ESclient {
    private static final Logger logger = Logger.getLogger(ESclient.class.getName());


    private static StructType RSS_SCHEMA = new StructType()
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


    public static void main(String[] args) throws InterruptedException, StreamingQueryException {

        Map<String, String> myconfig = new HashMap<String, String>();
        myconfig.put("es.read.field.as.array.include", "tags");
//        myconfig.put("es.read.field.as.array.include", "model");

        SparkSession spark = SparkSession
                .builder()
                .master("local[*]")
                .appName("JavaStructuredNetworkWordCount")
                .config("spark.driver.bindAddress", "localhost")
                .config("spark.mongodb.output.uri", "mongodb://localhost/olx.cars")
                .getOrCreate();

        spark.sparkContext().setLogLevel("ERROR");

        Dataset<Row> cars = spark.read().format("org.elasticsearch.spark.sql").options(myconfig).schema(RSS_SCHEMA).load("cars3/cars");
        cars.printSchema();

//        Dataset<Row> cars = JavaEsSparkSQL.esDF(spark, "olxua/cars",  "{\n" +
////                "    \"_source\": {\"excludes\": \"tags\"}," +
//                "    \"sort\" :" +
//                "        { \"published\" : {\"order\" : \"asc\"}},\n" +
//                "    \n" +
//                "    \"size\" : 1,"+
//                "    \"query\" : {\n" +
//                "        \"match_all\" : {}\n" +
//                "    }\n" +
//                "}"
////                , myconfig
//        );

        Dataset<Row> selected = cars.select("category", "model","price_usd","engine_cubic_cm","race_km","year","published");
        selected.show();
        System.out.println("Count is: "+selected.count());

//        StreamingQuery query = cars.writeStream()
//                .outputMode("append")
//                .format("console")
//                .option("truncate", false)
//                .start();

        Dataset<Row> labelDF = selected.withColumnRenamed("price_usd", "label");

        Imputer imputer = new Imputer()
                // .setMissingValue(1.0d)
                .setInputCols(new String[] { "engine_cubic_cm", "race_km","year" })
                .setOutputCols(new String[] { "~engine_cubic_cm~", "~race_km~","~year~" });

        VectorAssembler assembler = new VectorAssembler()
                .setInputCols(new String[] { "~engine_cubic_cm~","~race_km~","~year~" })
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
        Dataset<Row> evaluationDF = splitDF[1];

        PipelineModel pipelineModel = pipeline.fit(trainDF);

        // Evaluation
        Dataset<Row> predictionsDF = pipelineModel.transform(evaluationDF);

        predictionsDF.show(false);

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
                .option("subscribe", "olx2")
                .option("startingOffsets", "earliest")
                .load();

//Generate a result table.
        final Dataset<Row> carsStream = ads.select(
                from_json(
                        col("value").cast("string"), RSS_SCHEMA)
                        .alias("olx"))
//                .where("olx.price_usd>8000")
                .select("olx.*");

        Dataset<Row> streamPredictionsDF = pipelineModel.transform(carsStream);

        Dataset<Row> filteredDf = streamPredictionsDF.drop("features");


        StreamingQuery query = filteredDf.writeStream().foreachBatch(
                new VoidFunction2<Dataset<Row>, Long>(){

                    public void call(Dataset<Row> records, Long batchId) throws Exception {
                        MongoSpark.save(records);
                    }
                }
        ).start();

        query.awaitTermination();

    }
}
