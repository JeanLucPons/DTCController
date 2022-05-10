package DTCController;

public class DTC {

  int idx;

  private byte[] memory;
  private DTCInfo dtcInfo;

  public DTC(int idx,DTCInfo dtcInfo,byte[] memory) {
    this.idx = idx;
    this.memory = memory;
    this.dtcInfo = dtcInfo;
  }

  String getbaseCode() {
    return DTCInfo.getStr(memory[dtcInfo.codeAddr+2*idx],memory[dtcInfo.codeAddr+2*idx+1]);
  }

  String getCode(int n) {
    return DTCInfo.getStr(memory[dtcInfo.faultPathAddr+8*idx+2*n],memory[dtcInfo.faultPathAddr+8*idx+2*n+1]);
  }

  int getDTCClass() {
    return memory[dtcInfo.classAddr+idx];
  }

  void setDTCClass(int c) {
    Dump.setMemory(memory,dtcInfo.classAddr+idx,c);
  }

  int getEnv(int n) {
    return (int)memory[dtcInfo.envAddr+5*idx+n] & 0xFF;
  }

  void setEnv(int[] envs) {
    for(int i=0;i<envs.length;i++)
      Dump.setMemory(memory,dtcInfo.envAddr+5*idx+i,envs[i]);
  }

  boolean match(String filter) {

    if(filter==null)
      return true;

    if(filter.isEmpty())
      return true;

    return getbaseCode().contains(filter) ||
           getCode(0).contains(filter) ||
           getCode(1).contains(filter) ||
           getCode(2).contains(filter) ||
           getCode(3).contains(filter);

  }

}
