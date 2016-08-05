package p;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
public class MyTestWatcher extends TestWatcher {
    public MyTestWatcher() {
        this(defaultVerbosity);
    }
    public MyTestWatcher(boolean verbosity) {
        this.verbosity=verbosity;
    }
    @Override protected void starting(Description description) {
        if(verbosity) System.out.println("starting test: "+description.getMethodName());
    }
    @Override protected void finished(Description description) {
        if(verbosity) System.out.println("ending test: "+description.getMethodName());
    }
    public boolean verbosity;
    public static boolean defaultVerbosity=true;
}
