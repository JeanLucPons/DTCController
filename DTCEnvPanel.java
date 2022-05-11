package DTCController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DTCEnvPanel extends JDialog implements ActionListener {

  public final static int ENV_NUMBER = 5;

  private JLabel[] l = new JLabel[ENV_NUMBER];
  private JComboBox[] c = new JComboBox[ENV_NUMBER];

  private JButton applyButton;
  private JButton cancelButton;
  private DTCEnv  dtcEnv;

  public DTCEnvPanel(DTCEnv dtcEnv) {

    this.dtcEnv=dtcEnv;

    JPanel innerPanel = new JPanel();
    innerPanel.setLayout(null);
    innerPanel.setPreferredSize(new Dimension(375,200));

    setContentPane(innerPanel);
    setTitle("DTC environement #" + dtcEnv.getDTCIdx());

    int y = 10;
    for(int i=0;i<5;i++,y+=30) {

      l[i] = new JLabel(dtcEnv.getCode(i));
      l[i].setBounds(10,y,100,25);
      innerPanel.add(l[i]);
      c[i] = new JComboBox(DTCEnv.sigNames);
      c[i].setBounds(110,y,260,25);
      c[i].setSelectedIndex(dtcEnv.getEnv(i));
      innerPanel.add(c[i]);

    }

    applyButton = new JButton("Apply");
    applyButton.addActionListener(this);
    applyButton.setBounds( 165,y,100,25 );
    innerPanel.add(applyButton);

    cancelButton = new JButton("Cancel");
    cancelButton.setBounds( 270,y,100,25 );
    cancelButton.addActionListener(this);
    innerPanel.add(cancelButton);

    pack();
    setLocationRelativeTo(null);
    setVisible(true);

  }


  @Override
  public void actionPerformed(ActionEvent e) {
    if( e.getSource()==cancelButton ) {
      dispose();
      setVisible(false);
    } else if ( e.getSource()==applyButton ) {
      int[] envs = new int[ENV_NUMBER];
      for(int i=0;i<ENV_NUMBER;i++)
        envs[i] = c[i].getSelectedIndex();
      dtcEnv.apply(envs);
    }
  }

}
