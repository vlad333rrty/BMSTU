#include <iostream>
#include <vector>
#include <deque>
using namespace std;
enum Type{
    PLUS,MINUS,MULT,DEL,CELL,CONST,U_MINUS
};
template <class T>
class Cell{
private:
    struct content{
        Type type;
        const Cell *cell;
        T value;
        explicit content(const Cell *c):cell(c){
            type=CELL;
        }
        explicit content(T val): value(val){
            type=CONST;
            cell= nullptr;
        }
        explicit content(Type t): type(t){
            cell=nullptr;
        }
    };
    bool is_default = false;
    deque<content> order;
    typedef typename deque<content>::const_iterator iter;
    T eval(iter &it) const{
        switch (it->type){
            case CELL:
                return (T)*(it++)->cell;
            case CONST:
                return (it++)->value;
            case PLUS:
                it++;
                return eval(it)+eval(it);
            case MINUS:
                it++;
                return eval(it)-eval(it);
            case U_MINUS:
                return -eval(++it);
            case MULT:
                it++;
                return eval(it)*eval(it);
            case DEL:
                it++;
                return eval(it)/eval(it);
            default:
                break;
        }
    }
    void add(const Cell &c){
        if (c.is_default){
            order.emplace_back(&c);
        }else{
            order.insert(order.end(),c.order.begin(),c.order.end());
        }
    }
public:
    Cell():is_default(true){};
    Cell(const Cell &c1,const Cell &c2,Type t){
        add(c1);
        add(c2);
        order.emplace_front(t);
    }
    Cell(const Cell &c,T v,Type t){
        add(c);
        order.emplace_back(v);
        order.emplace_front(t);
    }
    Cell(T v,const Cell &c,Type t){
        add(c);
        order.emplace_front(v);
        order.emplace_front(t);
    }
    Cell(const Cell &c,Type t){
        add(c);
        order.emplace_front(t);
    }
    Cell &operator=(T val){
        order.clear();
        order.emplace_back(val);
        return *this;
    }
    explicit operator T() const{
        iter it=this->order.begin();
        return eval(it);
    }
    Cell<T>operator+(T v){
        return Cell(*this,v,PLUS);
    }
    Cell<T>operator-(T v){
        return Cell(*this,v,MINUS);
    }
    Cell<T>operator*(T v){
        return Cell(*this,v,MULT);
    }
    Cell<T>operator/(T v){
        return Cell(*this,v,DEL);
    }
    Cell<T>operator+(const Cell &cell){
        return Cell(*this,cell,PLUS);
    }
    Cell<T>operator-(const Cell &cell){
        return Cell(*this,cell,MINUS);
    }
    Cell<T>operator/(const Cell &cell){
        return Cell(*this,cell,DEL);
    }
    Cell<T>operator*(const Cell &cell){
        return Cell(*this,cell,MULT);
    }
    Cell<T>operator+=(const Cell &cell){
        add(cell);
        order.emplace_front(PLUS);
        return *this;
    }

    Cell<T>operator-=(const Cell &cell){
        add(cell);
        order.emplace_front(MINUS);
        return *this;
    }
    Cell<T>operator*=(const Cell &cell){
        add(cell);
        order.emplace_front(MULT);
        return *this;
    }
    Cell<T>operator/=(const Cell &cell){
        add(cell);
        order.emplace_front(DEL);
        return *this;
    }
    Cell<T>operator-(){
        return Cell(*this,U_MINUS);
    }
    friend Cell<T>operator-(T v,const Cell &cell){
        return Cell(v,cell,MINUS);
    }
    friend Cell<T>operator*(T v,const Cell &cell){
        return Cell(v,cell,MULT);
    }
    friend Cell<T>operator/(T v,const Cell &cell){
        return Cell(v,cell,DEL);
    }
    friend Cell<T>operator+(T v,const Cell &cell){
        return Cell(v,cell,PLUS);
    }
    Cell<T>operator+=(T v){
        vector<content> vec;
        vec.emplace_back(PLUS);
        vec.insert(vec.end(),order.begin(),order.end());
        vec.emplace_back(v);
        order=vec;
        return *this;
    }
    Cell<T>operator-=(T v){
        vector<content> vec;
        vec.emplace_back(MINUS);
        vec.insert(vec.end(),order.begin(),order.end());
        vec.emplace_back(v);
        order=vec;
        return *this;
    }
    Cell<T>operator*=(T v){
        vector<content> vec;
        vec.emplace_back(MULT);
        vec.insert(vec.end(),order.begin(),order.end());
        vec.emplace_back(v);
        order=vec;
        return *this;
    }
    Cell<T>operator/=(T v){
        vector<content> vec;
        vec.emplace_back(DEL);
        vec.insert(vec.end(),order.begin(),order.end());
        vec.emplace_back(v);
        order=vec;
        return *this;
    }
};

template<class T>
class SuperCalc{
private:
    Cell<T> **table;
    int k;
public:
    SuperCalc(int m,int n):k(m) {
        table=new Cell<T>*[m];
        for (int i=0;i<m;i++)
            table[i]=new Cell<T>[n];
    }
    virtual ~SuperCalc(){
        for (int i=0;i<k;i++)
            delete[](table[i]);
        delete[](table);
    }
    Cell<T>&operator()(int i,int j){
        return table[i][j];
    }
};
