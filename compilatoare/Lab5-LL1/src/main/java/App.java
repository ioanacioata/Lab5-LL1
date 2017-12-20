
public class App {
    public static void main(String[] args) {

//        step1();

        step2();
    }

    private static void step2() {
        Grammar g = new Grammar("E:\\workspace\\compilatoare\\Lab5-LL1\\src\\main\\resources\\program\\grammar.txt");
//        Grammar g = new Grammar("E:\\workspace\\compilatoare\\Lab5-LL1\\src\\main\\resources\\program\\f.txt");
        g.createParsingTable();
        g.print();

        AnalyserLL1 analyserLL1 = new AnalyserLL1(g);
        //ACCEPTED SEQUENCE
//        analyserLL1.parse("13 5 22 9 21 6 7 8 12 2 35 14 15 16 2 0 12 0 18 1 12 31 1 12 17");
        //NOT ACCEPTED
        analyserLL1.parse("13 5 22 9 21 6 7 8 12 2 35 14 15 16 31 1 2 0 12 0 18 1 12 12 17");
    }

    private static void step1() {
//        Grammar g = new Grammar("E:\\workspace\\compilatoare\\Lab5-LL1\\src\\main\\resources\\dummy\\grammar.txt");
        Grammar g = new Grammar("E:\\workspace\\compilatoare\\Lab5-LL1\\src\\main\\resources\\dummy\\a.txt");
        g.createParsingTable();
        g.print();

        AnalyserLL1 analyserLL1 = new AnalyserLL1(g);
        analyserLL1.parse("+ a * a a");
    }
}
