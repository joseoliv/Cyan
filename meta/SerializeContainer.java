package meta;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Paths;

public class SerializeContainer implements Serializable {

	public SerializeContainer(Object value) {
		super();
		this.value = value;
	}

	public Object value;
	private static final long serialVersionUID = 2672519035796868753L;

	public byte []saveToByteArray() {
		try(java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
				java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(bos)) {
	        // Method for serialization of object
	        out.writeObject(this);
	        out.flush();
	        return bos.toByteArray();
		}
		catch (IOException e) {
		}
		return null;
	}

	public void loadFromByteArray(byte []byteArray) {
		try (   java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(byteArray);
				java.io.ObjectInputStream in = new java.io.ObjectInputStream(bis) ) {
			 this.value = ((SerializeContainer) in.readObject()).value;

		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}


	public void save(String filename) {
		try(java.io.FileOutputStream file = new java.io.FileOutputStream(filename);
				java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(file)) {
	        // Method for serialization of object


			final java.nio.file.Path path = Paths.get(filename);

		    try {
		    	final java.nio.file.Path parentDir = path.getParent();
		    	if (!java.nio.file.Files.exists(parentDir))
		    		java.nio.file.Files.createDirectories(parentDir);
			}
			catch (final IOException e) {
			}


	        out.writeObject(this);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void load(String filename) {
		try (   java.io.FileInputStream file = new java.io.FileInputStream(filename);
				java.io.ObjectInputStream in = new java.io.ObjectInputStream(file) ) {
			 this.value = ((SerializeContainer) in.readObject()).value;

		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
