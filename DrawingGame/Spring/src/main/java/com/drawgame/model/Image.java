package com.drawgame.model;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

@Component
@Entity
public class Image implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	Integer Id;

	@Column(columnDefinition = "LONGTEXT")
	String bitmap;

	public Image() {
	}

	public Image(String bitmap) {
		this.bitmap = bitmap;
	}

	public Integer getId() {
		return Id;
	}

	public void setId(Integer Id) {
		this.Id = Id;
	}

	public String getBitmap() {
		return bitmap;
	}

	public void setBitmap(String bitmap) {
		this.bitmap = bitmap;
	}

}
