package com.o2o.action.server.app;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.google.actions.api.ActionContext;
import com.google.actions.api.ActionRequest;
import com.google.actions.api.ActionResponse;
import com.google.actions.api.DialogflowApp;
import com.google.actions.api.ForIntent;
import com.google.actions.api.response.ResponseBuilder;
import com.google.actions.api.response.helperintent.SelectionCarousel;
import com.google.api.services.actions_fulfillment.v2.model.BasicCard;
import com.google.api.services.actions_fulfillment.v2.model.CarouselSelectCarouselItem;
import com.google.api.services.actions_fulfillment.v2.model.Image;
import com.google.api.services.actions_fulfillment.v2.model.OptionInfo;
import com.google.api.services.actions_fulfillment.v2.model.SimpleResponse;

public class DefaultApp extends DialogflowApp {
	@ForIntent("support-find.symptom")
	public ActionResponse processFindSymptom(ActionRequest request) throws ExecutionException, InterruptedException {
		ResponseBuilder rb = getResponseBuilder(request);

		String symptom = null;
		Object oSymptom = (Object) request.getParameter("symptom");

		System.out.println("oSymptom : [" + oSymptom + "]");

		// 증상 파악을 위해서 단계별로 접근 할 수 있어야 한다.
		// 화면이 들어올 경우 깜박임까지 합쳐서 다음 단계로 넘어 가야 한다.
		if (oSymptom != null && oSymptom instanceof String) {
			symptom = (String) oSymptom;
		}

		return genFindSymptom(rb, symptom);
	}

