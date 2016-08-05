package p;
import static org.junit.Assert.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import p.Main.Group;
import p.Main.Tablet;
import static p.Main.*;
import static p.Main.IO.*;
public class MainTestCase {
	@Rule public TestRule watcher=new MyTestWatcher(false);
	@BeforeClass public static void setUpBeforeClass() throws Exception {
		// router=Inet4Address.getByName("10.0.0.1");
	}
	@AfterClass public static void tearDownAfterClass() throws Exception {}
	@Before public void setUp() throws Exception {
		threads=Thread.activeCount();
		myInetAddress=InetAddress.getLocalHost();
		int first=Byte.toUnsignedInt(myInetAddress.getAddress()[3]);
		Group group=new Group(first,first,false);
		main=new Main(group,Model.mark1.clone());
	}
	@After public void tearDown() throws Exception {
		int active=Thread.activeCount();
		if(printExtraThreads) {
			// p("active: "+active);
			if(active>threads) {
				p("extra threads: "+(active-threads));
				printThreads(excluded);
			}
		}
	}
	@Test public void testCtor() throws Exception {
		assertNotNull(main);
	}
	void check(Set<InetSocketAddress> socketAddresses) {
		// test is ugly!
	}
	@Test public void testGroupSocketAddresses() throws Exception {
		int first=Byte.toUnsignedInt(myInetAddress.getAddress()[3]);
		int n=32;
		Group group=new Group(first,first+n-1,false);
		Main main=new Main(group,Model.mark1.clone());
		Set<InetSocketAddress> socketAddresses=main.group.socketAddresses(myInetAddress);
		//p("false: "+socketAddresses);
		Iterator<InetSocketAddress> i=socketAddresses.iterator();
		for(int j=0;j<n;j++) {
			InetSocketAddress inetSocketAddress2=i.next();
			assertEquals(first+j,Byte.toUnsignedInt(inetSocketAddress2.getAddress().getAddress()[3]));
			assertEquals(main.group.serviceBase+first+j,inetSocketAddress2.getPort());
		}
		Group group2=new Group(first,first+n-1,true);
		main=new Main(group2,Model.mark1.clone());
		socketAddresses=main.group.socketAddresses(myInetAddress);
		//p("true: "+socketAddresses);
		i=socketAddresses.iterator();
		for(int j=0;j<n;j++) {
			InetSocketAddress inetSocketAddress2=i.next();
			assertEquals(first,Byte.toUnsignedInt(inetSocketAddress2.getAddress().getAddress()[3]));
			assertEquals(group2.serviceBase+first+j,inetSocketAddress2.getPort());
		}
	}
	@Test public void testTabletStartListeningAndStopListening() throws Exception {
		Tablet tablet=main.instance();
		int first=Byte.toUnsignedInt(myInetAddress.getAddress()[3]);
		int service=main.group.serviceBase+first;
		InetSocketAddress inetSocketAddress=new InetSocketAddress(myInetAddress,service);
		boolean ok=tablet.startListening(inetSocketAddress);
		assertTrue(ok);
		tablet.stopListening();
	}
	@Test public void testTabletStartListeningAndStopListeningTwice() throws Exception {
		Tablet tablet=main.instance();
		int first=Byte.toUnsignedInt(myInetAddress.getAddress()[3]);
		int service=main.group.serviceBase+first;
		InetSocketAddress inetSocketAddress=new InetSocketAddress(myInetAddress,service);
		boolean ok=tablet.startListening(inetSocketAddress);
		assertTrue(ok);
		tablet.stopListening();
		ok=tablet.startListening(inetSocketAddress);
		assertTrue(ok);
		tablet.stopListening();
	}
	@Test public void testTabletStartListeningAndStopListeningManyTimes() throws Exception {
		Tablet tablet=main.instance();
		int n=10;
		int first=Byte.toUnsignedInt(myInetAddress.getAddress()[3]);
		int service=main.group.serviceBase+first;
		InetSocketAddress inetSocketAddress=new InetSocketAddress(myInetAddress,service);
		for(int i=1;i<=n;i++) {
			// pn("i="+i+", ");
			boolean ok=tablet.startListening(inetSocketAddress);
			assertTrue(ok);
			tablet.stopListening();
			// some of these are in state new after join;
		}
	}
	@Test public void testClick() throws Exception {
		Tablet tablet=main.instance();
		int first=Byte.toUnsignedInt(myInetAddress.getAddress()[3]);
		int service=main.group.serviceBase+first;
		InetSocketAddress inetSocketAddress=new InetSocketAddress(myInetAddress,service);
		boolean ok=tablet.startListening(inetSocketAddress);
		assertTrue(ok);
		tablet.click(1,myInetAddress);
		assertTrue(main.model.state(1));
		Thread.sleep(200);
		tablet.stopListening();
	}
	@Test public void testClickWithSomeTablets() throws Exception {
		int first=Byte.toUnsignedInt(myInetAddress.getAddress()[3]);
		int n=32;
		Group group=new Group(first,first+n-1,true);
		ArrayList<Main> mains=new ArrayList<>();
		for(InetSocketAddress inetSocketAddress:group.socketAddresses(myInetAddress)) {
			Main main=new Main(group,Model.mark1.clone());
			mains.add(main);
			Tablet tablet=main.instance();
			tablet.startListening(inetSocketAddress);
		}
		Main m1=mains.get(0);
		m1.instance().click(1,myInetAddress);
		Thread.sleep(100);
		for(Main main:mains)
			assertTrue(main.model.state(1));
		for(Main main:mains) {
			Tablet tablet=main.instance();
			tablet.stopListening();
		}
	}
	@Test public void testClickOnSecondTablet() throws Exception {
		int first=Byte.toUnsignedInt(myInetAddress.getAddress()[3]);
		int n=3;
		Group group=new Group(first,first+n-1,true);
		ArrayList<Main> mains=new ArrayList<>();
		// use group to get list of socket addresses?
		for(InetSocketAddress inetSocketAddress:group.socketAddresses(myInetAddress)) {
			Main main=new Main(group,Model.mark1.clone());
			mains.add(main);
			Tablet tablet=main.instance();
			tablet.startListening(inetSocketAddress);
		}
		Main m1=mains.get(0);
		m1.instance().click(1,myInetAddress);
		Thread.sleep(100);
		for(Main main:mains)
			assertTrue(main.model.state(1));
		Main m2=mains.get(1);
		m2.instance().click(1,myInetAddress);
		Thread.sleep(100);
		for(Main main:mains)
			assertFalse(main.model.state(1));
		Main m3=mains.get(2);
		m3.instance().click(Model.mark1.resetButtonId,myInetAddress);
		Thread.sleep(100);
		for(Main main:mains)
			for(int i=1;i<=Model.mark1.buttons;i++)
				assertFalse(main.model.state(1));
		for(Main main:mains) {
			Tablet tablet=main.instance();
			tablet.stopListening();
		}
	}
	int threads;
	Main main;
	InetAddress myInetAddress;
	boolean printExtraThreads=true;
	static List<String> excluded=Arrays.asList(new String[]{"main","ReaderThread"});
}
