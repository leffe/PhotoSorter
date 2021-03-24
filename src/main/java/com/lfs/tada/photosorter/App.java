package com.lfs.tada.photosorter;

import java.io.File;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.drew.imaging.ImageProcessingException;

public class App 
{
    public static void main( String[] args ) throws ImageProcessingException, IOException, NoSuchAlgorithmException, ClassNotFoundException, SQLException, ParseException
    {
        Option optRestruct = new Option("r", "restruct", false, "Restruct directory");
        Option optUDB = new Option("udb", "update-db", false, "Update database");
        Option optOpen = new Option("o", "open", false, "Open");
        Option optDump = new Option("dmp", "dump-all", false, "Dump all info");
        Option optPath = new Option("p", "path", true, "File path");
        Option optDirPath = new Option("dir", true, "Directory path");

        Options options = new Options();
        options.addOption(optRestruct);
        options.addOption(optUDB);        
        options.addOption(optOpen);
        options.addOption(optDump);
        options.addOption(optPath);
        options.addOption(optDirPath);
        
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse( options, args, true);
        
 
    	if (cmd.hasOption(optRestruct.getOpt())) {
        	System.out.print("Restruct directory: ");
        	if (cmd.hasOption(optDirPath.getOpt())) {
        		System.out.println(cmd.getOptionValue(optDirPath.getOpt()));

        		Path p = Paths.get(cmd.getOptionValue(optDirPath.getOpt())); 
        		if (Files.exists(p)) {
        			if (p.toFile().isDirectory()) {
        		    	Restruct restruct = new Restruct();
        				restruct.restructDir(p);
        			}
        		} else {
        			System.out.println("No such file....");
        		}
        	} else {
        		System.out.println("missing argument: " + optDirPath.getOpt());
        		return;
        	}
        	return;
        }
        
    	if (cmd.hasOption(optUDB.getOpt())) {
        	System.out.println("Update DB: ");
        	if (cmd.hasOption(optDirPath.getOpt())) {
        		Path p = Paths.get(cmd.getOptionValue(optDirPath.getOpt())); 
        		if (Files.exists(p)) {
        			UDB uDB = new UDB();
        			if (p.toFile().isDirectory()) {
            			System.out.println("Directory: " + p);
            			uDB.go(p);
        			} else {
            			uDB.go(Paths.get(p.toFile().getParent()));
        			}
        		} else {
        			System.out.println("No such file....");
        		}
        	}
        	else {
        		System.out.println("missing argument: " + optDirPath.getOpt());
        		return;
        	}
        	return;
    	}
    	
    	
    	
    	if (cmd.hasOption(optOpen.getOpt())) {
        	System.out.print("Open: ");
        	if (cmd.hasOption(optPath.getOpt())) {
        		Path p = Paths.get(cmd.getOptionValue(optPath.getOpt())); 
        		if (Files.exists(p)) {
        			if (p.toFile().isDirectory()) {
            			System.out.println("Directory: " + cmd.getOptionValue(optPath.getOpt()));
        			} else {
            			System.out.println("File: " + cmd.getOptionValue(optPath.getOpt()));
//            			if (Desktop.isDesktopSupported()) {
//            			    Desktop desktop = Desktop.getDesktop();
//            			    for (Desktop.Action action : Desktop.Action.values()) {
//            			      System.out.println("action " + action + " supported?  " 
//            			         + desktop.isSupported(action));
//            			        }
//            			}
            			Desktop dt = Desktop.getDesktop();
            			
            			Class.forName("com.mysql.cj.jdbc.Driver");
            			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/iscanner?serverTimezone=UTC", "pic_scanner", "Scanning-8");        

            			PreparedStatement st = connection.prepareStatement(
            					"select filePath, fileName, created from test1 where created in " + 
            			        "(select created from test1 where filePath = ? and fileName = ?)"
            			);            			

            			st.setString(1, p.getParent().toString());
            			st.setString(2, p.getFileName().toString());
            			
            			ResultSet rs = st.executeQuery();
            			
            			int count = 0;
            			
            			while (rs.next() ) {
            				count++;
            				String path = rs.getString(1);
            				String name = rs.getString(2);
            				String file = path	+ "\\" + name;
            				System.out.println(file);
            				try {
            					dt.open(new File(file));
            				}
            				catch (IllegalArgumentException e) {
            					System.out.println("Do not exist: " + file);
            				}
            			}
            			if (count == 0) {
            				System.out.println("Not found in DB");
            			}
        			}
        		} else {
        			System.out.println("No such file....");
        		}
        	}
        	else {
        		System.out.println("missing argument: " + optPath.getOpt());
        		return;
        	}
        	return;
    	}
    	
    	
    	
    	
    	
      	if (cmd.hasOption(optDump.getOpt())) {
            	System.out.print("Dump all info: ");
        	if (cmd.hasOption(optDirPath.getOpt())) {
        		System.out.println(cmd.getOptionValue(optDirPath.getOpt()));

        		Path p = Paths.get(cmd.getOptionValue(optDirPath.getOpt())); 
        		if (Files.exists(p)) {
        			if (p.toFile().isDirectory()) {
        		    	Restruct restruct = new Restruct();
        				restruct.dump(p);
        			}
        		} else {
        			System.out.println("No such file....");
        		}
        	} else {
        		System.out.println("missing argument: " + optDirPath.getOpt());
        		return;
        	}
        	return;
        }
        
        
//        String sha256hex = DigestUtils.sha256Hex("dsdsd");
//		// This will load the MySQL driver, each DB has its own driver
//		Class.forName("com.mysql.cj.jdbc.Driver");
//		// Setup the connection with the DB
//		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/iscanner?" + "user=pic_scanner&password=Scanning-8");        
//        
//        PreparedStatement st = connection.prepareStatement("insert into test1 values (default, ?, ?)");
//        
//        st.setString(1, sha256hex);
//        st.setString(2, "Swert");
//        
//        st.execute();
//        
//        System.out.println(sha256hex);
//        
//        File jpegFile = new File("LFS_7377.NEF");
////        listMeta(jpegFile);
//        
//
////        final File folder = new File("e:\\Foto\\Bulk\\2016");
////        App.listFilesForFolder(folder);    
//        
//        final File folder = new File("e:\\Foto\\Raw\\2016");
//        App.listFilesForFolder(folder);    
    }



    public static  void listFilesForFolder(final File folder) throws ImageProcessingException, IOException, NoSuchAlgorithmException {
    	MessageDigest digest = MessageDigest.getInstance("SHA-256");

        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                System.out.println(fileEntry.getName());
                
                if (fileEntry.getName().toLowerCase().endsWith(".nef")) {
                	byte[] encodedhash = digest.digest(Files.readAllBytes(Paths.get(fileEntry.getPath())));
                	System.out.println(bytesToHex(encodedhash));
                	
//                	restruct.listMeta(fileEntry);
                }
                
            }
            System.out.println();
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
        String hex = Integer.toHexString(0xff & hash[i]);
        if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
    
    
    
    
}
