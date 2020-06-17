import java.util.Scanner;

public class Gauss {
    private static Fraction[][] matrix;
    public static void main(String[] args) throws NoSolution {
        Scanner scanner=new Scanner(System.in);
        int n=scanner.nextInt();
        matrix=new Fraction[n][n+1];
        for (int i=0;i<n;i++){
            for (int j=0;j<=n;j++){
                matrix[i][j]=new Fraction(scanner.nextInt(),1);
            }
        }
        Fraction[] answer=solve();
        for (Fraction fraction : answer) {
            System.out.println(fraction);
        }
    }

    private static void prepare() throws NoSolution{
        for (int i=0,j;i<matrix.length;i++){
            for (j=0;j<matrix[i].length;j++){
                if (matrix[i][j].num != 0) break;
            }
            if (j==matrix[i].length) throw new NoSolution();
        }
        for (int i=0;i<matrix.length;i++){
            if (matrix[i][i].num==0){
                for (int j=0;j<matrix.length;j++){
                    if (matrix[j][i].num!=0 && matrix[i][j].num!=0) {
                        swapRows(i, j);
                        break;
                    }
                }
            }
        }
    }

    private static void makeStair(int i) throws NoSolution {
        boolean flag=false;
        for (int j=i+1;j<matrix.length;j++){
            if (matrix[j][i].num!=0) {
                swapRows(i, j);
                flag=true;
                break;
            }
        }
        if (!flag) throw new NoSolution();
    }

    private static void swapRows(int i,int j){
        for (int k=0;k<matrix[i].length;k++){
            Fraction t=matrix[i][k];
            matrix[i][k]=matrix[j][k];
            matrix[j][k]=t;
        }
    }

    private static void makeIdentity() throws NoSolution {
        prepare();
        for (int i=0;i<matrix.length;i++){
            Fraction t=matrix[i][i];
            if (t.num==0) {
                makeStair(i);
                t=matrix[i][i];
            }
            for (int j=i;j<matrix[i].length;j++){
                matrix[i][j]=matrix[i][j].div(t);
            }
            for (int j=i+1;j<matrix.length;j++){
                t=matrix[j][i];
                for (int k=i;k<matrix[j].length;k++){
                    matrix[j][k]=matrix[j][k].sub(matrix[i][k].mul(t));
                }
            }
        }
    }

    private static Fraction[] solve() throws NoSolution {
        makeIdentity();
        Fraction[] answer=new Fraction[matrix.length];
        for (int i=matrix.length-1;i>=0;i--){
            for (int j=matrix.length-1;j>i;j--){
                matrix[i][matrix.length]=matrix[i][matrix.length].sub(answer[j].mul(matrix[i][j]));
            }
            answer[i]=matrix[i][matrix.length];
        }
        return answer;
    }
}

class Fraction{
    int num,den;
    public Fraction(int num, int den) {
        int gcd=gcd(Math.abs(num),Math.abs(den));
        this.num = num/gcd;
        this.den = den/gcd;
    }

    private int gcd(int a,int b){
        int t;
        while (b>0){
            a%=b;
            t=a;
            a=b;
            b=t;
        }
        return a;
    }

    private Fraction makeNormal(int n,int d){
        int gcd=gcd(Math.abs(n),Math.abs(d));
        n/=gcd;
        d/=gcd;
        if (n*d>0) return new Fraction(Math.abs(n),Math.abs(d));
        if (n==0) return new Fraction(0,1);
        return new Fraction(-Math.abs(n),Math.abs(d));
    }

    Fraction mul(Fraction fraction){
        int n=num*fraction.num;
        int d=den*fraction.den;
        return makeNormal(n,d);
    }


    Fraction div(Fraction fraction){
        int n=num*fraction.den;
        int d=den*fraction.num;
        return makeNormal(n,d);
    }

    Fraction add(Fraction fraction){
        int n1=num*fraction.den;
        int n2=fraction.num*den;
        int n=n1+n2;
        int d=den*fraction.den;
        return makeNormal(n,d);
    }

    Fraction sub(Fraction fraction){
        int n1=num*fraction.den;
        int n2=fraction.num*den;
        int n=n1-n2;
        int d=den*fraction.den;
        return makeNormal(n,d);
    }

    @Override
    public String toString() {
        return num+"/"+den;
    }
}

class NoSolution extends Exception{
    NoSolution(){
        System.out.println("No solution");
        System.exit(0);
    }
}
