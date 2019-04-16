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
import com.google.actions.api.response.helperintent.SelectionCarousel;
import com.google.actions.api.response.helperintent.SelectionList;
import com.google.api.services.actions_fulfillment.v2.model.CarouselBrowse;
import com.google.api.services.actions_fulfillment.v2.model.CarouselBrowseItem;
import com.google.api.services.actions_fulfillment.v2.model.CarouselSelectCarouselItem;
import com.google.api.services.actions_fulfillment.v2.model.DateTime;
import com.google.api.services.actions_fulfillment.v2.model.Image;
import com.google.api.services.actions_fulfillment.v2.model.ListSelectListItem;
import com.google.api.services.actions_fulfillment.v2.model.MediaObject;
import com.google.api.services.actions_fulfillment.v2.model.MediaResponse;
import com.google.api.services.actions_fulfillment.v2.model.OpenUrlAction;
import com.google.api.services.actions_fulfillment.v2.model.OptionInfo;
import com.o2o.action.server.repo.CategoryRepository;
import com.o2o.action.server.repo.ChannelRepository;
import com.o2o.action.server.repo.ScheduleRepository;

public class English_k extends DialogflowApp {
	@ForIntent("CurrentTime")
	public ActionResponse processCurrentTime(ActionRequest request) throws ExecutionException, InterruptedException {
		ResponseBuilder responseBuilder = getResponseBuilder(request);
		return responseBuilder
		    .add("This is the Date time helper intent")
		    .add(
		        new DateTimePrompt()
		            .setDateTimePrompt("When would ilke to schedule the appointment")
		            .setDatePrompt("2019-04-10")
		            .setTimePrompt("12:30"))
		    .build();

	}

	@ForIntent("CurrentTime - response")
	public ActionResponse processCurrentTimeResponse(ActionRequest request) throws ExecutionException, InterruptedException {

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
	
	@ForIntent("Conversation")
	public ActionResponse processConversation(ActionRequest request) throws ExecutionException, InterruptedException {

		ResponseBuilder responseBuilder = getResponseBuilder(request);
		if (!request.hasCapability(Capability.SCREEN_OUTPUT.getValue())) {
			return responseBuilder
					.add("Sorry, try ths on a screen device or select the phone surface in the simulator.")
					.build();
		}

		List<ListSelectListItem> items = new ArrayList<>();
		ListSelectListItem item = new ListSelectListItem();
		item.setTitle("요리")
		.setOptionInfo(
				new OptionInfo()
				.setKey("요리")
				.setSynonyms(
						Arrays.asList("식사", "아침", "점심", "저녁")))
		.setImage(
				new Image()
				.setUrl("https://tistory3.daumcdn.net/tistory/3084370/skin/images/KakaoTalk_20190404_123944655.jpg")
				.setAccessibilityText("Math and prime numbers"));
		items.add(item);

		item = new ListSelectListItem();
		item.setTitle("음악")
		.setOptionInfo(
				new OptionInfo()
				.setKey("음악")
				.setSynonyms(Arrays.asList("악기", "노래")))
		.setImage(new Image().setUrl("https://tistory3.daumcdn.net/tistory/3084370/skin/images/KakaoTalk_20190404_123944655.jpg").setAccessibilityText("Egypt"));
		items.add(item);

		item = new ListSelectListItem();
		item.setTitle("직장")
		.setOptionInfo(
				new OptionInfo()
				.setKey("직장")
				.setSynonyms(Arrays.asList("업무", "근무")))
		.setImage(new Image().setUrl("https://tistory3.daumcdn.net/tistory/3084370/skin/images/KakaoTalk_20190404_123944655.jpg").setAccessibilityText("Recipe"));
		items.add(item);

		return responseBuilder.add("빈칸")
				.add(new SelectionList().setTitle("어떤 주제에 대해 대화를 할까요?").setItems(items))
				.addSuggestions( new String[]{ "요리", "음악", "직장" }).build();

	}

	@ForIntent("topic")
	public ActionResponse processTopic(ActionRequest request) throws ExecutionException, InterruptedException {
		ResponseBuilder responseBuilder = getResponseBuilder(request);
		List<String> suggestions = new ArrayList<String>();


		List<CarouselSelectCarouselItem> items = new ArrayList<>();
		CarouselSelectCarouselItem item = new CarouselSelectCarouselItem();
		item.setTitle("Animal")
		.setDescription("Play with animals")
		.setOptionInfo(
				new OptionInfo()
				.setKey("animal"))
		.setImage(
				new Image()
				.setUrl("https://storage.googleapis.com/automotive-media/album_art.jpg")
				.setAccessibilityText("Animal"));
		items.add(item);
		suggestions.add(item.getTitle());
		item = new CarouselSelectCarouselItem();
		item.setTitle("Dinosaur")
		.setDescription("Play with dinosaurs")
		.setOptionInfo(
				new OptionInfo()
				.setKey("dinosaur")
				.setSynonyms(Arrays.asList("dino")))
		.setImage(new Image().setUrl("https://storage.googleapis.com/automotive-media/album_art.jpg")
				.setAccessibilityText("Dinosaur"));
		items.add(item);
		suggestions.add(item.getTitle());		
		
		item = new CarouselSelectCarouselItem();
		item.setTitle("Daily routine")
		.setDescription("Daily routine")
		.setOptionInfo(
				new OptionInfo()
				.setKey("daily")
				.setSynonyms(Arrays.asList("routine")))
		.setImage(new Image().setUrl("https://storage.googleapis.com/automotive-media/album_art.jpg")
				.setAccessibilityText("Daily rountine"));
		items.add(item);
		suggestions.add(item.getTitle());
		responseBuilder.addSuggestions(suggestions.toArray(new String[suggestions.size()]));

		return responseBuilder
				.add("topic")
				.add(new SelectionCarousel().setItems(items))
				.build();
	}

	@ForIntent("topic.option")
	public ActionResponse processTopicOption(ActionRequest request) throws ExecutionException, InterruptedException {
		ResponseBuilder responseBuilder = getResponseBuilder(request);
		String selectedItem = request.getSelectedOption();
		if(selectedItem.toLowerCase().equals("animal")) {
			responseBuilder.add("Ok, What's your favorite animal?");
		}
		else if(selectedItem.toLowerCase().equals("dinosaur")) {
			responseBuilder.add("Ok, What's your favorite dino?");
		}
		else
		{
			responseBuilder.add(
					new DateTimePrompt()
					.setDateTimePrompt("When would ilke to schedule the appointment"));
		            //.setDatePrompt("What day?")
		            //.setTimePrompt("What time?");
		}
		return responseBuilder.build();
	}

	@ForIntent("animal")
	public ActionResponse processAnimal(ActionRequest request) throws ExecutionException, InterruptedException {
		ResponseBuilder responseBuilder = getResponseBuilder(request);
		
		String selectedItem = request.getSelectedOption();
		responseBuilder.add("result : " +selectedItem);
		return responseBuilder.build();
	}

	@ForIntent("dinosaur")
	public ActionResponse processDinosaur(ActionRequest request) throws ExecutionException, InterruptedException {
		ResponseBuilder responseBuilder = getResponseBuilder(request);

		String selectedItem = request.getSelectedOption();
		responseBuilder.add("result : " +selectedItem);
		return responseBuilder.build();
	}
	
	@ForIntent("time")
	public ActionResponse processTIme(ActionRequest request) throws ExecutionException, InterruptedException {
		ResponseBuilder responseBuilder = getResponseBuilder(request);
		responseBuilder.add(request.getDateTime().getTime().toString());
		
		return responseBuilder.build();
	}
}
