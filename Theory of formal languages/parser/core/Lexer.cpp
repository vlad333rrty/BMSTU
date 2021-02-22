#include "../modules/Lexer.h"
#include <regex>

using namespace std;

void Lexer::normalize(string &s) {
    regex excess_spaces(" {2,}");
    regex eq_reg("( *)=( *)");
    regex target_reg("( *):( *)");
    regex empty_line("\n\\s*\n");
    regex last_line("\n+$");
    s = regex_replace(s, excess_spaces, " ");
    s = regex_replace(s, eq_reg, "=");
    s = regex_replace(s, target_reg, ":");
    s = regex_replace(s, empty_line, "\n");
    s = regex_replace(s, last_line, "");
    result = s;
}

string getTranslatedRepresentation(Token::Type type, int ind){
    //return Token::leftDelimiter+Token::getTranslatedRepresentation(type)+","+to_string(ind)+Token::rightDelimiter;
    return Token::getTranslatedRepresentation(type);
}

string getTranslatedRepresentation(Token::Type type){
    return Token::getTranslatedRepresentation(type);
}

void fillTargetTable(string &s, string &result, vector<Token> &target_table) {
    regex target_reg("([A-Za-z_][\\w.]*)(:)");
    for(sregex_iterator it=sregex_iterator(s.begin(), s.end(), target_reg),end; it != end; it++){
        string target=it->str(1);
        target_table.emplace_back(Token(target));
        string replacement= getTranslatedRepresentation(Token::TARGET, target_table.size() - 1);
        int index=result.find(it->str());
        result.replace(index,target.length(),replacement);
    }
}

string gatherDependencies(string &s, vector<Token> &dependence_table, vector<Token> &target_table){
    regex reg("[^\\s]+");
    string replacement;
    for(sregex_iterator it=sregex_iterator(s.begin(), s.end(), reg),end; it != end; it++){
        string dep=it->str();
        int i;
        for (i=0;i<target_table.size();i++){
            if (target_table[i].getRepresentation() == dep){
                replacement+= getTranslatedRepresentation(Token::TARGET, i);
                break;
            }
        }
        if (i==target_table.size()){
            dependence_table.emplace_back(Token(dep));
            replacement+= getTranslatedRepresentation(Token::DEPENDENCY, dependence_table.size() - 1);
        }
    }
    return replacement;
}

void fillDependenciesTable(string &s, string &result, vector<Token> &dependence_table, vector<Token> &target_table){
    regex dep_reg(":(.+)");
    for(sregex_iterator it=sregex_iterator(s.begin(), s.end(), dep_reg),end; it != end; it++){
        string dependencies=it->str(1);
        if (!dependencies.empty()) {
            string replacement= gatherDependencies(dependencies, dependence_table, target_table);
            int index=result.find(it->str(1));
            result.replace(index,it->length(1),replacement);
        }
    }
}

void fillDefinitionTables(string &s, string &result, vector<Token> &target_table, vector<Token> &dependence_table){
    fillTargetTable(s, result, target_table);
    fillDependenciesTable(s, result, dependence_table, target_table);
    result=regex_replace(result, regex(":"), getTranslatedRepresentation(Token::COLON));
}

void fillVariableTables(string &s, string &result, vector<Token> &id_table, vector<Token> &val_table){
    regex id_val("([A-Za-z_]\\w*)=(\\w+)");
    for(sregex_iterator it=sregex_iterator(s.begin(),s.end(),id_val),end;it!=end;it++){
        string replacement;
        string identificator=it->str(1);
        string val=it->str(2);
        id_table.emplace_back(identificator);
        val_table.emplace_back(val);
        replacement+= getTranslatedRepresentation(Token::ID, id_table.size() - 1);
        replacement+= getTranslatedRepresentation(Token::EQUALS);
        replacement+= getTranslatedRepresentation(Token::VAL, val_table.size() - 1);
        result=regex_replace(result,regex(it->str()),replacement);
    }
}

