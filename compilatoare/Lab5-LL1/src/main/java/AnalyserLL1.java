import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class AnalyserLL1 {
    private Grammar grammar;
    String input;
    Stack<String> stack;
    List<Integer> parsingTree;

    public AnalyserLL1(Grammar grammar) {
        this.grammar = grammar;
    }

    public void parse(String s) {
        input = s + Grammar.EOF;
        stack = new Stack();
        stack.push(Grammar.EOF);
        stack.push(grammar.getStartSymbol());
        parsingTree = new ArrayList<>();

        while (stack.size() > 1) {
            System.out.println(this.toString());
            String firstElem = stack.pop();
            String firstChar = String.valueOf(input.charAt(0));
            //when pop
            if (grammar.isTerminal(firstElem)) {
                if (firstElem.equals(firstChar)) {
                    input = input.substring(1);
                } else {
                    throw new RuntimeException("Sequence not accepted");
                }
            }
            else {
                //when replacing non terminal
                System.out.println("firstElem " + firstElem + " firstChar " + firstChar);
                Integer rule = grammar.getParsingTable().get(firstElem).get(firstChar);
                if (rule != null) {
                    List<String> elements = grammar.getProductionById(rule);
                    if (!elements.get(0).equals(Grammar.EPSILON)) {
                        for (int i = elements.size() - 1; i >= 0; i--) {
                            stack.push(elements.get(i));
                        }
                    }

                    parsingTree.add(rule);
                }
            }
        }

        if (!input.equals(Grammar.EOF)) {
            System.out.println("\nERROR  - Sequence is not accepted");
        }
        else{
            System.out.println("\nGRAMMAR ACCEPTED: " + this.toString());
        }
    }

    @Override
    public String toString() {
        return "AnalyserLL1{" +
            "input='" + input + '\'' +
            ", stack=" + stack.toString() +
            ", parsingTree=" + parsingTree.toString() +
            '}';
    }
}
