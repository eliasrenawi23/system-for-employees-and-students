package Entities;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;


/**
 * This class contains all the needed data about the file as represented in the database.
 * it also contains functions that allows for writing to local hard drive and to copy from hard drive and convert the data to binary.
 * 
 * @author Bshara
 * */
public class File extends SqlObject implements Serializable {

	private static File emptyInstance = new File(0, 0, null, null);
	public static File getEmptyInstance() {
		return emptyInstance;
	}
	
	public long ID, requestID;
	public String fileName, type;
	

	public File(long iD, long requestID , String fileName, String type) {
		super();
		ID = iD;
		this.requestID = requestID;
		this.fileName = fileName;
		this.type = type;
		
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	public long getID() {
		return ID;
	}

	public void setID(long iD) {
		ID = iD;
	}

	public long getRequestID() {
		return requestID;
	}

	public void setRequestID(long requestID) {
		this.requestID = requestID;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public int getPrimaryKeyIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getForeignKeyIndex() {
		return 1;
	}

	@Override
	public String getReferenceTableName() {
		return "ChangeRequest";
	}

	@Override
	public boolean hasForeignKey() {
		return true;
	}

	@Override
	public String getReferenceTableForeignKeyName() {
		// TODO Auto-generated method stub
		return "requestID";
	}

	@Override
	public int fieldsLastIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	

	private byte[] storedBytes;
	
	public void loadBytes() {
		java.io.File file= new java.io.File(fileName);
		try {
			FileInputStream inputStream= new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(inputStream);
			storedBytes = new byte[(int) file.length()];
			bis.read(storedBytes, 0, storedBytes.length);
			bis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void autoSetTypeAndNameFromPath() {
		
		setType(getFileName().substring(getFileName().lastIndexOf('.') + 1));
		setFileName(getFileName().substring(getFileName().lastIndexOf('/') + 1));
	}

	public InputStream getBinaryStream() {
		return new ByteArrayInputStream(storedBytes);
	}
	
	public long getStoredBytesSize() {
		if(storedBytes == null)
			return 0;
		return storedBytes.length;
	}
	
	public void setBytes(InputStream is, int lenght) {
		try {
			storedBytes = new byte[lenght];
			is.read(storedBytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeData(String path) {
		try {
			InputStream is = getBinaryStream();
			FileOutputStream fos = new FileOutputStream(path + getFileName());

			BufferedOutputStream bos = new BufferedOutputStream(fos);

			// Starts writing the bytes in it
			try {
				byte[] buffer = new byte[1];
				while (is.read(buffer) > 0) {
					bos.write(buffer);
				}

				bos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public String toString() {
		return "File [ID=" + ID + ", requestID=" + requestID + ", fileName=" + fileName + ", type=" + type
				+ ", storedBytes=" + storedBytes.length + "]";
	}
	
	
	
	
}
