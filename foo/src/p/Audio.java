package p;
import java.io.BufferedInputStream;
import java.util.*;
import java.util.Observable;
import java.util.concurrent.Callable;
//import java.util.concurrent.*;
import java.util.function.Consumer;
import javax.sound.sampled.*;
import static p.Main.*;
import static p.Main.IO.*;
public interface Audio {
    enum Sound {
        electronic_chime_kevangc_495939803,glass_ping_go445_1207030150,store_door_chime_mike_koenig_570742973;
    }
    void play(Sound sound);
    class AudioObserver implements Observer {
        public AudioObserver(Model model) {
            this.model=model;
        }
        public synchronized boolean isChimimg() {
            return timer!=null;
        }
        public synchronized void startChimer() {
            if(!isChimimg()) {
                timer=new Timer();
                timer.schedule(new TimerTask() {
                    @Override public void run() {
                        p("start chimer.");
                        Audio.audio.play(Sound.electronic_chime_kevangc_495939803);
                    }
                },0,10_000);
            }
        }
        public synchronized void stopChimer() {
            if(isChimimg()) {
                p("stop chimer.");
                timer.cancel();
                timer=null;
            }
        }
        @Override public void update(Observable observable,Object hint) {
            if(observable instanceof Model) if(this.model.equals(observable)) {
                if(hint instanceof Sound) Audio.audio.play((Sound)hint);
                if(model.areAnyButtonsOn()) startChimer();
                else stopChimer();
            } else p("not our model!");
            else p("not a model!");
        }
        private final Model model;
        volatile Timer timer;
    }
    class Instance {
        public static void main(String[] args) throws InterruptedException {
            Audio.Instance.sound=true;
            for(Sound sound:Sound.values()) {
                Audio.audio.play(sound);
                //Thread.sleep(1_000);
            }
            Thread.sleep(10000);
            p("exit main");
            printThreads();
        }
        public static boolean sound=true;
    }
    interface Factory {
        Audio create();
        class FactoryImpl implements Factory {
            private FactoryImpl() {}
            @Override public Audio create() {
                return isAndroid()?new AndroidAudio():new WindowsAudio();
            }
            public static class AndroidAudio implements Audio {
                AndroidAudio() {}
                @Override public void play(Sound sound) {
                    if(Audio.Instance.sound) if(consumer!=null) consumer.accept(sound);
                    else p("callback is not set: "+sound);
                }
                public void setCallback(Consumer<Sound> callback) {
                    this.consumer=callback;
                }
                public Consumer<Sound> consumer;
            }
            private static class WindowsAudio implements Audio {
                WindowsAudio() {}
                private static void play_(final Sound sound) {
                    try {
                        String filename=sound.name()+".wav";
                        Clip clip=AudioSystem.getClip();
                        AudioInputStream inputStream=AudioSystem.getAudioInputStream(new BufferedInputStream(Audio.class.getResourceAsStream(filename)));
                        if(inputStream!=null) {
                            clip.open(inputStream);
                            FloatControl gainControl=(FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
                            gainControl.setValue(-25.0f); // ?
                            clip.start();
                            // maybe do not wait?
                            while(clip.getMicrosecondLength()!=clip.getMicrosecondPosition())
                                Thread.sleep(1); // wait
                            // or at least don't wait here?
                            //Thread.sleep(500);
                            clip.close();
                        } else p("input stream is null!");
                    } catch(Exception e) {
                        e.printStackTrace();
                        p("caught: "+e);
                        p("failed to play: "+sound);
                    }
                }
                private class AudioCallable implements Callable<Void>,Runnable {
                    private AudioCallable(Sound sound) {
                        this.sound=sound;
                    }
                    @Override public void run() {
                        Thread.currentThread().setName(getClass().getName());
                        play_(sound);
                    }
                    @Override public Void call() throws Exception {
                        run();
                        return null;
                    }
                    final Sound sound;
                }
                @Override public void play(final Sound sound) {
                    if(Audio.Instance.sound) if(runOnSeparateThread) {
                        p("starting audio thread for: "+sound);
                        new Thread(new AudioCallable(sound)).start();
                    } else play_(sound);
                }
                boolean runOnSeparateThread=true;
            }
        }
    }
    Factory factory=new Factory.FactoryImpl();
    Audio audio=factory.create();
}
