package DTCController;

public class DTCClass {

  public final static String LAMP_MODE[] = {
    "OFF",
    "ON",
    "BLINK"
  };

  public final static String TRIGGER[] = {
    "no handling",
    "time quantum",
    "driving cycle",
    "warm up cycle",
    "flc driving cycle"
  };

  public final static String TRIGGER_TDLC[] = {
    "no handling",
    "each afterrun_1",
    "each afterrun_2",
    "warm up cycle"
  };

  public final static String SCATT[] = {
    "not visible for OBD-Tester",
    "visible for OBD-Tester"
  };

  public final static String RES_ERF[] = {
    "no Reset",
    "Reset on Init",
    "Reset on Restart",
    "Reset on Init or Restart"
  };

  public final static String READINESS[] = {
    "EGR system monitoring",
    "catalyst monitoring",
    "misfire monitoring",
    "fuel system",
    "comprehensive components",
    "oxygen sensor",
    "oxygen sensor heater"
  };

  int classId;   // Class ID
  int mil;       // Mode for MIL activation
  int tflc;      // Trigger type for fault validation debouncing
  int flc;       // Number of trigger events for fault validation debouncing (->MIL on)
  int thlc;      // Trigger type for fault healing (->MIL off)
  int hlc;       // Number of trigger events for fault healing (->MIL off)
  int scatt;     // Fault is visible for OBD Scan tool tester (SCATT) if value = 1
  int tdlcpen;   // Trigger type for delete debouncing of pending faults
  int dlcpen;    // Number of trigger events to delete pending faults
  int tdlc;      // Trigger type for delete debouncing
  int dlckd;     // Number of trigger events to delete faults in status service specific
  int dlc;       // Number of trigger events to delete fault code memory entries
  int prio;      // Priority of fault code memory entry for displacement an freeze frame assignment
  int syslamp;   // Mode of System Lamp activation
  int reserf;    // Behaviour of fault flags at reset and canceled afterrun
  int readiness; // Debounce class assignment to Readiness Flags

  private byte[] memory;
  private DTCInfo dtcInfo;

  public DTCClass(int id,DTCInfo dtcInfo,byte[] memory) {

    classId = id;
    this.memory = memory;
    this.dtcInfo = dtcInfo;

    mil=memory[dtcInfo.defClassAddr+classId*15];
    tflc=memory[dtcInfo.defClassAddr+classId*15+1];
    flc=memory[dtcInfo.defClassAddr+classId*15+2];
    thlc=memory[dtcInfo.defClassAddr+classId*15+3];
    hlc=memory[dtcInfo.defClassAddr+classId*15+4];
    scatt=memory[dtcInfo.defClassAddr+classId*15+5];
    tdlcpen=memory[dtcInfo.defClassAddr+classId*15+6];
    dlcpen=memory[dtcInfo.defClassAddr+classId*15+7];
    tdlc=memory[dtcInfo.defClassAddr+classId*15+8];
    dlckd=memory[dtcInfo.defClassAddr+classId*15+9];
    dlc=memory[dtcInfo.defClassAddr+classId*15+10];
    prio=memory[dtcInfo.defClassAddr+classId*15+11];
    syslamp=memory[dtcInfo.defClassAddr+classId*15+12];
    reserf=memory[dtcInfo.defClassAddr+classId*15+13];
    readiness=memory[dtcInfo.defClassAddr+classId*15+14];

  }

  public static final String getReadinessStr(int readiness) {
    if(readiness==0) {
      return "non";
    } else {
      StringBuffer ret = new StringBuffer();
      int b = 1;
      for (int i = 0; i<7;i++) {
        if((readiness & b) != 0) {
          ret.append(READINESS[i]);
          ret.append(" ");
        }
        b = b << 1;
      }
      return ret.toString();
    }
  }

  public int getAddr() {
    return dtcInfo.defClassAddr + 15*classId;
  }

  public void apply(
          int mil,
          int tflc,
          int flc,
          int thlc,
          int hlc,
          int scatt,
          int tdlcpen,
          int dlcpen,
          int tdlc,
          int dlckd,
          int dlc,
          int prio,
          int syslamp,
          int reserf,
          int readiness) {

    Dump.setMemory(memory,dtcInfo.defClassAddr + 15*classId+0 , mil);
    Dump.setMemory(memory,dtcInfo.defClassAddr + 15*classId+1 , tflc);
    Dump.setMemory(memory,dtcInfo.defClassAddr + 15*classId+2 , flc);
    Dump.setMemory(memory,dtcInfo.defClassAddr + 15*classId+3 , thlc);
    Dump.setMemory(memory,dtcInfo.defClassAddr + 15*classId+4 , hlc);
    Dump.setMemory(memory,dtcInfo.defClassAddr + 15*classId+5 , scatt);
    Dump.setMemory(memory,dtcInfo.defClassAddr + 15*classId+6 , tdlcpen);
    Dump.setMemory(memory,dtcInfo.defClassAddr + 15*classId+7 , dlcpen);
    Dump.setMemory(memory,dtcInfo.defClassAddr + 15*classId+8 , tdlc);
    Dump.setMemory(memory,dtcInfo.defClassAddr + 15*classId+9 , dlckd);
    Dump.setMemory(memory,dtcInfo.defClassAddr + 15*classId+10 , dlc);
    Dump.setMemory(memory,dtcInfo.defClassAddr + 15*classId+11 , prio);
    Dump.setMemory(memory,dtcInfo.defClassAddr + 15*classId+12 , syslamp);
    Dump.setMemory(memory,dtcInfo.defClassAddr + 15*classId+13 , reserf);
    Dump.setMemory(memory,dtcInfo.defClassAddr + 15*classId+14 , readiness);

  }

  @Override
  public String toString() {
    return "DTCClass{" +
            "classID=" + classId +
            ", mil=" + LAMP_MODE[mil] +
            ", tflc=" + TRIGGER[tflc] +
            ", flc=" + flc +
            ", thlc=" + TRIGGER[thlc] +
            ", hlc=" + hlc +
            ", scatt=" + SCATT[scatt] +
            ", tdlcpen=" + TRIGGER_TDLC[tdlcpen] +
            ", dlcpen=" + dlcpen +
            ", tdlc=" + TRIGGER_TDLC[tdlc] +
            ", dlckd=" + dlckd +
            ", dlc=" + dlc +
            ", prio=" + prio +
            ", syslamp=" + LAMP_MODE[syslamp] +
            ", reserf=" + RES_ERF[reserf] +
            ", readiness=" + getReadinessStr(readiness) +
            '}';
  }

}
