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

	@ForIntent("support-find.symptom")
	public ActionResponse processFindSymptom(ActionRequest request) throws ExecutionException, InterruptedException {
		ResponseBuilder rb = getResponseBuilder(request);
		List<String> suggestions = new ArrayList<String>();

		String symptom = null;
		Object oSymptom = (Object) request.getParameter("symptom");

		System.out.println("oSymptom : [" + oSymptom + "]");

		// 증상 파악을 위해서 단계별로 접근 할 수 있어야 한다.
		// 화면이 들어올 경우 깜박임까지 합쳐서 다음 단계로 넘어 가야 한다.
		if (oSymptom != null && oSymptom instanceof String) {
			symptom = (String) oSymptom;
		}
		if (symptom == null || symptom.length() <= 0) {
			rb.add("화면이 어떻게 이상한가요?");
			suggestions.add("화면이 깜박여요");
			suggestions.add("화면이 안나와요");
		} else {
			// check status로 유도
			if (symptom.equalsIgnoreCase("sym1")) {
				rb.add("TV 케이블 연결은 어떻게 되어 있나요?");

				suggestions.add("HDMI로 연결");
				suggestions.add("컴포넌트로 연결");
			}
			if (symptom.equalsIgnoreCase("sym2")) {

			}
		}

		rb.addSuggestions(suggestions.toArray(new String[suggestions.size()]));

		return rb.build();
	}

	@ForIntent("support-check.status")
	public ActionResponse processCheckStatus(ActionRequest request) throws ExecutionException, InterruptedException {
		ResponseBuilder rb = getResponseBuilder(request);
		List<String> suggestions = new ArrayList<String>();

		ActionContext context = request.getContext("support-find-symptom");
		Map<String, Object> SymptomParas = context.getParameters();

		String symptom = null;
		Object oSymptom = (Object) SymptomParas.get("symptom");
		String connectionType = null;
		Object oConnectionType = (Object) request.getParameter("connectionType");

		System.out.println(oSymptom);
		System.out.println(oConnectionType);

		if (oSymptom != null && oSymptom instanceof String) {
			symptom = (String) oSymptom;
		}
		if (oConnectionType != null && oConnectionType instanceof String) {
			connectionType = (String) oSymptom;
		}

		// resolution으로 유도
		rb.add("알겠습니다. 다음과 같이 시도해 보세요. 케이블 연결을 다시 한번 확인해 보시겠습니까?");
		suggestions.add("잘되요");
		suggestions.add("여전히 이상해요");

		Map<String, Object> data = rb.getConversationData();
		data.remove("solution");
		data.put("solution", "1");

		rb.addSuggestions(suggestions.toArray(new String[suggestions.size()]));

		return rb.build();
	}

	@ForIntent("support-resolution.notwork")
	public ActionResponse processResolutionNotwork(ActionRequest request)
			throws ExecutionException, InterruptedException {
		ResponseBuilder rb = getResponseBuilder(request);
		List<String> suggestions = new ArrayList<String>();

		ActionContext context = request.getContext("support-find-symptom");
		Map<String, Object> SymptomParas = context.getParameters();

		String symptom = null;
		Object oSymptom = (Object) SymptomParas.get("symptom");
		String connectionType = null;
		Object oConnectionType = (Object) SymptomParas.get("connectionType");

		Map<String, Object> data = rb.getConversationData();
		int solution = 0;
		Object oSolution = data.get("solution");

		System.out.println(oSymptom);
		System.out.println(oConnectionType);
		System.out.println(oSolution);

		if (oSymptom != null && oSymptom instanceof String) {
			symptom = (String) oSymptom;
		}
		if (oConnectionType != null && oConnectionType instanceof String) {
			connectionType = (String) oSymptom;
		}
		if (oSolution != null && oSolution instanceof String) {
			solution = Integer.parseInt((String) oSolution);
		}

		if (solution == 1) {
			data.put("solution", "2");
			rb.add("알겠습니다. TV 전원을 다시 한번 껏다 켜보시겠어요?");
			suggestions.add("잘되요");
			suggestions.add("여전히 이상해요");
		} else if (solution == 2) {
			data.put("solution", "3");
			rb.add("알겠습니다. RF 케이블 연결 상태를 확인해 주시겠어요?");
			suggestions.add("잘되요");
			suggestions.add("여전히 이상해요");
		} else {
			data.put("solution", "-1");
			rb.add(new Confirmation().setConfirmationText("원격진단 이나 콜센터 연결 방법을 알려 드릴까요?"));
		}
		// resolution으로 유도
		rb.addSuggestions(suggestions.toArray(new String[suggestions.size()]));

		return rb.build();
	}

	@ForIntent("support-resolution.notwork-call.confirm")
	public ActionResponse processResolutionNotworkCallConfirm(ActionRequest request)
			throws ExecutionException, InterruptedException {
		ResponseBuilder rb = getResponseBuilder(request);

		boolean userConfirmation = request.getUserConfirmation();
		if (userConfirmation) {
			rb.add(new ActionContext("support-callcenter-followup", 5));
			rb.add("1588-0032 로 전화해 주시면 됩니다.\\n전화전에 스마트카드 번호를 확인해 주세요.\\n\\n 확인 방법을 알려 드릴까요?");
		} else {
			rb.add("도움이 못되어서 죄송합니다. 다음에 다시 만나요.");
		}
		return rb.build();
	}

	private String clientId = "<your_client_id>";

	@ForIntent("account-test")
	public ActionResponse processAccount(ActionRequest request) throws ExecutionException, InterruptedException {
		ResponseBuilder rb = getResponseBuilder(request);

		return rb.add("로그인 정보를 얻을 수 있을까요?").add(new SignIn().setContext("To get your account details")).build();
	}

	@ForIntent("account-test-process")
	public ActionResponse processAccountResult(ActionRequest request) throws ExecutionException, InterruptedException {
		ResponseBuilder responseBuilder = getResponseBuilder(request);
		if (request.isSignInGranted()) {
			String token = request.getUser().getIdToken();
			responseBuilder.add("I got your account details, " + token + ". What do you want to do next?");
		} else {
			responseBuilder.add("I won't be able to save your data, but what do you want to do next?");
		}
		return responseBuilder.build();
	}

	@ForIntent("to-mobile")
	public ActionResponse processToMobile(ActionRequest request) throws ExecutionException, InterruptedException {
		ResponseBuilder responseBuilder = getResponseBuilder(request);

		Map<String, Object> storage = request.getUserStorage();
		storage.put("testkey", "Good");

		String encodedUrl = null;
		try {
			encodedUrl = URLEncoder.encode(
					"https://assistant.google.com/services/invoke/uid/0000007ec4fe9129?intent=resume.link&param.pa1=good&param.pa2=bad",
					StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		System.out.println(encodedUrl);

		responseBuilder.add("다음 QR 코드를 모바일 장치로 찍으면 됩니다.")
		.add(new BasicCard().setTitle("QR코드를 통한 모바일 링크")
				.setFormattedText("다음 QR코드를 모바일에서 읽을 경우 Actions를 모바일에서 계속 하실 수 있습니다.")
				.setImage(new Image().setUrl("https://actions.o2o.kr/csnopy/api/1.0/qrcode?url=" + encodedUrl)
						.setAccessibilityText("모바일 장치 연결을 위한 QR코드"))
				.setImageDisplayOptions("DEFAULT"));

		return responseBuilder.build();
	}

	@ForIntent("resume.link")
	public ActionResponse processResumeLink(ActionRequest request) throws ExecutionException, InterruptedException {
		ResponseBuilder responseBuilder = getResponseBuilder(request);

		Map<String, Object> storage = request.getUserStorage();
		System.out.println(storage.get("testkey"));

		responseBuilder.add("계속 다시 하면 될듯 합니다.");
		return responseBuilder.build();
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
			responseBuilder.add("Ok, When time did you get up?");
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
		responseBuilder.add(request.getDateTime().toString());
		
		return responseBuilder.build();
	}




}
