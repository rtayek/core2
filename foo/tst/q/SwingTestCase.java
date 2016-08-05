package q;

import static org.junit.Assert.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import p.Main;
import p.Model;
import p.Audio.AudioObserver;
import p.Main.Group;
import p.Main.Tablet;

public class SwingTestCase {
	@BeforeClass public static void setUpBeforeClass() throws Exception {}
	@AfterClass public static void tearDownAfterClass() throws Exception {}
	@Before public void setUp() throws Exception {
		InetAddress inetAddress=InetAddress.getLocalHost();
		int first=Byte.toUnsignedInt(inetAddress.getAddress()[3]);
		InetAddress router=Inet4Address.getByName("10.0.0.1");
		Group group=new Group(first,first,false);
		int service=group.serviceBase+first;
		InetSocketAddress inetSocketAddress=new InetSocketAddress(inetAddress,service);
		Main main=new Main(group,Model.mark1.clone());
		Tablet tablet=main.instance();
		main.model.addObserver(swing=Swing.create(main));
		main.model.addObserver(new AudioObserver(main.model));
		tablet.startListening(inetSocketAddress);

	}
	@After public void tearDown() throws Exception {}
	@Test public void test() {
		swing.guiAdapter.processClick(0);
		assertTrue(swing.main.model.state(1));
	}
	Swing swing;
}
