package lhg.common.utils;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class BufferReader {
    byte[] buf;
    int offset;

    public BufferReader(byte[] buf, int offset) {
        this.buf = buf;
        this.offset = offset;
    }

    public void skip(int len) {
        offset += len;
    }

    public String nextStr(int len) {
        try {
            return new String(buf, offset, len, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        } finally {
            offset += len;
        }
    }

    public String nextHex(int len) {
        String ret = Utils.bytesToHEX(buf, offset, len);
        offset += len;
        return ret;
    }

    public byte nextByte() {
        return buf[offset++];
    }

    public int nextInt() {
        return 0xff & nextByte();
    }

    public byte[] nextBuf(int len) {
        byte[]ret = Arrays.copyOfRange(buf, offset, offset + len);
        offset += len;
        return ret;
    }


    public int nextIntHL4(){
        // {*((char *)&outdword+LONG_HIGH3)=*((UBYTE *)inbuf+0);*((char *)&outdword+LONG_HIGH2)=*((UBYTE *)(inbuf)+1);*((char *)&outdword+LONG_HIGH1)=*((UBYTE *)(inbuf)+2);*((char *)&outdword+LONG_HIGH0)=*((UBYTE *)(inbuf)+3);}
        return ((0xff & buf[offset++]) << 24) +
                ((0xff & buf[offset++]) << 16) +
                ((0xff & buf[offset++]) << 8) +
                ((0xff & buf[offset++]) << 0)
                ;
    }

}
