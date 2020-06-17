import java.util.Scanner;

public class MinDist {
    public static void main(String[] args){
        Scanner scan = new Scanner(System.in);
        String s = scan.nextLine();
        char x = scan.next().charAt(0), y = scan.next().charAt(0);
        int dist = Integer.MAX_VALUE;
        int a=s.indexOf(x),b = s.indexOf(y);
        do {
            if (Math.abs(a - b) - 1 < dist) dist = Math.abs(a - b)-1;
            if (a < b) s = s.substring(a+1);
            else s = s.substring(b+1);
            a = s.indexOf(x);
            b = s.indexOf(y);
        } while (a != -1 && b != -1);
        System.out.println(dist);
    }
}

