package com.o2o.action.server.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.google.actions.api.ActionRequest;
import com.google.actions.api.ActionResponse;
import com.google.actions.api.DialogflowApp;
import com.google.actions.api.ForIntent;
import com.google.actions.api.response.ResponseBuilder;
import com.google.actions.api.response.helperintent.DateTimePrompt;
import com.google.actions.api.response.helperintent.SelectionList;
import com.google.api.services.actions_fulfillment.v2.model.DateTime;
import com.google.api.services.actions_fulfillment.v2.model.Image;
import com.google.api.services.actions_fulfillment.v2.model.ListSelectListItem;
import com.google.api.services.actions_fulfillment.v2.model.OptionInfo;

public class English_y extends DialogflowApp {

	static boolean usedSchool = false;
	static boolean usedStudy = false;
	static boolean usedLife = false;


	@ForIntent("Default Welcome Intent")
	public ActionResponse Start(ActionRequest request) throws ExecutionException, InterruptedException {
		ResponseBuilder responseBuilder = getResponseBuilder(request);

		List<ListSelectListItem> items = new ArrayList<>();
		ListSelectListItem item = new ListSelectListItem();
		List<String> suggestions = new ArrayList<>();
		if(!usedSchool) {
		
			item.setTitle("School")
			.setOptionInfo(
					new OptionInfo()
					.setKey("School"))
			.setImage(
					new Image()
					.setUrl("https://tistory3.daumcdn.net/tistory/3084370/skin/images/KakaoTalk_20190404_123944655.jpg")
					.setAccessibilityText("Math and prime numbers"));
			items.add(item);
			suggestions.add(item.getTitle());
		}
		if(!usedStudy) {
		
			item = new ListSelectListItem();
			item.setTitle("Study")
			.setOptionInfo(
					new OptionInfo()
					.setKey("Study"))
			.setImage(new Image().setUrl("https://tistory3.daumcdn.net/tistory/3084370/skin/images/KakaoTalk_20190404_123944655.jpg")
					.setAccessibilityText("Recipe"));
			items.add(item);
			suggestions.add(item.getTitle());
		}
		

		if(!usedLife) {

			item = new ListSelectListItem();
			item.setTitle("Life")
			.setOptionInfo(
					new OptionInfo()
					.setKey("Life"))
			.setImage(new Image().setUrl("https://tistory3.daumcdn.net/tistory/3084370/skin/images/KakaoTalk_20190404_123944655.jpg")
					.setAccessibilityText("Recipe"));
			items.add(item);
			suggestions.add(item.getTitle());
		}

		String welcome = " Pick what you want to talk.";
		String [] sug = new String [suggestions.size()];
		return responseBuilder.add(welcome)
				.add(new SelectionList().setTitle("Category").setItems(items))
				.addSuggestions((String [])suggestions.toArray(sug))
				.build();
	}

	@ForIntent("Category_R")
	public ActionResponse processResponse(ActionRequest request) throws ExecutionException, InterruptedException {

		ResponseBuilder responseBuilder = getResponseBuilder(request);
		String selectedItem = request.getSelectedOption();

		if (selectedItem.equals("School")) {// computer
			responseBuilder.add("What's your major?");
		}
		else if (selectedItem.equals("Study")) {// operating system, os
			responseBuilder.add("What is your favorite subject?");
		}
		else {
			responseBuilder
			.add("This is the Date time helper intent")
			.add(
					new DateTimePrompt()
					.setDateTimePrompt("What time did you get up?")
					.setDatePrompt("What day?")
					.setTimePrompt("What time?"));
		}
		return responseBuilder.build();
	}

	@ForIntent("Category_R_school")
	public ActionResponse School(ActionRequest request) throws ExecutionException, InterruptedException {
		ResponseBuilder responseBuilder = getResponseBuilder(request);
		if (request.getRawText().equals(request.getParameter("School"))) {
			responseBuilder.addSuggestions( new String[]{ "Coding", "Teaching", "Experiment"});
			responseBuilder.add("Oh, " + request.getRawText() + "? That's good. What are you do?");
		}
		
		return responseBuilder.build();
	}

	@ForIntent("Category_R_study")
	public ActionResponse Study(ActionRequest request) throws ExecutionException, InterruptedException {

		ResponseBuilder responseBuilder = getResponseBuilder(request);
		if (request.getRawText().equals(request.getParameter("School"))) {
			responseBuilder.addSuggestions( new String[]{ "Computer Engineer", "Lawyer", "Official"});
			responseBuilder.add("That's good. Studying it, what can you be?");
		}
		
		return responseBuilder.build();

	}

	@ForIntent("Category_R_life")
	public ActionResponse Life(ActionRequest request) throws ExecutionException, InterruptedException {

		ResponseBuilder responseBuilder = getResponseBuilder(request);
		DateTime dateTime = request.getDateTime();
		String response = "";
		if(dateTime == null)
			return responseBuilder.add("time is null").build();

		int hours = dateTime.getTime().getHours();
		int minutes = dateTime.getTime().getMinutes();

		if(hours <= 8 && hours >= 4) {
			response += hours+":"+minutes+". oh, you get up early.";
			responseBuilder.addSuggestions( new String[]{ "Business", "Exercise", "Breakfast" });
		}
		else {
			response += hours+":"+minutes+". oh, you get up late.";
			responseBuilder.addSuggestions( new String[]{ "I lost sleep last night", "Assignment", "Nothing" });
		}

		response += " Why did you get up that?";
		return responseBuilder.add(response).build();
	}
	
	@ForIntent("School_conversation")
	public ActionResponse School_conv(ActionRequest request) throws ExecutionException, InterruptedException {

		usedSchool = true;
		ResponseBuilder responseBuilder = getResponseBuilder(request);
		String sug[] = new String[] {"Yes", "No"};
		responseBuilder.add("So was it. Umm.. " + "Why don't you talk about other topic?");
		
		return responseBuilder.addSuggestions(sug).build();

	}
	
	@ForIntent("Study_conversation")
	public ActionResponse Study_conv(ActionRequest request) throws ExecutionException, InterruptedException {

		usedStudy = true;
		ResponseBuilder responseBuilder = getResponseBuilder(request);
		String sug[] = new String[] {"Yes", "No"};
		responseBuilder.add("So was it. Umm.. " + "Why don't you talk about other topic?");
		
		return responseBuilder.addSuggestions(sug).build();

	}

	@ForIntent("Life_conversation")
	public ActionResponse Life_conv(ActionRequest request) throws ExecutionException, InterruptedException {

		usedLife = true;
		ResponseBuilder responseBuilder = getResponseBuilder(request);
		String sug[] = new String[] {"Yes", "No"};
		responseBuilder.add("So was it. Umm.. " + "Why don't you talk about other topic?");
		
		return responseBuilder.addSuggestions(sug).build();
	}
	
	@ForIntent("Home")	// "Yes", No setting "No"
	public ActionResponse Home(ActionRequest request) throws ExecutionException, InterruptedException {
		return Start(request);
	}

}