string fillCommandParametersTables(string &s, vector<Token> &flag_table, vector<Token> &val_table, vector<Token> &id_table){
    regex reg(R"((-[a-z]+)|(\$\((\w+)\))|([^\s]+))");
    string replacement;
    for (sregex_iterator it=sregex_iterator(s.begin(),s.end(),reg),end;it!=end;it++){
        string flag=it->str(1);
        string defined_var=it->str(2);
        string val=it->str(4);
        if (!flag.empty()){
            flag_table.emplace_back(Token(flag));
            replacement+= getTranslatedRepresentation(Token::FLAG, flag_table.size() - 1);
        }else if (!defined_var.empty()){
            for (int i=0; i < id_table.size(); i++){
                if (id_table[i].getRepresentation() == it->str(3)){
                    replacement+= getTranslatedRepresentation(Token::DOLLAR);
                    replacement+= getTranslatedRepresentation(Token::LEFT_P);
                    replacement+= getTranslatedRepresentation(Token::ID, i);
                    replacement+= getTranslatedRepresentation(Token::RIGHT_P);
                    break;
                }
            }
        }else if (!val.empty()){
            val_table.emplace_back(Token(val));
            replacement+= getTranslatedRepresentation(Token::ARG, val_table.size() - 1);
        }
    }
    return replacement;
}

string proceedCommand(string &cmd, string &parameters, vector<Token> &command_table, vector<Token> &flag_table
        , vector<Token> &val_table, vector<Token> &id_table){
    string replacement;
    command_table.emplace_back(Token(cmd));
    replacement+= getTranslatedRepresentation(Token::COMMAND, command_table.size() - 1);
    replacement+= fillCommandParametersTables(parameters, flag_table, val_table, id_table);
    return replacement;
}

string getValOrId(string &val,string &id,vector<Token> &val_table, vector<Token> &id_table){
    string replacement;
    if (val.empty()){
        replacement+= getTranslatedRepresentation(Token::DOLLAR);
        replacement+= getTranslatedRepresentation(Token::LEFT_P);
        for (int i=0;i<id_table.size();i++){
            if (id_table[i].getRepresentation()==id){
                replacement+= getTranslatedRepresentation(Token::ID, i);
                break;
            }
        }
        replacement+= getTranslatedRepresentation(Token::RIGHT_P);
    }else{
        val_table.emplace_back(Token(val));
        replacement+= getTranslatedRepresentation(Token::VAL, val_table.size() - 1);
    }
    return replacement;
}

string proceedCondition(const string &condition, vector<Token> &val_table, vector<Token> &id_table){
    string replacement= getTranslatedRepresentation(Token::IF);
    string s = R"(\s*(\[\s*((\w+)|(\$\((\w+)\)))\s*(>|<|>=|<=|==)\s*((\w+)|(\$\((\w+)\)))\s*\]) (then))";
    regex cond(s);
    smatch match;
    regex_search(condition.begin(),condition.end(),match,cond);

    replacement+= getTranslatedRepresentation(Token::LEFT_SQ_P);
    string first_var=match.str(3);
    string first_var_id=match.str(5);
    string op=match.str(6);
    string second_var=match.str(8);
    string second_var_id=match.str(10);
    replacement+=getValOrId(first_var,first_var_id,val_table,id_table);
    if (op==">"){
        replacement+= getTranslatedRepresentation(Token::GREATER);
    }else if (op=="<"){
        replacement+= getTranslatedRepresentation(Token::LESS);
    }else if (op==">="){
        replacement+= getTranslatedRepresentation(Token::GREATER_OR_EQUAL);
    }else if (op=="<="){
        replacement+= getTranslatedRepresentation(Token::LESS_OR_EQUAL);
    }else if (op=="=="){
        replacement+= getTranslatedRepresentation(Token::EQUAL);
    }
    replacement+=getValOrId(second_var,second_var_id,val_table,id_table);
    replacement+= getTranslatedRepresentation(Token::RIGHT_SQ_P);
    replacement += getTranslatedRepresentation(Token::THEN);
    return replacement;
}

