package DTCController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InfoPanel extends JDialog implements ActionListener {

    public final static Font fFont = new Font("monospaced",Font.PLAIN,12);
    JTextArea carInfoArea;
    JTextArea csInfoArea;
    JButton dismissButton;

    public InfoPanel(Dump d) {

        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(null);
        innerPanel.setPreferredSize(new Dimension(500, 450));

        carInfoArea = new JTextArea();
        carInfoArea.setEditable(false);
        carInfoArea.setBorder(BorderFactory.createTitledBorder("Possible car(s)"));
        CarItem[] items = CarInfo.getCars(d.software);
        StringBuffer carInfos = new StringBuffer();
        for(int i=0;i<items.length;i++) {
            carInfos.append(items[i]);
            carInfos.append('\n');
        }
        carInfoArea.setText(carInfos.toString());
        carInfoArea.setBounds(5,5,490,100);
        innerPanel.add(carInfoArea);

        csInfoArea = new JTextArea();
        csInfoArea.setEditable(false);
        csInfoArea.setFont(fFont);
        csInfoArea.setBorder(BorderFactory.createTitledBorder("Checksum info"));
        csInfoArea.setText(ChecksumHelper.getCSinfos(d.memory));
        csInfoArea.setBounds(5,110,490,300);
        innerPanel.add(csInfoArea);

        dismissButton = new JButton("Dismiss");
        dismissButton.addActionListener(this);
        dismissButton.setBounds(495,420,95,25);
        innerPanel.add(dismissButton);

        setContentPane(innerPanel);
        setTitle("Info panel");
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if( e.getSource()==dismissButton ) {
            dispose();
            setVisible(false);
        }
    }
}
