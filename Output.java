import java.io.*;
import java.util.*;

public class Output {
    private PrintWriter print = null;

    /**
     * make a new output
     * @param filename where the output sends to
     */
    public Output (String filename) {
        // This code was originally in Main.java, but moved here to make it easier to do timestamped logs.
        File f = new File(filename);
        if (!f.exists()) { // Make sure the file is created
            try { // Or, at least make sure its blank
                f.createNewFile();
            } catch (IOException ex) {
                System.out.println(ex);
            }
        } else {
            try (PrintWriter p = new PrintWriter(f)) {
                p.print(""); // Clear the file
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }

        // ChatGPT was used here to correct NoSuchElementException; taken from Main
        // new FileWriter(f, true) makes it be in append mode so its not just continuously overwriting.
        try {
            this.print = new PrintWriter(new FileWriter(f, true));
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    /**
     * print a line, with timestamp
     * @param s that line to print
     */
    public void println (String s) {
        // the reason why this is a whole file
        GregorianCalendar t = new GregorianCalendar();
        this.print.println(String.format("[%02d/%02d/%04d %02d:%02d:%02d] %s", t.get(Calendar.MONTH) + 1, t.get(Calendar.DAY_OF_MONTH), t.get(Calendar.YEAR), t.get(Calendar.HOUR_OF_DAY), t.get(Calendar.MINUTE), t.get(Calendar.SECOND), s));
    }

    /**
     * close the printwriter
     */
    public void close () {
        print.println("END OF FILE");
        this.print.close();
    }
}
