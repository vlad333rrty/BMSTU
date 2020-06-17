#include <vector>
#include <functional>
using namespace std;

template<class T>
struct Cell{
    function<T()> func;
    Cell(function<T()> f):func(f){};
    Cell()= default;

    operator T(){
        return func();
    }

    Cell<T>&operator=(const Cell<T> &cell){
        if (this!=&cell) func=cell.func;
        return *this;
    }
    Cell<T>&operator=(T val){
        func=[val]()->T{ return val;};
        return *this;
    }

    Cell<T>operator+(const Cell<T> &cell){
        return Cell([&cell,this]()->T{ return this->func()+cell.func();});
    }
    Cell<T>operator-(const Cell<T> &cell){
        return Cell([&cell,this]()->T{ return this->func()-cell.func();});
    }
    Cell<T>operator*(const Cell<T> &cell){
        return Cell([&cell,this]()->T{ return this->func()*cell.func();});
    }
    Cell<T>operator/(const Cell<T> &cell){
        return Cell([&cell,this]()->T{ return this->func()/cell.func();});
    }

    Cell<T>operator+=(const Cell<T> &cell){
        Cell<T> c=*this;
        return Cell(func=[&cell,c]()->T{ return c.func()+cell.func();});
    }
    Cell<T>operator-=(const Cell<T> &cell){
        Cell<T> c=*this;
        return Cell(func=[&cell,c]()->T{ return c.func()-cell.func();});
    }
    Cell<T>operator*=(const Cell<T> &cell){
        Cell<T> c=*this;
        return Cell(func=[&cell,c]()->T{ return c.func()*cell.func();});
    }
    Cell<T>operator/=(const Cell<T> &cell){
        Cell<T> c=*this;
        return Cell(func=[&cell,c]()->T{ return c.func()/cell.func();});
    }

    Cell<T>operator+(T val){
        return Cell([val,this]()->T{ return this->func()+val;});
    }
    Cell<T>operator-(T val){
        return Cell([val,this]()->T{ return this->func()-val;});
    }
    Cell<T>operator*(T val){
        return Cell([val,this]()->T{ return this->func()*val;});
    }
    Cell<T>operator/(T val){
        return ([val,this]()->T{ return this->func()/val;});
    }

    Cell<T>operator+=(T val){
        Cell<T> c=*this;
        return Cell(func=[val,c]()->T{ return c.func()+val;});
    }
    Cell<T>operator-=(T val){
        Cell<T> c=*this;
        return Cell(func=[val,c]()->T{ return c.func()-val;});
    }
    Cell<T>operator*=(T val){
        Cell<T> c=*this;
        return Cell(func=[val,c]()->T{ return c.func()*val;});
    }
    Cell<T>operator/=(T val){
        Cell<T> c=*this;
        return Cell(func=[val,c]()->T{ return c.func()/val;});
    }

    friend Cell<T>operator+(T val,const Cell &cell){
        return Cell([val,&cell]()->T{ return val+cell.func();});
    }
    friend Cell<T>operator-(T val,const Cell &cell){
        return Cell([val,&cell]()->T{ return val-cell.func();});
    }
    friend Cell<T>operator*(T val,const Cell &cell){
        return Cell([val,&cell]()->T{ return val*cell.func();});
    }
    friend Cell<T>operator/(T val,const Cell &cell){
        return Cell([val,&cell]()->T{ return val/cell.func();});
    }

    Cell<T>operator-(){
        return Cell([this]()->T{ return -func();});
    }
};

template <class T>
class SuperCalc{
    vector<vector<Cell<T>>> table;
public:
    SuperCalc(int m, int n):table(m,vector<Cell<T>>(n)){}
    Cell<T>& operator() (int i, int j){
        return table[i][j];
    }
};
