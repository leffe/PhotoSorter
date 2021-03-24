package com.lfs.tada.photosorter;

import java.util.Date;

public class InfoBuilder {
	
	enum Kind {Photo, Mp4Video};
 
	private Kind kind;
	private Date creationDate;

	public InfoBuilder(Kind kind) {
		this.kind = kind;
	}
	
	public InfoBuilder withCreationdate(Date date) {
		this.creationDate = date;
		return this;
	}
	
	public Info build() {
		Info info = new Info(kind);
		info.creationDate = this.creationDate;
		
		return info;
	}
	
	public class Info {
		
		private Kind kind;
		private Date creationDate;
		
		private Info(Kind kind) {
			this.kind = kind;
		}
		
		public Date getCreationDate() {
			return creationDate;
		}
		
		public Kind getKind() {
			return kind;
		}	
		
		
		public void print() {
			
			
			System.out.print("Kind: " + kind + ", creation date: " + creationDate);
		}
		
	}	
}
	
	

