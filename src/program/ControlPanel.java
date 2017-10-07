package program;

import scala.Function1;
import scala.runtime.BoxedUnit;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by weijiayi on 3/6/15.
 */
public class ControlPanel {
    private JTextField widthField;
    private JTextField heightField;
    private JTextField angleField;
    private JTextField numberField;
    private JTextField pixelField;
    private JTextField gridSizeField;
    private JButton plotButton;
    private JPanel rootPanel;
    private JLabel timeUseLabel;

    private CanvasController canvasController = new CanvasController();

    public static void main(String[] args) {
        JFrame frame = new JFrame("ControlPanel");
        frame.setContentPane(new ControlPanel().rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public static double castDouble(JTextField field){
        return Double.parseDouble(field.getText());
    }
    public static int castInt(JTextField field) {return Integer.parseInt(field.getText()); }

    public ControlPanel() {
        plotButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Double width = castDouble(widthField);
                Double height = castDouble(heightField);
                RecRegion recRegion = new RecRegion(width, height);

                int sample = castInt(numberField);
                int angle = castInt(angleField);
                int pixelPerUnit = castInt(pixelField);
                Double gridSize = castDouble(gridSizeField);

                canvasController.generateNetwork(recRegion,sample,angle,pixelPerUnit,gridSize,timeUseLabel);
            }
        });
    }
}
