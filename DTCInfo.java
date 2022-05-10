package DTCController;

public class DTCInfo {

  int nbDTC;          // Number of DTC
  int faultPathAddr;  // Fault path (list of [4xUWORD])
  int codeAddr;       // Fault code (list of [UWORD])
  int classAddr;      // Fault class (list of [UBYTE])
  int envAddr;        // Fault environment (list ot [5xUBYTE])
  int defClassAddr;   // Start of class definition

  /**
   * Return string representation of a DTC Info
   * @param b1 First byte
   * @param b2 Second byte
   */
  public static String getStr(byte b1,byte b2) {

    StringBuffer code = new StringBuffer();
    int c0 = (((int)b1 & 0xC0) >> 6) & 0x0F;
    int c1 = (((int)b1 & 0x30) >> 4) & 0x0F;
    switch (c0) {
      case 0:
        code.append('P');
        break;
      case 1:
        code.append('C');
        break;
      case 2:
        code.append('B');
        break;
      case 3:
        code.append('U');
        break;
    }
    code.append(Integer.toString(c1));
    code.append(Integer.toString(((int)b1) & 0x0F));
    code.append(String.format("%02X",b2));
    return code.toString();

  }

  public static String getEnvStr(byte env) {
    int envIdx = (((int) env) & 0xFF);
    return DTCEnv.sigNames[envIdx];
  }


  @Override
  public String toString() {
    return "DTC{" +
            "nbDTC=" + nbDTC +
            ", faultPathAddr=" + String.format("%06X",faultPathAddr) +
            ", codeAddr=" + String.format("%06X",codeAddr) +
            ", classAddr="+ String.format("%06X",classAddr) +
            ", envAddr="+ String.format("%06X",envAddr) +
            ", defClassAddr="+ String.format("%06X",defClassAddr) +
            '}';
  }

}
