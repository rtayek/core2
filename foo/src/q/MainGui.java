package q;
import java.awt.BorderLayout;
import javax.swing.*;
public abstract class MainGui extends JPanel {
    @SuppressWarnings("serial") public static class MyJApplet extends JApplet {
        public void start() {}
        public void init() {
            getContentPane().setLayout(new BorderLayout());
            getContentPane().add(create(this),BorderLayout.CENTER);
        }
    }
    public MainGui() {
        this(null);
    }
    public MainGui(MyJApplet applet) {
        this.applet=applet;
        frame=!isApplet()?frame():null;
    }
    public JFrame frame() {
        JFrame frame=new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return frame;
    }
    public abstract void initialize(); // runs on current thread
    public abstract String title();
    public abstract void addContent(); // runs on awt/edt thread
    public void build() {
        if(MainGui.this.isApplet()) MainGui.this.addContent();
        else {
            MainGui.this.frame.setTitle(MainGui.this.title());
            MainGui.this.frame.getContentPane().add(MainGui.this,BorderLayout.CENTER);
            MainGui.this.addContent();
            MainGui.this.frame.pack();
            MainGui.this.frame.setVisible(true);
        }
    }
    protected void run() {
        initialize(); // non gui stuff
        setLayout(new BorderLayout());
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                build(); // gui stuff
            }
        });
    }
    public boolean isApplet() {
        return applet!=null;
    }
    @SuppressWarnings("serial") public static MainGui create(MainGui.MyJApplet applet) {
        return new MainGui(applet) {
            @Override public void initialize() {}
            @Override public String title() {
                return null;
            }
            @Override public void addContent() {};
        };
    }
    public static void main(String[] args) throws Exception {
        create(null);
    }
    /*protected final*/ public JFrame frame;
    private final MyJApplet applet;
    private static final long serialVersionUID=1L;
}
