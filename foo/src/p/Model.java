package p;
import java.util.Observable;
import static p.Main.IO.*;
public class Model extends Observable implements Cloneable {
	public Model(int buttons,Integer resetButtonId) {
		this(buttons,resetButtonId,++serialNumbers);
	}
	private Model(int buttons,Integer resetButtonId,int serialNumber) {
		this.serialNumber=serialNumber; // so clones will have the same serial
										// number
		this.buttons=buttons;
		states=new Boolean[buttons];
		for(int i=0;i<buttons;i++)
			states[i]=false;
		this.resetButtonId=resetButtonId;
		// this.colors=new Colors();
		reset();
	}
	@Override public Model clone() {
		return new Model(buttons,resetButtonId,serialNumber);
	}
	public void reset() {
		synchronized(states) {
			for(int i=1;i<=buttons;i++)
				setState(i,false);
		}
	}
	public void setChangedAndNotify(Object object) {
		setChanged();
		notifyObservers(object);
	}
	public boolean areAnyButtonsOn() {
		boolean areAnyButtonsOn=false;
		for(int i=0;i<buttons;i++)
			areAnyButtonsOn|=states[i];
		return areAnyButtonsOn;
	}
	public void setState(Integer id,Boolean state) {
		if(1<=id&&id<=buttons) synchronized(states) {
			boolean old=state(id);
			states[id-1]=state;
			if(old!=state) setChangedAndNotify(id);
		}
		else p("out of bounds in set state: "+id);
	}
	public Boolean state(Integer id) {
		if(1<=id&&id<=buttons) synchronized(states) {
			return states[id-1];
		}
		else {
			p("out of bounds in get state: "+id);
			return null;
		}
	}
	public Boolean[] states() {
		Boolean[] copy=new Boolean[buttons];
		synchronized(states) {
			System.arraycopy(states,0,copy,0,buttons);
			return copy;
		}
	}
	public static Character toCharacter(Boolean state) {
		return state==null?null:state?'T':'F';
	}
	public String toCharacters() { // should be synchronized?
		String s="";
		for(boolean state:states())
			s+=toCharacter(state);
		return s;
	}
	@Override public String toString() {
		String s="(#"+serialNumber+"): {";
		synchronized(states) {
			s+=toCharacters();
			s+='}';
			return s;
		}
	}
	public static void main(String[] args) throws Exception {
		Model model=new Model(7,null);
		p(model.toString());
	}
	public final Integer serialNumber;
	public final Integer buttons;
	public final Integer resetButtonId;
	private final Boolean[] states;
	static int serialNumbers=0;
	public static final Model mark1=new Model(11,11);
}
