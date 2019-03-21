package com.o2o.action.server.app;

import com.google.actions.api.ActionRequest;
import com.google.actions.api.ActionResponse;
import com.google.actions.api.DialogflowApp;
import com.google.actions.api.ForIntent;
import com.google.actions.api.response.ResponseBuilder;
import com.google.actions.api.response.helperintent.SelectionCarousel;
import com.google.api.client.util.Lists;
import com.google.api.services.actions_fulfillment.v2.model.BasicCard;
import com.google.api.services.actions_fulfillment.v2.model.CarouselSelectCarouselItem;
import com.google.api.services.actions_fulfillment.v2.model.Image;
import com.google.api.services.actions_fulfillment.v2.model.OptionInfo;
import com.google.api.services.dialogflow_fulfillment.v2.model.QueryResult;
import com.o2o.action.server.DBInit;
import com.o2o.action.server.db.Category;
import com.o2o.action.server.db.Channel;
import com.o2o.action.server.db.Schedule;
import com.o2o.action.server.repo.CategoryRepository;
import com.o2o.action.server.repo.ChannelRepository;
import com.o2o.action.server.repo.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class DefaultApp extends DialogflowApp {
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

    @ForIntent("Channel")
    public ActionResponse processChannel(ActionRequest request) throws ExecutionException, InterruptedException {
        ResponseBuilder responseBuilder = getResponseBuilder(request);
        List<String> suggestions = new ArrayList<String>();
        suggestions.add("채널로 이동");

        QueryResult qr = request.getWebhookRequest().getQueryResult();

        String eChannel = null;

        if (qr != null) {
            Map<String, Object> params = qr.getParameters();
            eChannel = (String) params.get("Ent_channelname");
        }

        System.out.println(eChannel);

        if (eChannel == null) {
            processError(responseBuilder, suggestions, "방송 정보가 없습니다.");
            return responseBuilder.build();
        }

        List<Channel> channels = Lists.newArrayList(channelRepository.findByChName(eChannel));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmm");
        String tmpStr = formatter.format(new Date()) + "00";

        long curTime = Long.parseLong(tmpStr) + 90000;
        System.out.println(curTime);

        for (Channel channel : channels) {
            List<Schedule> schedules = scheduleRepository.findByChannelAndStartTimeLessThanEqualAndEndTimeGreaterThan(channel, curTime, curTime);
            for (Schedule schedule : schedules) {
                responseBuilder.add(schedule.getName());
            }
        }
        return responseBuilder.build();
    }


    @ForIntent("Shopping")
    public ActionResponse processShop(ActionRequest request) throws ExecutionException, InterruptedException {
        ResponseBuilder responseBuilder = getResponseBuilder(request);
        List<String> suggestions = new ArrayList<String>();
        suggestions.add("쇼핑으로 이동");

        Category root = null;
        List<Category> roots = categoryRepository.findByKeycodeOrderByDispOrderAsc(DBInit.KEYCODE_SHOPPING_ROOT);

        if (roots != null && roots.size() > 0) {
            root = roots.get(0);
            System.out.println(root.getId() + "," + root.getKeycode());
        }
        if (root != null)
            processRootCategories(responseBuilder, suggestions, root);
        else
            processError(responseBuilder, suggestions, "쇼핑 정보를 읽어올 수 없습니다.");

        return responseBuilder.build();
    }

    @ForIntent("Support")
    public ActionResponse processSupport(ActionRequest request) throws ExecutionException, InterruptedException {
        ResponseBuilder responseBuilder = getResponseBuilder(request);
        List<String> suggestions = new ArrayList<String>();
        suggestions.add("홈으로 돌아가기");

        Category root = null;
        List<Category> roots = categoryRepository.findByKeycodeOrderByDispOrderAsc(DBInit.KEYCODE_CUSTOMER_SERVICE_ROOT);

        if (roots != null && roots.size() > 0) {
            root = roots.get(0);
            System.out.println(root.getId() + "," + root.getKeycode());
        }

        if (root != null)
            processRootCategories(responseBuilder, suggestions, root);
        else
            processError(responseBuilder, suggestions, "고객 서비스 정보를 읽어올 수 없습니다.");

        return responseBuilder.build();
    }

    @ForIntent("Category - option")
    public ActionResponse processCategoryOption(ActionRequest request) throws ExecutionException, InterruptedException {
        ResponseBuilder responseBuilder = getResponseBuilder(request);
        List<String> suggestions = new ArrayList<String>();
        suggestions.add("홈으로 돌아가기");

        long parentId = 0;
        String selectedItem = request.getSelectedOption();
        if (selectedItem != null) {
            parentId = Long.parseLong(selectedItem);
        } else {
            processError(responseBuilder, suggestions, "선택 정보가 없습니다.");
            return responseBuilder.build();
        }

        System.out.println("selectedItem : " + parentId);

        Optional<Category> current = categoryRepository.findById(parentId);
        if (current.isPresent()) {
            Category parent = current.get();

            Map<String, Object> data = request.getConversationData();
            data.put("currentItem", Long.toString(parentId));

            if (parent.getChildren() == null || parent.getChildren().size() <= 1) {
                processRootCategory(responseBuilder, suggestions, parent);
            } else {
                processRootCategories(responseBuilder, suggestions, parent);
            }
        } else {
            processError(responseBuilder, suggestions, "해당 내용을 찾을 수 없습니다.");
        }

        return responseBuilder.build();
    }

    @ForIntent("Category - option.connect")
    public ActionResponse processCategoryOptionConnect(ActionRequest request) throws ExecutionException, InterruptedException {
        ResponseBuilder responseBuilder = getResponseBuilder(request);
        List<String> suggestions = new ArrayList<String>();
        suggestions.add("홈으로 돌아가기");

        long parentId = 0;
        Map<String, Object> data = request.getConversationData();
        Object tmp = data.get("currentItem");
        if (tmp != null && tmp instanceof String) {
            parentId = Long.parseLong((String) tmp);
        }

        if (parentId == 0) {
            processError(responseBuilder, suggestions, "기본 정보가 없습니다.");
        } else {
            Optional<Category> current = categoryRepository.findById(parentId);
            if (current.isPresent()) {
                Category parent = current.get();
                processCategoryQRCode(responseBuilder, suggestions, parent);
            } else {
                processError(responseBuilder, suggestions, "기본 정보를 불러 올 수 없습니다.");
            }
        }

        System.out.println("selectedItem : " + parentId);

        return responseBuilder.build();
    }

    private void processError(ResponseBuilder builder, List<String> suggestions, String errorMessage) {
        builder.add("내부 오류 입니다." + errorMessage);
        builder.addSuggestions(suggestions.toArray(new String[suggestions.size()]));
    }

    private void processRootCategories(ResponseBuilder builder, List<String> suggestions, Category parent) {
        processCategories(builder, suggestions, parent.getSpeach(), parent.getChildren());
    }

    private void processCategories(ResponseBuilder builder, List<String> suggestions, String speach, List<Category> categories) {
        List<CarouselSelectCarouselItem> items = new ArrayList<>();
        CarouselSelectCarouselItem item;

        if (categories == null || categories.size() == 0) {
            System.out.println(categories);
            processError(builder, suggestions, "데이터를 조회 할 수 없습니다.");
            return;
        } else if (categories.size() == 1) {
            processRootCategory(builder, suggestions, categories.get(0));
        } else {
            int count = 0;
            for (Category category : categories) {
                List<String> synonyms = new ArrayList<String>();
                if (category.getSynonyms() != null) {
                    String[] tmpStrs = category.getSynonyms().split(";");
                    for (String tmp : tmpStrs) {
                        synonyms.add(tmp);
                    }
                }
                item = new CarouselSelectCarouselItem().setTitle(category.getTitle()).setDescription(category.getDescription())
                        .setOptionInfo(new OptionInfo().setKey(Long.toString(category.getId())).setSynonyms(synonyms));
                items.add(item);
                if(count > 8 )
                    break;
                count++;


            }

            if (suggestions.size() > 0)
                builder.addSuggestions(suggestions.toArray(new String[suggestions.size()]));
            if (speach == null)
                builder.add("선택해주십시요.");
            else
                builder.add(speach);

            builder.add(new SelectionCarousel().setItems(items));
        }

    }

    private void processRootCategory(ResponseBuilder builder, List<String> suggestions, Category category) {
        List<CarouselSelectCarouselItem> items = new ArrayList<>();
        CarouselSelectCarouselItem item;

        switch (category.getCatType()) {
            case ITEM:
            case CATEGORY:
                if (category.getSpeach() == null)
                    builder.add(category.getTitle());
                else
                    builder.add(category.getSpeach());
                BasicCard basicCard = new BasicCard()
                        .setTitle(category.getTitle())
                        .setFormattedText(category.getDescription());
                if (category.getImagePath() != null)
                    basicCard.setImage(new Image().setUrl(category.getImagePath()).setAccessibilityText(category.getImageAltText() == null ? "이미지" : category.getImageAltText()));
                builder.add(basicCard);

                if (category.getDetail() != null && category.getDetail().getLinkURL() != null && category.getDetail().getLinkURL().length() > 0)
                    suggestions.add("모바일");
                break;
            default:
                builder.add(category.getTitle() + "," + category.getDescription());
                break;
        }

        if (suggestions.size() > 0)
            builder.addSuggestions(suggestions.toArray(new String[suggestions.size()]));
    }

    private void processCategoryQRCode(ResponseBuilder builder, List<String> suggestions, Category category) {
        List<CarouselSelectCarouselItem> items = new ArrayList<>();
        CarouselSelectCarouselItem item;

        if (category != null && category.getDetail() != null && category.getDetail().getLinkURL() != null) {
            String encodedUrl = null;
            try {
                encodedUrl = URLEncoder.encode(category.getDetail().getLinkURL(), StandardCharsets.UTF_8.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            builder.add("다음 QR 코드를 모바일 장치로 찍으면 됩니다.")
                    .add(new BasicCard()
                            .setTitle(category.getTitle())
                            .setFormattedText(category.getDescription())
                            .setImage(new Image().setUrl("https://actions.o2o.kr/skylife/api/1.0/qrcode?url=" + encodedUrl + "&" + System.currentTimeMillis()).setAccessibilityText("모바일 장치 연결을 위한 QR코드"))
                            .setImageDisplayOptions("DEFAULT")
                    );
            if (suggestions.size() > 0)
                builder.addSuggestions(suggestions.toArray(new String[suggestions.size()]));
        } else {
            processError(builder, suggestions, "내부 오류 입니다. 링크 정보가 없는 아이템 입니다.");
        }
    }

    @ForIntent("Support - internet")
    public ActionResponse processSupportInternet(ActionRequest request) throws ExecutionException, InterruptedException {
        return processMidCategory(request, "cs.internet");
    }

    @ForIntent("Support - internet.lan")
    public ActionResponse processSupportInternetLan(ActionRequest request) throws ExecutionException, InterruptedException {
        return processMidCategory(request, "cs.internet.lan");
    }

    @ForIntent("Support - internet.pwd")
    public ActionResponse processSupportInternetPwd(ActionRequest request) throws ExecutionException, InterruptedException {
        return processMidCategory(request, "cs.internet.pwd");
    }

    @ForIntent("Support - internet.wps")
    public ActionResponse processSupportInternetWps(ActionRequest request) throws ExecutionException, InterruptedException {
        return processMidCategory(request, "cs.internet.wps");
    }

    @ForIntent("Support - internet.wireless")
    public ActionResponse processSupportInternetWireless(ActionRequest request) throws ExecutionException, InterruptedException {
        List<String> keycodes = new ArrayList<>();
        keycodes.add("cs.internet.pwd");
        keycodes.add("cs.internet.wps");

        return processMidCategory(request, keycodes);
    }

    @ForIntent("Support - remotecontroll")
    public ActionResponse processSupportRemotecontroll(ActionRequest request) throws ExecutionException, InterruptedException {
        return processMidCategory(request, "cs.remote");
    }

    @ForIntent("Support - remotecontroll.color")
    public ActionResponse processRemotecontrollColor(ActionRequest request) throws ExecutionException, InterruptedException {
        return processMidCategory(request, "cs.remote.color");
    }

    @ForIntent("Support - remotecontroll.out")
    public ActionResponse processRemotecontrollOut(ActionRequest request) throws ExecutionException, InterruptedException {
        return processMidCategory(request, "cs.remote.out");
    }

    @ForIntent("Support - remotecontroll.all")
    public ActionResponse processRemotecontrollAll(ActionRequest request) throws ExecutionException, InterruptedException {
        return processMidCategory(request, "cs.remote.all");
    }

    @ForIntent("Support - remotecontroll.back")
    public ActionResponse processRemotecontrollBack(ActionRequest request) throws ExecutionException, InterruptedException {
        return processMidCategory(request, "cs.remote.back");
    }

    @ForIntent("Support - remotecontroll.ai")
    public ActionResponse processRemotecontrollAi(ActionRequest request) throws ExecutionException, InterruptedException {
        return processMidCategory(request, "cs.remote.ai");
    }

    @ForIntent("Support - android")
    public ActionResponse processSupportAndroid(ActionRequest request) throws ExecutionException, InterruptedException {
        return processMidCategory(request, "cs.android");
    }

    @ForIntent("Support - android.add")
    public ActionResponse processAndroidAdd(ActionRequest request) throws ExecutionException, InterruptedException {
        return processMidCategory(request, "cs.android.add");
    }

    @ForIntent("Support - android.delete")
    public ActionResponse processAndroidDelete(ActionRequest request) throws ExecutionException, InterruptedException {
        return processMidCategory(request, "cs.android.delete");
    }

    @ForIntent("Support - android.ytube")
    public ActionResponse processAndroidYtube(ActionRequest request) throws ExecutionException, InterruptedException {
        return processMidCategory(request, "cs.android.ytube");
    }

    @ForIntent("Support - android.pwd")
    public ActionResponse processAndroidInitpwd(ActionRequest request) throws ExecutionException, InterruptedException {
        return processMidCategory(request, "cs.android.pwd");
    }

    @ForIntent("Support - android.app")
    public ActionResponse processAndroidApp(ActionRequest request) throws ExecutionException, InterruptedException {
        return processMidCategory(request, "cs.android.app");
    }

    @ForIntent("Support - android.casting")
    public ActionResponse processAsCasting(ActionRequest request) throws ExecutionException, InterruptedException {
        return processMidCategory(request, "cs.as.casting");
    }

    @ForIntent("Support - use")
    public ActionResponse processSupportUse(ActionRequest request) throws ExecutionException, InterruptedException {
        return processMidCategory(request, "cs.use");
    }

    @ForIntent("Support - use.my")
    public ActionResponse processUseMy(ActionRequest request) throws ExecutionException, InterruptedException {
        return processMidCategory(request, "cs.use.my");
    }

    @ForIntent("Support - use.set")
    public ActionResponse processUseSet(ActionRequest request) throws ExecutionException, InterruptedException {
        return processMidCategory(request, "cs.use.set");
    }

    @ForIntent("Support - use.inform")
    public ActionResponse processUseInform(ActionRequest request) throws ExecutionException, InterruptedException {
        return processMidCategory(request, "cs.use.inform");
    }

    @ForIntent("Support - use.vod")
    public ActionResponse processUseVod(ActionRequest request) throws ExecutionException, InterruptedException {
        return processMidCategory(request, "cs.use.vod");
    }

    @ForIntent("Support - as")
    public ActionResponse processSupportAs(ActionRequest request) throws ExecutionException, InterruptedException {
        return processMidCategory(request, "cs.as");
    }

    @ForIntent("Support - as.signal")
    public ActionResponse processAsSignal(ActionRequest request) throws ExecutionException, InterruptedException {
        return processMidCategory(request, "cs.as.signal");
    }

    @ForIntent("Support - as.mosaic")
    public ActionResponse processAsMosaic(ActionRequest request) throws ExecutionException, InterruptedException {
        return processMidCategory(request, "cs.android.mosaic");
    }

    @ForIntent("Support - as.black")
    public ActionResponse processAsBlack(ActionRequest request) throws ExecutionException, InterruptedException {
        return processMidCategory(request, "cs.as.black");
    }

    @ForIntent("Support - as.novoice")
    public ActionResponse processAsNovoice(ActionRequest request) throws ExecutionException, InterruptedException {
        return processMidCategory(request, "cs.as.novoice");
    }

    @ForIntent("Support - as.volume")
    public ActionResponse processAsVolume(ActionRequest request) throws ExecutionException, InterruptedException {
        return processMidCategory(request, "cs.as.volume");
    }

    @ForIntent("Support - as.control")
    public ActionResponse processAsControl(ActionRequest request) throws ExecutionException, InterruptedException {
        return processMidCategory(request, "cs.as.control");
    }

    @ForIntent("Support - as.tell")
    public ActionResponse processAsTell(ActionRequest request) throws ExecutionException, InterruptedException {
        return processMidCategory(request, "cs.as.tell");
    }

    @ForIntent("Support - as.power")
    public ActionResponse processAsPower(ActionRequest request) throws ExecutionException, InterruptedException {
        return processMidCategory(request, "cs.as.power");
    }

    @ForIntent("Support - as.showars")
    public ActionResponse processAsShowars(ActionRequest request) throws ExecutionException, InterruptedException {
        return processMidCategory(request, "cs.as.showars");
    }

    @ForIntent("Support - as.smartid")
    public ActionResponse processAsSmartid(ActionRequest request) throws ExecutionException, InterruptedException {
        return processMidCategory(request, "cs.as.smartid");
    }

    @ForIntent("Shopping - direct")
    public ActionResponse processShoppingDirect(ActionRequest request) throws ExecutionException, InterruptedException {
        return processMidCategory(request, "shop.direct");
    }

    @ForIntent("Shopping - gs")
    public ActionResponse processShoppingGs(ActionRequest request) throws ExecutionException, InterruptedException {
        return processMidCategory(request, "shop.gs");
    }

    @ForIntent("Shopping - kshop")
    public ActionResponse processShoppingKshpo(ActionRequest request) throws ExecutionException, InterruptedException {
        return processMidCategory(request, "shop.kshop");
    }

    @ForIntent("Shopping - hyundai")
    public ActionResponse processShoppingHyundai(ActionRequest request) throws ExecutionException, InterruptedException {
        return processMidCategory(request, "shop.hyundai");
    }

    @ForIntent("Shopping - lotte")
    public ActionResponse processShoppingLotte(ActionRequest request) throws ExecutionException, InterruptedException {
        return processMidCategory(request, "shop.lotte");
    }

    private ActionResponse processMidCategory(ActionRequest request, List<String> keycords) {
        ResponseBuilder responseBuilder = getResponseBuilder(request);
        List<String> suggestions = new ArrayList<String>();
        suggestions.add("홈으로 돌아가기");

        List<Category> roots = categoryRepository.findByKeycodeInOrderByDispOrderAsc(keycords);

        if (roots != null && roots.size() > 0) {
            processCategories(responseBuilder, suggestions, null, roots);
        } else
            processError(responseBuilder, suggestions, "정보를 읽어올 수 없습니다.");

        return responseBuilder.build();
    }

    private ActionResponse processMidCategory(ActionRequest request, String keycord) {
        ResponseBuilder responseBuilder = getResponseBuilder(request);
        List<String> suggestions = new ArrayList<String>();
        suggestions.add("홈으로 돌아가기");

        Category root = null;
        List<Category> roots = categoryRepository.findByKeycodeOrderByDispOrderAsc(keycord);

        if (roots != null && roots.size() > 0) {
            root = roots.get(0);
            System.out.println(root.getId() + "," + root.getKeycode());
        }

        if (root != null) {
            if (root.getCatType().equals(Category.Type.ITEM)) {
                processRootCategory(responseBuilder, suggestions, root);
            } else {
                processRootCategories(responseBuilder, suggestions, root);
            }
        } else
            processError(responseBuilder, suggestions, "정보를 읽어올 수 없습니다.");


        return responseBuilder.build();
    }
}
