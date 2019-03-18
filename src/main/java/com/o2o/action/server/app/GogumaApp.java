package com.o2o.action.server.app;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.actions.api.ActionRequest;
import com.google.actions.api.ActionResponse;
import com.google.actions.api.DialogflowApp;
import com.google.actions.api.ForIntent;
import com.google.actions.api.response.ResponseBuilder;
import com.google.actions.api.response.helperintent.SelectionCarousel;
import com.google.actions.api.response.helperintent.SelectionList;
import com.google.api.services.actions_fulfillment.v2.model.AndroidApp;
import com.google.api.services.actions_fulfillment.v2.model.BasicCard;
import com.google.api.services.actions_fulfillment.v2.model.Button;
import com.google.api.services.actions_fulfillment.v2.model.CarouselSelectCarouselItem;
import com.google.api.services.actions_fulfillment.v2.model.Image;
import com.google.api.services.actions_fulfillment.v2.model.LinkOutSuggestion;
import com.google.api.services.actions_fulfillment.v2.model.ListSelectListItem;
import com.google.api.services.actions_fulfillment.v2.model.OpenUrlAction;
import com.google.api.services.actions_fulfillment.v2.model.OptionInfo;
import com.google.api.services.actions_fulfillment.v2.model.TableCard;
import com.google.api.services.actions_fulfillment.v2.model.TableCardCell;
import com.google.api.services.actions_fulfillment.v2.model.TableCardColumnProperties;
import com.google.api.services.actions_fulfillment.v2.model.TableCardRow;

public class GogumaApp extends DialogflowApp {

	@ForIntent("Default Welcome Intent")
	public ActionResponse welcome(ActionRequest request) throws ExecutionException, InterruptedException {
		ResponseBuilder responseBuilder = getResponseBuilder(request);

		AndroidApp app = new AndroidApp();
		app.setPackageName("kr.o2o.app.android.o2omediaviewer");

		Button learnMoreButton = new Button().setTitle("링크용 버튼")
				.setOpenUrlAction(new OpenUrlAction().setUrl("http://www.daum.net"));// .setAndroidApp(app));
		List<Button> buttons = new ArrayList<>();
		buttons.add(learnMoreButton);
		String text = "This is a basic card.  Text in a basic card can include \"quotes\" and\n"
				+ "  most other unicode characters including emoji \uD83D\uDCF1. Basic cards also support\n"
				+ "  some markdown formatting like *emphasis* or _italics_, **strong** or\n"
				+ "  __bold__, and ***bold itallic*** or ___strong emphasis___ as well as other\n"
				+ "  things like line  \nbreaks";
		responseBuilder.add("베이직 카드입니다.")
				.add(new BasicCard().setTitle("This is a title").setSubtitle("This is a subtitle")
						.setFormattedText(text)
						.setImage(new Image().setUrl("https://actions.o2o.kr/content/img/7LCp2.gif")
								.setAccessibilityText("Image alternate text"))
						.setImageDisplayOptions("CROPPED").setButtons(buttons));

		responseBuilder.addSuggestions(new String[] { "영화 찾아줘", "음악 찾아줘" })
				.add(new LinkOutSuggestion().setDestinationName("링크용 Suggestion").setUrl("http://www.naver.com"));
		ActionResponse response = responseBuilder.build();
		return response;
	}

	@ForIntent("Table demo")
	public ActionResponse mediaDemo(ActionRequest request) throws ExecutionException, InterruptedException {
		ResponseBuilder responseBuilder = getResponseBuilder(request);

		List<TableCardColumnProperties> columnProperties = new ArrayList<>();
		columnProperties.add(new TableCardColumnProperties().setHeader("Column #1"));
		columnProperties.add(new TableCardColumnProperties().setHeader("Column #2"));
		columnProperties.add(new TableCardColumnProperties().setHeader("Column #3"));

		List<TableCardRow> rows = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			List<TableCardCell> cells = new ArrayList<>();
			for (int j = 0; j < 3; j++) {
				String cellText = MessageFormat.format("Cell #{0}", (i + 1));
				cells.add(new TableCardCell().setText(cellText));
			}
			rows.add(new TableCardRow().setCells(cells));
		}

		TableCard table = new TableCard().setColumnProperties(columnProperties).setRows(rows);
		table.setTitle("Title 입니다.");
		table.setSubtitle("Sub Title 입니다.");
		table.setImage(new Image().setUrl("https://actions.o2o.kr/content/img/aog/ansi.jpg"));

		responseBuilder.add("This is an example of Table card.").add(table);

