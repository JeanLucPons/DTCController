package DTCController;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class RootResult {
    BigInteger y;
    boolean isPerfectRoot;
}

public class ChecksumHelper {

    // RSA modulus, public exponent is 3
    // This key is stored in the full dump
    // All PSA EDC16C34 seems to share the same public key
    static BigInteger N = new BigInteger(
                "9f63259b570099e4435112058526405d"+
                    "edd701b8b51d42d80b46c71839171620"+
                    "fe91c5d46aaf2ccf8921b6eb56c62f46"+
                    "4f2930d1b0970bb77c686a30123400ef"+
                    "b406bff133151cc4100b4d715c038f0f"+
                    "50aa247e5b710e0a8893de467077d169"+
                    "db2c496d7205c3c44a6b3b1e37bfa03b"+
                    "35d9a44f1879f76523ffde1a0e501151",
            16
    );

    static public RootResult nthroot(BigInteger x,int nth) {

        RootResult r = new RootResult();

        // Finds the integer component of the n'th root of x,
        // or an integer such that y^n < x < (y + 1)^n

        BigInteger high = new BigInteger("1");
        while(high.pow(nth).compareTo(x)<=0) {
            high = high.shiftLeft(1);
        }
        BigInteger low = new BigInteger(String.valueOf(high));
        low = low.shiftRight(1);
        boolean eof = false;
        while(!eof) {
            BigInteger mid = low.add(high);
            mid = mid.shiftRight(1);
            if(mid.pow(nth).compareTo(x)<0) {
                low = mid;
            } else if (mid.pow(nth).compareTo(x)>0) {
                high = mid;
            } else {
                // Perfect root
                r.y = mid;
                r.isPerfectRoot = true;
                return r;
            }
            BigInteger diff = high.subtract(low);
            eof = diff.compareTo(BigInteger.ONE)<=0;
        }
        // Not a perfect root
        r.y = low;
        r.isPerfectRoot = false;
        return r;

    }

    public static BigInteger RSARootAttack1024(BigInteger sig,BigInteger n,int nth,int freeBit) {

        // Sig has his message in the first (1024 - freeBit) bits. Other bits are unused.
        // Try to find a RSA signature x such that pow(x,nth) = sig + offset(freeBit) + (lambda).n

        RootResult rr = null;
        boolean eos = false;
        while( !eos ) {
            rr = nthroot(sig, nth);
            eos = rr.isPerfectRoot;
            if(!eos) {
                BigInteger offset = rr.y.add(BigInteger.ONE).pow(nth).subtract(sig);
                if(offset.bitLength()<freeBit)
                    // done
                    sig = sig.add(offset);
                else
                    // one more lambda
                    sig = sig.add(n);
            }
        }

        return rr.y.mod(n);

    }


    public static BigInteger getBI(byte[] dump,int address,int size) {

        // Extract BigInteger from the dump
        StringBuffer buff = new StringBuffer();
        for(int i=0;i<size;i++)
            buff.append(String.format("%02X",dump[address+i]));
        return new BigInteger(buff.toString(),16);

    }


    public static byte[] getBuffer(BigInteger a,int size) {
        byte[] ret = new byte[size];
        byte[] buff = a.toByteArray();
        int h = ret.length - buff.length;
        int o = 0;
        int l = buff.length;
        if(h<0) {
            o = -h;
            h = 0;
            l = size;
        }
        System.arraycopy(buff,o,ret,h,l);
        return ret;
    }

    public static String getASCII(byte[] array) {

        StringBuffer buff = new StringBuffer();

        buff.append(". ");
        for(int i=0;i<array.length;i++ ) {
            byte b = array[i];
            if(b>=32 && b<=127)
                buff.append((char)b + " ");
            else
                buff.append(". ");
        }
        return buff.toString();

    }

    public static String getHex(byte[] array) {
        return getHex(array,true);
    }

    public static String getHex(byte[] array,boolean insertSpace) {
        StringBuffer buff = new StringBuffer();
        for(int i=0;i<array.length;i++) {
            if(insertSpace && i%4==0 && i!=0) buff.append(" ");
            buff.append(String.format("%02x", array[i]));
        }
        return buff.toString();
    }

    public static long getU32(byte[] dump, int addr, boolean littleEndian) {

        long b0 = ((long)dump[addr+0]) & 0xFF;
        long b1 = ((long)dump[addr+1]) & 0xFF;
        long b2 = ((long)dump[addr+2]) & 0xFF;
        long b3 = ((long)dump[addr+3]) & 0xFF;

        if(littleEndian) {
            return (b3<<24) + (b2<<16) + (b1<<8) + b0;
        } else {
            return (b0 << 24) + (b1 << 16) + (b2 << 8) + b3;
        }

    }

    public static long SUM32(byte[] dump,int start,int end) {

        long sum = 0;
        for(int i=start;i<end;i+=4)
            sum += getU32(dump,i,false);
        return sum;

    }

