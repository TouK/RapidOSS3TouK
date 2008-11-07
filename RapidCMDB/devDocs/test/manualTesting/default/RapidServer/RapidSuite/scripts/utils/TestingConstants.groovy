package utils

import java.text.SimpleDateFormat;
class TestingConstants
{
    public static java.text.SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss.SSS");
    public static String MEMORY_TEST_RESULTS_ROOT_DIR = "memoryTestResults";
    public static String MEMORY_DUMP_DIR = "${MEMORY_TEST_RESULTS_ROOT_DIR}/dumps";
    public static String MEMORY_HISTOGRAMS_DIR = "${MEMORY_TEST_RESULTS_ROOT_DIR}/histograms";

    public static File getHeapDumpFile()
    {
        return new File("$MEMORY_DUMP_DIR/heap${format.format (new Date())}.bin")
    }
    public static File getHeapHistogramFile()
    {
        return new File("$MEMORY_HISTOGRAMS_DIR/dump${format.format (new Date())}.txt")
    }
}