package big.data.tp2;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;

import java.util.Arrays;

import static jersey.repackaged.com.google.common.base.Preconditions.checkArgument;
public class WordCountTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(WordCountTask.class);

    public static void main(String[] args) {
        String hadoopHome = "C:\\winutils\\winutils\\hadoop-2.7.1";
        System.setProperty("hadoop.home.dir", hadoopHome);

        // Modify PATH
        String path = System.getenv("PATH");
        path = hadoopHome + "\\bin;" + path;
        System.setProperty("java.library.path", path);

        // Reload the modified library path
        try {
            java.lang.reflect.Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
            fieldSysPath.setAccessible(true);
            fieldSysPath.set(null, null);
        } catch (Exception ex) {
            // Handle the exception as needed
        }
        checkArgument(args.length > 1, "Please provide the path of input file and output dir as parameters.");
        new WordCountTask().run(args[0], args[1]);
    }

    public void run(String inputFilePath, String outputDir) {
        String master = "local[*]";
        SparkConf conf = new SparkConf()
                .setAppName(WordCountTask.class.getName())
                .setMaster(master);
        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaRDD<String> textFile = sc.textFile(inputFilePath);
        JavaPairRDD<String, Integer> counts = textFile
                .flatMap(s -> Arrays.asList(s.split(" t")).iterator())
                .mapToPair(word -> new Tuple2<>(word, 1))
                .reduceByKey((a, b) -> a + b);
        counts.saveAsTextFile(outputDir);
    }
}