    public static long computeCS1(byte[] dump,long offset) {

        // Compute MAP checksum
        // All orig file using RSA has an offset of 0 and
        // This CS might not be checked by the ECU
        long sum = 0xA03FCA00L - SUM32(dump,0x1c0000,0x1FDF78) + offset;
        return sum&0xFFFFFFFFL;

    }

    public static long computeCS2(byte[] dump,long offset) {

        // Compute signature checksum
        // All orig file using RSA has an offset of 0
        // This CS might not be checked by the ECU
        long sum = 0x2FE01B00L - SUM32(dump, 0x1FDF7C,0x1FDFFC) + offset;
        return sum&0xFFFFFFFFL;

    }

    public static byte[] extractMD5(byte[] sig) {

        // Extract MD5 from the signature (must be 128 byte array)
        byte[] retMD5 = new byte[16];
        System.arraycopy(sig,11,retMD5,0,16);
        return retMD5;

    }

    public static byte[] computeMD5(byte[] dump) {

        // Compute the MD5 of the MAP area
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(dump, 0x1c0000, 0x3DF7C);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Failed to get MD5: " + e.getMessage());
        }

        return null;

    }

    public static boolean checkSig(byte[] sig) {

        // Check RSA signature prefix
        return
                (sig[0] ==  0) && (sig[1] ==  1) && (sig[2] == -1) && (sig[3] == -1) &&
                (sig[4] == -1) && (sig[5] == -1) && (sig[6] == -1) && (sig[7] == -1) &&
                (sig[8] == -1) && (sig[9] == -1) && (sig[10] == 0);

    }

    public static String getCSinfos(byte[] dump) {

        StringBuffer ret = new StringBuffer();

        ret.append("RSA signature:\n");
        BigInteger a = getBI(dump,0x1FDF7C ,128);
        BigInteger sig = a.pow(3).mod(N);
        byte[] sigArr = getBuffer(sig,128);
        for(int row=0;row<8;row++) {
            for(int i=0;i<16;i++)
                ret.append(String.format("%02X " , (int)sigArr[row*16+i] & 0xFF) );
            ret.append("  ");
            for(int i=0;i<16;i++) {
                if (sigArr[row * 16 + i] > 32 && sigArr[row * 16 + i] <= 127)
                    ret.append((char) sigArr[row * 16 + i]);
                else
                    ret.append('.');
            }
            ret.append("\n");
        }

        ret.append("\nChecksums :\n");

        String mcs32 = String.format("%08X",getU32(dump,0x1FDF78,false));
        String calcmcs32 = String.format("%08X",computeCS1(dump,0));
        String md5 = getHex(extractMD5(sigArr),false);
        String caldMD5 = getHex(computeMD5(dump),false);
        String scs32 = String.format("%08X",getU32(dump,0x1FDFFC,false));
        String calcscs32 = String.format("%08X",computeCS2(dump,0));

        ret.append( "MCS32:" + mcs32 + " (" + calcmcs32 + ")\n");
        ret.append( "MD5  :" + md5 + "\n     (" + caldMD5 + ")\n");
        ret.append( "SCS32:" + scs32 + " (" + calcscs32 + ")\n");

        return ret.toString();

    }

    public static void correct(byte[] dump) throws IOException {

        // Check that the DUMP is using RSA
        BigInteger a = getBI(dump,0x1FDF7C ,128);
        BigInteger sig = a.pow(3).mod(N);
        byte[] sigArr = getBuffer(sig,128);
        if(!checkSig(sigArr))
            throw new IOException("Cannot correct checksum, your dump is not using RSA or has been modified");

        // Correct the CSs

        // MAP CS
        long newCS1 = computeCS1(dump,0);
        dump[0x1FDF78] = (byte)( newCS1>>24 );
        dump[0x1FDF79] = (byte)( newCS1>>16 );
        dump[0x1FDF7A] = (byte)( newCS1>>8 );
        dump[0x1FDF7B] = (byte)( newCS1 );

        // Compute new RSA signature
        BigInteger newSig = new BigInteger("0001ffffffffffffffff00" +
                getHex(computeMD5(dump),false),16 );

        int freeBit = (128-(11/*prefix length*/+16/*MD5 length*/))*8;
        newSig = newSig.shiftLeft(freeBit);

        // Copy the new RSA signature into the dump
        BigInteger root = RSARootAttack1024(newSig,N,3,freeBit);
        byte[] rootArr = getBuffer(root,128);
        System.arraycopy(rootArr,0,dump,0x1FDF7C,128);

        // RSA signature CS
        long newCS2 = computeCS2(dump,0);
        dump[0x1FDFFC] = (byte)( newCS2>>24 );
        dump[0x1FDFFD] = (byte)( newCS2>>16 );
        dump[0x1FDFFE] = (byte)( newCS2>>8 );
        dump[0x1FDFFF] = (byte)( newCS2 );

    }

}
