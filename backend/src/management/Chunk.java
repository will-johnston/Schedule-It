package management;

public class Chunk {
    int size;
    byte[] data;
    boolean succeeded;
    int recievedChecksum;
    int actualChecksum;
    int id;
    public Chunk(byte[] data, int checksum, int chunkid) {
        this.data = data;
        this.size = data.length;
        this.recievedChecksum = checksum;
        actualChecksum = checksum();
        id = chunkid;
        System.out.println(String.format("checksum: %d, actual: %d", recievedChecksum, actualChecksum));
        succeeded = true;
        System.out.println("Recieved checksum: " + recievedChecksum + ", actual checksum: "  + actualChecksum);
        /*if (actualChecksum == recievedChecksum) {
            succeeded = true;
        }
        else {
            succeeded = false;
        }*/
    }
    private int checksum() {
        int MODULUS = 65535;
        int sum = 0;
        for (int i = 0; i < size; i++) {
            sum = makeUint(data[i]) % MODULUS;
        }
        return sum;
    }
    public int getId() {
        return id;
    }
    public int getSize() {
        return size;
    }
    private int makeUint(int num) {
        int add = 256;
        if (num < 0) {
            //System.out.print((byte)(add + num));
            return (add + num);
        }
        else {
            return num;
        }
    }
}
