import java.math.BigInteger;
import java.util.Scanner;

public class FastFib {
    public static void main(String[] args){
        Scanner scanner=new Scanner(System.in);
        int n=scanner.nextInt(); 
        System.out.println(binPow(n)[1][0]);
    }

    private static BigInteger[][] binPow(int n){
        BigInteger[][] a=new BigInteger[][] {{BigInteger.ONE,BigInteger.ONE},{BigInteger.ONE,BigInteger.ZERO}};
        BigInteger[][] b=new BigInteger[][] {{BigInteger.ONE,BigInteger.ZERO},{BigInteger.ZERO,BigInteger.ONE}};
        while (n>0){
            if (n%2!=0) b=mul(b,a);
            a=mul(a,a);
            n/=2;
        }
        return b;
    }

    private static BigInteger[][] mul(BigInteger[][] a,BigInteger[][] b){
        BigInteger[][] res=new BigInteger[][]{{BigInteger.ZERO,BigInteger.ZERO},{BigInteger.ZERO,BigInteger.ZERO}};
        for (int i=0;i<2;i++){
            for (int j=0;j<2;j++){
                for (int k=0;k<2;k++) {
                    res[i][j] = res[i][j].add(a[i][k].multiply(b[k][j]));
                }
            }
        }
        return res;
    }
}

