import java.io.File;
import java.io.IOException;
import java.io.SequenceInputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFormat;
import java.util.concurrent.ThreadLocalRandom;

public class WaveChop {
    public File sourceFile;
    public File prevWavResult;
    public File wavResult;
    public File tempFile;
    public File tempAppendFile;
    public int lengthSeconds;

    public void run() throws Exception {        
        sourceFile = new File("sourceFile.wav");
        int wavLengthSeconds = getWavLengthSeconds(sourceFile);
        wavResult = new File("wavResult.wav");
        tempFile = new File("tempFile.wav");
        prevWavResult = new File("prevWavResult.wav");
        tempAppendFile = new File("tempAppendFile.wav");
        createChop(sourceFile, prevWavResult, 0, 0);
        lengthSeconds = 0;
        while (lengthSeconds < 300) {
            int startSecond = ThreadLocalRandom.current().nextInt(0, wavLengthSeconds);
            int secondsToCopy = ThreadLocalRandom.current().nextInt(0, 5);
            secondsToCopy = secondsToCopy * 4;
            lengthSeconds += secondsToCopy;
            createChop(sourceFile, tempFile, startSecond, secondsToCopy);
            appendStream(tempFile, prevWavResult, wavResult);

            AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(sourceFile);
            AudioSystem.write(AudioSystem.getAudioInputStream(wavResult), fileFormat.getType(), prevWavResult);
        }
    }
    
    public int getWavLengthSeconds(File sourceFile) throws Exception {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(sourceFile);
        AudioFormat format = audioInputStream.getFormat();
        long audioFileLength = sourceFile.length();
        int frameSize = format.getFrameSize();
        float frameRate = format.getFrameRate();
        float durationInSeconds = (audioFileLength / (frameSize * frameRate));
        // downcast to int
        return (int) durationInSeconds;
    }
      
    public void createChop(File sourceFile, File destinationFile, int startSecond, int secondsToCopy) {
        AudioInputStream inputStream = null;
        AudioInputStream shortenedStream = null;
        try {
            AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(sourceFile);
            AudioFormat format = fileFormat.getFormat();
            inputStream = AudioSystem.getAudioInputStream(sourceFile);
            int bytesPerSecond = format.getFrameSize() * (int)format.getFrameRate();
            inputStream.skip(startSecond * bytesPerSecond);
            long framesOfAudioToCopy = secondsToCopy * (int)format.getFrameRate()/4;
            shortenedStream = new AudioInputStream(inputStream, format, framesOfAudioToCopy);
            AudioSystem.write(shortenedStream, fileFormat.getType(), destinationFile);
        } catch (Exception e) {
            System.out.println(e.toString());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            }
            if (shortenedStream != null) {
                try {
                    shortenedStream.close();
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            }
        }
    }

    public void appendStream(File file1, File file2, File destinationFile) {
        AudioInputStream audioStream1 = null;
        AudioInputStream audioStream2 = null;
        AudioInputStream combinedStream = null;
        try {
            AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(file1);
            AudioFormat format = fileFormat.getFormat();
            audioStream1 = AudioSystem.getAudioInputStream(file1);
            audioStream2 = AudioSystem.getAudioInputStream(file2);
            int bytesPerSecond = format.getFrameSize() * (int)format.getFrameRate();
            long framesOfAudioFinal = audioStream1.getFrameLength() + audioStream2.getFrameLength();
            combinedStream = new AudioInputStream(new SequenceInputStream(audioStream1, audioStream2), format, framesOfAudioFinal);
            AudioSystem.write(combinedStream, fileFormat.getType(), destinationFile);
        } catch (Exception e) {
            System.out.println(e.toString());
        } finally {
            if (audioStream1 != null) {
                try {
                    audioStream1.close();
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            }
            if (audioStream2 != null) {
                try {
                    audioStream2.close();
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            }            
            if (combinedStream != null) {
                try {
                    combinedStream.close();
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            }
        }
    }
}