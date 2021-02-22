#include "modules/Application.h"

const std::string CODE_FILE_NAME="test/test.txt";
const std::string GRAMMAR_FILE_NAME="test/grammar1.txt";

int main() {
    Application(CODE_FILE_NAME,GRAMMAR_FILE_NAME).start();
    return 0;
}
