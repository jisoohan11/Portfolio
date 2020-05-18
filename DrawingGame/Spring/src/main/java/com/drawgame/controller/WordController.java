package com.drawgame.controller;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.drawgame.model.Room;
import com.drawgame.model.Word;
import com.drawgame.repository.WordRepository;
import com.drawgame.websocket.GameRoomMng;



@RestController
public class WordController {
	

	@Autowired
	WordRepository wb;
	
	GameRoomMng grm = GameRoomMng.getInstance();
	
	Random r = new Random();


		
	@RequestMapping("/words")
	List<Word> listWords() {
		return wb.findAll();
	}
	
	
	// adds word with given def to wordbase
	@GetMapping("/addword/{word}/{definition}")
	String addWord(@PathVariable String word, @PathVariable String definition) {
		if (wordbaseIsEmpty()) {
			Word w = new Word();
			w.setWord(word);
			w.setDefinition(definition);
			wb.save(w);
			//return;
			return word + " has been added";
			
		}
		Integer found = findWord(word);
		if (found != -1) {
			return word +" already exists";
			//return;
		}
		Word w = new Word();
		w.setWord(word);
		w.setDefinition(definition);
		wb.save(w);
		return word + " has been added";
	}
	
	//gets a random word from wordbase for the start of the game
	@RequestMapping("/getWord")
	JSONObject getWord() {
		if (wordbaseIsEmpty()) {
			return new JSONObject(); // return empty json if no words are in the wordbase
		}
		List<Word> words = listWords();
		int num_words = words.size();
		int selectRandomWord = r.nextInt(num_words);
		Word w = words.get(selectRandomWord);
		
		JSONObject wordObject = new JSONObject();
			
		wordObject.put("ID",w.getId());
		wordObject.put("Word",w.getWord());
		wordObject.put("Definition",w.getDefinition());
		
		return wordObject;
	}
	
	//
	@RequestMapping("/changeWord/{currentWord}/{roomUid}")
	public JSONObject changeWord(@PathVariable String currentWord, @PathVariable String roomUid) {
		if (wordbaseIsEmpty()) {
			return new JSONObject(); // return empty json if no words are in the wordbase
		}
		
		Room room = grm.getRoom(UUID.fromString(roomUid));
		
		Word newWord = getRandomWord();
		System.out.println(room.getCorAnswer());
		room.setCorAnswer(newWord.getWord());
		System.out.println(room.getCorAnswer());
		JSONObject wordObject = new JSONObject();
		
		// return same is word if only 1 word in wordbase
		if (wordbaseSize() == 1) {
			wordObject.put("ID",newWord.getId());
			wordObject.put("Word",newWord.getWord());
			wordObject.put("Definition",newWord.getDefinition());
			return wordObject;
		}
		
		
		// to make sure it does not get the same word
		while (currentWord.equalsIgnoreCase(newWord.getWord())) {
			newWord = getRandomWord();
			System.out.println(room.getCorAnswer());
			room.setCorAnswer(newWord.getWord());
			System.out.println(room.getCorAnswer());
		}
		
		wordObject.put("ID",newWord.getId());
		wordObject.put("Word",newWord.getWord());
		wordObject.put("Definition",newWord.getDefinition());
		
		return wordObject;

	}
	
	// if word is not found returns -1
	// else returns the word ID
	public Integer findWord(String word) {
		if (wordbaseIsEmpty()) {
			return -1;
		}
		Integer result = -1;
		List<Word> list = wb.findAll();
		Iterator<Word> iterator = list.iterator();
	      while(iterator.hasNext()) {
	    	 Word current = iterator.next();
	    	 String currentWord = current.getWord();
	         if (currentWord.equals(word)) {
	        	 result = current.getId();
	        	 return result;
	         }
	      }
		
		
		return result;
	}
	
	Word getRandomWord() {
		if (wordbaseIsEmpty()) {
			return new Word(); // no words in database returns empty word object
		}
		List<Word> words = listWords();
		int num_words = words.size();
		int selectRandomWord = r.nextInt(num_words);
		Word w = words.get(selectRandomWord);
		
		return w;
		
	}
	
	boolean wordbaseIsEmpty() {
		return wb.findAll().size() == 0;
	}
	
	Integer wordbaseSize() {
		return wb.findAll().size();
	}
	
	@GetMapping("/initwb")
	String initilaizeWordbase() {
		if (!wordbaseIsEmpty()) {
			return "wordbase is not empty (already initialized)"; 
		} 
	
		addWord("Car", "a vehicle used for ground transportation");
		addWord("Cat", "an animal with 4 legs and meows");
		addWord("Dog", "an animal with 4 legs and barks");
		addWord("Tree", "a big plant that has a lot of leaves");
		addWord("Flower", "a small plant with beautiful smell");
		addWord("House", "a place where humans live, sleep and eat");
		addWord("Camel", "an animal that have a hump and lives in the desert");
		addWord("Book", "an object used to write stuff in it or read stuff from it");
		addWord("Cake", "a dessert food usually served for celebration");
		addWord("Fire", "a source of heat that can be created by burning stuff");
		
		return "wordbase initialized";
	}
	
	@GetMapping("/removeword/{word}")
	String removeWord(@PathVariable String word) {
		Integer found = findWord(word);
		if (found == -1 || wordbaseIsEmpty()) {
			return word + " is not in the word database";
		} else {
			wb.deleteById(found);
			return word + " has been removed from the word database";
		}
	}
	
	public String newWord(String currentWord) {
		if (wordbaseIsEmpty()) {
			System.out.println("wordbase IsEmpty");
			return ""; // return empty string if no words are in the wordbase
		}
		Word newWord = getRandomWord();
		// return same is word if only 1 word in wordbase
		if (wordbaseSize() == 1) {
			return newWord.toString();
		}

		// to make sure it does not get the same word
		while (currentWord.equalsIgnoreCase(newWord.getWord())) {
			newWord = getRandomWord();
		}
		return newWord.toString();
	}
}