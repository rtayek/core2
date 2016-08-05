package q;
import static p.Main.IO.*;

import static java.lang.Math.*;
import static p.Main.IO.*;
import static java.lang.Math.*;
public class Colors {
    // this wants to be a map <widgetId,Info>
    // sort of a layout!
    
    public int color(int index,boolean state) {
        if(index==rows*columns) // reset
            return state?resetOn:resetOff;
        else if(index/columns%2==0) return state?whiteOn:whiteOff;
        else return state?on[index%columns]:off[index%columns];
    }
    public int aColor(int index,boolean state) {
        return color(index,state)|0xff000000; // android seems to want this
    }
    public static int aColor(int color) {
        return color|0xff000000; // android seems to want this
    }
    public static int color(int r,int g,int b) {
        int c=min(r,255)&0xff;
        c=(c<<8)|(min(g,255)&0xff);
        c=(c<<8)|(min(b,255)&0xff);
        return c;
    }
    public static int color(double rate) {
        if(!(0<=rate&&rate<=1)) {
            p("bad rate: "+rate);
            return 0;
        }
        int color=0;
        if(rate<.1) color=green;
        else if(rate<.2) color=yellow;
        else if(rate<.3) color=orange;
        else color=red;
        return color;
    }
    public static int smooth(double rate) {
        double yellow=.15;
        int color=0;
        if(rate<yellow) color=Colors.color((int)(rate/yellow*255*1.2),255,0);
        else if(rate>yellow) color=Colors.color(255,(int)((1-rate)/(1-yellow)*255*1.5),0);
        else color=Colors.yellow;
        return color;
    }
    public static int color2(double rate) {
        if(!(0<=rate&&rate<=1)) {
            p("bad rate: "+rate);
            return 0;
        }
        int red=(int)(255*rate);
        int green=(int)(255*(1-rate));
        return color(red,green,0);
    }
    public static String toString(int color) {
        //return ""+(((color&0xff000000)>>24)&0xff)+" "+(((color&0x00ff0000)>>16)&0xff)+" "+(((color&0x0000ff00)>>8)&0xff)+" "+(((color&0x000000ff)>>0)&0xff);
        return ""+((color&0xff000000)>>24&0xff)+" "+((color&0x00ff0000)>>16&0xff)+" "+((color&0x0000ff00)>>8&0xff)+" "+((color&0x000000ff)>>0&0xff);
    }
    public final int rows=2,columns=5,n=rows*columns+1;
    public final int background=0xd0d0e0;
    public final Integer[] on=new Integer[columns],off=new Integer[columns];
    public final Integer whiteOn=0xffffff,whiteOff=0xe0e0e0;
    public final Integer resetOn=0xffff80,resetOff=0x80ffff;
    {
        on[0]=red;
        on[1]=yellow;
        on[2]=green;
        on[3]=blue;
        on[4]=0xff8000;
        off[0]=0x800000;
        off[1]=0x808000;
        off[2]=0x008000;
        off[3]=0x000080;
        off[4]=0x804000;
    }
    public static final int red=0x00ff0000,orange=0x00ffc800,yellow=0x00ffff00,green=0x0000ff00,blue=0x000000ff;
}
