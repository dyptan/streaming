package com.dyptan;

import org.apache.spark.SparkConf;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.feature.Imputer;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.regression.LinearRegression;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import scala.collection.JavaConversions;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

//enum TRAINER_SETTINGS {
//    ELASTIC_TYPE,
//    MODEL_PATH
//}

public class ModelTrainer {
    private static final Logger logger = Logger.getLogger(ModelTrainer.class.getName());

    public SparkConf sparkConf = null;//    just for testing
    private PipelineModel pipelineModel = null;
    private SparkSession spark = null;
    private Properties prop = null;

//    Map<TRAINER_SETTINGS, String> TRAINER_CONFIG = new HashMap<TRAINER_SETTINGS, String>();

    Map<String, String> ES_CONFIG = new HashMap<String, String>();

//    public String MODEL_PATH = ".trainedModel";
//    {
//        ES_CONFIG.put("es.read.field.as.array.include","tags");
////        ES_CONFIG.put("es.read.field.as.array.include","com.dyptan.web.model");
//        ES_CONFIG.put("es.nodes","localhost");
//        ES_CONFIG.put("es.port","9200");
//    }

//    public final String ELASTIC_TYPE = "olx/cars";

//    public ModelTrainer set(TRAINER_SETTINGS setting, String value) {
//        TRAINER_CONFIG.put(setting, value);
//        return this;
//    }

    public ModelTrainer() throws IOException {

//        Spark config section

        InputStream sparkDefaults = getClass().getClassLoader()
                .getResourceAsStream("spark-defaults.properties");

        prop = new Properties();
        prop.load(sparkDefaults);

        Map<String, String> scalaProps = new HashMap<>();
        scalaProps.putAll((Map)prop);

        sparkConf = new SparkConf();
        sparkConf.setAll(JavaConversions.mapAsScalaMap(scalaProps));

        spark = SparkSession
                .builder()
                .appName("ReadFromElasticAndTrainModel")
                .config(sparkConf)
                .getOrCreate();

        spark.sparkContext().setLogLevel("ERROR");


//        ES config section

        InputStream elasticProperties = getClass().getClassLoader()
                .getResourceAsStream("trainer.properties");

        prop.load(elasticProperties);
        ES_CONFIG.putAll((Map)prop);

    }

    public void train() {


        Dataset<Row> cars = spark.read().format("org.elasticsearch.spark.sql").options(ES_CONFIG)
                .option("inferSchema", true)
                .load();

        logger.warning(cars.schema().mkString());

        Dataset<Row> selected = cars.select("category", "price_usd","engine_cubic_cm","race_km",
                "model",
                "year","published").limit(100);
        logger.info("Pre-transformed data sample: \n"+selected.showString(10, 10, false));
        logger.info("Rows count in train data set: "+selected.count());

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
        linearRegression.setMaxIter(100);

        Pipeline pipeline = new Pipeline()
                .setStages(new PipelineStage[] {
                        imputer, assembler, linearRegression
                });

        // Splitting train and evaluating data
        Dataset<Row>[] splitDF = labelDF.randomSplit(new double[] { 0.8, 0.2 });

        Dataset<Row> trainDF = splitDF[0];
//        Dataset<Row> evaluationDF = splitDF[1];

        pipelineModel = pipeline.fit(trainDF);

        // Evaluation itself
//        Dataset<Row> predictionsDF = pipelineModel.transform(evaluationDF);
//
////        predictionsDF.show(false);
//
//        Dataset<Row> forEvaluationDF = predictionsDF.select("label",
//                "prediction");
//
//        RegressionEvaluator evaluteR2 = new RegressionEvaluator().setMetricName("r2");
//        RegressionEvaluator evaluteRMSE = new RegressionEvaluator().setMetricName("rmse");
//
//        double r2 = evaluteR2.evaluate(forEvaluationDF);
//        double rmse = evaluteRMSE.evaluate(forEvaluationDF);
//
//        logger.warning("---------------------------");
//        logger.warning("R2 =" + r2);
//        logger.warning("RMSE =" + rmse);
//        logger.warning("---------------------------");

        spark.cloneSession();
    }

    public void save() {
        //Saving com.dyptan.web.model to disk
        try {
            logger.warning("Saving to "+ES_CONFIG.getOrDefault("model.path", ".trainedModel"));
            pipelineModel.write().overwrite().save(ES_CONFIG.getOrDefault("model.path", ".trainedModel"));
            logger.warning("Model successfully saved.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