	private ActionResponse genFindSymptom(ResponseBuilder rb, String symptom) {
		List<String> suggestions = new ArrayList<String>();
		List<CarouselSelectCarouselItem> items = new ArrayList<>();
		if (symptom == null || symptom.length() <= 0) {

			rb.add("화면이 어떻게 이상한가요?");
			suggestions.add("화면이 깜빡여요");
			suggestions.add("화면이 안나와요");

			BasicCard basicCard = new BasicCard();

			basicCard.setTitle("Skylife 서비스센터 AI 상담원").setFormattedText("화면이 어떻게 이상하신지 말씀해주세요.");
			basicCard.setImage(new Image().setUrl("https://actions.o2o.kr/content/servicecenter/skylifeaiperson.gif")
					.setAccessibilityText("Skylife 서비스센터 AI 상담원 이미지"));

			rb.add(basicCard);

		} else {
			Map<String, Object> data = rb.getConversationData();
			data.put("symptom", symptom);

			// check status로 유도
			if (symptom.equalsIgnoreCase("sym1")) {

				rb.add("TV 케이블 연결은 어떻게 되어 있나요?");

				CarouselSelectCarouselItem item1, item2;

				List<String> synonyms1 = new ArrayList<String>();
				synonyms1.add("컴포넌트");

				item1 = new CarouselSelectCarouselItem().setTitle("컴포넌트").setDescription("컴포넌트 케이블")
						.setOptionInfo(new OptionInfo().setKey("con2").setSynonyms(synonyms1));
				item1.setImage(new Image().setUrl("https://actions.o2o.kr/content/servicecenter/cablecomponent.jpg")
						.setAccessibilityText("컴포넌트케이블이미지"));
				items.add(item1);

				List<String> synonyms2 = new ArrayList<String>();
				synonyms2.add("HDMI");

				item2 = new CarouselSelectCarouselItem().setTitle("HDMI").setDescription("HDMI 케이블")
						.setOptionInfo(new OptionInfo().setKey("con1").setSynonyms(synonyms2));

				item2.setImage(new Image().setUrl("https://actions.o2o.kr/content/servicecenter/cablehdmi.jpg")
						.setAccessibilityText("hdmi케이블이미지"));

				items.add(item2);

				rb.add(new SelectionCarousel().setItems(items));

				suggestions.add("컴포넌트");
				suggestions.add("HDMI");

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
		Map<String, Object> data = rb.getConversationData();

		String symptom = null;
		Object oSymptom = (Object) data.get("symptom");
		Object oConnectionType = (Object) request.getParameter("connectionType");

		String connectionType = null;
		connectionType = request.getSelectedOption();

		if (oSymptom != null && oSymptom instanceof String) {
			symptom = (String) oSymptom;
		}

		if (connectionType == null) {
			if (oConnectionType != null && oConnectionType instanceof String) {
				connectionType = (String) oConnectionType;
			}
		}
		data.put("connectionType", connectionType);
		data.put("solution", "1");

		System.out.println(symptom);
		System.out.println(connectionType);

		return genCheckStatus(rb, symptom, connectionType);
	}

	public ActionResponse genCheckStatus(ResponseBuilder rb, String symptom, String connectionType) {
		List<String> suggestions = new ArrayList<String>();

		// resolution으로 유도

		BasicCard basicCard = new BasicCard();

		SimpleResponse simpleResponse = new SimpleResponse();

		//
		if (connectionType.equalsIgnoreCase("con2")) {
			simpleResponse.setTextToSpeech(
					"<speak>알겠습니다. 다음과 같이 컴포넌트 케이블 연결을 다시 한번 확인해 보시겠습니까? <audio src = 'https://actions.o2o.kr/content/servicecenter/componentcheck.mp3'></audio></speak>");
			basicCard.setTitle("컴포넌트 연결확인 방법").setFormattedText("컴포넌트 연결확인 방법입니다.");
			basicCard.setImage(new Image().setUrl("https://actions.o2o.kr/content/servicecenter/componentcheck.gif")
					.setAccessibilityText("컴포넌트연결확인방법 이미지"));

		} else { // if (connectionType.equalsIgnoreCase("con1")
			simpleResponse.setTextToSpeech(
					"<speak> 알겠습니다. 다음과 같이 HDMI 케이블 연결을 다시 한번 확인해 보시겠습니까? <audio src = 'https://actions.o2o.kr/content/servicecenter/hdmicheck.mp3'></audio></speak>");
			basicCard.setTitle("HDMI 연결확인 방법").setFormattedText("HDMI 연결확인 방법입니다.");
			basicCard.setImage(new Image().setUrl("https://actions.o2o.kr/content/servicecenter/hdmicheck.gif")
					.setAccessibilityText("HDMI연결확인방법 이미지"));
		}

		rb.add(simpleResponse);
		rb.add(basicCard);

		suggestions.add("잘돼요");
		suggestions.add("여전히 이상해요");

		rb.addSuggestions(suggestions.toArray(new String[suggestions.size()]));

		return rb.build();
	}

	@ForIntent("support-resolution.notwork")
	public ActionResponse processResolutionNotwork(ActionRequest request)
			throws ExecutionException, InterruptedException {
		ResponseBuilder rb = getResponseBuilder(request);

		Map<String, Object> data = rb.getConversationData();

		String symptom = null;
		Object oSymptom = (Object) data.get("symptom");
		String connectionType = null;
		Object oConnectionType = (Object) data.get("connectionType");
		int solution = 0;
		Object oSolution = data.get("solution");

		if (oSymptom != null && oSymptom instanceof String) {
			symptom = (String) oSymptom;
		}
		if (oConnectionType != null && oConnectionType instanceof String) {
			connectionType = (String) oConnectionType;
		}
		if (oSolution != null && oSolution instanceof String) {
			solution = Integer.parseInt((String) oSolution);
		}
		System.out.println(symptom);
		System.out.println(connectionType);
		System.out.println(solution);

		return genResolutionNotwork(rb, symptom, connectionType, solution);
	}

	public ActionResponse genResolutionNotwork(ResponseBuilder rb, String symptom, String connectionType,
			int solution) {
		List<String> suggestions = new ArrayList<String>();
		BasicCard basicCard = new BasicCard();
		SimpleResponse simpleResponse = new SimpleResponse();
		Map<String, Object> data = rb.getConversationData();

		if (solution == 1) {
			data.put("solution", "2");

			simpleResponse.setTextToSpeech(
					"<speak>알겠습니다. 다음과 같이 TV 전원을 다시 껏다 켜보시겠습니까? <audio src ='https://actions.o2o.kr/content/servicecenter/suggesttvonoff.mp3'></audio></speak>");

			basicCard.setTitle("TV 전원껏다켜는 방법").setFormattedText("TV 전원을 다시 껏다 켜는 방법입니다.");
			basicCard.setImage(new Image().setUrl("https://actions.o2o.kr/content/servicecenter/suggesttvonoff.gif")
					.setAccessibilityText("TV 전원껏다켜는 방법 이미지"));

			suggestions.add("잘되요");
			suggestions.add("다시 연결했는데도 이상해요");
			rb.add(simpleResponse);
			rb.add(basicCard);
		} else if (solution == 2) {
			data.put("solution", "3");
			simpleResponse.setTextToSpeech(
					"<speak>알겠습니다. RF 케이블 연결을 다시 한번 확인해 보시겠습니까? <audio src = 'https://actions.o2o.kr/content/servicecenter/suggestrfcable.mp3'></audio></speak>");

			basicCard.setTitle(" RF 케이블 연결 확인하는 방법").setFormattedText("RF 케이블 연결 확인하는 방법입니다.");
			basicCard.setImage(new Image().setUrl("https://actions.o2o.kr/content/servicecenter/suggestrfcable.gif")
					.setAccessibilityText("RF 케이블 연결 확인하는 방법 이미지"));

			suggestions.add("잘돼요");
			suggestions.add("여전히 이상해요");
			rb.add(simpleResponse);
			rb.add(basicCard);
		} else {
			data.put("solution", "-1");
			rb.add("원격진단 이나 콜센터 연결 방법을 알려 드릴까요?");

			basicCard.setTitle("Skylife 서비스센터 AI 상담원").setFormattedText("화면이 어떻게 이상하신지 말씀해주세요.");
			basicCard.setImage(new Image().setUrl("https://actions.o2o.kr/content/servicecenter/skylifeaiperson.gif")
					.setAccessibilityText("Skylife 서비스센터 AI 상담원 이미지"));
			suggestions.add("원격진단 방법");
			suggestions.add("콜센터 연결방법");

			rb.add(basicCard);
		}
		// resolution으로 유도

		rb.addSuggestions(suggestions.toArray(new String[suggestions.size()]));

		return rb.build();
	}

	@ForIntent("support-ascontrol")
	public ActionResponse processSupportAscontrol(ActionRequest request)
			throws ExecutionException, InterruptedException {
		ResponseBuilder rb = getResponseBuilder(request);
		List<String> suggestions = new ArrayList<String>();

		// boolean userConfirmation = request.getUserConfirmation();

		BasicCard basicCard = new BasicCard();
		SimpleResponse simpleResponse = new SimpleResponse();

		String cfinal = null;

		Object oFinal = (Object) request.getParameter("Ent_final");
		if (oFinal != null && oFinal instanceof String) {
			cfinal = (String) oFinal;
		}

		if (cfinal == null || cfinal.length() <= 0) {
			rb.add("콜센터 연결 방법 중 무엇을 도와드릴까요? ");

			basicCard.setTitle("Skylife 서비스센터 AI 상담원").setFormattedText("원하시는 콜센터 연결 방법을 선택해 주세요.");
			basicCard.setImage(new Image().setUrl("https://actions.o2o.kr/content/servicecenter/skylifeaiperson.gif")
					.setAccessibilityText("Skylife 서비스센터 AI 상담원 이미지"));

			rb.add(basicCard);
			suggestions.add("홈으로 돌아가기");
			suggestions.add("원격진단 방법");
			suggestions.add("콜센터 연결방법");
			suggestions.add("스마트카드번호 확인방법");

		} else {
			// check status로 유도
			if (cfinal.equalsIgnoreCase("ascontrol")) {
				simpleResponse.setTextToSpeech(
						"<speak>네 원격진단 연결하는 방법을 설명해 드릴게요.  <audio src = 'https://actions.o2o.kr/content/servicecenter/ascontrol.mp3'></audio></speak>");
				basicCard.setTitle("원격진단 방법").setFormattedText("원격으로 진단하는 방법입니다.");
				basicCard.setImage(new Image().setUrl("https://actions.o2o.kr/content/servicecenter/ascontrol.gif")
						.setAccessibilityText("원격진단 방법 이미지"));

				suggestions.add("홈으로 돌아가기");
				suggestions.add("콜센터 연결방법");
				suggestions.add("스마트카드번호 확인방법");

			} else if (cfinal.equalsIgnoreCase("callcenter")) {
				simpleResponse.setTextToSpeech(
						"<speak>네 콜센터 연결하는 방법을 설명해 드릴게요. <audio src = 'https://actions.o2o.kr/content/servicecenter/callcenter.mp3'></audio></speak>");
				basicCard.setTitle("콜센터 연결방법").setFormattedText("콜센터 연결하는 방법입니다.");
				basicCard.setImage(new Image().setUrl("https://actions.o2o.kr/content/servicecenter/callcenter.gif")
						.setAccessibilityText("콜센터 연결방법 이미지"));
				suggestions.add("홈으로 돌아가기");
				suggestions.add("원격진단 방법");

			} else if (cfinal.equalsIgnoreCase("smartcardno")) {
				simpleResponse.setTextToSpeech(
						"<speak>네 스마트카드번호를 확인하는 방법을 설명해 드릴게요. <audio src = 'https://actions.o2o.kr/content/servicecenter/smartcard.mp3'></audio></speak>");
				basicCard.setTitle("스마트카드번호 확인방법").setFormattedText("스마트카드번호 확인하는 방법입니다.");
				basicCard.setImage(new Image().setUrl("https://actions.o2o.kr/content/servicecenter/smartcard.gif")
						.setAccessibilityText("스마트카드번호 확인방법 이미지"));
				suggestions.add("홈으로 돌아가기");
				suggestions.add("원격진단 방법");
			}
			rb.add(simpleResponse);
			rb.add(basicCard);

		}

		rb.addSuggestions(suggestions.toArray(new String[suggestions.size()]));

		return rb.build();
	}

	@ForIntent("support-mobile")
	public ActionResponse processToMobile(ActionRequest request) throws ExecutionException, InterruptedException {
		ResponseBuilder rb = getResponseBuilder(request);

		Map<String, Object> data = rb.getConversationData();

		String symptom = null;
		Object oSymptom = (Object) data.get("symptom");
		String connectionType = null;
		Object oConnectionType = (Object) data.get("connectionType");
		int solution = 0;
		Object oSolution = data.get("solution");

		if (oSymptom != null && oSymptom instanceof String) {
			symptom = (String) oSymptom;
		}
		if (oConnectionType != null && oConnectionType instanceof String) {
			connectionType = (String) oConnectionType;
		}
		if (oSolution != null && oSolution instanceof String) {
			solution = Integer.parseInt((String) oSolution);
		}
		System.out.println(symptom);
		System.out.println(connectionType);
		System.out.println(solution);

		Map<String, Object> storage = request.getUserStorage();
		storage.put("sym", symptom);
		storage.put("con", connectionType);
		storage.put("sol", solution);
		System.out.println("solution");

		String encodedUrl = null;
		try {
			encodedUrl = URLEncoder.encode(
					"https://assistant.google.com/services/invoke/uid/000000ed4bb85dee?intent=support-resume",
					StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		System.out.println(encodedUrl);

		rb.add("다음 QR 코드를 모바일 장치로 찍으면 됩니다.")
				.add(new BasicCard().setTitle("QR코드를 통한 모바일 링크")
						.setFormattedText("다음 QR코드를 모바일에서 읽을 경우 Actions를 모바일에서 계속 하실 수 있습니다.")
						.setImage(new Image().setUrl("https://actions.o2o.kr/csnopy/api/1.0/qrcode?url=" + encodedUrl)
								.setAccessibilityText("모바일 장치 연결을 위한 QR코드"))
						.setImageDisplayOptions("DEFAULT"));

		return rb.build();
	}

	@ForIntent("support-resume")
	public ActionResponse processResume(ActionRequest request) throws ExecutionException, InterruptedException {
		ResponseBuilder rb = getResponseBuilder(request);
		Map<String, Object> storage = request.getUserStorage();

		String symptom = null;
		Object oSymptom = (Object) storage.get("sym");
		String connectionType = null;
		Object oConnectionType = (Object) storage.get("con");
		int solution = 0;
		Object oSolution = storage.get("sol");

		if (oSymptom != null && oSymptom instanceof String) {
			symptom = (String) oSymptom;
		}
		if (oConnectionType != null && oConnectionType instanceof String) {
			connectionType = (String) oConnectionType;
		}
		if (oSolution != null && oSolution instanceof String) {
			try {
				solution = Integer.parseInt((String) oSolution);
			} catch (Exception e) {

			}
		}

		System.out.println(symptom);
		System.out.println(connectionType);
		System.out.println(solution);
		
		//
		
		ActionContext context = new ActionContext("support-find-symptom", 10);
		rb.add(context);
		
		Map<String, Object> data = rb.getConversationData();

		data.put("symptom", symptom);
		data.put("connectionType", connectionType);
		data.put("solution", solution);

		if (solution != 0) {
			genResolutionNotwork(rb, symptom, connectionType, solution);
		} else if (connectionType != null && connectionType.length() > 0) {
			genCheckStatus(rb, symptom, connectionType);
		} else if (symptom != null && symptom.length() > 0) {
			genFindSymptom(rb, symptom);
		} else {
			genFindSymptom(rb, symptom);
		}

		//rb.add("계속 하기 위해 노력 중입니다.");
		return rb.build();
	}
}
