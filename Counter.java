public class Counter {
  private int value;

  Counter(int value)
  {
    this.value = value;
  }

  // Threads-safe counter value getting and incrementing
  public int getAndIncrement()
  {
    synchronized (this) 
    {
      int temp = this.value;
      this.value = temp + 1;
      return temp;
    }
  }

  // Threads-safe counter value getting and decrementing
  public int getAndDecrement()
  {
    synchronized (this) 
    {
      int temp = this.value;
      this.value = temp - 1;
      return temp;
    }
  }

  public int get()
  {
    synchronized (this)
    {
      return this.value;
    }
  }
}
