package q;
import java.util.*;
import p.Main;
import p.Main.Tablet;
import static p.Main.*;
import static p.Main.IO.*;
public interface GuiAdapter extends Observer {
	void setButtonState(int id,boolean state); // of the widget!
	void setButtonText(int id,String string); // of the widget!
	public abstract class GuiAdapterABC implements GuiAdapter {
		public GuiAdapterABC(Main main) {
			this.main=main;
		}
		public void processClick(int index) {
			int id=index+1;
			p("click: "+index+" in: "+this);
			if(1<=id&&id<=main.model.buttons) main.instance().click(id,null);
			else p(id+" is bad button id!");
		}
		@Override public void update(Observable observable,Object hint) {
			for(Integer buttonId=1;buttonId<=main.model.buttons;buttonId++) {
				setButtonState(buttonId,main.model.state(buttonId));
				setButtonText(buttonId,"id");
			}
		}
		final Main main;
	}
}
