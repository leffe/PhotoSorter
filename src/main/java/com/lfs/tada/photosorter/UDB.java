package com.lfs.tada.photosorter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.drew.imaging.ImageProcessingException;
import com.lfs.tada.photosorter.InfoBuilder.Info;

public class UDB {

	public void go(Path p) throws IOException, SQLException, ClassNotFoundException {
		System.out.println("UDB: " + p.toString());
		
		
		
		
		// This will load the MySQL driver, each DB has its own driver
		Class.forName("com.mysql.cj.jdbc.Driver");
		// Setup the connection with the DB
//		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/iscanner?" + "user=pic_scanner&password=Scanning-8");        
		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/iscanner?serverTimezone=UTC", "pic_scanner", "Scanning-8");        
//        
//        PreparedStatement st = connection.prepareStatement("insert into test1 values (default, ?, ?)");
//        
//        st.setString(1, sha256hex);
//        st.setString(2, "Swert");
//        
//        st.execute();
//        
//        PreparedStatement st = connection.prepareStatement("insert into test1 values (default, ?, ?, ?)");

		PreparedStatement st = connection.prepareStatement(
				"insert into test1 (hash, filePath, fileName, created) " +
				"select * from (select ? as hash_, ? as path_, ? as file_, ? as created_) as tmp " +
				"where not exists (select filePath, fileName from test1 where filepath = path_ and fileName = file_) limit 1"
		);
		
		Files.walk(p)
//			.filter(f -> f.toFile().isDirectory() && !Util.isSourceDir(f.getFileName().toString()))
			.forEach(f -> {
				
				
				if (f.toFile().isDirectory()) {
				
				
					System.out.println("Dir:" + f.getFileName().toString());
				}
				else {
					
					File file = f.toFile();

					Info info = null;
					try {
						info = Restruct.getExifInfo(file);
					} catch (ImageProcessingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					
					Date date = info.getCreationDate();
					
					

					System.out.println(date.getTimezoneOffset());
					System.out.println(date);
					System.out.println(date.getTime());
					
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
					df.setTimeZone(TimeZone.getTimeZone("GMT"));
					System.out.println("Current date and time in GMT: " + df.format(date));
					
					
					
					Path pp = file.toPath();
					
					System.out.println("File:" + f.getFileName().toString());
					try {
						System.out.println("Parent: " + pp.getParent().toString());

						st.setString(1, "abc123");
						st.setString(2, pp.getParent().toString());
//						st.setString(2, f.getFileName().getParent().toString());
						st.setString(3, f.getFileName().toString());
						st.setDouble(4, date.getTime());
						System.out.println(st.execute());
//						ResultSet rs = st.getResultSet();
//						System.out.println(rs.first());
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();}
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					}
				
				
//				try {
//					scanFiles(f);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
		});
		
		
		connection.close();
	}
	
	
	

}
