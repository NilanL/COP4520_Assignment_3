import java.util.concurrent.locks.*;

public class LockBasedArrayList<T>
{
    Lock lock;
    Double [] objects;
    volatile int currIndex;

    LockBasedArrayList(int size)
    {
        lock = new ReentrantLock();
        objects = new Double [size];
        currIndex = 0;
    }

    public void add(Double val)
    {
        lock.lock();
        try
        {
            objects[currIndex] = val;
            currIndex++;
        }
        finally
        {
            lock.unlock();
        }
    }

    public Double get(int index)
    {
        lock.lock();
        try
        {
            return objects[index];
        }
        finally
        {
            lock.unlock();
        }
    }

    public void set(int index, Double val)
    {
        lock.lock();
        try
        {
            objects[index] = val;
        }
        finally
        {
            lock.unlock();
        }
    }

    public int size()
    {
        return currIndex;
    }
}
