import java.io.File;
import java.io.IOException;
import java.io.SequenceInputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFormat;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    public static File sourceFile;
    public static File prevWavResult;
    public static File wavResult;
    public static File tempFile;
    public static File tempAppendFile;
    public static int lengthSeconds;

    public static void main(String[] args) throws Exception {
        WaveChop waveChop = new WaveChop();
        waveChop.run();
    }
}