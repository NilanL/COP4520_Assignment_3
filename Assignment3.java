public class Assignment3 
{
    private static void part1()
    {
        Problem1 prob1 = new Problem1();
        prob1.runProblem1();
    }

    private static void part2()
    {
        Problem2 prob2 = new Problem2(1);
        prob2.runProblem2();
    }

    public static void main(String [] args)
    {
        if (args.length == 0)
        {
            part1();
            part2();
        }
        else if (args[0].equals("part1"))
        {
            part1();
        }
        else if (args[0].equals("part2"))
        {
            part2();
        }
    }
}
