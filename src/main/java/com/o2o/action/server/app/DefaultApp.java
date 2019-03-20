package com.o2o.action.server.app;

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
import com.o2o.action.server.DBInit;
import com.o2o.action.server.db.Category;
import com.o2o.action.server.repo.CategoryRepository;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class DefaultApp extends DialogflowApp {
    private CategoryRepository categoryRepository;

    public void setCategoryRepository(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @ForIntent("Support")
    public ActionResponse processSupport(ActionRequest request) throws ExecutionException, InterruptedException {
        ResponseBuilder responseBuilder = getResponseBuilder(request);
        List<String> suggestions = new ArrayList<String>();
        suggestions.add("고객센터로 가기");

        Category root = null;
        List<Category> roots = categoryRepository.findByKeycodeOrderByDispOrderAsc(DBInit.KEYCODE_CUSTOMER_SERVICE_ROOT);

        if (roots != null && roots.size() > 0) {
            root = roots.get(0);
            System.out.println(root.getId() + "," + root.getKeycode());
        }

        if (root != null)
            processCategories(responseBuilder, suggestions, root);
        else
            processError(responseBuilder, suggestions, "고객 서비스 정보를 읽어올 수 없습니다.");

        return responseBuilder.build();
    }

    @ForIntent("Category - option")
    public ActionResponse processCategoryOption(ActionRequest request) throws ExecutionException, InterruptedException {
        ResponseBuilder responseBuilder = getResponseBuilder(request);
        List<String> suggestions = new ArrayList<String>();
        suggestions.add("고객센터로 가기");

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
                processCategory(responseBuilder, suggestions, parent);
            } else {
                processCategories(responseBuilder, suggestions, parent);
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
        suggestions.add("고객센터로 가기");

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

    private void processCategories(ResponseBuilder builder, List<String> suggestions, Category parent) {
        List<CarouselSelectCarouselItem> items = new ArrayList<>();
        CarouselSelectCarouselItem item;

        if (parent == null || parent.getChildren().size() == 0) {
            processError(builder, suggestions, "데이터를 조회 할 수 없습니다.");
            return;
        } else if (parent.getChildren().size() == 1) {
            processCategory(builder, suggestions, parent.getChildren().get(0));
        } else {
            for (Category category : parent.getChildren()) {
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
            }

            if (suggestions.size() > 0)
                builder.addSuggestions(suggestions.toArray(new String[suggestions.size()]));
            if (parent.getSpeach() == null)
                builder.add("선택해주십시요.");
            else
                builder.add(parent.getSpeach());

            builder.add(new SelectionCarousel().setItems(items));
        }

    }

    private void processCategory(ResponseBuilder builder, List<String> suggestions, Category category) {
        List<CarouselSelectCarouselItem> items = new ArrayList<>();
        CarouselSelectCarouselItem item;

        switch (category.getCatType()) {
            case ITEM:
            case CATEGORY:
                if (category.getSpeach() == null)
                    builder.add(category.getTitle());
                else
                    builder.add(category.getSpeach());
                BasicCard basicCard =new BasicCard()
                        .setTitle(category.getTitle())
                        .setFormattedText(category.getDescription());
                if (category.getImagePath() != null)
                        basicCard.setImage(new Image().setUrl(category.getImagePath()).setAccessibilityText(category.getImageAltText() == null ? "이미지" : category.getImageAltText()));
                builder.add(basicCard);
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

    @ForIntent("Support - Internet")
    public ActionResponse processSupportInternet(ActionRequest request) throws ExecutionException, InterruptedException {
        return processMidCategory(request,"cs.internet");
    }

    private ActionResponse processMidCategory(ActionRequest request, String keycord){
        ResponseBuilder responseBuilder = getResponseBuilder(request);
        List<String> suggestions = new ArrayList<String>();
        suggestions.add("고객센터로 가기");

        Category root = null;
        List<Category> roots = categoryRepository.findByKeycodeOrderByDispOrderAsc(keycord);

        if (roots != null && roots.size() > 0) {
            root = roots.get(0);
            System.out.println(root.getId() + "," + root.getKeycode());
        }

        if (root != null)
            processCategories(responseBuilder, suggestions, root);
        else
            processError(responseBuilder, suggestions, "고객 서비스 정보를 읽어올 수 없습니다.");

        return responseBuilder.build();
    }
}
