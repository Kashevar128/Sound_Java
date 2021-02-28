import javax.swing.*;
import java.awt.*;

public class frame extends Frame {
    public JPanel getPanel() {
        JLabel label = new JLabel("Введите время записи и нажмите <Включить запись>");
        TextField textField = new TextField(5);
        JButton start = new JButton("Включить запись");
        JButton exit = new JButton("Выход");
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(0, 15, 20));

        panel.add(label);
        panel.add(textField);
        panel.add(start);
        panel.add(exit);
        return panel;
    }

    public void createFrame() {
        setSize(350, 150);
        setLocationRelativeTo(null);
        setResizable(false);
        add(getPanel());

        setVisible(true);
    }

}
