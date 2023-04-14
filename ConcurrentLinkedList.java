import java.util.*;
import java.util.concurrent.locks.*;

public class ConcurrentLinkedList<T extends Comparable<T>> 
{
	private Node<T> head;
    public volatile Counter length;
    private volatile Random rand;
	
	public ConcurrentLinkedList() 
	{
		this.head = null;
        rand = new Random(System.currentTimeMillis());
        length = new Counter(0);
	}

	public ConcurrentLinkedList(T head) 
	{
		this.head = new Node<T>(head);
        this.head.next = null;
        rand = new Random(System.currentTimeMillis());
        length = new Counter(1);
	}

    public ConcurrentLinkedList(T head, T tail) 
	{
		this.head = new Node<T>(head);
        this.head.next = new Node<T>(tail);
        rand = new Random(System.currentTimeMillis());
        length = new Counter(2);
	}
	
    public boolean add(T item) 
    {
        int key = item.hashCode();
        while (true) 
        {
            Node<T> pred = head;
            Node<T> curr = pred.next;

            while (curr.key < key) 
            {
                pred = curr; 
                curr = curr.next;
            }
            
            pred.lock(); 
            curr.lock();
            
            try 
            {
                if (validate(pred, curr)) 
                {
                    if (curr.key == key) 
                    {
                        return false;
                    } 
                    else 
                    {
                        Node<T> node = new Node<T>(item);
                        node.next = curr;
                        pred.next = node;
                        length.getAndIncrement();
                        return true;
                    }
                }
            } 
            finally 
            {
                pred.unlock(); 
                curr.unlock();
            }
        }
    }
    
    public boolean remove(int key) 
    {
        while (true)
        {
            Node<T> pred = head;
            Node<T> curr = pred.next;
            
            while (curr.key < key) 
            {
                pred = curr; 
                curr = curr.next;
            }
            pred.lock(); 
            curr.lock();
            
            try 
            {
                if (validate(pred, curr)) 
                {
                    if (curr.key == key) 
                    {
                        pred.next = curr.next;
                        length.getAndDecrement();
                        return true;
                    } 
                    else 
                    {
                        return false;
                    }
                }
            } 
            finally 
            {
                pred.unlock(); 
                curr.unlock();
            }
        }
    }

    public Integer getRandom() 
    {
        int nodeIndex = 0;
        int currIndex = 0;

        if (length.get() == 2)
        {
            return null;
        }
        else if (length.get() == 3)
        {
            nodeIndex = 1;
        }
        else
        {
            while (nodeIndex == 0 || nodeIndex == length.get() - 1)
                nodeIndex = rand.nextInt(length.get() - 1) + 1;
        }
        //System.out.println("Index looked for: " + nodeIndex);

        Node<T> pred = head;
        Node<T> curr = pred.next;

        while (currIndex < nodeIndex)
        {
            pred = curr;
            curr = curr.next;
            currIndex++;
        }

        return pred.key;
    }
    
    public boolean contains(int key) 
    {
        while (true) 
        {
            Node<T> pred = this.head; // sentinel node;
            Node<T> curr = pred.next;

            while (curr.key < key) 
            {
                pred = curr; 
                curr = curr.next;
            }

            pred.lock(); 
            curr.lock(); 

            try 
            {
                if (validate(pred, curr)) 
                {
                    return (curr.key == key);
                }
            } 
            finally 
            { 
                // always unlock
                pred.unlock(); 
                curr.unlock();
            }
        }
    }
    
    private boolean validate(Node<T> pred, Node<T> curr) 
    {
        Node<T> node = head; 
        while (node.key <= pred.key) 
        {
            if (node == pred)
            {
                return pred.next == curr;
            }

            node = node.next;
        }
        return false;
    }

    public int length()
    {
        return length.get();
    }

    public class Node<T extends Comparable<T>> 
    {
        T item;
        int key;
        Node<T> next;
        Lock lock;

        public Node(T element)
        {
            item = element;
            lock = new ReentrantLock();
            key = item.hashCode();
        }

        public void lock()
        {
            lock.lock();
        }

        public void unlock()
        {
            lock.unlock();
        }
    }
}
