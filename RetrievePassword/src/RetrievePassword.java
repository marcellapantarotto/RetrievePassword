import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.Provider.Service;
import java.util.Random;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;

public class RetrievePassword {

	public static void main(String[] args) {
		if (args.length < 1) {
			help();
		}
		try {
			switch (args[0]) {
			case "--list":
				list(args);
				break;
			case "--generate-key":
				generateKey(args);
			case "--generate-password":
				generatePassword(args);
				break;
			case "--retrieve-password":
				retrievePassword(args);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void generateKey(String args[]) throws Exception {
		KeyGenerator kg = KeyGenerator.getInstance("AES");
		SecretKey key = kg.generateKey();
		ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(new File(args[1])));
		os.writeObject(key);
		os.flush();
		os.close();
	}

	private static void generatePassword(String args[]) throws Exception {
		byte[] password = new byte[16];
		new Random().nextBytes(password);

		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(args[1])));

		SecretKey key = (SecretKey) ois.readObject();

		Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] res = cipher.doFinal(password);

		System.out.println("Plain text password: \t" + DatatypeConverter.printHexBinary(password));
		System.out.println("Ciphered password: \t" + DatatypeConverter.printHexBinary(res));

		ois.close();
	}

	private static void retrievePassword(String args[])
			throws IllegalBlockSizeException, BadPaddingException, FileNotFoundException, IOException,
			ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {

		byte[] cipheredPassword = DatatypeConverter.parseHexBinary(args[1]);

		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(args[2])));
		SecretKey key = (SecretKey) ois.readObject();

		Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
		cipher.init(Cipher.DECRYPT_MODE, key);

		byte[] res = cipher.doFinal(cipheredPassword);

		System.out.println("Ciphered Password:\t" + DatatypeConverter.printHexBinary(cipheredPassword));
		System.out.println("Plain Text Password:\t" + DatatypeConverter.printHexBinary(res));

		ois.close();
	}

	private static void list(String args[]) {
		if (args.length == 1) {
			Provider[] providers = Security.getProviders();
			for (Provider p : providers) {
				System.out.println(String.format("%s: %s", p.getName(), p.getInfo()));
			}
		} else if (args.length == 3) {
			String providerName = args[1];
			String primitiveName = args[2];

			Provider p = Security.getProvider(providerName);

			System.out.println("List of available algorithms for " + primitiveName);

			assert p != null;
			assert p.getServices() != null;

			for (Service service : p.getServices()) {
				if (service.getType().equals(primitiveName)) {
					System.out.println(service.getAlgorithm());
				}
			}
		} else {
			help();
		}
	}

	private static void help() {
		System.out.println("Try the following commands");
		System.out.println(" --list    : list the name of the installed providers");
		System.out.println(
				" --list <provider> <algorithm>  : list the implementations of an algorithm from a given provider");
		System.exit(0);
	}

}
