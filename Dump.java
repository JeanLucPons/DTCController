package DTCController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Class to read, write and find DTC info from a dump
 */

public class Dump {

    public final String ERROR_STR = "Cannot find DTC Table, please contribute to this project to add new platform.";

    public static final int DUMP_SIZE = 2048*1024; // 2MBytes
    public static final int MAP_ADD = 0x1C0000;
    public static final int MAP_SIZE = 0x3DF78;
    public static final int SWT_REF = MAP_ADD + 0x10;
    public static final int A2L_REF = MAP_ADD + 0x1A;
    public static final int MAX_DTC = 1024;
    public static int CLASS_NUMBER = 30;

    byte[] memory;
    String software;
    String project;

    public Dump(String fileName) throws IOException {

        Path p = Paths.get(fileName);
        memory = Files.readAllBytes(p);

        StringBuffer sft = new StringBuffer();
        for(int i=0;i<10;i++)
            sft.append((char)memory[SWT_REF+i]);
        software = sft.toString();

        StringBuffer a2l = new StringBuffer();
        for(int i=0;i<8;i++)
            a2l.append((char)memory[A2L_REF+i]);
        project = a2l.toString();

        System.out.println(fileName + ":" + software + "/" + project);

    }

    public void write(String fileName) throws IOException {
        Path p = Paths.get(fileName);
        Files.write(p,memory);
        System.out.println("Write: " + fileName);
    }

    public static void setMemory(byte[] memory,int addr,int b) {

        int old = ((int)memory[addr]) & 0xFF;
        if( old!=b ) {
            System.out.println(String.format("Dump.setMemory(): 0x%06X %d => %d",addr,old,b));
        }
        memory[addr] = (byte)b;

    }

    public DTCInfo findDTCInfo() throws IOException {

        DTCInfo ret = new DTCInfo();

        // Known software
        // Please add know project there
        if (project.equals("C35374A_")) {

            // 206 1.6 HDI 110cv FAP
            ret.nbDTC = 195;             // Number of DTC
            ret.classAddr = 0x1C6734;    // Addr of DSM_ClaDfp_ACCDPresAna_C
            ret.defClassAddr = 0x1C5CCC; // Addr of DSM_Class1Mil_C

        } else if (project.startsWith("C")) {

            // PSA strategy, we assume that:
            // First code of the codeTable is P0530
            // This code corresponds to DSM_CDKDfp_ACCDPresAna_C (AirConditioningComponentDriver Pressure default)
            // Last code of the codeTable is P1621
            // This code corresponds to DSM_CDKDfp_WdCom_C (ECU failure)
            // Characteristics are sorted by alphabetic order (case sensitive) when damos is generated

            // Try to find an occurrence of 0x1621 in reverse order
            byte[] p1621 = new byte[]{0x16,0x21};
            ret.classAddr = rSearch(MAP_ADD+MAP_SIZE-p1621.length,p1621) + 2;
            if(ret.classAddr<0)
                throw new IOException(ERROR_STR);
            byte[] p0530 = new byte[]{0x05,0x30};
            int firstCode = rSearch(ret.classAddr,p0530);
            if(firstCode<0)
                throw new IOException(ERROR_STR);

            ret.nbDTC = (ret.classAddr - firstCode) / 2;

            if(ret.nbDTC>MAX_DTC)
                throw new IOException(ERROR_STR);

            // Start of DTC class definition 1 (from orig file)
            ret.defClassAddr = search(MAP_ADD,new byte[]{1,1,0,2,3});

        } else {
            throw new IOException(ERROR_STR);
        }

        ret.codeAddr = ret.classAddr - ret.nbDTC*2;
        ret.faultPathAddr = ret.codeAddr - ret.nbDTC*8;
        ret.envAddr = ret.classAddr + ret.nbDTC;

        System.out.println(ret);

        return ret;
    }

    @Override
    public String toString() {
        return "Dump{" +
                "memory=" + memory.length +
                ", software='" + software + '\'' +
                ", project='" + project + '\'' +
                '}';
    }

    private boolean match(int add, byte[] pattern) {
        boolean equal = true;
        int i = 0;
        while(equal && i<pattern.length) {
            equal = pattern[i] == memory[add + i];
            i++;
        }
        return equal;
    }

    private int search(int startAdd,byte[] pattern) {

        int add = startAdd;
        boolean found = false;
        while(!found && add<MAP_ADD+MAP_SIZE-pattern.length) {
            found = match(add, pattern);
            if (!found) add++;
        }

        if(!found)
            return -1;
        else
            return add;

    }

    private int rSearch(int startAdd,byte[] pattern) {

        int add = startAdd;
        boolean found = false;
        while(!found && add>=MAP_ADD) {
            found = match(add, pattern);
            if (!found) add--;
        }

        if(!found)
            return -1;
        else
            return add;

    }


}