		return responseBuilder.build();
	}

	@ForIntent("Find movie")
	public ActionResponse findMovie(ActionRequest request) throws ExecutionException, InterruptedException {
		ResponseBuilder responseBuilder = getResponseBuilder(request);

		List<CarouselSelectCarouselItem> items = new ArrayList<CarouselSelectCarouselItem>();
		CarouselSelectCarouselItem item;
		item = new CarouselSelectCarouselItem().setTitle("The Simpsons").setDescription(
				"The Simpsons is an American animated sitcom created by Matt Groening for the Fox Broadcasting Company. The series is a satirical depiction of working-class life, epitomized by the Simpson family, which consists of Homer, Marge, Bart, Lisa, and Maggie. The show is set in the fictional town of Springfield and parodies American culture and society, television, and the human condition.")
				.setOptionInfo(new OptionInfo().setKey("SELECTION_KEY_ONE")
						.setSynonyms(Arrays.asList("synonym 1", "synonym 2", "synonym 3")))
				// .setOpenUrlAction(new
				// OpenUrlAction().setUrl("https://actions.o2o.kr/content/movie/1"))
				.setImage(new Image().setUrl("https://actions.o2o.kr/content/img/aog/simpsons.png")
						.setAccessibilityText("Image alternate text"));
		items.add(item);
		item = new CarouselSelectCarouselItem().setTitle("안시성(영화)").setDescription(
				"넥스트엔터테인먼트월드가 새로 신설한 회사인 스튜디오앤뉴에서 제작을 맡아 215억 원의 제작비가 투입된 블록버스터 사극으로 고구려-당 전쟁 당시 있었던 안시성 전투를 다루고 있다. 다만 영화 제작사 측에서 언급한 등장인물 소개글들을 보면 정통 사극보다는 퓨전 사극으로 추정된다.")
				.setOptionInfo(new OptionInfo().setKey("SELECTION_KEY_TWO")
						.setSynonyms(Arrays.asList("synonym 1-2", "synonym 2-2", "synonym 3-2")))
				// .setOpenUrlAction(new
				// OpenUrlAction().setUrl("https://actions.o2o.kr/content/movie/2"))
				.setImage(new Image().setUrl("https://actions.o2o.kr/content/img/aog/ansi.jpg")
						.setAccessibilityText("Image alternate text"));
		items.add(item);
		item = new CarouselSelectCarouselItem().setTitle("아쿠아맨")
				.setDescription("아쿠아맨은 DC 확장 유니버스의 6번째 영화이자 아쿠아맨의 탄생을 그린 해양 판타지 액션, 슈퍼히어로 영화이다.")
				.setOptionInfo(new OptionInfo().setKey("SELECTION_KEY_GOOGLE_PIXEL")
						.setSynonyms(Arrays.asList("synonym 1-3", "synonym 2-3", "synonym 3-3")))
				// .setOpenUrlAction(new
				// OpenUrlAction().setUrl("https://actions.o2o.kr/content/movie/3"))
				.setImage(new Image().setUrl("https://actions.o2o.kr/content/img/aog/aqua.jpg")
						.setAccessibilityText("Image alternate text"));
		items.add(item);

		responseBuilder.add("Carousel 입니다.").add(new SelectionCarousel().setItems(items));
		responseBuilder.addSuggestions(new String[] { "음악 찾아줘", "안녕" });

		return responseBuilder.build();
	}

	@ForIntent("Find audio")
	public ActionResponse findMovie2(ActionRequest request) throws ExecutionException, InterruptedException {
		ResponseBuilder responseBuilder = getResponseBuilder(request);

		List<ListSelectListItem> items = new ArrayList<>();
		ListSelectListItem item = new ListSelectListItem();
		item.setTitle("Title of first list item").setDescription("This is a description of the list item")
				.setOptionInfo(new OptionInfo().setKey("SELECTION_KEY_ONE")
						.setSynonyms(Arrays.asList("synonym 1", "synonym 2", "synonym 3")))
				.setImage(new Image().setUrl("https://actions.o2o.kr/content/img/aog/simpsons.png")
						.setAccessibilityText("Image alternate text"));
		items.add(item);

		item = new ListSelectListItem();
		item.setTitle("안시성").setDescription("Google Home is a voice activated speaker powered by the Google Assistant.")
				.setOptionInfo(new OptionInfo().setKey("SELECTION_KEY_GOOGLE_HOME")
						.setSynonyms(Arrays.asList("Google Home assistant", "Assistant")))
				.setImage(new Image().setUrl("https://actions.o2o.kr/content/img/aog/ansi.jpg")
						.setAccessibilityText("Google Home"));
		items.add(item);

		item = new ListSelectListItem();
		item.setTitle("아쿠아맨").setDescription("Pixel. Phone by Google")
				.setOptionInfo(new OptionInfo().setKey("SELECTION_KEY_GOOGLE_PIXEL")
						.setSynonyms(Arrays.asList("Pixel", "Pixel XL")))
				.setImage(new Image().setUrl("https://actions.o2o.kr/content/img/aog/aqua.jpg")
						.setAccessibilityText("Google Pixel"));
		items.add(item);

		responseBuilder.add("List 입니다.").add(new SelectionList().setTitle("List title").setItems(items));
		responseBuilder.addSuggestions(new String[] { "영화 찾아줘", "안녕" });

		return responseBuilder.build();
	}
}
