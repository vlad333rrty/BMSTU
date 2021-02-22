#include <vector>

class Zn{
public:
    explicit Zn(int n):table(n,std::vector<int>(n)){
        if (n<2) throw std::range_error("");
        this->n=n;
    }

    std::vector<std::vector<int>> getTable(){
        if (!evaluated){
            evaluate();
            evaluated= true;
        }
        return table;
    }
private:
    int n;
    std::vector<std::vector<int>> table;
    bool evaluated=false;
    void evaluate(){
        for (int i=0;i<n;i++){
            for (int j=0;j<n;j++){
                table[i][j]=(i*j)%n;
            }
        }
    }
};