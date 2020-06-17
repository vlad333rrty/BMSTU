import java.util.Scanner;
import static java.lang.Math.pow;

public class Kth{
    public static void main(String[] args){
        Scanner scanner=new Scanner(System.in);
        long k=scanner.nextLong();
        int i=0;
        while (k>=9*longPow(i)*(i+1)) k -= 9 * longPow(i) * (i++ + 1);
        i++;
        long mod=k%i;
        k/=i;
        long t=longPow(i-1)+k;
        mod=i-1-mod;
        while (mod>0) {
            t /= 10;
            mod--;
        }
        System.out.println(t%10);
    }

    private static long longPow(int i){
        return (long)pow(10,i);
    }
}
