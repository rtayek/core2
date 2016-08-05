package p;
import static org.junit.Assert.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import p.Main.IO.*;
import static p.Main.IO.*;
public class BindTestCase {
	@Rule public TestRule watcher=new MyTestWatcher(false);
	@BeforeClass public static void setUpBeforeClass() throws Exception {}
	@AfterClass public static void tearDownAfterClass() throws Exception {}
	@Before public void setUp() throws Exception {
		InetAddress inetAddress=InetAddress.getLocalHost();
		int service=23456; // different service for each test case helps
		socketAddress=new InetSocketAddress(inetAddress,service);
	}
	@After public void tearDown() throws Exception {
		if(serverSocket!=null) ;// p(toS(serverSocket));
		int active=Thread.activeCount();
		if(printExtraThreads) {
			// p("active: "+active);
			if(active>threads) {
				p("extra threads: "+(active-threads));
				printThreads(excluded);
			}
		}
	}
	void stopListening() {
		if(serverSocket!=null) {
			if(serverSocket.isBound()) {
				try {
					serverSocket.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			if(!serverSocket.isClosed()) {
				try {
					serverSocket.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	boolean startListening() { // move to acceptor?
		if(serverSocket==null) try {
			serverSocket=new ServerSocket();
			p("constructed: "+serverSocket);
		} catch(IOException e) {
			e.printStackTrace();
		}
		if(serverSocket!=null) {
			try {
				serverSocket.bind(socketAddress);
				return true;
			} catch(IOException e) {
				p("bind exception!");
				// e.printStackTrace();
			}
		}
		return false;
	}
	@Test public void testStopListeningWithNullServerSocket() {
		stopListening();
	}
	@Test public void testStopListeningWithNonNullServerSocket() throws Exception {
		serverSocket=new ServerSocket();
		stopListening();
	}
	@Test public void testStartListening() throws Exception {
		serverSocket=new ServerSocket();
		boolean ok=startListening();
		assertTrue(ok);
	}
	@Test public void testStartAndStopLstening() throws Exception {
		serverSocket=new ServerSocket();
		boolean ok=startListening();
		assertTrue(ok);
		stopListening();
	}
	@Test public void testStartAndStopLsteningManyTimes() throws Exception {
		for(int i=1;i<=10;i++) {
			serverSocket=new ServerSocket();
			// pn("before: ");
			// p(toS(serverSocket));
			boolean ok=startListening();
			// p(i+": "+ok);
			assertTrue(ok);
			stopListening();
			// pn("after: ");
			// p(toS(serverSocket));
		}
	}
	int threads;
	SocketAddress socketAddress;
	ServerSocket serverSocket;
	boolean printExtraThreads=true;
	static List<String> excluded=Arrays.asList(new String[]{"main","ReaderThread"});
}
