package com.drawgame.model;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.json.simple.JSONObject;

@Entity
public class Word implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Integer Id;

	@Column
	private String word;

	@Column
	private String definition;

	public void setId(Integer ID) {
		this.Id = ID;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public Integer getId() {
		return Id;
	}

	public String getWord() {
		return word;
	}

	public String getDefinition() {
		return definition;
	}

	/*
	 * public JSONObject getJSONObject() { JSONObject wordObject = new JSONObject();
	 * 
	 * wordObject.put("ID",Id); wordObject.put("Word",word);
	 * wordObject.put("Definition",definition);
	 * 
	 * return wordObject; }
	 */

}
