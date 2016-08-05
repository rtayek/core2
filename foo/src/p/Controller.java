package p;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.*;
import p.Main.Group;
import static p.Main.IO.*;
class CommandLine implements Observer {
	public CommandLine(Model model) {
		this.model=model;
	}
	@Override public void update(Observable observable,Object hint) {
		p(observable+" "+hint);
		if(observable instanceof Model) {
			if(observable==model) {
				p(id+" received update from observable"+observable+" with hint: "+hint);
			} else p(this+" "+id+" not our model!");
		} else p(this+" "+id+" not a model!");
	}
	private final int id=++ids;
	private final Model model;
	private static int ids=0;
}
public class Controller {
	Controller(Main main) throws UnknownHostException {
		this(main,System.in,System.out);
	}
	Controller(Main main,InputStream in,PrintStream out) throws UnknownHostException {
		this.main=main;
		this.in=in;
		this.out=out;
	}
	protected void help() {
		p(out,"help:");
		p(out,"a add/remove audio observer");
		p(out,"b <buttonId> - click on button");
		p(out,"c - add/remove a command line view");
		p(out,"h - help");
		p(out,"p - print view");
		p(out,"q - quit");
		p(out,"r - reset");
		p(out,"s - start listening");
		p(out,"t - stop listening");
	}
	private String[] splitNext(String command,int i) {
		while(command.charAt(i)==' ')
			i++;
		String[] tokens=command.substring(i).split(" ");
		return tokens;
	}
	protected boolean process(String command) {
		if(command.length()==0) return true;
		String[] tokens=null;
		switch(command.charAt(0)) {
			case 'h':
				help();
				break;
			case 'a':
				if(audioObserver==null) {
					audioObserver=new Audio.AudioObserver(main.model);
					main.model.addObserver(audioObserver);
				} else {
					main.model.deleteObserver(audioObserver);
					audioObserver=null;
				}
				break;
			case 'b':
				if(command.charAt(1)==' ') {
					tokens=splitNext(command,2);
					if(tokens.length==1) try {
						int buttonId=Integer.valueOf(tokens[0]);
						main.instance().click(buttonId,main.myInetAddress);
					} catch(Exception e) {
						p(out,"controller split caught: '"+e+"'");
						p(out,"syntax error: "+command);
					}
					else p(out,"too many tokens!");
				} else p(out,"syntax error: "+command);
				break;
			case 'c':
				if(commandLineView==null) {
					commandLineView=new CommandLine(main.model);
					main.model.addObserver(commandLineView);
					p(out,"added command line view: "+commandLineView);
				} else {
					main.model.deleteObserver(commandLineView);
					p(out,"removed command line view: "+commandLineView);
					commandLineView=null;
				}
				break;
			case 'p':
				p(out,main.model.toString());
				break;
			case 'r':
				main.model.reset();
				break;
			case 's':
				int service=main.group.serviceBase+main.group.first; // assume
																		// first
																		// in
																		// group
				InetSocketAddress inetSocketAddress=new InetSocketAddress(main.myInetAddress,service);
				boolean ok=main.instance().startListening(inetSocketAddress);
				if(!ok) p(out,"badness");
				break;
			case 't':
				main.instance().stopListening();
				break;
			case 'q':
				return false;
			default:
				p(out,"unimplemented: "+command.charAt(0));
				help();
				break;
		}
		return true;
	}
	void run() {
		BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(in));
		String string=null;
		help();
		prompt();
		try {
			while((string=bufferedReader.readLine())!=null) {
				if(!process(string)) {
					p(out,"quitting.");
					return;
				}
				prompt();
			}
		} catch(IOException e) {
			p(out,"controller readln caught: '"+e+"'");
			p(out,"quitting.");
			return;
		}
		p(out,"end of file.");
	}
	void prompt() {
		out.print(lineSeparator+">");
		out.flush();
	}
	public static void main(String[] arguments) throws UnknownHostException, InterruptedException, ExecutionException {
		InetAddress inetAddress=InetAddress.getLocalHost();
		int first=Byte.toUnsignedInt(inetAddress.getAddress()[3]);
		Group group=new Group(first,first,false);
		Main main=new Main(group,Model.mark1);
		new Controller(main).run();
	}
	protected final Main main;
	protected final InputStream in;
	protected final PrintStream out;
	protected SocketHandler socketHandler;
	private CommandLine commandLineView;
	private Observer audioObserver;
	public static final String lineSeparator=System.getProperty("line.separator");
}
