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
import com.google.actions.api.response.helperintent.Confirmation;
import com.google.actions.api.response.helperintent.SelectionCarousel;
import com.google.api.services.actions_fulfillment.v2.model.*;

import javax.xml.bind.DatatypeConverter;

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
			data.clear();
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

		System.out.println(symptom);
		System.out.println(connectionType);

		return genResolutionNotwork(rb, symptom, connectionType, 1);
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
			solution++;
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

		data.put("solution", Integer.toString(solution));
		if (solution == 1) {
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
			suggestions.add("잘돼요");
			suggestions.add("여전히 이상해요");
			rb.add(simpleResponse);
			rb.add(basicCard);
		} else if (solution == 2) {
			simpleResponse.setTextToSpeech(
					"<speak>알겠습니다. 다음과 같이 TV 전원을 다시 껏다 켜보시겠습니까? <audio src ='https://actions.o2o.kr/content/servicecenter/suggesttvonoff.mp3'></audio></speak>");

			basicCard.setTitle("TV 전원껏다켜는 방법").setFormattedText("TV 전원을 다시 껏다 켜는 방법입니다.");
			basicCard.setImage(new Image().setUrl("https://actions.o2o.kr/content/servicecenter/suggesttvonoff.gif")
					.setAccessibilityText("TV 전원껏다켜는 방법 이미지"));

			suggestions.add("잘돼요");
			suggestions.add("다시 연결했는데도 이상해요");
			rb.add(simpleResponse);
			rb.add(basicCard);
		} else if (solution == 3) {
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

		String encodedUrl = null;
		try {
			encodedUrl = URLEncoder.encode(
					"https://assistant.google.com/services/invoke/uid/000000ed4bb85dee?intent=support-resume&param.pa1="
							+ symptom + "&param.pa2=" + connectionType + "&param.pa3=" + solution,
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

		String symptom = null;
		Object oSymptom = (Object) request.getParameter("pa1");
		String connectionType = null;
		Object oConnectionType = (Object) request.getParameter("pa2");
		int solution = 0;
		Object oSolution = request.getParameter("pa3");

		if (oSymptom != null && oSymptom instanceof String) {
			symptom = (String) oSymptom;
			if (symptom.equalsIgnoreCase("null")) {
				symptom = null;
			}
		}
		if (oConnectionType != null && oConnectionType instanceof String) {
			connectionType = (String) oConnectionType;
			if (connectionType.equalsIgnoreCase("null"))
				connectionType = null;
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
			genResolutionNotwork(rb, symptom, connectionType, 1);
		} else if (symptom != null && symptom.length() > 0) {
			genFindSymptom(rb, symptom);
		} else {
			genFindSymptom(rb, symptom);
		}

		// rb.add("계속 하기 위해 노력 중입니다.");
		return rb.build();
	}


	@ForIntent("English - topic")
	public ActionResponse processEnglishTopic(ActionRequest request) throws ExecutionException, InterruptedException {
		ResponseBuilder rb = getResponseBuilder(request);
		Map<String, Object> data = rb.getConversationData();

		List<String> suggestions = new ArrayList<String>();

		Object oTopic = (Object) request.getParameter("topic");

		String selectTopic = null;
		selectTopic = request.getSelectedOption();

		if (selectTopic == null) {
			if (oTopic != null && oTopic instanceof String) {
				selectTopic = (String) oTopic;
				System.out.println(selectTopic);
			}
		}

		data.put("topicType", selectTopic);

		SimpleResponse simpleResponse = new SimpleResponse();
		BasicCard basicCard = new BasicCard();

		if(selectTopic.equals("daily routine")){
			simpleResponse.setTextToSpeech("<speak><p><s>Well... I respect your choice.</s><s>Let's start talk about daily routine.</s><s>Did you wake up early today?</s></p></speak>");
			basicCard.setTitle("Daily routine").setFormattedText("Did you wake up early today?  \n오늘은 일찍 일어나셨나요?");
			basicCard.setImage(new Image().setUrl("https://actions.o2o.kr/content/servicecenter/skylifeaiperson.gif")
					.setAccessibilityText("daily routine image"));
			suggestions.add("No");
			suggestions.add("Yes");
			suggestions.add("Stop talking");
		}else if(selectTopic.equals("personality")){
			simpleResponse.setTextToSpeech("<speak><p><s>Well... I respect your choice.</s><s>Let's start talk about daily routine.</s><s>Did you wake up early today?</s></p></speak>");
			basicCard.setTitle("personality").setFormattedText("오늘은 일찍 일어나셨나요?");
			basicCard.setImage(new Image().setUrl("https://actions.o2o.kr/content/servicecenter/skylifeaiperson.gif")
					.setAccessibilityText("daily routine image"));
			suggestions.add("No");
			suggestions.add("Yes");
			suggestions.add("Stop talking");
		}else if(selectTopic.equalsIgnoreCase("free time")){
			simpleResponse.setTextToSpeech("<speak><p><s>Well... I respect your choice.</s><s>Let's start talk about daily routine.</s><s>Did you wake up early today?</s></p></speak>");
			basicCard.setTitle("free time").setFormattedText("오늘은 일찍 일어나셨나요?");
			basicCard.setImage(new Image().setUrl("https://actions.o2o.kr/content/servicecenter/skylifeaiperson.gif")
					.setAccessibilityText("daily routine image"));
			suggestions.add("No");
			suggestions.add("Yes");
			suggestions.add("Stop talking");
		}else if(selectTopic.equalsIgnoreCase("pop culture")){
			simpleResponse.setTextToSpeech("<speak><p><s>Well... I respect your choice.</s><s>Let's start talk about daily routine.</s><s>Did you wake up early today?</s></p></speak>");
			basicCard.setTitle("pop culture").setFormattedText("오늘은 일찍 일어나셨나요?");
			basicCard.setImage(new Image().setUrl("https://actions.o2o.kr/content/servicecenter/skylifeaiperson.gif")
					.setAccessibilityText("daily routine image"));
			suggestions.add("No");
			suggestions.add("Yes");
			suggestions.add("Stop talking");
		}else{
			simpleResponse.setTextToSpeech("<speak>Sorry, I don't understand. Please, choose topic again correctly!</speak>");
			basicCard.setTitle("No Topic").setFormattedText("Sorry, I don't understand. Please, choose topic again correctly!  \n죄송해요, 이해하지 못했어요. 다시 주제를 정확하게 선택해주세요.");
			basicCard.setImage(new Image().setUrl("https://actions.o2o.kr/content/servicecenter/skylifeaiperson.gif")
					.setAccessibilityText("daily routine image"));
			suggestions.add("daily routine");
			suggestions.add("personality");
			suggestions.add("free time");
			suggestions.add("pop culture");
			suggestions.add("Stop talking");
			data.clear();
		}

		rb.add(simpleResponse);
		rb.add(basicCard);
		rb.addSuggestions(suggestions.toArray(new String[suggestions.size()]));

		return rb.build();
	}

	@ForIntent("English - topic - say")
	public ActionResponse englishTopicYes (ActionRequest request) throws ExecutionException, InterruptedException {
		ResponseBuilder rb = getResponseBuilder(request);
		Map<String, Object> data = rb.getConversationData();

		//	boolean userConfirmation = request.getUserConfirmation();

		String topic = null;
		Object oTopic = (Object) data.get("topicType");
		Object oTime = (Object) request.getParameter("time");

		String answer = null;
		int sceen = 0;
		Object osceen = (Object) data.get("sceen");

		if (oTopic != null && oTopic instanceof String) {
			topic = (String) oTopic;
		}
		if (osceen != null && osceen instanceof String) {
			sceen = Integer.parseInt((String) osceen);
		}


		if(oTime != null && oTime instanceof String){
			answer = (String) oTime;
		}

		System.out.println(request.getRawText());
		System.out.println(topic);
		System.out.println(sceen);

		return getEnglishsceentext(request, rb, topic, sceen, request.getRawText());
	}

	public ActionResponse getEnglishsceentext(ActionRequest request,ResponseBuilder rb, String topic, int sceen, String answer) {
		List<String> suggestions = new ArrayList<String>();
		BasicCard basicCard = new BasicCard();

		SimpleResponse simpleResponse = new SimpleResponse();
		Map<String, Object> data = rb.getConversationData();
		sceen++;

		data.put("sceen", Integer.toString(sceen));

		System.out.println(topic);
		System.out.println(sceen);
		System.out.println(answer);


		if (topic.contains("daily routine") ) {
			if(sceen == 1) {
				if(answer.contains("Yes")||answer.contains("yes")||answer.contains("early")){
					simpleResponse.setTextToSpeech(
							"<speak><p><s>Oh really?</s><s>Don't you get up early and don't get tired?</s></p></speak>");
					basicCard.setTitle("Daily routine").setFormattedText("Oh really? Don't you get up early and don't get tired?  \n정말요? 일찍 일어나서 피곤하시진 않으세요?");
					basicCard.setImage(new Image().setUrl("https://actions.o2o.kr/content/servicecenter/skylifeaiperson.gif")
							.setAccessibilityText("Did you wake up early today1 image"));
				}else{
					simpleResponse.setTextToSpeech(
							"<speak><p><s>Are you sure?</s><s>Did you mind, if you overslept?</s></p></speak>");
					basicCard.setTitle("Daily routine").setFormattedText("Are you sure? Did you mind, if you overslept?  \n정말인가요? 늦잠자도 괜찮으신거에요?");
					basicCard.setImage(new Image().setUrl("https://actions.o2o.kr/content/servicecenter/skylifeaiperson.gif")
							.setAccessibilityText("Did you wake up early today2 image"));
				}
				rb.add(simpleResponse);
				rb.add(basicCard);
				suggestions.add("No");
				suggestions.add("Yes");
			}else if(sceen == 2){
				if(answer.contains("Yes")||answer.contains("yes")||!answer.contains("not")){
					simpleResponse.setTextToSpeech(
							"<speak><p><s>I'm glad you're okay.</s><s>Do you like rain?</s></p></speak>");
					basicCard.setTitle("Daily routine").setFormattedText("I'm glad you're okay. Do you like rain?  \n괜찮다니 다행이네요. 혹시 비 내리는거 좋아하세요?");
					basicCard.setImage(new Image().setUrl("https://actions.o2o.kr/content/servicecenter/skylifeaiperson.gif")
							.setAccessibilityText("Did you wake up early today3 image"));
				}else{
					simpleResponse.setTextToSpeech(
							"<speak><p><s>I'm sorry to hear that.</s><s>Do you like rain?'</s></p></speak>");
					basicCard.setTitle("Daily routine").setFormattedText("I'm sorry to hear that. Do you like rain?  \n유감이네요. 혹시 비 내리는거 좋아하세요?");
					basicCard.setImage(new Image().setUrl("https://actions.o2o.kr/content/servicecenter/skylifeaiperson.gif")
							.setAccessibilityText("Did you wake up early today4 image"));
				}
				rb.add(simpleResponse);
				rb.add(basicCard);
				suggestions.add("No");
				suggestions.add("Yes");
			}else if(sceen == 3){
				if((answer.contains("Yes")||answer.contains("yes"))||answer.equalsIgnoreCase("I like it")){
					simpleResponse.setTextToSpeech(
							"<speak><p><s>That's great! I personally like the sound of rain drops.</s><s>Do you wear any rain boots?'</s></p></speak>");
					basicCard.setTitle("Daily routine").setFormattedText("That's great! I personally like the sound of rain drops. Do you wear any rain boots?  \n멋져요! 저는 개인적으로 빗 방울소리 듣는걸 좋아해요. 혹시 장화를 신기도 하나요?");
					basicCard.setImage(new Image().setUrl("https://actions.o2o.kr/content/servicecenter/skylifeaiperson.gif")
							.setAccessibilityText("Did you wake up early today5 image"));

				}else{
					simpleResponse.setTextToSpeech(
							"<speak><p><s>That's too bad.. I personally like the sound of rain drops.</s><s>Do you wear any rain boots?'</s><s><sub alias = ''>아쉽네요.. 저는 개인적으로 빗 방울소리 듣는걸 좋아해요. 혹시 장화를 신기도 하나요?</sub></s></p></speak>");
					basicCard.setTitle("Daily routine").setFormattedText("That's too bad.. I personally like the sound of rain drops. Do you wear any rain boots?  \n아쉽네요.. 저는 개인적으로 빗 방울소리 듣는걸 좋아해요. 혹시 장화를 신기도 하나요?");
					basicCard.setImage(new Image().setUrl("https://actions.o2o.kr/content/servicecenter/skylifeaiperson.gif")
							.setAccessibilityText("Did you wake up early today6 image"));

				}
				rb.add(simpleResponse);
				rb.add(basicCard);
				suggestions.add("No");
				suggestions.add("Yes");
			}else if(sceen == 4){
				if((answer.contains("No")||answer.contains("no")) || answer.equalsIgnoreCase("I don't have")){
					simpleResponse.setTextToSpeech(
							"<speak><p><s>Oh really? You want need to worry about getting wet on rainy days then.</s><s>I'm afraid this is the last time we talk.</s></p></speak>");
					basicCard.setTitle("Daily routine").setFormattedText("Oh really? You want need to worry about getting wet on rainy days then.  \nI'm afraid this is the last time we talk  \n아 정말요? 비오는 날 젖을 까봐 걱정 되겠어요.  \n아쉽지만 이번 대화는 여기가 마지막이에요.");
					basicCard.setImage(new Image().setUrl("https://actions.o2o.kr/content/servicecenter/skylifeaiperson.gif")
							.setAccessibilityText("Did you wake up early today7 image"));

				}else{
					simpleResponse.setTextToSpeech(
							"<speak><p><s>Nice, You won't need to worry about getting wet on rainy days then.</s><s>I'm afraid this is the last time we talk.</s></p></speak>");
					basicCard.setTitle("Daily routine").setFormattedText("Nice, You won't need to worry about getting wet on rainy days then.  \nI'm afraid this is the last time we talk.  \n좋네요, 비오는 날 젖는건 걱정 하지 않아도 되겠어요.  \n아쉽지만 이번 대화는 여기가 마지막이에요.");
					basicCard.setImage(new Image().setUrl("https://actions.o2o.kr/content/servicecenter/skylifeaiperson.gif")
							.setAccessibilityText("Did you wake up early today8 image"));

				}
				rb.add(simpleResponse);
				rb.add(basicCard);
				data.clear();
			}
			suggestions.add("Stop talking");
		}

		// resolution으로 유도
		rb.addSuggestions(suggestions.toArray(new String[suggestions.size()]));

		return rb.build();
	}
}
