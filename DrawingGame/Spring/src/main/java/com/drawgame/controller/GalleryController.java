package com.drawgame.controller;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.drawgame.model.Image;
import com.drawgame.model.Word;
import com.drawgame.repository.ImageRepository;

@Component
@RestController
public class GalleryController {
    public static String uploadDirectory = System.getProperty("user.dir") + "/uploads";
    
    @Autowired
    ImageRepository ib;

    @RequestMapping("/images")
	List<Image> listWords() {
		return ib.findAll();
	}
    
    boolean imagebaseIsEmpty() {
		return ib.findAll().size() == 0;
	}
	
	Integer imagebaseSize() {
		return ib.findAll().size();
	}	
	
	public Integer findImage(String encoded) {
		if (imagebaseIsEmpty()) {
			return -1;
		}
		Integer result = -1;
		List<Image> list = ib.findAll();
		Iterator<Image> iterator = list.iterator();
	      while(iterator.hasNext()) {
	    	 Image current = iterator.next();
	    	 String currentBitmap = current.getBitmap();
	         if (currentBitmap.equals(encoded)) {
	        	 result = current.getId();
	        	 return result;
	         }
	      }
		
		
		return result;
	}
    
//    @GetMapping("/upload/{encoded}")
//	String uploadImage(@PathVariable String encoded) {
//    	if (imagebaseIsEmpty()) {
//    		Image image = new Image();
//    		image.setBitmap(encoded);
//			ib.save(image);
//			//return;
//			return encoded + " has been added";
//		}
//		Integer found = findImage(encoded);
//		if (found != -1) {
//			return encoded +" already exists";
//			//return;
//		}
//		Image image = new Image();
//		image.setBitmap(encoded);
//		ib.save(image);
//		return encoded + " has been added";
//	}
    
	@PostMapping("/upload")
	String uploadImage(@RequestBody String string) throws ParseException, UnsupportedEncodingException {
		String[] split = string.split("=");
		String encoded = split[1];
		
    	if (imagebaseIsEmpty()) {
    		Image image = new Image();
    		image.setBitmap(encoded);
			ib.save(image);
			//return;
			return encoded + " has been added";
		}
		Integer found = findImage(encoded);
		if (found != -1) {
			return encoded +" already exists";
			//return;
		}
		Image image = new Image();
		image.setBitmap(encoded);
		ib.save(image);
		return encoded + " has been added";
	}
	
	@RequestMapping("/getbyid/{id}")
	@ResponseBody
	String getbyId(@PathVariable Integer id) {
		System.out.println("aaaaaaaaaaaa");
		for (Image image : ib.findAll()) {
			if (image.getId() == id) {
				String ret = image.getBitmap();
				System.out.println(ret.length());
				return ret;
			}
		}
		
		return null;
	}
	
	@PostMapping("/getidlist")
	String getIdList() {
		String ret = "";
		for (Image image : ib.findAll()) {
			if (ret.equals(""))
				ret = image.getId() + "";
			else
				ret = ret + "," + image.getId();
		}
		
		return ret;
	}
	
	@PostMapping("/getAllImage")
	JSONObject getAllImage() {
		JSONObject map = new JSONObject();
		JSONArray jsonArray = new JSONArray();
    	if (imagebaseIsEmpty()) {
    		map.put("isEmpty", true);
			return map;
		}
    	map.put("isEmpty", false);
    	for (Image image : ib.findAll()) {
    		JSONObject json = new JSONObject();
    		json.put("id", image.getId());
    		System.out.println(image.getBitmap().length());
    		json.put("bitmap", image.getBitmap());
    		System.out.println(json.get("bitmap").toString().length());
    		jsonArray.add(json);
    	}
    	map.put("Images", jsonArray);
		return map;
	}
    
    @GetMapping("/removeImage/{id}")
	String removeWord(@PathVariable Integer id) {
		ib.deleteById(id);
		return id + " has been removed from the word database";
	}
    
}