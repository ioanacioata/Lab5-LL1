import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Grammar {
    public static final String EPSILON = "epsilon";
    public static final String EOF = "$";
    private Set<Production> productions;
    private String filename;
    private Map<String, List<String>> firstTable;
    private Map<String, List<String>> followTable;
    private Set<String> nonterminals;
    private Map<String, Map<String, Integer>> parsingTable;
    private Integer ruleNumber;

    public Map<String, List<String>> getFirstTable() {
        return firstTable;
    }

    public Map<String, List<String>> getFollowTable() {
        return followTable;
    }

    public Map<String, Map<String, Integer>> getParsingTable() {
        return parsingTable;
    }

    public Grammar(String filename) {
        this.filename = filename;
        productions = new LinkedHashSet<>();
        firstTable = new HashMap<>();
        followTable = new HashMap<>();
        parsingTable = new HashMap<>();
        readGrammar();
        createSetOfNonTerminals();

        createFirstTable();
        createFollowTable();
    }

    public Set<Production> getProductions() {
        return productions;
    }

    public void createParsingTable() {
        for (Production p : productions) {
            System.out.println("PRODUCTION: " + p.toString());
            //first
            if (!parsingTable.containsKey(p.getNonterminal())) {
                parsingTable.put(p.getNonterminal(), new HashMap<>());
            }
            List<String> firstOfRHS = getFirstForElements(p.getElements());
            System.out.println(firstOfRHS.toString()+"  ---firstOfRHS");
            populateRowOfTable(p, firstOfRHS);

            //folow
            if (firstOfRHS.contains(EPSILON)) {
                List<String> followOfLHS = getFollowForSymbol(p.getNonterminal());

                for (String s : followOfLHS) {
                    if (parsingTable.get(p.getNonterminal()).containsKey(s)) {
                        throw new RuntimeException(" NOT LL1 GRAMMAR------ CONFLICT line "+ p.getNonterminal()
                            + "   column " + s + " " +
                            "contains : " +
                        parsingTable.get(p.getNonterminal()).get(s) + " what rule we want  :"+ p.getRuleNumber());
                    }
                    parsingTable.get(p.getNonterminal()).put(s, p.getRuleNumber());
                }
            }
        }
    }

    private void populateRowOfTable(Production p, List<String> firstOfRHS) {
        for (String s : firstOfRHS) {
            if (!s.equals(EPSILON)) {
                if (parsingTable.get(p.getNonterminal()).containsKey(s)) {
                    throw new RuntimeException(" NOT LL1 GRAMMAR------ CONFLICT column " + s + " contains : " +
                        parsingTable.get(p.getNonterminal()).get(s) + " what rule we want  :"+ p.getRuleNumber());
                }
                parsingTable.get(p.getNonterminal()).put(s, p.getRuleNumber());
            }
        }
    }

    private List<String> getFirstForElements(List<String> elements) {
        Set<String> firstList = new LinkedHashSet<>();

        if(isTerminal(elements.get(0))){
            firstList.add(elements.get(0));
            return firstList.stream().collect(Collectors.toList());
        }

        for (String e : elements) {
            if (isTerminal(e)) {
                firstList.add(e);
            } else {
                firstList.addAll(firstTable.get(e));
            }
        }
        return firstList.stream().collect(Collectors.toList());
    }

    /**
     * Populates the FIRST1 table
     */
    public void createFirstTable() {
        for (String s : nonterminals) {
            List<String> list = getFirstForSymbol(s);

            StringBuilder str = new StringBuilder("First of ").append(s).append(" : ");
            for (String i : list) {
                str.append(i).append(" ");
            }
            System.out.println(str.toString());
        }
    }

    public void createFollowTable() {
        for (String s : nonterminals) {
            List<String> list = getFollowForSymbol(s);

            StringBuilder str = new StringBuilder("Follow of ").append(s).append(" : ");
            for (String i : list) {
                str.append(i).append(" ");
            }
            System.out.println(str.toString());
        }
    }

    public void readGrammar() {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line = br.readLine();
            int id = 1;
            while (line != null) {
                String[] el = line.split(" ");

                String nonterminal = el[0];
                List<String> elements = new ArrayList<>();
                for (int i = 2; i < el.length; i++) {
                    if (!el[i].equals("")) {
                        elements.add(el[i]);
                    }
                }
                Production p = new Production(nonterminal, elements, id);
                id++;
                productions.add(p);
                line = br.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Populates the set with the nonTerminals from the grammar
     */
    public void createSetOfNonTerminals() {
        nonterminals = new LinkedHashSet<>();
        for (Production p : productions) {
            nonterminals.add(p.getNonterminal());
        }
    }

    /**
     * Recursive function that finds FIRST1 for a given symbol - populates firstTable
     *
     * @param symbol - non-terminal
     * @return First1 for given symbol  - set of terminals
     */
    private List<String> getFirstForSymbol(String symbol) {
        if (firstTable.containsKey(symbol) && firstTable.get(symbol) != null && !firstTable.get(symbol).isEmpty()) {
            return firstTable.get(symbol);
        }
        firstTable.put(symbol, new ArrayList<>());

        List<Production> productionsForSymbol = getProductionsForSymbol(symbol);
        for (Production p : productionsForSymbol) {
            List<String> elements = p.getElements();
            for (int j = 0; j < elements.size(); j++) {
                String e = elements.get(j);

                if (e.equals(EPSILON)) {
                    //if epsilon is found
                    firstTable.get(symbol).add(e);
                    break;
                }

                if (isTerminal(e)) {
                    //if  terminal just add
                    firstTable.get(symbol).add(e);
                    break;
                }
                //if non terminal
                List<String> fistOfNonterminal = getFirstForSymbol(e);

                if (!fistOfNonterminal.contains(EPSILON)) {
                    //merge non terminals
                    for (String f : fistOfNonterminal) {
                        if (!firstTable.get(symbol).contains(f)) {
                            firstTable.get(symbol).add(f);
                        }
                    }
                    break;
                }

                //if it contains epsilon merge without epsilon
                for (String f : fistOfNonterminal) {
                    if (!firstTable.get(symbol).contains(f) && !f.equals(EPSILON)) {
                        firstTable.get(symbol).add(f);
                    }
                }
                if (j == elements.size() - 1) {
                    firstTable.get(symbol).add(EPSILON);
                }
            }
        }
        return firstTable.get(symbol);
    }

    /**
     * Recursive function that finds FOLLOW1 for a given symbol - populates followTable
     *
     * @param symbol - non-terminal
     * @return Follow1 for given symbol - set of terminals
     */
    private List<String> getFollowForSymbol(String symbol) {
        if (followTable.containsKey(symbol) && followTable.get(symbol) != null && !followTable.get(symbol).isEmpty()) {
            return followTable.get(symbol);
        }
        followTable.put(symbol, new ArrayList<>());

        if (isStartSymbol(symbol)) {
            System.out.println(symbol + " is FIRST ");
            if (!followTable.get(symbol).contains(EOF)) {
                followTable.get(symbol).add(EOF);
            }
        }

        List<Production> productionList = getProductionsWithSymbol(symbol);

        for (Production p : productionList) {
            List<String> elements = p.getElements();
            int followIndex = getIndexOfNonTerminalInRHS(symbol, elements) + 1;
            while (true) {
                //if we reached the end of the RHS of the production rule
                if (followIndex == elements.size()) {
                    if (!p.getNonterminal().equals(symbol)) {
                        //to avoid cases like A->aA
                        if (!followTable.get(symbol).contains(EOF)) {
                            followTable.get(symbol).add(EOF);
                        }
                    } else {
                        //merge with follow from left hand side nonterminal from the followSymbol production rule
                        List<String> folowOfLHS = getFollowForSymbol(p.getNonterminal());
                        for (String f : folowOfLHS) {
                            if (!followTable.get(symbol).contains(f)) {
                                followTable.get(symbol).add(f);
                            }
                        }
                    }
                    break;
                }

                //add terminals
                String followSymbol = p.getElements().get(followIndex);
                if (isTerminal(followSymbol) && !followTable.get(symbol).contains(followSymbol)) {
                    followTable.get(symbol).add(followSymbol);
                }

                //if not at the end of the production rule
//                List<String> firstOfFollow = getFirstForSymbol(followSymbol);
                List<String> firstOfFollow = getFirstForSymbol(followSymbol);
                if (!firstOfFollow.contains(EPSILON)) {
                    //merge with fist of follow in follow of followSymbol symbol
                    for (String f : firstOfFollow) {
                        if (!followTable.get(symbol).contains(f)) {
                            followTable.get(symbol).add(f);
                        }
                    }
                    break;
                }

                //if we have epsilon
                for (String f : firstOfFollow) {
                    if (!followTable.get(symbol).contains(f) && !f.equals(EPSILON)) {
                        followTable.get(symbol).add(f);
                    }
                }
                followIndex++;
            }
        }
        return followTable.get(symbol);
    }

    private int getIndexOfNonTerminalInRHS(String symbol, List<String> elements) {
        for (int i = 0; i < elements.size(); i++) {
            if (elements.get(i).equals(symbol)) {
                return i;
            }
        }
        throw new RuntimeException("NOT SUPPOSED TO GET HERE!!!!!");
    }

    /**
     * @param symbol
     * @return all the production rules where the nonterminal symbol is used on the right side
     */
    private List<Production> getProductionsWithSymbol(String symbol) {
        List<Production> prod = new ArrayList<>();
        for (Production p : productions) {
            if (p.getElements().contains(symbol)) {
                prod.add(p);
            }
        }
        return prod;
    }

    /**
     * @param symbol - non-terminal (String)
     * @return TRUE - if the given non-terminal is the start symbol of the grammar
     */
    public boolean isStartSymbol(String symbol) {
        return nonterminals.toArray()[0].equals(symbol);
    }

    public String getStartSymbol() {
        return nonterminals.toArray()[0].toString();
    }

    /**
     * @param nonTerminal - String
     * @return all production rules that have on the left side the non terminal given
     */
    private List<Production> getProductionsForSymbol(String nonTerminal) {
        List<Production> productionsForSymbol = new ArrayList<>();
        for (Production p : productions) {
            if (p.getNonterminal().equals(nonTerminal)) {
                productionsForSymbol.add(p);
            }
        }
        return productionsForSymbol;
    }

    /**
     * @param symbol - String
     * @return true if the given symbol is a terminal, false otherwise
     */
    public boolean isTerminal(String symbol) {
        for (Production p : productions) {
            if (symbol.equals(p.getNonterminal())) {
                return false;
            }
        }
        return true;
    }

    public void print() {

        System.out.println("\n Productions: ");
        for (Production p : getProductions()) {
            System.out.println(p.toString());
        }

        System.out.println("Table : \n");
        for (String line : parsingTable.keySet()) {
            System.out.println(" Line " + line);
            for (String column : parsingTable.get(line).keySet()) {
                System.out.println(" column: " + column + " elem " + parsingTable.get(line).get(column));
            }
        }
    }

    public List<String> getProductionById(Integer rule) {
        for (Production p : productions) {
            if (p.getRuleNumber().equals(rule)) {
                return p.getElements();
            }
        }
        return null;
    }
}