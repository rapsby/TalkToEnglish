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
	@ForIntent("YH_First")
	public ActionResponse processYH(ActionRequest request) throws ExecutionException, InterruptedException {
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

		return responseBuilder.add("empty")
				.add(new SelectionList().setTitle("Category").setItems(items))
				.addSuggestions( new String[]{ "School", "Study", "Life" }).build();

	}

	@ForIntent("YH_First - response")
	public ActionResponse processYH_response(ActionRequest request) throws ExecutionException, InterruptedException {

		ResponseBuilder responseBuilder = getResponseBuilder(request);
		String selectedItem = request.getSelectedOption();

		if (selectedItem.equals("School")) 
			return YH_F_r_school(request);
		else if (selectedItem.equals("Study")) 
			return YH_F_r_study(request);
		else if (selectedItem.equals("Life")) 	// Life 키워드 선택
			return YH_F_r_life(request);
		else 
			return responseBuilder.build();
	}

	@ForIntent("YH_First - response - school")
	public ActionResponse YH_F_r_school(ActionRequest request) throws ExecutionException, InterruptedException {
		ResponseBuilder responseBuilder = getResponseBuilder(request);
		responseBuilder.add("Let's talk about school");
		
		return responseBuilder.build();

	}

	@ForIntent("YH_First - response - study")
	public ActionResponse YH_F_r_study(ActionRequest request) throws ExecutionException, InterruptedException {

		ResponseBuilder responseBuilder = getResponseBuilder(request);
		responseBuilder.add("Let's talk about Study");
		return responseBuilder.build();

	}

	@ForIntent("YH_First - response - life")
	public ActionResponse YH_F_r_life(ActionRequest request) throws ExecutionException, InterruptedException {

		ResponseBuilder responseBuilder = getResponseBuilder(request);
		responseBuilder.add("Let's talk about life");
		String cfinal = "";
		Object oFinal = (Object) request.getParameter("Life");
		if (oFinal != null && oFinal instanceof String) {
			cfinal = (String) oFinal;
		} else
			cfinal = "temp";
		
		if (cfinal.equalsIgnoreCase("weather"))
		{
			responseBuilder.add("weather22");
		}
		
		else if (cfinal.equalsIgnoreCase("cooking"))
		{
			responseBuilder.add("cooking22");
		}
		
		else if (cfinal.equalsIgnoreCase("life"))
			responseBuilder.add("life22");
		
		else
			responseBuilder.add("nope");
		
		return responseBuilder.build();

	}


	@ForIntent("YH_Date")
	public ActionResponse processDate(ActionRequest request) throws ExecutionException, InterruptedException {

		ResponseBuilder responseBuilder = getResponseBuilder(request);
		return responseBuilder
				.add("This is the Date time helper intent")
				.add(
						new DateTimePrompt()
						.setDateTimePrompt("When would ilke to schedule the appointment")
						.setDatePrompt("2019-04-12")
						.setTimePrompt("18:00"))
				.build();

	}

	@ForIntent("YH_Date - response")
	public ActionResponse processDate_response(ActionRequest request) throws ExecutionException, InterruptedException {

		ResponseBuilder responseBuilder = getResponseBuilder(request);
		String response;
		DateTime dateTimeValue = request.getDateTime();
		if (dateTimeValue != null) {
			response = "Alright, date set.";
		} else {
			response = "I'm having a hard time finding an appointment";
		}
		return responseBuilder.add(response).build();

	}
}
