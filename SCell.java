import java.util.ArrayList;
import java.util.List;

public class SCell {
    private String value;
    private boolean isFormula;
    private List<String> dependencies;

    public SCell(String value) {
        this.value = value;
        this.isFormula = value.startsWith("=");
        this.dependencies = new ArrayList<>();
        if (isFormula) {
            extractDependencies();
        }
    }

    public boolean isNumber() {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean isText() {
        return !isNumber() && !isFormula;
    }

    public boolean isFormula() {
        return isFormula;
    }

    public String getValue() {
        return value;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public String getText() {
        if (isText()) {
            return value;
        }
        throw new IllegalStateException("Cell is not text.");
    }

    public double computeForm() {
        if (isFormula) {
            String formula = value.substring(1); // Remove the '=' character
            return evaluateFormula(formula);
        }
        throw new IllegalStateException("Cell is not a formula");
    }

    // Custom formula evaluator (handles +, -, *, /, and parentheses)
    private double evaluateFormula(String formula) {
        return evaluateExpression(formula.replaceAll("\\s+", ""));
    }

    // Helper function to evaluate a mathematical expression
    private double evaluateExpression(String expr) {
        return parseAddSubtract(expr);
    }

    private double parseAddSubtract(String expr) {
        double result = parseMultiplyDivide(expr);
        int index = findOperatorIndex(expr, '+', '-');

        while (index != -1) {
            char operator = expr.charAt(index);
            String left = expr.substring(0, index);
            String right = expr.substring(index + 1);

            double leftValue = parseMultiplyDivide(left);
            double rightValue = parseMultiplyDivide(right);

            result = operator == '+' ? leftValue + rightValue : leftValue - rightValue;

            index = findOperatorIndex(right, '+', '-');
        }
        return result;
    }

    private double parseMultiplyDivide(String expr) {
        double result = parseValue(expr);
        int index = findOperatorIndex(expr, '*', '/');

        while (index != -1) {
            char operator = expr.charAt(index);
            String left = expr.substring(0, index);
            String right = expr.substring(index + 1);

            double leftValue = parseValue(left);
            double rightValue = parseValue(right);

            result = operator == '*' ? leftValue * rightValue : leftValue / rightValue;

            index = findOperatorIndex(right, '*', '/');
        }
        return result;
    }

    private double parseValue(String expr) {
        try {
            return Double.parseDouble(expr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid value in formula: " + expr);
        }
    }

    // Helper to find the next operator index with precedence
    private int findOperatorIndex(String expr, char op1, char op2) {
        int depth = 0;
        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (c == '(') depth++;
            if (c == ')') depth--;
            if (depth == 0 && (c == op1 || c == op2)) return i;
        }
        return -1;
    }

    private void extractDependencies() {
        String regex = "[A-Z]+\\d+";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(value);
        while (matcher.find()) {
            dependencies.add(matcher.group());
        }
    }
}
