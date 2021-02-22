#include <vector>
#include <set>

class Permutation{
public:
    Permutation(int *perm,int n):n(n){
        permutation.resize(n);
        std::copy(perm,perm+n,permutation.begin());
    }

    Permutation(std::vector<int> perm,int n):n(n){
        permutation.resize(n);
        copy(perm.begin(),perm.end(),permutation.begin());
    }

    explicit Permutation(int n):n(n){
        permutation.resize(n);
    }

    void printPermCycles(){
        if (!isBijection()) return;
        std::vector<bool> checked(n);
        for (int i=0;i<n;i++){
            if (!checked[i]){
                checked[i]=true;
                if (permutation[i]!=i){
                    std::vector<int> perm;
                    int j=i;
                    do{
                        checked[j] = true;
                        perm.push_back(j);
                        j=permutation[j];
                    }while (i!=j);
                    std::cout<<"( ";
                    for (int k : perm){
                        std::cout<<k<<" ";
                    }
                    std::cout<<")";
                }
            }
        }
    }

    std::vector<Permutation> generateAndGetPermutations(){
        Permutation current=Permutation(permutation,n);
        Permutation sigma=Permutation(permutation,n);
        std::vector<Permutation> res;
        if (!isBijection()){
            for (int i=0;i<10;i++){
                res.push_back(current);
                current=current*sigma;
            }
        }else {
            while (!current.isIdentity()) {
                res.push_back(current);
                current = current * sigma;
            }
        }
        res.push_back(current);
        return res;
    }

    std::set<int> getOrbit(int q){
        if (q<0 || q>=n) throw std::range_error("Wrong argument");
        std::vector<Permutation> generatedPermutations= generateAndGetPermutations();
        std::set<int> result;
        for (auto & i : generatedPermutations){
            result.insert(i.permutation[q]);
        }
        return result;
    }

    Permutation& operator=(const Permutation &perm){
        if (n!=perm.n) throw std::range_error("Wrong argument");
        if (this!=&perm){
            copy(perm.permutation.begin(),perm.permutation.end(),permutation.begin());
        }
        return *this;
    }

    Permutation operator*(const Permutation &perm){
        Permutation result=Permutation(n);
        for (int i=0;i<n;i++){
            result.permutation[i]=permutation[perm.permutation[i]];
        }
        return result;
    }

    friend std::ostream& operator<<(std::ostream &os,const Permutation &perm){
        os<<'('<<" ";
        for (int i=0;i<perm.n;i++){
            os<<i<<" ";
        }
        os<<std::endl;
        os<<"  ";
        for (int i=0;i<perm.n;i++){
            os<<perm.permutation[i]<<" ";
        }
        os<<" "<<')';
        return os;
    }
private:
    int n;
    std::vector<int> permutation;
    bool isBijection(){
        std::set<int> s;
        return std::all_of(permutation.begin(),permutation.end(),[](int i){return s.insert(i).second});
    }
    bool isIdentity(){
        for (int i=0;i<n;i++){
            if (i!=permutation[i]) return false;
        }
        return true;
    }
};