string proceedLoop(const string &loopBody, vector<Token> &val_table, vector<Token> &id_table){
    string replacement=getTranslatedRepresentation(Token::FOR);
    regex body("([A-Za-z_]\\w*)\\s(in)\\s((\\w+\\s?)+)");
    smatch match;
    regex_search(loopBody,match,body);
    string id=match.str(1);
    if (!id.empty()){
        replacement+=getTranslatedRepresentation(Token::ID,id_table.size());
        id_table.emplace_back(Token(id));
    }
    if (!match.str(2).empty()){
        replacement+=getTranslatedRepresentation(Token::IN);
    }
    string vars=match.str(3);
    if (!vars.empty()){
        regex varsReg("\\w+");
        for (sregex_iterator it=sregex_iterator(vars.begin(),vars.end(),varsReg),end;it!=end;it++){
            replacement+=getTranslatedRepresentation(Token::VAL,val_table.size());
            string val=it->str();
            val_table.emplace_back(Token(val));
        }
    }
    return replacement;
}

void fillCommandTable(string &s, string &result, vector<Token> &command_table, vector<Token> &flag_table
        , vector<Token> &val_table, vector<Token> &arg_table, vector<Token> &id_table){
    regex cmd_line_reg("\t((if(.+))|(for(.+))|((\\w+[+-]*) (.*)))"); // 7 8
    for (sregex_iterator it=sregex_iterator(s.begin(),s.end(),cmd_line_reg),end;it!=end;it++){
        string replacement;
        string ifClause=it->str(2);
        string forClause=it->str(4);
        string commandClause=it->str(6);
        if (!ifClause.empty()){
            string cond=it->str(3);
            replacement+=proceedCondition(cond,val_table,id_table);
        }else if (!forClause.empty()){
            string loopBody=it->str(5);
            replacement+=proceedLoop(loopBody,val_table,id_table);
        }else if (!commandClause.empty()){
            string cmd=it->str(7);
            string parameters=it->str(8);
            replacement+=proceedCommand(cmd,parameters,command_table,flag_table,arg_table,id_table);
        }
        int index=result.find(it->str(1));
        result.replace(index,it->length(1),replacement);
    }
}


void translateWord(std::string &result,regex &reg,Token::Type type,int group){
    for (sregex_iterator it=sregex_iterator(result.begin(),result.end(),reg),end;it!=end;it++){
        string s=it->str(group);
        if (!s.empty()) {
            int index = it->position(group);
            result.replace(index, s.length(), getTranslatedRepresentation(type));

        }
    }
}

void translateKeyWords(std::string &result){
    regex elseReg("\t(else)\n*");
    regex fiReg("\t(fi)\n*");
    regex doneReg("\t(done)\n*");
    regex doReg("\t(do)\n*");
    translateWord(result,elseReg,Token::ELSE,1);
    translateWord(result,fiReg,Token::FI,1);
    translateWord(result,doneReg,Token::DONE,1);
    translateWord(result,doReg,Token::DO,1);
    result=regex_replace(result,regex("\t"),getTranslatedRepresentation(Token::TAB));
    // result=regex_replace(result,regex("\n"),getTranslatedRepresentation("Token::NEWLINE"));
    result=regex_replace(result,regex("\n"),"");
}

void Lexer::fillTables(std::string &s) {
    fillVariableTables(s, result, id_table, val_table);
    fillDefinitionTables(s, result, target_table, dependence_table);
    fillCommandTable(s, result, command_table, flag_table, val_table, arg_table, id_table);
    translateKeyWords(result);
}

string Lexer::translate(std::string s) {
    normalize(s);
    fillTables(s);
    return result;
}