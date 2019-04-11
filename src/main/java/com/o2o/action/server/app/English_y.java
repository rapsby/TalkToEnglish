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

public class English_y extends DialogflowApp {
	@ForIntent("YH_First")
	public ActionResponse processCurrentTime(ActionRequest request) throws ExecutionException, InterruptedException {
		ResponseBuilder responseBuilder = getResponseBuilder(request);
		
		List<ListSelectListItem> items = new ArrayList<>();
		ListSelectListItem item = new ListSelectListItem();
		item.setTitle("1")
		.setOptionInfo(
				new OptionInfo()
				.setKey("1"))
		.setImage(
				new Image()
				.setUrl("https://tistory3.daumcdn.net/tistory/3084370/skin/images/KakaoTalk_20190404_123944655.jpg")
				.setAccessibilityText("Math and prime numbers"));
		items.add(item);

		item = new ListSelectListItem();
		item.setTitle("2")
		.setOptionInfo(
				new OptionInfo()
				.setKey("2"))
		.setImage(new Image().setUrl("https://tistory3.daumcdn.net/tistory/3084370/skin/images/KakaoTalk_20190404_123944655.jpg").setAccessibilityText("Egypt"));
		items.add(item);

		item = new ListSelectListItem();
		item.setTitle("Life")
		.setOptionInfo(
				new OptionInfo()
				.setKey("Life"))
		.setImage(new Image().setUrl("https://tistory3.daumcdn.net/tistory/3084370/skin/images/KakaoTalk_20190404_123944655.jpg").setAccessibilityText("Recipe"));
		items.add(item);

		return responseBuilder.add("empty")
				.add(new SelectionList().setTitle("Category").setItems(items))
				.addSuggestions( new String[]{ "1", "2", "Life" }).build();

	}
	
	@ForIntent("YH_First - response")
	public ActionResponse processCurrentTimeResponse(ActionRequest request) throws ExecutionException, InterruptedException {

		ResponseBuilder responseBuilder = getResponseBuilder(request);
		String selectedItem = request.getSelectedOption();
		List<String> suggestions = new ArrayList<String>();
		//Map<String, Object> data = request.getConversationData();

		if (selectedItem == null) {
			responseBuilder.add("No keyword");
		} else if (selectedItem.equals("1")) {
			responseBuilder.add("1111111111");
		} else if (selectedItem.equals("2")) {
			responseBuilder.add("2222222222");
		} else if (selectedItem.equals("Life")) {
			responseBuilder.add("333333333");
		} else {
			responseBuilder.add("00000000000");
		}

		responseBuilder.addSuggestions(suggestions.toArray(new String[suggestions.size()]));
		return responseBuilder.build();

	}
	
}
