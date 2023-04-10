package com.chatapp.server.Persistence;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.List;

import com.chatapp.server.BusinessLogicServer;

/*
 * This class is used to serialize and deserialize objects
 */
public class SerializerUtil {

    /*
     * This enum is used to determine which file to read/write to
     */
    public enum TextType {
        ACCOUNT, MESSAGE;

        @Override
        public String toString() {
            switch(this) {
                case ACCOUNT: return "Account";
                case MESSAGE: return "Message";
                default: throw new IllegalArgumentException();
            }
        }

        public String toFile() {
            switch(this) {
                case ACCOUNT: return Constants.ACCOUNT_FILE;
                case MESSAGE: return Constants.MESSAGE_FILE;
                default: throw new IllegalArgumentException();
            }
        }

        public String toBackupFile() {
            switch(this) {
                case ACCOUNT: return Constants.ACCOUNT_BACKUP_FILE;
                case MESSAGE: return Constants.MESSAGE_BACKUP_FILE;
                default: throw new IllegalArgumentException();
            }
        }
    }

/* ============================== FILE FUNCTIONS ============================== */

    /*
     * Reads account or message files and returns a list of strings
     * @param r the type of file to read
     * @return a list of strings containing the lines of the file
     */
    public static List<String> read(TextType r) {
        List<String> lines = null;

        String mainFile = BusinessLogicServer.getReplicaFolder() + r.toFile();
        String backupFile = BusinessLogicServer.getReplicaFolder() + r.toBackupFile();

        // Attempt to read from file
        try {
            lines = Files.readAllLines(Paths.get(mainFile));
        } catch (IOException e) {
            System.out.println("INFO: " + r + " file is empty");
        }

        // If the file is not corrupted, copy it to the backup file
        if (lines != null && lines.size() > 0) {
          try {
            FileInputStream src = new FileInputStream(mainFile);
            FileOutputStream dest = new FileOutputStream(backupFile);
            dest.getChannel().transferFrom(src.getChannel(), 0, src.getChannel().size());
            src.close();
            dest.close();
          } catch (IOException e) {
            System.out.println("WARNING: Could not copy " + r + " file to backup file");
          }
        }

        // If the file was corrupted, try to read from the backup file
        else {
          // Attempt to read from the backup file
          if (lines == null || lines.size() == 0) {
            try {
              lines = Files.readAllLines(Paths.get(backupFile));
            } catch (IOException e) {
              System.out.println("INFO: Backup file is empty");
            }
          }

          // If the backup file is not corrupted, copy it to the file
          if (lines != null && lines.size() > 0) {
            try {
              FileInputStream src = new FileInputStream(backupFile);
              FileOutputStream dest = new FileOutputStream(mainFile);
              dest.getChannel().transferFrom(src.getChannel(), 0, src.getChannel().size());
              src.close();
              dest.close();
            } catch (IOException e) {
              System.out.println("WARNING: Could not copy backup file to " + r + " file");
            }
          }
        }
        
        // If the file and backup file are corrupted or empty, return an empty list
        if(lines == null) {
            return new ArrayList<String>();
        }
        return lines;
    }
    
    /*
     * Write to both the main file and the backup file
     * one at a time in case the server crashes
     * @param r the type of file to write to
     * @param args the arguments to write to the file
     */
    public static void write(TextType r, String[] args) {
        String text = marshallArguments(args);

        // Write to both files, one at a time
        write(text, BusinessLogicServer.getReplicaFolder() + r.toFile());
        write(text, BusinessLogicServer.getReplicaFolder() + r.toBackupFile());
    }

    /*
     * Write the desired text to the specified file
     * @param marshalledText the text to write to the file
     * @param filename the filename to write to
     */
    private static void write(String marshalledText, String filename) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(filename, true)));
        } catch (IOException e) {
            // Failed to open file
            System.out.println("ERROR: Failed to open file");
            e.printStackTrace();
        }
        
        out.print(marshalledText);
        out.close();
    }
    
    /*
     * Delete the contents of the specified file
     * @param filename the filename to clear
     */
    public static void clear(String filename) {
        // Clear the file
        try {
            PrintWriter writer = new PrintWriter(filename);
            writer.print("");
            writer.close();
        } catch (IOException e) {
            System.out.println("WARNING: Could not clear file " + filename);
        }
    }

    /*
     * Copy the contents of a file to another file
     * @param src the source file
     * @param dest the destination file
     */
    public static void copy(String src, String dest) {
        try {
            FileInputStream srcFile = new FileInputStream(src);
            FileOutputStream destFile = new FileOutputStream(dest);
            destFile.getChannel().transferFrom(srcFile.getChannel(), 0, srcFile.getChannel().size());
            srcFile.close();
            destFile.close();
        } catch (IOException e) {
            System.out.println("WARNING: Could not copy " + src + " to " + dest);
        }
    }

