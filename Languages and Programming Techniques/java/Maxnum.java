import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;
import java.util.stream.IntStream;

public class MaxNum{
    public static void main(String[] args){
        Scanner scan=new Scanner(System.in);
        int n=scan.nextInt();
        String[] s= IntStream.range(0, n).mapToObj(i -> scan.next()).toArray(String[]::new);
        Comparator<String> comparator= (s1,s2)->(s2+s1).compareTo(s1+s2);
        Arrays.sort(s,comparator);
        Arrays.stream(s).forEach(System.out::print);
    }
}


