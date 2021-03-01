import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;


public class JavaSoundRecorder extends JFrame {
    private final String ACCESS_TOKEN = "b9qvxQYNftIAAAAAAAAAAa1-deVZmVEjtF6JgTRvzpJMmRINCq5FtB_qdAkFy5rn";
    DbxRequestConfig config;
    DbxClientV2 client;
    AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
    TargetDataLine line;
    DataLine.Info info;
    AudioFormat format;
    InputStream in;
    private JFrame fd;

    public JavaSoundRecorder() {
        createFrame();
        config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
        client = new DbxClientV2(config, ACCESS_TOKEN);
        format = getAudioFormat();
        info = new DataLine.Info(TargetDataLine.class, format);
    }

    AudioFormat getAudioFormat() {
        float sampleRate = 16000;
        int sampleSizeInBits = 8;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

    public void recordAudio(int milliseconds) {
        Date date = new Date();
        SimpleDateFormat form = new SimpleDateFormat("yyyyMMdd_HHmmss");
        form.format(date);
        String now = form.format(date);
        String pathToFile = "C:/Users/steve/sound" + now + ".wav";
        File file = new File(pathToFile);
        start(file);
        finish(file, milliseconds, now);
    }

    void start(File file) {
        Thread thread = new Thread(() -> {
            try {
                if (!AudioSystem.isLineSupported(info)) {
                    System.out.println("Line not supported");
                    System.exit(0);
                }
                line = (TargetDataLine) AudioSystem.getLine(info);
                line.open(format);
                line.start();
                System.out.println("Start capturing...");

                AudioInputStream ais = new AudioInputStream(line);

                System.out.println("Start recording...");
                fd = createFrameDialog("Идет аудиозапись...");

                AudioSystem.write(ais, fileType, file);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        thread.start();
    }

    void finish(File file, int milliseconds, String now) {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(milliseconds);
                line.stop();
                line.close();
                System.out.println("Finished");
                fd.dispose();
                fd = createFrameDialog("Идет передача данных на DropBox...");
                in = new FileInputStream(file.getPath());
                client.files().uploadBuilder("/" + now + ".wav")
                        .uploadAndFinish(in);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (file.delete()) {
                System.out.println("файл " + now + ".wav" + " удален");
            } else System.out.println("файл " + now + ".wav" + " не был найден");

            fd.dispose();
            this.setEnabled(true);
        });
        thread.start();
    }

    public int getTime(String time) {
        int i = 0;
        try {
            i = Integer.parseInt(time);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        i = Math.abs(i);
        return i * 1000;
    }

    public JPanel getPanel() {
        JLabel label = new JLabel("Введите время записи в сек. и нажмите <Включить запись>");
        TextField textField = new TextField(5);
        JButton start = new JButton("Включить запись");
        JButton exit = new JButton("Выход");
        JPanel panel = new JPanel();

        start.addActionListener(actionEvent -> {
            this.setEnabled(false);
            this.recordAudio(getTime(textField.getText()));
        });

        exit.addActionListener(actionEvent -> {
            try {
                line.stop();
                line.close();
                in.close();
            } catch (Exception e) {
                dispose();
                System.exit(0);
            }
            dispose();
            System.exit(0);
        });

        panel.setLayout(new FlowLayout(0, 15, 20));
        panel.add(label);
        panel.add(textField);
        panel.add(start);
        panel.add(exit);
        return panel;
    }

    public void createFrame() {
        setSize(400, 150);
        setLocationRelativeTo(null);
        setResizable(false);
        add(getPanel());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setVisible(true);
    }

    public JFrame createFrameDialog(String text) {
        JFrame frameDialog = new JFrame();
        JPanel panel = new JPanel();
        JLabel label = new JLabel(text);
        JButton button = new JButton("Отмена");
        button.addActionListener(actionEvent -> {
            try {
                line.stop();
                line.close();
                in.close();
            } catch (Exception e) {
                frameDialog.dispose();
                System.exit(0);
            }
            frameDialog.dispose();
            System.exit(0);
        });
        label.setFont(new Font("Serif", Font.ITALIC, 20));
        frameDialog.setSize(350, 120);
        frameDialog.setLocationRelativeTo(null);
        frameDialog.setResizable(false);
        panel.add(label, CENTER_ALIGNMENT);
        panel.add(button);
        frameDialog.add(panel);

        frameDialog.setVisible(true);
        return frameDialog;
    }
}
