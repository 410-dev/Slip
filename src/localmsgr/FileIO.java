package localmsgr;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class FileIO {
    public static String convertFileToString(String filePath) {
		try {
			File file = new File(filePath);
			long megabytes = (file.length() / 1024 / 1024);

            if (megabytes > 5) {
            	SystemLogger.error("Unable to attach the file: File exceeds 5 MB.", true, SystemLogger.CONTINUE, null);
                return null;
            }


			InputStream is = new FileInputStream(filePath);

			ArrayList<Integer> bytesRead = new ArrayList<>();

			int byteRead = -1;
			while((byteRead = is.read()) != -1) {
				bytesRead.add(byteRead);
			}

			is.close();

			String toSave = bytesRead.toString().replaceAll(" ", "");
			toSave = toSave.substring(1, toSave.length() - 1);

            return toSave;
		}catch (Exception e) {
			SystemLogger.error("Unable to attach file: " + e.getMessage(), true, SystemLogger.CONTINUE, e);
            return null;
		}
	}

	public static void restoreFileFromString(String content, String saveFilePath) {
		try {
			String[] bytesString = content.split(",");
			int[] bytes = new int[bytesString.length];

			int index = 0;
			for(String s2 : bytesString) {
				bytes[index] = Integer.parseInt(s2);
				index++;
			}

			OutputStream os = new FileOutputStream(saveFilePath);
			index = 0;
			for(int i : bytes) {
				os.write(i);
				index++;
			}

			os.close();
		}catch (Exception e) {
			SystemLogger.error("Unable to save file: " + e.getMessage(), true, SystemLogger.CONTINUE, e);
		}
	}
}
