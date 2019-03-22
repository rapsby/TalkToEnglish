package com.o2o.action.server.app;

import com.google.actions.api.*;
import com.google.actions.api.response.ResponseBuilder;
import com.google.actions.api.response.helperintent.Confirmation;
import com.o2o.action.server.repo.CategoryRepository;
import com.o2o.action.server.repo.ChannelRepository;
import com.o2o.action.server.repo.ScheduleRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class GogumaApp extends DialogflowApp {
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
            //check status로 유도
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

        //resolution으로 유도
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
    public ActionResponse processResolutionNotwork(ActionRequest request) throws ExecutionException, InterruptedException {
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
        //resolution으로 유도
        rb.addSuggestions(suggestions.toArray(new String[suggestions.size()]));

        return rb.build();
    }

    @ForIntent("support-resolution.notwork-call.confirm")
    public ActionResponse processResolutionNotworkCallConfirm(ActionRequest request) throws ExecutionException, InterruptedException {
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
}
