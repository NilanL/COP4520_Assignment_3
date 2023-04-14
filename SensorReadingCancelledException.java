public class SensorReadingCancelledException 
  extends RuntimeException {
    public SensorReadingCancelledException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}