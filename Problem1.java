import java.util.*;
import java.util.stream.Collectors;

public class Problem1 {

    Thread [] threads;
    double execTime;
    PresentRunnable [] helperRunnables;

    static final int THREAD_COUNT = 4;
    static final int PRESENTS_COUNT = 500000;
    volatile ConcurrentLinkedList<Integer> presentChain;
    volatile List<Integer> unorderedPresents;
    volatile Integer headPresent;
    volatile Integer tailPresent;

    public Problem1()
    {
        unorderedPresents = new ArrayList<Integer>(PRESENTS_COUNT);
        threads = new Thread[THREAD_COUNT];
        helperRunnables = new PresentRunnable[THREAD_COUNT];
    }

    public class PresentRunnable implements Runnable
    {
        Random rand;
        PresentRunnable()
        {
            rand = new Random(System.currentTimeMillis());
        }

        public void wake()
        {
            synchronized(this)
            {
                this.notify();
            }
        }

        private void addPresent()
        {            
            synchronized(this)
            {
                int presentNum = this.rand.nextInt(unorderedPresents.size());
                Integer present = unorderedPresents.remove(presentNum);

                if (present != null)
                {
                    System.out.println("Present #" + present + " added to present chain");

                    presentChain.add(present);
                }
            }
        }

        private void writeThankYou()
        {
            synchronized(this)
            {
                Integer key = presentChain.getRandom();
                
                if (key != null && key != headPresent && key != tailPresent)
                {
                    System.out.println("Present #" + key + " given a 'thank you' note and removed from the present chain");
                    
                    presentChain.remove(key);
                }
            }
        }

        private void checkPresent()
        {
            synchronized(this)
            {
                Integer key = presentChain.getRandom();

                if (key != null && key != headPresent && key != tailPresent)
                {
                    if (presentChain.contains(key))
                    {
                        System.out.println("Present chain contains present #" + key);
                    }
                    else
                    {
                        System.out.println("Present chain does NOT contain present #" + key);
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
                    int action = this.rand.nextInt(3) + 1;

                    switch (action)
                    {
                        case 1: // Add present to list
                            if (unorderedPresents.size() > 0)
                            {
                                this.addPresent();
                            }
                            else if (presentChain.length() > 2)
                            {
                                this.writeThankYou();
                            }
                            else
                            {
                                System.out.println("No more presents can be added to the chain");
                            }
                            break;
                        case 2: // Write thank you letter
                            if (presentChain.length() > 2)
                            {
                                this.writeThankYou();
                            }
                            else if (unorderedPresents.size() > 0)
                            {
                                this.addPresent();
                            }
                            break;
                        case 3: // Check present
                            if (presentChain.length() > 2)
                            {
                                this.checkPresent();
                            }
                            break;
                    }

                    try 
                    {
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

    public void runProblem1()
    {

        Random rand = new Random(System.currentTimeMillis());

        // Generate unordered presents
        unorderedPresents = rand.ints(0, PRESENTS_COUNT)
                            .distinct()
                            .limit(PRESENTS_COUNT)
                            .boxed()
                            .collect(Collectors.toList());
        
        headPresent = Integer.MIN_VALUE;
        tailPresent = Integer.MAX_VALUE;

        for (int i = 0; i < THREAD_COUNT; i++)
        {
            helperRunnables[i] = new PresentRunnable();
            threads[i] = new Thread(helperRunnables[i]);
        }

        presentChain = new ConcurrentLinkedList<Integer>(headPresent, tailPresent);

        long startTime = System.currentTimeMillis();

        while (presentChain.length() > 2 || !unorderedPresents.isEmpty())
        {
            int index = rand.nextInt(THREAD_COUNT);

            if (threads[index].getState().equals(Thread.State.NEW))
            {
                threads[index].start();
            }
            else
            {
                helperRunnables[index].wake();
            }
        }

        long endTime = System.currentTimeMillis();

        System.out.println("All presents have been gone through");
        System.out.println("Unordered presents left: " + unorderedPresents.size());
        System.out.println("Presents still in present chain: " + presentChain.length());


        System.out.println("Completed in " + ((endTime - startTime) / 1000.0) + " seconds");
    }
}
