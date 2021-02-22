#include "FileReadUtils.h"
#include <fstream>

std::string readFile(const std::string &fileName){
    std::ifstream in(fileName);
    return std::string(std::istreambuf_iterator<char>(in),std::istreambuf_iterator<char>());
}