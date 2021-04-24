package bmstu.iu9.generator.table;

import javax.swing.plaf.PanelUI;

public final class TableConstants {
    public static final int E = 3;
    public static final int E1 = 0;
    public static final int T = 2;
    public static final int T1 = 1;
    public static final int F = 4;

    public static final int AT1 = 5;
    public static final int END1 = 6;
    public static final int NUM1 = 7;
    public static final int PLUS1 = 8;
    public static final int STAR1 = 9;
    public static final int RIGHT_BRACE1 = 10;
    public static final int LEFT_BRACE1 = 11;


    public static final int AT = 0;
    public static final int END = 1;
    public static final int NUM = 2;
    public static final int PLUS = 3;
    public static final int STAR = 4;
    public static final int RIGHT_BRACE = 5;
    public static final int LEFT_BRACE = 6;

    public static int getConstant(String value){
        switch (value){
            case "E":
                return E;
            case "E'":
                return E1;
            case "T":
                return T;
            case "T'":
                return T1;
            case "F":
                return F;
            case "\"+\"":
                return PLUS1;
            case "\"*\"":
                return STAR1;
            case "@":
                return AT1;
            case "\"(\"":
                return LEFT_BRACE1;
            case "\")\"":
                return RIGHT_BRACE1;
            case "\"n\"":
                return NUM1;
            default:
                throw new IllegalArgumentException("Unknown state");
        }
    }

    public static String getNonTerm(int n){
        switch (n){
            case E:
                return "E";
            case E1:
                return "E'";
            case T:
                return "T";
            case T1:
                return "T'";
            case F:
                return "F";
            default:
                throw new IllegalArgumentException("gg");
        }
    }

    public static String getTerm(int n){
        switch (n){
            case AT:
                return "@";
            case PLUS:
                return "\"+\"";
            case STAR:
                return "\"*\"";
            case LEFT_BRACE:
                return "\"(\"";
            case RIGHT_BRACE:
                return "\")\"";
            case NUM:
                return "\"n\"";
            case END:
                return "$";
            default:
                throw new IllegalArgumentException("gg");
        }
    }

    public static String getCommon(int n){
        switch (n){
            case E:
                return "E";
            case E1:
                return "E'";
            case T:
                return "T";
            case T1:
                return "T'";
            case F:
                return "F";
            case AT1:
                return "@";
            case PLUS1:
                return "\"+\"";
            case STAR1:
                return "\"*\"";
            case LEFT_BRACE1:
                return "\"(\"";
            case RIGHT_BRACE1:
                return "\")\"";
            case NUM1:
                return "\"n\"";
            case END1:
                return "$";
            default:
                throw new IllegalArgumentException("gg");
        }
    }
}
