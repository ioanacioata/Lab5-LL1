import java.util.List;

public class Production {
    private String nonterminal;
    private List<String> elements;
    private Integer ruleNumber;

    public Production(String nonterminal, List<String> elements) {
        this.nonterminal = nonterminal;
        this.elements = elements;
    }

    public Production(String nonterminal, List<String> elements, Integer ruleNumber) {
        this.nonterminal = nonterminal;
        this.elements = elements;
        this.ruleNumber = ruleNumber;
    }

    public String getNonterminal() {
        return nonterminal;
    }

    public void setNonterminal(String nonterminal) {
        this.nonterminal = nonterminal;
    }

    public List<String> getElements() {
        return elements;
    }

    public void setElements(List<String> elements) {
        this.elements = elements;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(nonterminal).append(" -> ");
        for (String e : elements) {
            str.append(e).append(" ");
        }
        str.append("  rule: ").append(ruleNumber);
        return str.toString();
    }

    public Integer getRuleNumber() {
        return this.ruleNumber;
    }
}
