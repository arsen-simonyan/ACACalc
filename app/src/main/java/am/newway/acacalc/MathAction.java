package am.newway.acacalc;

public enum MathAction
{
    DIVIDE,
    MULTIPLY,
    PLUS,
    MINUS,
    ANY;

    public static MathAction get(String str)
    {
        switch (str)
        {
            case "/": return DIVIDE;
            case "*": return MULTIPLY;
            case "+": return PLUS;
            case "-": return MINUS;
            default: return ANY;
        }
    }
}
