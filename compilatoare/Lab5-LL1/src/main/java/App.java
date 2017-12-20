
public class App {
    public static void main(String[] args) {

        Grammar g = new Grammar("E:\\workspace\\compilatoare\\Lab5-LL1\\src\\main\\resources\\dummy\\grammar.txt");
        g.createParsingTable();
        g.print();

        AnalyserLL1 analyserLL1 = new AnalyserLL1(g);
        analyserLL1.parse("b");
    }
}
