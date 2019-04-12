package com.o2o.action.server.app;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import com.google.actions.api.ActionContext;
import com.google.actions.api.ActionRequest;
import com.google.actions.api.ActionResponse;
import com.google.actions.api.DialogflowApp;
import com.google.actions.api.ForIntent;
import com.google.actions.api.response.ResponseBuilder;
import com.google.actions.api.response.helperintent.Confirmation;
import com.google.actions.api.response.helperintent.DateTimePrompt;
import com.google.actions.api.response.helperintent.SelectionCarousel;
import com.google.actions.api.response.helperintent.SignIn;
import com.google.api.services.actions_fulfillment.v2.model.BasicCard;
import com.google.api.services.actions_fulfillment.v2.model.CarouselSelectCarouselItem;
import com.google.api.services.actions_fulfillment.v2.model.Image;
import com.google.api.services.actions_fulfillment.v2.model.OptionInfo;
import com.o2o.action.server.db.Category;
import com.o2o.action.server.repo.CategoryRepository;
import com.o2o.action.server.repo.ChannelRepository;
import com.o2o.action.server.repo.ScheduleRepository;

public class OnionApp extends DialogflowApp {
	private CategoryRepository categoryRepository;
	private ChannelRepository channelRepository;
	private ScheduleRepository scheduleRepository;

	public void setCategoryRepository(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	public void setChannelRepository(ChannelRepository channelRepository) {
		this.channelRepository = channelRepository;
	}

	public void setScheduleRepository(ScheduleRepository scheduleRepository) {
		this.scheduleRepository = scheduleRepository;
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
					.setDateTimePrompt("When would ilke to schedule the appointment")
		            .setDatePrompt("What day?")
		            .setTimePrompt("What time?"));
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
		responseBuilder.add("sfgdfgdfkerfmksdf");
		
		return responseBuilder.build();
	}




}
