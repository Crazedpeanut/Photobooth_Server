import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * Created by John on 6/06/2015.
 */
public class FilesSerializable implements Serializable
{
    static final long serialVersionUID = 42L;
    final static int INTEGER_SIZE = 4;

    String[] fileNames;
    byte[][] fileBytes;

    File[] files;


    public FilesSerializable(String[] fileNames, byte[][] fileBytes)
    {
        this.fileNames = fileNames;
        this.fileBytes = fileBytes;
    }

    public FilesSerializable(File[] files) {
        this.files = files;
    }

    public FilesSerializable(ByteBuffer byteBuffer)
    {
        int numFiles;
        int fileNameSizes[];
        int fileSizes[];

        numFiles = byteBuffer.getInt();

        fileNameSizes = new int[numFiles];
        fileSizes = new int[numFiles];

        for(int i = 0; i < numFiles; i++) //Get the sizes of all the file names in byte buffer
        {
            fileNameSizes[i] = byteBuffer.getInt();
        }

        for(int i = 0; i < numFiles; i++) //Get the sizes of all the files in byte buffer
        {
            fileSizes[i] = byteBuffer.getInt();
        }

        for(int i = 0; i < numFiles; i++)//Extract the file names from byte buffer
        {
            byte[] fileNameBuffer = new byte[fileNameSizes[i]];
            byteBuffer.get(fileNameBuffer);

            fileNames[i] = fileNameBuffer.toString();
        }

        for(int i = 0; i < numFiles; i++)//Extract the file data from byte buffer
        {
            byte[] fileBuffer = new byte[fileSizes[i]];
            byteBuffer.get(fileBuffer);

            fileBytes[i] = fileBuffer;
        }


    }

    public byte[] toByteArray()
    {
        byte[][] fileNames;
        byte[][] fileBytes;
        int[] fileSizes;
        ByteBuffer buffer;
        int[] fileNameSizes;
        int numFiles;
        int bufferSize = 0;

        FileInputStream fileInputStream;
        BufferedInputStream bufferedInputStream;

        numFiles = files.length;
        fileNames = new byte[files.length][];
        fileBytes = new byte[files.length][];
        fileSizes = new int[files.length];
        fileNameSizes = new int[files.length];

        bufferSize += INTEGER_SIZE; //For the total bytes required at the beginning of message
        bufferSize += INTEGER_SIZE; //For the number of files to send

        System.out.println("Preparing to buffer files");

        try
        {
            for (int i = 0; i < files.length; i++) {
                byte[] fileDataBuffer = new byte[(int) files[i].length()];

                fileNames[i] = files[i].getName().getBytes();
                bufferSize += fileNames[i].length;

                fileNameSizes[i] = fileNames[i].length;
                bufferSize += INTEGER_SIZE;

                fileSizes[i] = (int)files[i].length();
                bufferSize += INTEGER_SIZE;

                fileInputStream = new FileInputStream(files[i]);
                bufferedInputStream = new BufferedInputStream(fileInputStream);

                System.out.println(String.format("adding %s to Serilizable", files[i].getPath()));

                if (files[i].exists()) {
                    bufferedInputStream.read(fileDataBuffer, 0, (int) files[i].length());
                    fileBytes[i] = fileDataBuffer;

                    bufferSize += fileBytes[i].length;
                }
            }

            //Loading up the buffer
            buffer = ByteBuffer.allocate(bufferSize);

            //Put the total size of the message at the beginning, minus the space taken for the size of the message
            buffer.putInt(bufferSize - INTEGER_SIZE);

            buffer.putInt(numFiles); //Put the number of files to send

            for(int i : fileSizes) //Put each of the file sizes onto the buffer
            {
                buffer.putInt(i);
            }

            for(int i : fileNameSizes) //Put each of the file name sizes onto the buffer
            {
                buffer.putInt(i);
            }

            for(byte[] b : fileNames) //Put each of the filenames onto the buffer
            {
                buffer.put(b);
            }

            for(byte[] b : fileBytes)//Put each of the file byte arrays onto the buffer
            {
                buffer.put(b);
            }

            return buffer.array();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
