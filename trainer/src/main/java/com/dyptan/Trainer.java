
package com.dyptan;

import org.apache.spark.SparkConf;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.evaluation.RegressionEvaluator;
import org.apache.spark.ml.feature.Imputer;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.regression.LinearRegression;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import static scala.collection.JavaConversions.mapAsScalaMap;

public class Trainer {
    private static final Logger logger = Logger.getLogger(Trainer.class.getName());


    private PipelineModel pipelineModel = null;
    public SparkSession spark = null;
    private Map<String, String> ES_CONFIG = new HashMap<String, String>();
    private String MODEL_PATH;
    private Dataset<Row>[] splitDF = null;
    private Properties prop = new Properties();

    public Trainer() throws IOException {

        InputStream properties = getClass().getClassLoader()
                .getResourceAsStream("conf/application.properties");

        prop.load(properties);

        Map<String, String> scalaProps = new HashMap<>();
        scalaProps.putAll((Map)prop);

        SparkConf sparkConf = new SparkConf();
        sparkConf.setAll(mapAsScalaMap(scalaProps));

        spark = SparkSession
                .builder()
                .appName("ReadFromElasticAndTrainModel")
                .config(sparkConf)
                .getOrCreate();
        spark.sparkContext().setLogLevel("ERROR");

        //      load ES default config
        // InputStream elasticProperties = getClass().getClassLoader()
        //         .getResourceAsStream("conf/application.properties");

        // prop.load(elasticProperties);

        ES_CONFIG.putAll((Map)prop);

    }

    // override default ES config
    public void setSource(URL source, int limit, int iterations) {
        Properties properties = new Properties();
        properties.setProperty("es.nodes", source.getHost());
        properties.setProperty("es.port", String.valueOf(source.getPort()));
        properties.setProperty("es.resource", source.getPath());
        //TODO duplicate properties in config file
        properties.setProperty("ml.source.limit", String.valueOf(limit));
        properties.setProperty("ml.iterations", String.valueOf(iterations));

        ES_CONFIG.putAll((Map) properties);
    }

    public void train() {
        Dataset<Row> cars = spark.read().format("org.elasticsearch.spark.sql").options(ES_CONFIG)
                .option("inferSchema", true)
                .load();

        logger.info("Source schema: " + cars.schema().treeString());

        Dataset<Row> selected = cars.select(
                "category",
                "price_usd",
                "engine_cubic_cm",
                "race_km",
                "model",
                "year",
                "published")
                .limit(Integer.valueOf(ES_CONFIG.getOrDefault("ml.source.limit", "100")));

        logger.info("Pre-transformed data sample: \n"+selected.showString(10, 10, false));
        logger.info("Train dataset is limited to " + selected.count() + " rows");

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
        linearRegression.setMaxIter(Integer.valueOf(ES_CONFIG.getOrDefault("ml.iterations", "1000")));

        Pipeline pipeline = new Pipeline()
                .setStages(new PipelineStage[] {
                        imputer, assembler, linearRegression
                });

        // Splitting train and evaluating data

        splitDF = labelDF.randomSplit(new double[] { 0.8, 0.2 });

        Dataset<Row> trainDF = splitDF[0];

        pipelineModel = pipeline.fit(trainDF);

    }

    public void evaluate(){

        Dataset<Row> evaluationDF = splitDF[1];

        Dataset<Row> predictionsDF = pipelineModel.transform(evaluationDF);

        logger.info("Transformed data with predictions: \n"+predictionsDF.showString(10, 10, false));

        Dataset<Row> forEvaluationDF = predictionsDF.select("label", "prediction");

        RegressionEvaluator evaluateR2 = new RegressionEvaluator().setMetricName("r2");
        RegressionEvaluator evaluateRMSE = new RegressionEvaluator().setMetricName("rmse");

        double r2 = evaluateR2.evaluate(forEvaluationDF);
        double rmse = evaluateRMSE.evaluate(forEvaluationDF);

        logger.warning("---------------------------");
        logger.warning("R2 =" + r2);
        logger.warning("RMSE =" + rmse);
        logger.warning("---------------------------");
    }

    public void save() throws IOException {
        //Saving model to disk
        MODEL_PATH = ES_CONFIG.getOrDefault("model.path", "/tmp/trainedmodel");
        
            logger.warning("Saving to "+MODEL_PATH);
            pipelineModel.write().overwrite().save(MODEL_PATH);

            logger.warning("Model successfully saved.");          
    }
}
