package com.lfs.tada.photosorter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.mp4.Mp4MetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.file.FileSystemDirectory;
import com.drew.metadata.mp4.Mp4Directory;
import com.lfs.tada.photosorter.InfoBuilder.Info;
import com.lfs.tada.photosorter.InfoBuilder.Kind;

public class Restruct {
	
	
	public void restructDir(Path p) throws IOException {
		System.out.println("Restruct: " + p.toString());
		Files.walk(p)
			.filter(f -> f.toFile().isDirectory() && !Util.isSourceDir(f.getFileName().toString()))
			.forEach(f -> {
				System.out.println("Dir:" + f.getFileName().toString());
				try {
					scanFiles(f);
				} catch (IOException e) {
					e.printStackTrace();
				}
		});
	}

	private void scanFiles(Path path) throws IOException {
		Set<Pair> files = new HashSet<Pair>();

		Files.list(path)
			.filter(f -> !f.toFile().isDirectory())
			.forEach(f -> {
			try {
				File f_ = getRestructInfo(f.toFile(), path.toFile());
				if (f_ != null) {
					files.add(new Pair(f.toFile(), f_));
				}
				
			} catch (ImageProcessingException | IOException | NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		});

		for (Pair p: files) {
			System.out.println("MOVE: " + p.left().getCanonicalPath() + " TO: "+p.right().getCanonicalPath());
			Path newPath = Files.move(p.left().toPath(), p.right().toPath());
			System.out.println("Successfully moved to: " + newPath.toString());
		}
		
	}


	private File getRestructInfo(File fileToMove, File sourceDirectory)
			throws ImageProcessingException, IOException, NoSuchAlgorithmException {

		if ("picasa.ini".equals(fileToMove.getName().toLowerCase()) ||
			".picasa.ini".equals(fileToMove.getName().toLowerCase()) ||
			"zbthumbnail.info".equals(fileToMove.getName().toLowerCase())) {
			return null;
		}
		
		String extension = FileParts.getExtension(fileToMove).toLowerCase();

		//		String digest = Digest.getInstance().digest(targetFile);
//		System.out.println(digest);
		Info info = null;
		if (info == null) {
		   info = getExifInfo(fileToMove);
		} 
		if (info == null) {
			info = getMp4Info(fileToMove);
		}
		if (info == null) {
			System.out.println("No  Info found in file: " + fileToMove.getName());
			return null;
		} 

		String targetDirPrefix;
		if ("nef".equals(extension) || 
			"crw".equals(extension)) {
			targetDirPrefix = "RAW";
		}  else if ("jpg".equals(extension) || 
				    "jpeg".equals(extension)) {
			targetDirPrefix = "JPG";
		}  else {
			targetDirPrefix = "OTHER";
		}
		
		
		File targetParentDir = sourceDirectory.getParentFile();

		File prefixDirFile = new File(targetParentDir, targetDirPrefix);
		if (!prefixDirFile.exists()) {
			prefixDirFile.mkdir();
		}

		Date targetDate = info.getCreationDate();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(targetDate);
		
		String targetDir =
				targetDirPrefix + "\\" +
				"C-" + 
				String.format(Locale.US, "%04d", calendar.get(Calendar.YEAR)) + "-" + 
				String.format(Locale.US, "%02d", calendar.get(Calendar.MONTH) + 1, 2) + "-" + 
				String.format(Locale.US, "%02d", calendar.get(Calendar.DAY_OF_MONTH));
		
		System.out.print(fileToMove.getName() + ", ");
		info.print(); System.out.println();
		
		File targetDirFile = new File(targetParentDir, targetDir);
		if (!targetDirFile.exists()) {
			targetDirFile.mkdir();
		}

		FileParts fp = new FileParts(fileToMove, targetDirFile);
		
		long cnt = Files.list(targetDirFile.toPath()).filter(c -> c.toFile().getName().equals(fp.getFile().getName())).count();
		
		while (cnt > 0) {
			fp.tryNewName();
			cnt = Files.list(targetDirFile.toPath()).filter(c -> c.toFile().getName().equals(fp.getFile().getName())).count();
		}
		
		return fp.getFile();

	}
	
	
	private Info getMp4Info(File file) throws ImageProcessingException, IOException {
		Metadata meta = Mp4MetadataReader.readMetadata(file);
		Mp4Directory dir = meta.getFirstDirectoryOfType(Mp4Directory.class);
		if (dir != null) {
			return new InfoBuilder(Kind.Mp4Video).withCreationdate(dir.getDate(Mp4Directory.TAG_CREATION_TIME)).build();
		}
		return null;
	}

	public static Info getExifInfo(File file) throws ImageProcessingException, IOException {
		Metadata metadata = ImageMetadataReader.readMetadata(file);
		Date dateOrig = null;
		if (dateOrig == null) {
			ExifIFD0Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
			if (directory != null) {
				dateOrig = directory.getDate(ExifIFD0Directory.TAG_DATETIME_ORIGINAL);
			}
		}
		if (dateOrig == null) {
			ExifSubIFDDirectory dir= metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
			if (dir != null) {
				dateOrig = dir.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
			}
		}
		if (dateOrig == null) {
			FileSystemDirectory dir = metadata.getFirstDirectoryOfType(FileSystemDirectory.class);
			if (dir != null) {
				dateOrig = dir.getDate(FileSystemDirectory.TAG_FILE_MODIFIED_DATE);
			}
		}			

		return new InfoBuilder(Kind.Photo).withCreationdate(dateOrig).build();
	}

	public void dump(Path p) throws IOException, ImageProcessingException {
		System.out.println("Dump: " + p.toString());

		Files.walk(p)
			.filter(f -> f.toFile().isDirectory() && !Util.isSourceDir(f.getFileName().toString()))
			.forEach(f -> {
				System.out.println("Dir:" + f.getFileName().toString());
				try {
					
//					Metadata metadata = ImageMetadataReader.readMetadata(f);
					
					Files.list(f)
					.filter(f2 -> !f2.toFile().isDirectory())
					.forEach(f2 -> {

						System.out.println(f2.getFileName());
						try {
							Metadata metadata = ImageMetadataReader.readMetadata(f2.toFile());
							for (Directory d : metadata.getDirectories()) {
							System.out.println("DD - " + d.getName());
							for (Tag tag : d.getTags()) {
								System.out.println("Directory: " + d.getName() + ", Tag: " + tag.toString());
							}
						}		
						} catch (ImageProcessingException | IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
				});
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		});
	}
}
