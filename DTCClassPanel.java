package DTCController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DTCClassPanel extends JDialog implements ActionListener {

  private JComboBox  mil;       // Mode for MIL activation
  private JComboBox  tflc;      // Trigger type for fault validation debouncing
  private JTextField flc;       // Number of trigger events for fault validation debouncing (->MIL on)
  private JComboBox  thlc;      // Trigger type for fault healing (->MIL off)
  private JTextField hlc;       // Number of trigger events for fault healing (->MIL off)
  private JComboBox  scatt;     // Fault is visible for OBD Scan tool tester (SCATT) if value = 1
  private JComboBox  tdlcpen;   // Trigger type for delete debouncing of pending faults
  private JTextField dlcpen;    // Number of trigger events to delete pending faults
  private JComboBox  tdlc;      // Trigger type for delete debouncing
  private JTextField dlckd;     // Number of trigger events to delete faults in status service specific
  private JTextField dlc;       // Number of trigger events to delete fault code memory entries
  private JTextField prio;      // Priority of fault code memory entry for displacement an freeze frame assignment
  private JComboBox  syslamp;   // Mode of System Lamp activation
  private JComboBox  reserf;    // Behaviour of fault flags at reset and canceled afterrun
  private JCheckBox  readinessEGR;                // Readiness: EGR system monitoring
  private JCheckBox  readinessCatalyst;           // Readiness: catalyst monitoring
  private JCheckBox  readinessMisfire;            // Readiness: misfire monitoring
  private JCheckBox  readinessFuel;               // Readiness: catalyst monitoring
  private JCheckBox  readinessComprehensive;      // Readiness: comprehensive components
  private JCheckBox  readinessOxygenSensor;       // Readiness: Oxygen sensor
  private JCheckBox  readinessOxygenSensorHeater; // Readiness: Oxygen sensor heater

  private JButton applyButton;
  private JButton cancelButton;

  private DTCClass dtcClass;

  public DTCClassPanel(DTCClass dtcClass) {

    this.dtcClass = dtcClass;

    JPanel innerPanel = new JPanel();
    innerPanel.setLayout(null);
    innerPanel.setPreferredSize(new Dimension(320,605));

    int y = 10;

    JLabel milLabel = new JLabel("MIL");
    milLabel.setBounds(10,y,150,25);
    innerPanel.add(milLabel);
    mil = new JComboBox(DTCClass.LAMP_MODE);
    mil.setToolTipText("Mode for Malfunction Indication Lamp activation");
    mil.setBounds(160,y,150,25);
    mil.setSelectedIndex(dtcClass.mil);
    innerPanel.add(mil);

    y+=30;
    JLabel tflcLabel = new JLabel("Trigger FLC");
    tflcLabel.setBounds(10,y,150,25);
    innerPanel.add(tflcLabel);
    tflc = new JComboBox(DTCClass.TRIGGER);
    tflc.setToolTipText("Trigger type for fault validation debouncing");
    tflc.setBounds(160,y,150,25);
    tflc.setSelectedIndex(dtcClass.tflc);
    innerPanel.add(tflc);

    y+=30;
    JLabel flcLabel = new JLabel("FLC");
    flcLabel.setBounds(10,y,150,25);
    innerPanel.add(flcLabel);
    flc = new JTextField();
    flc.setToolTipText("Number of trigger events for fault validation debouncing (->MIL on)");
    flc.setBounds(160,y,150,25);
    flc.setText(Integer.toString(dtcClass.flc));
    innerPanel.add(flc);

    y+=30;
    JLabel thlcLabel = new JLabel("Trigger HLC");
    thlcLabel.setBounds(10,y,150,25);
    innerPanel.add(thlcLabel);
    thlc = new JComboBox(DTCClass.TRIGGER);
    thlc.setToolTipText("Trigger type for fault healing (->MIL off)");
    thlc.setBounds(160,y,150,25);
    thlc.setSelectedIndex(dtcClass.thlc);
    innerPanel.add(thlc);

    y+=30;
    JLabel hlcLabel = new JLabel("HLC");
    hlcLabel.setBounds(10,y,150,25);
    innerPanel.add(hlcLabel);
    hlc = new JTextField();
    hlc.setToolTipText("Number of trigger events for fault healing (->MIL off)");
    hlc.setBounds(160,y,150,25);
    hlc.setText(Integer.toString(dtcClass.hlc));
    innerPanel.add(hlc);

    y+=30;
    JLabel scattLabel = new JLabel("OBD");
    scattLabel.setBounds(10,y,150,25);
    innerPanel.add(scattLabel);
    scatt = new JComboBox(DTCClass.SCATT);
    scatt.setToolTipText("Fault is visible for OBD Scan tool tester (SCATT) if value = 1");
    scatt.setBounds(160,y,150,25);
    scatt.setSelectedIndex(dtcClass.scatt);
    innerPanel.add(scatt);

    y+=30;
    JLabel tdlcpenLabel = new JLabel("Trigger DLC Pending");
    tdlcpenLabel.setBounds(10,y,150,25);
    innerPanel.add(tdlcpenLabel);
    tdlcpen = new JComboBox(DTCClass.TRIGGER_TDLC);
    tdlcpen.setToolTipText("Trigger type for delete debouncing of pending faults");
    tdlcpen.setBounds(160,y,150,25);
    tdlcpen.setSelectedIndex(dtcClass.tdlcpen);
    innerPanel.add(tdlcpen);

    y+=30;
    JLabel tdlcPenLabel = new JLabel("DLC Pending");
    tdlcPenLabel.setBounds(10,y,150,25);
    innerPanel.add(tdlcPenLabel);
    dlcpen = new JTextField();
    dlcpen.setToolTipText("Number of trigger events to delete pending faults");
    dlcpen.setBounds(160,y,150,25);
    dlcpen.setText(Integer.toString(dtcClass.dlcpen));
    innerPanel.add(dlcpen);

    y+=30;
    JLabel tdlcLabel = new JLabel("Trigger DLC");
    tdlcLabel.setBounds(10,y,150,25);
    innerPanel.add(tdlcLabel);
    tdlc = new JComboBox(DTCClass.TRIGGER_TDLC);
    tdlc.setToolTipText("Trigger type for delete debouncing");
    tdlc.setBounds(160,y,150,25);
    tdlc.setSelectedIndex(dtcClass.tdlc);
    innerPanel.add(tdlc);

    y+=30;
    JLabel dlckdLabel = new JLabel("DLCkd");
    dlckdLabel.setBounds(10,y,150,25);
    innerPanel.add(dlckdLabel);
    dlckd = new JTextField();
    dlckd.setToolTipText("Number of trigger events to delete faults in status service specific");
    dlckd.setBounds(160,y,150,25);
    dlckd.setText(Integer.toString(dtcClass.dlckd));
    innerPanel.add(dlckd);

    y+=30;
    JLabel dlcLabel = new JLabel("DLC");
    dlcLabel.setBounds(10,y,150,25);
    innerPanel.add(dlcLabel);
    dlc = new JTextField();
    dlc.setToolTipText("Number of trigger events to delete fault code memory entries");
    dlc.setBounds(160,y,150,25);
    dlc.setText(Integer.toString(dtcClass.dlc));
    innerPanel.add(dlc);

    y+=30;
    JLabel prioLabel = new JLabel("Priority");
    prioLabel.setBounds(10,y,150,25);
    innerPanel.add(prioLabel);
    prio = new JTextField();
    prio.setToolTipText("Priority of fault code memory entry for displacement an freeze frame assignment");
    prio.setBounds(160,y,150,25);
    prio.setText(Integer.toString(dtcClass.prio));
    innerPanel.add(prio);

    y+=30;
    JLabel sysLampLabel = new JLabel("SYSLamp");
    sysLampLabel.setBounds(10,y,150,25);
    innerPanel.add(sysLampLabel);
    syslamp = new JComboBox(DTCClass.LAMP_MODE);
    syslamp.setToolTipText("Mode for System Lamp activation");
    syslamp.setBounds(160,y,150,25);
    syslamp.setSelectedIndex(dtcClass.syslamp);
    innerPanel.add(syslamp);

    y+=30;
    JLabel resetLabel = new JLabel("Reset");
    resetLabel.setBounds(10,y,150,25);
    innerPanel.add(resetLabel);
    reserf = new JComboBox(DTCClass.RES_ERF);
    reserf.setToolTipText("Behaviour of fault flags at reset and canceled afterrun");
    reserf.setBounds(160,y,150,25);
    reserf.setSelectedIndex(dtcClass.reserf);
    innerPanel.add(reserf);

    y+=30;
    JPanel readinessPanel = new JPanel();
    readinessPanel.setLayout(null);
    readinessPanel.setBorder(BorderFactory.createTitledBorder("Readiness"));
    readinessPanel.setBounds(5,y,310,140);

    y=20;
    readinessEGR = new JCheckBox("EGR");
    readinessEGR.setSelected( (dtcClass.readiness & 0x01)!=0 );
    readinessEGR.setBounds(10,y,150,25);
    readinessPanel.add(readinessEGR);

    readinessCatalyst = new JCheckBox("Catalyst");
    readinessCatalyst.setSelected( (dtcClass.readiness & 0x02)!=0 );
    readinessCatalyst.setBounds(160,y,140,25);
    readinessPanel.add(readinessCatalyst);

    y+=30;
    readinessMisfire = new JCheckBox("Misfire");
    readinessMisfire.setSelected( (dtcClass.readiness & 0x04)!=0 );
    readinessMisfire.setBounds(10,y,150,25);
    readinessPanel.add(readinessMisfire);

    readinessFuel = new JCheckBox("Fuel");
    readinessFuel.setSelected( (dtcClass.readiness & 0x08)!=0 );
    readinessFuel.setBounds(160,y,140,25);
    readinessPanel.add(readinessFuel);

    y+=30;
    readinessComprehensive = new JCheckBox("C.Components");
    readinessComprehensive.setSelected( (dtcClass.readiness & 0x10)!=0 );
    readinessComprehensive.setBounds(10,y,150,25);
    readinessPanel.add(readinessComprehensive);

    readinessOxygenSensor = new JCheckBox("O2 Sensor");
    readinessOxygenSensor.setSelected( (dtcClass.readiness & 0x20)!=0 );
    readinessOxygenSensor.setBounds(160,y,140,25);
    readinessPanel.add(readinessOxygenSensor);

    y+=30;
    readinessOxygenSensorHeater = new JCheckBox("O2 SensorHeater");
    readinessOxygenSensorHeater.setSelected( (dtcClass.readiness & 0x40)!=0 );
    readinessOxygenSensorHeater.setBounds(10,y,150,25);
    readinessPanel.add(readinessOxygenSensorHeater);

    innerPanel.add(readinessPanel);

    y = 575;
    applyButton = new JButton("Apply");
    applyButton.addActionListener(this);
    applyButton.setBounds( 105,y,100,25 );
    innerPanel.add(applyButton);

    cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(this);
    cancelButton.setBounds( 210,y,100,25 );
    innerPanel.add(cancelButton);

    setContentPane(innerPanel);
    setTitle("Class #" + dtcClass.classId + String.format("[0x%06X]",dtcClass.getAddr()));
    pack();
    setLocationRelativeTo(null);
    setVisible(true);

  }


  @Override
  public void actionPerformed(ActionEvent e) {

    if(e.getSource()==cancelButton) {
      dispose();
      setVisible(false);
    } else if(e.getSource()==applyButton) {

      int readiness =
              (readinessEGR.isSelected()?1:0) +
              (readinessCatalyst.isSelected()?2:0) +
              (readinessMisfire.isSelected()?4:0) +
              (readinessFuel.isSelected()?8:0) +
              (readinessComprehensive.isSelected()?16:0) +
              (readinessOxygenSensor.isSelected()?32:0) +
              (readinessOxygenSensorHeater.isSelected()?64:0);

      dtcClass.apply(
              mil.getSelectedIndex(),
              tflc.getSelectedIndex(),
              Integer.parseInt(flc.getText()),
              thlc.getSelectedIndex(),
              Integer.parseInt(hlc.getText()),
              scatt.getSelectedIndex(),
              tdlcpen.getSelectedIndex(),
              Integer.parseInt(dlcpen.getText()),
              tdlc.getSelectedIndex(),
              Integer.parseInt(dlckd.getText()),
              Integer.parseInt(dlc.getText()),
              Integer.parseInt(prio.getText()),
              syslamp.getSelectedIndex(),
              reserf.getSelectedIndex(),
              readiness
      );

    }

  }
}
