package com.o2o.action.server.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.actions.api.ActionRequest;
import com.google.actions.api.ActionResponse;
import com.google.actions.api.Capability;
import com.google.actions.api.DialogflowApp;
import com.google.actions.api.ForIntent;
import com.google.actions.api.response.ResponseBuilder;
import com.google.actions.api.response.helperintent.DateTimePrompt;
import com.google.actions.api.response.helperintent.SelectionList;
import com.google.api.services.actions_fulfillment.v2.model.CarouselBrowse;
import com.google.api.services.actions_fulfillment.v2.model.CarouselBrowseItem;
import com.google.api.services.actions_fulfillment.v2.model.DateTime;
import com.google.api.services.actions_fulfillment.v2.model.Image;
import com.google.api.services.actions_fulfillment.v2.model.ListSelectListItem;
import com.google.api.services.actions_fulfillment.v2.model.MediaObject;
import com.google.api.services.actions_fulfillment.v2.model.MediaResponse;
import com.google.api.services.actions_fulfillment.v2.model.OpenUrlAction;
import com.google.api.services.actions_fulfillment.v2.model.OptionInfo;
import com.google.api.services.actions_fulfillment.v2.model.SimpleResponse;
import com.google.api.services.actions_fulfillment.v2.model.DateTimeValueSpecDateTimeDialogSpec;

public class English_y extends DialogflowApp {
	@ForIntent("Category") // 호출 : 123
	public ActionResponse processCategory(ActionRequest request) throws ExecutionException, InterruptedException {
		ResponseBuilder responseBuilder = getResponseBuilder(request);

		List<ListSelectListItem> items = new ArrayList<>();
		ListSelectListItem item = new ListSelectListItem();
		item.setTitle("School")
		.setOptionInfo(
				new OptionInfo()
				.setKey("School"))
		.setImage(
				new Image()
				.setUrl("https://tistory3.daumcdn.net/tistory/3084370/skin/images/KakaoTalk_20190404_123944655.jpg")
				.setAccessibilityText("Math and prime numbers"));
		items.add(item);

		item = new ListSelectListItem();
		item.setTitle("Study")
		.setOptionInfo(
				new OptionInfo()
				.setKey("Study"))
		.setImage(new Image().setUrl("https://tistory3.daumcdn.net/tistory/3084370/skin/images/KakaoTalk_20190404_123944655.jpg")
				.setAccessibilityText("Recipe"));
		items.add(item);

		item = new ListSelectListItem();
		item.setTitle("Life")
		.setOptionInfo(
				new OptionInfo()
				.setKey("Life"))
		.setImage(new Image().setUrl("https://tistory3.daumcdn.net/tistory/3084370/skin/images/KakaoTalk_20190404_123944655.jpg")
				.setAccessibilityText("Recipe"));
		items.add(item);

		return responseBuilder.add("Pick what you want to talk.")
				.add(new SelectionList().setTitle("Category").setItems(items))
				.addSuggestions( new String[]{ "School", "Study", "Life" }).build();

	}

	@ForIntent("Category_R")
	public ActionResponse processResponse(ActionRequest request) throws ExecutionException, InterruptedException {

		ResponseBuilder responseBuilder = getResponseBuilder(request);
		String selectedItem = request.getSelectedOption();

		if (selectedItem.equals("School")) {
			responseBuilder.add("Let's talk about school. What will you do at school?");
		}
		else if (selectedItem.equals("Study")) {
			responseBuilder.add("Let's talk about Study. What will you do study?");
		}
		else {

			responseBuilder
				    .add("This is the Date time helper intent")
				    .add(
				        new DateTimePrompt()
				        	.setTimePrompt("What time?")
				            .setDateTimePrompt("When would ilke to schedule the appointment")
				            .setDatePrompt("What day?")
				            );
		}
		return responseBuilder.build();
	}

	@ForIntent("Category_R_school")
	public ActionResponse School(ActionRequest request) throws ExecutionException, InterruptedException {
		ResponseBuilder responseBuilder = getResponseBuilder(request);
		return responseBuilder.build();
	}

	@ForIntent("Category_R_study")
	public ActionResponse Study(ActionRequest request) throws ExecutionException, InterruptedException {

		ResponseBuilder responseBuilder = getResponseBuilder(request);
		return responseBuilder.build();

	}

	@ForIntent("Category_R_life")
	public ActionResponse Life(ActionRequest request) throws ExecutionException, InterruptedException {

		ResponseBuilder responseBuilder = getResponseBuilder(request);
		DateTime dateTime = request.getDateTime();
		if(dateTime == null)
		{
			return responseBuilder.add("time is null").build();
		}
		int hours = dateTime.getTime().getHours();
		int minutes = dateTime.getTime().getMinutes();
		if(hours<=8 && hours >=4)
		{
			responseBuilder.add(hours+":"+minutes+". you get up early.");
		}
		else
		{
			responseBuilder.add(hours+":"+minutes+". you get up late.");
		}
		return responseBuilder.build();
	}
	@ForIntent("input.welcome")
	public ActionResponse welcome(ActionRequest request) {
		  ResponseBuilder responseBuilder = getResponseBuilder(request);
		  if (request.getUser().getLastSeen() != null) {
		    responseBuilder.add("Hey you're back...");
		  } else {
		    responseBuilder.add("Welcome to Number Genie!...");
		  }
		  return responseBuilder.build();
		}

}
