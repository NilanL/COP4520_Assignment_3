import java.util.*;

public class Problem2 {

    Thread [] threads;
    double execTime;
    SensorRunnable [] sensorRunnables;
    int runForHours;

    static final int THREAD_COUNT = 8;
    static int currentMinute;
    volatile LockBasedArrayList<Double> lowestTemps;
    volatile LockBasedArrayList<Double> highestTemps;
    volatile List<LockBasedArrayList<Double>> temperatureList;

    public Problem2(int hours)
    {
        threads = new Thread[THREAD_COUNT];
        sensorRunnables = new SensorRunnable[THREAD_COUNT];
        runForHours = hours;
        lowestTemps = new LockBasedArrayList<>(5);
        highestTemps = new LockBasedArrayList<>(5);
        temperatureList = new ArrayList<>();
        currentMinute = 0;
    }

    public class SensorRunnable implements Runnable
    {
        Random rand;
        int sensorID;
        boolean isCancelled = false;
        SensorRunnable(int index)
        {
            rand = new Random(System.currentTimeMillis());
            sensorID = index;
        }

        public void wake()
        {
            synchronized(this)
            {
                this.notify();
            }
        }

        public void cancel()
        {
            synchronized(this)
            {
                this.isCancelled = true;
            }
        }

        private void setNewLowReading(Double reading) throws SensorReadingCancelledException
        {            
            synchronized(this)
            {
                for (int i = 0; i < lowestTemps.size(); i++) 
                {
                    if (this.isCancelled) 
                    {
                        throw new SensorReadingCancelledException("Sensor reading aborted", new Throwable());
                    }

                    if (lowestTemps.get(i) > reading && !this.isCancelled) 
                    {
                        lowestTemps.set(i, reading);
                    }
                }
            }
        }

        private void setNewHighReading(Double reading) throws SensorReadingCancelledException
        {            
            synchronized(this)
            {
                for (int i = 0; i < lowestTemps.size(); i++) 
                {
                    if (this.isCancelled) 
                    {
                        throw new SensorReadingCancelledException("Sensor reading cancelled", new Throwable());
                    }

                    if (lowestTemps.get(i) < reading && !this.isCancelled) 
                    {
                        lowestTemps.set(i, reading);
                    }
                }
            }
        }

        @Override
        public void run()
        {
            while (true)
            {
                synchronized(this)
                {
                    try
                    {
                        Double reading = rand.nextDouble(70 - (-100)) + (-100);

                        var test1 = temperatureList.get(currentMinute);
                        
                        test1.set(sensorID, reading);

                        setNewLowReading(reading);
                        setNewHighReading(reading);
                    }
                    catch (SensorReadingCancelledException e)
                    {
                        System.out.println(e.getMessage());
                    }
                    finally
                    {
                        try 
                        {
                            this.isCancelled = false;
                            this.wait();
                        } 
                        catch (InterruptedException e) 
                        {
                            e.printStackTrace();
                        }
                    }   
                }
            }
        }
    }

    public void checkThreads()
    {
        for (int j = 0; j < THREAD_COUNT; j++)
        {
            if (threads[j].getState().equals(Thread.State.WAITING))
            {
                sensorRunnables[j].cancel();
            }                 
        }
    }

    public static Double getAverage(LockBasedArrayList<Double> list) {
        Double sum = 0.0;
        for (int i = 0; i < list.size(); i++) 
        {
            sum += list.get(i);
        }
        return sum / list.size();
    }

    public List<Double> getLargestDifference(List<Double> list) 
    {
        List<Double> largestDiffList = new ArrayList<Double>();
        double largestDiff = 0;
        
        for (int i = 0; i < list.size() - 9; i += 10) 
        {
            double malist = list.get(i);
            double min = list.get(i);
            
            for (int j = i + 1; j < i + 10; j++) 
            {
                if (list.get(j) > malist) 
                {
                    malist = list.get(j);
                }
                if (list.get(j) < min) 
                {
                    min = list.get(j);
                }
            }
            
            double diff = malist - min;
            if (diff > largestDiff) 
            {
                largestDiff = diff;
                largestDiffList.clear();
                for (int j = i; j < i + 10; j++) 
                {
                    largestDiffList.add(list.get(j));
                }
            }
        }
        
        return largestDiffList;
    }

    public void printTemperatureList(LockBasedArrayList<Double> list)
    {
        System.out.print("[ ");
        for (int i = 0; i < list.size(); i++)
        {
            System.out.print(list.get(i) + " ");
        }
        System.out.println("]");
    }

    public void printTemperatureList(List<Double> list)
    {
        System.out.print("[ ");
        for (Double element : list)
        {
            System.out.print(element + " ");
        }
        System.out.println("]");
    }

    public void writeReport(int hour)
    {
        List<Double> averagedList = new ArrayList<Double>();
        
        for (LockBasedArrayList<Double> sensorList : temperatureList)
        {
            averagedList.add(getAverage(sensorList));
        }

        List<Double> largestDiff = getLargestDifference(averagedList);

        System.out.println("REPORT" + " (hour: " + hour + ")");
        System.out.println("----------------------------------------");
        System.out.println("5 highest temperature");
        printTemperatureList(highestTemps);

        System.out.println("5 lowest temperature");
        printTemperatureList(lowestTemps);

        System.out.println("10 minute interval of largest difference");
        printTemperatureList(largestDiff);
        System.out.println("----------------------------------------");

        lowestTemps = new LockBasedArrayList<>(5);
        highestTemps = new LockBasedArrayList<>(5);
    }

    public void runProblem2()
    {        
        for (int i = 0; i < THREAD_COUNT; i++)
        {
            sensorRunnables[i] = new SensorRunnable(i);
            threads[i] = new Thread(sensorRunnables[i]);
        }

        long currTime = System.currentTimeMillis();
        long lastTime = System.currentTimeMillis();
        int allottedHours = 1;
        int allotedMinutes = 0;
        while (temperatureList.size() < (runForHours * 60))
        {
            currTime = System.currentTimeMillis();
            //System.out.println("time: " + (currTime - lastTime) + (currTime - lastTime >= 1000.0));
            if (currTime - lastTime >= 1000.0)
            {
                //System.out.println("LETS GO");

                checkThreads();
                currentMinute++;
                temperatureList.add(new LockBasedArrayList<>(THREAD_COUNT));

                for (int i = 0; i < THREAD_COUNT; i++)
                {
                    if (threads[i].getState().equals(Thread.State.NEW))
                    {
                        threads[i].start();
                    }
                    else
                    {
                        sensorRunnables[i].wake();
                    }
                }
                lastTime = System.currentTimeMillis();
                allotedMinutes++;
            }

            if (allotedMinutes >= 60)
            {
                writeReport(allottedHours);
                allottedHours++;
                allotedMinutes = 0;
            }
        }
    }
}
