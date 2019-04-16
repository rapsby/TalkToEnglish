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

public class English extends DialogflowApp {
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
		responseBuilder.add(request.getDateTime().getTime().toString());
		return responseBuilder.build();
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

	@ForIntent("Conversation - response")
	public ActionResponse getConversation(ActionRequest request) throws ExecutionException, InterruptedException{
		ResponseBuilder responseBuilder = getResponseBuilder(request);
		String selectedItem = request.getSelectedOption();
		List<String> suggestions = new ArrayList<String>();
		//Map<String, Object> data = request.getConversationData();

		if (selectedItem == null) {
			responseBuilder.add("관련된 키워드가 없는 것 같다.");
		} else if (selectedItem.equals("요리")) {
			responseBuilder.add("요리에 대해 말해볼까요? 어떤 요리 할 줄 아세요?");
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
		} else if (selectedItem.equals("음악")) {
			responseBuilder.add("오! 음악에 관심이 있으신가보군요. 좋아하는 장르가 어떻게 되죠?");
		} else if (selectedItem.equals("직장")) {
			responseBuilder.add("아이고.. 고생많으시겠네요. 어떤 일을 하고 계세요?");
		} else {
			responseBuilder.add("관련된 키워드를 모르겠다.");
		}

		responseBuilder.addSuggestions(suggestions.toArray(new String[suggestions.size()]));
		return responseBuilder.build();
	}

	@ForIntent("Today Works")
	public ActionResponse processColors(ActionRequest request) throws ExecutionException, InterruptedException {

		ResponseBuilder responseBuilder = getResponseBuilder(request);
		if (!request.hasCapability(Capability.SCREEN_OUTPUT.getValue())) {
			return responseBuilder
					.add("Sorry, try ths on a screen device or select the phone surface in the simulator.")
					.build();
		}

		MediaObject mediaObject = new MediaObject();
		mediaObject
		.setName("Jazz in Paris")
		.setContentUrl("https://storage.googleapis.com/automotive-media/Jazz_In_Paris.mp3")
		.setDescription("A funky Jazz tune");
		List<MediaObject> mediaObjects = new ArrayList<>();
		mediaObjects.add(mediaObject);

		List<CarouselBrowseItem> items = new ArrayList<>();
		CarouselBrowseItem item;
		item =
				new CarouselBrowseItem()
				.setTitle("Title of item 1")
				.setDescription("Description of item 1")
				.setOpenUrlAction(new OpenUrlAction().setUrl("http://www.naver.com"))
				.setFooter("Item 1 footer")
				.setImage(
						new Image().setUrl("https://tistory3.daumcdn.net/tistory/3084370/skin/images/KakaoTalk_20190404_123944655.jpg").setAccessibilityText("Image alternate text"));
		items.add(item);
		item =
				new CarouselBrowseItem()
				.setTitle("Title of item 2")
				.setDescription("Description of item 2")
				.setOpenUrlAction(new OpenUrlAction().setUrl("http://www.naver.com"))
				.setFooter("Item 2 footer")
				.setImage(
						new Image().setUrl("https://tistory3.daumcdn.net/tistory/3084370/skin/images/KakaoTalk_20190404_123944655.jpg").setAccessibilityText("Image alternate text"));
		items.add(item);

		responseBuilder.add("This is a browse carousel").add(new CarouselBrowse().setItems(items));
		return responseBuilder.build();
	}

	@ForIntent("Favorite Colors")
	public ActionResponse processWorks(ActionRequest request) throws ExecutionException, InterruptedException {

		ResponseBuilder responseBuilder = getResponseBuilder(request);
		if (!request.hasCapability(Capability.SCREEN_OUTPUT.getValue())) {
			return responseBuilder
					.add("Sorry, try ths on a screen device or select the phone surface in the simulator.")
					.build();
		}

		MediaObject mediaObject = new MediaObject();
		mediaObject
		.setName("Jazz in Paris")
		.setContentUrl("https://storage.googleapis.com/automotive-media/Jazz_In_Paris.mp3")
		.setDescription("A funky Jazz tune")
		.setIcon(
				new Image()
				.setUrl("'https://storage.googleapis.com/automotive-media/album_art.jpg")
				.setAccessibilityText("Ocean view"));
		List<MediaObject> mediaObjects = new ArrayList<>();
		mediaObjects.add(mediaObject);

		return responseBuilder
				.add("This is a browse carousel")
				.add(new MediaResponse().setMediaObjects(mediaObjects)).build();

	}

	@ForIntent("Music")
	public ActionResponse processSelf_composed(ActionRequest request) throws ExecutionException, InterruptedException {

		ResponseBuilder responseBuilder = getResponseBuilder(request);
		if (!request.hasCapability(Capability.SCREEN_OUTPUT.getValue())) {
			return responseBuilder
					.add("Sorry, try ths on a screen device or select the phone surface in the simulator.")
					.build();
		}
		MediaObject mediaObject = new MediaObject();
		mediaObject
		.setName("Jazz in Paris")
		.setContentUrl("https://storage.googleapis.com/automotive-media/Jazz_In_Paris.mp3")
		.setDescription("A funky Jazz tune")
		.setIcon(
				new Image()
				.setUrl("'https://storage.googleapis.com/automotive-media/album_art.jpg")
				.setAccessibilityText("Ocean view"));
		List<MediaObject> mediaObjects = new ArrayList<>();
		mediaObjects.add(mediaObject);

		return responseBuilder.add(new MediaResponse().setMediaObjects(mediaObjects)).build();
	}
}
