package com.o2o.action.server.app;

import com.google.actions.api.ActionRequest;
import com.google.actions.api.ActionResponse;
import com.google.actions.api.DialogflowApp;
import com.google.actions.api.ForIntent;
import com.google.actions.api.response.ResponseBuilder;
import com.google.actions.api.response.helperintent.SelectionCarousel;
import com.google.actions.api.response.helperintent.SelectionList;
import com.google.api.services.actions_fulfillment.v2.model.*;
import com.google.api.services.dialogflow_fulfillment.v2.model.QueryResult;
import com.o2o.action.server.db.MealDetail;
import com.o2o.action.server.db.MealMenu;
import com.o2o.action.server.repo.DateMenuRepository;
import com.sun.javafx.animation.TickCalculation;
import org.springframework.beans.factory.annotation.Autowired;
import sun.util.resources.cldr.ar.CalendarData_ar_LB;

import javax.xml.bind.DatatypeConverter;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.*;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class ChefApp extends DialogflowApp {

    private DateMenuRepository menuRepository;

    public void setMenuRepository(DateMenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    @ForIntent("Menu")
    public ActionResponse process(ActionRequest request) throws ExecutionException, InterruptedException {
        ResponseBuilder responseBuilder = getResponseBuilder(request);
        QueryResult qr = request.getWebhookRequest().getQueryResult();
        Map<String, Object> params = qr.getParameters();

        String pDate = null;
        List<String> pTime = null;
        String pPlace = null;
        List<String> pPrice = null;

        if (params.get("Date") != null)
            pDate = (String) params.get("Date"); //파라미터 값
        if (params.get("Time") != null)
            pTime = (List<String>) params.get("Time");
        if (params.get("Place") != null)
            pPlace = (String) params.get("Place");
        if (params.get("Price") != null)
            pPrice = (List<String>) params.get("Price");


        Date tDate = null;

        if (pDate == null || pDate.length() == 0) {
            tDate = new Date(); // 오늘

        } else {
            tDate = DatatypeConverter.parseDateTime(pDate).getTime();
        }

        if (pPrice == null || pPrice.size() == 0) {
            pPrice.add("키친");
        }

        if (pTime == null || pTime.size() == 0) {
            pTime = new ArrayList<String>();
            System.out.println(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
            pTime.add("점심");
        }

        if (pPlace == null || pPlace.length() == 0) {
            pPlace = "기업지원허브";
        }


        List<MealMenu> menus = menuRepository.findByServiceDate(tDate);
        if (menus.size() > 0) {
            MealMenu menu = menus.get(0);
            StringBuffer responseText = new StringBuffer();
            //"해당 메뉴를 찾을 수 없습니다."
            if (menu != null) {
                System.out.println(menu);
                MealDetail launch1 = null;
                MealDetail launch2 = null;
                MealDetail dinner = null;

                for (MealDetail subMenu : menu.getMeals()) {
                    if (subMenu.getMealType() == MealDetail.MealType.LAUNCH_1) //델리
                        launch1 = subMenu;
                    if (subMenu.getMealType() == MealDetail.MealType.LAUNCH_2) //델리
                        launch2 = subMenu;
                    if (subMenu.getMealType() == MealDetail.MealType.DINNER) //델리
                        dinner = subMenu;
                }

                if (pTime.contains("점심")) {
                    responseText.append(" 점심 메뉴 알려드릴게요. ");
                    if (pPrice.contains("델리")) {
                        if (launch1 != null) {
                            responseText.append(" 델리 메뉴는 " + launch1.getMenu1() + ", " + launch1.getMenu2() +", " +launch1.getMenu3() + ", " +
                                    launch1.getMenu4() +", " +launch1.getMenu5() + ", " + launch1.getMenu6() + ", " + launch1.getMenu7() +
                                    ", 입니다.");
                        } else{
                            responseText.append(" 아직 등록된 델리 메뉴가 없습니다.");
                        }
                        responseText.append(" 종료를 원하시면 종료, 다른 메뉴를 원하시면 종류를 말씀 해 주세요");
                    } else if (pPrice.contains("키친")) {
                        if (launch2 != null) {
                            responseText.append(" 키친 메뉴는 " + launch2.getMenu1() + ", " + launch2.getMenu2() +", " +launch2.getMenu3() + ", " +
                                    launch2.getMenu4() +", " +launch2.getMenu5() + ", " + launch2.getMenu6() + ", " + launch2.getMenu7() +
                                    ", 입니다.");
                        }else{
                            responseText.append(" 아직 등록된 델리 메뉴가 없습니다.");
                        }
                        responseText.append(" 종료를 원하시면 종료, 다른 메뉴를 원하시면 종류를 말씀 해 주세요");
                    } else if (pPrice.contains("샐러드바")) {
                        responseText.append(" 샐러드바는 " + menu.getSalad1() + menu.getSalad2() + "입니다.");
                        responseText.append(" 종료를 원하시면 종료, 다른 메뉴를 원하시면 종류를 말씀 해 주세요");
                    } else if (pPrice.contains("후식")) {
                        responseText.append(" 후식은 " + menu.getDessert() + "입니다.");
                        responseText.append(" 종료를 원하시면 종료, 다른 메뉴를 원하시면 종류를 말씀 해 주세요");
                    }else {
                        responseText.append(" 델리 메뉴는 " + launch1.getMenu1() + ", " +launch1.getMenu2() + ", " + "키친 메뉴는 " +
                                launch2.getMenu2() + ", " + launch2.getMenu3() + "입니다. 상세 메뉴를 원하시면 델리나 키친과 같은 종류를 말씀 해 주시고 종료를 원하시면 종료라고 말씀해주세요.");
                    }
                }else if (pTime.contains("저녁")) {
                    responseText.append(" 저녁 메뉴 알려드릴게요. ");

                    if (dinner != null && pPrice.contains("키친")) {
                        responseText.append(" 저녁 메뉴는 " + dinner.getMenu1() + ", " + dinner.getMenu2() + ", " + dinner.getMenu3() +
                                ", " + dinner.getMenu4() + ", " + dinner.getMenu5() + ", " + dinner.getMenu6() + ", "+ dinner.getMenu7() + "입니다.");
                    }else{
                        responseText.append(" 아직 등록된 저녁 메뉴가 없습니다. 저녁에는 키친만 가능합니다. ");
                    }
                    responseText.append(" 종료를 원하시면 종료, 다른 메뉴를 원하시면 종류를 말씀 해 주세요");
                }

                String resertText = responseText.toString();

                resertText = resertText.replaceAll("&"," 그리고 ");

                responseBuilder.add(resertText);

            } else {
                responseBuilder.add(" 메뉴 검색에 오류가 발생하였습니다.");
            }

        } else {
            responseBuilder.add(" 등록된 메뉴가 없습니다.  종료를 원하시면 종료, 다른 메뉴를 원하시면 종류를 말씀 해 주세요 ");
        }
        return responseBuilder.build();
    }

    // 요일 및 날짜 마
}