/* ============================== TEXT ESCAPING ============================== */

    /*
     * Marshalls arguments by escaping special characters and then concatenating 
     * them with the argument separator
     * @param arguments the arguments to marshall
     * @return the marshalled arguments
     */
    private static String marshallArguments(String[] arguments) {
        // parse arguments into a StringBuilder, perform escaping, then convert back to String
        StringBuilder output = new StringBuilder();
        boolean first = true;
        for (String argument : arguments) {
          // Only add argument separators when it isn't the first argument
          if (!first) {
            output.append(Constants.ARGUMENT_SEPARATOR);
          }
          else {
            first = false;
          }
          for (char c : escapeRestrictedCharacters(argument).toCharArray()) {
            output.append(c);
          }
        }
        // Add message separator character
        output.append(Constants.MESSAGE_SEPARATOR);
  
        return output.toString();
    }

    /* 
     * Unmarshalls arguments by separating them by the the argument separator and 
     * unescaping special characters
     * @param marshalledMessage the marshalled message to unmarshall
     * @return the unmarshalled arguments
    */
    public static ArrayList<String> unmarshallArguments(String marshalledMessage) {
        ArrayList<String> result = new ArrayList<String>();

        // split the arguments by the argument separator and then unescape them
        String[] args = marshalledMessage.split(String.valueOf(Constants.ARGUMENT_SEPARATOR));
        for (String arg : args) {
            result.add(unescapeRestrictedCharacters(arg));
        }
        
        return result;
    }

    /*
     * Escapes restricted characters by adding the escape character in front of them. 
     * Ex: \t becomes \\t and \n becomes \\n
     * @param input the byte array to escape
     * @return the escaped byte array
     */
    private static String escapeRestrictedCharacters(String input) {
        // parse input into an StringBuilder, perform escaping, then convert back to String
        StringBuilder output = new StringBuilder();
  
        for (char c : input.toCharArray()) {
          // "\t" --> "\\t"
          if (c == Constants.ARGUMENT_SEPARATOR) {
            output.append(Constants.ESCAPE_CHARACTER);
            output.append(Constants.ARGUMENT_SEPARATOR_LETTER);
          }
          // "\n" --> "\\n"
          else if (c == Constants.MESSAGE_SEPARATOR) {
            output.append(Constants.ESCAPE_CHARACTER);
            output.append(Constants.MESSAGE_SEPARATOR_LETTER);
          }
          else {
            output.append(c);
          }
        }
  
        return output.toString();
      }

    /*
     * Unescapes restricted characters by removing the escape character in front of them. 
     * Ex: \\t becomes \t and \\n becomes \n
     * @param input the byte array to unescape
     * @return the unescaped byte array
     */
    private static String unescapeRestrictedCharacters(String inputString) {
        // parse input into a StringBuilder, perform unescaping, then convert back to String
        StringBuilder output = new StringBuilder();

        char[] input = inputString.toCharArray();

        for (int i = 0; i < input.length; i++) {
            // "\\t" -> "\t"
            if (i != input.length - 1 && input[i] == Constants.ESCAPE_CHARACTER
            && input[i + 1] == Constants.ARGUMENT_SEPARATOR_LETTER) {
            output.append(Constants.ARGUMENT_SEPARATOR);
            i++;
            }
            // "\\n" -> "\n"
            else if (i != input.length - 1 && input[i] == Constants.ESCAPE_CHARACTER
            && input[i + 1] == Constants.MESSAGE_SEPARATOR_LETTER) {
            output.append(Constants.MESSAGE_SEPARATOR);
            i++;
            }
            else
            output.append(input[i]);
        }
        
        return output.toString();
    }

}
