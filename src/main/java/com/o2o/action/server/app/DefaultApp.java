package com.o2o.action.server.app;

import com.google.actions.api.ActionRequest;
import com.google.actions.api.ActionResponse;
import com.google.actions.api.DialogflowApp;
import com.google.actions.api.ForIntent;
import com.google.actions.api.response.ResponseBuilder;
import com.google.actions.api.response.helperintent.SelectionCarousel;
import com.google.api.services.actions_fulfillment.v2.model.CarouselSelectCarouselItem;
import com.google.api.services.actions_fulfillment.v2.model.OptionInfo;
import com.o2o.action.server.DBInit;
import com.o2o.action.server.db.Category;
import com.o2o.action.server.repo.CategoryRepository;

import java.util.ArrayList;
import java.util.List;
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

        Category root = null;
        List<Category> roots = categoryRepository.findByKeycodeOrderByDispOrderAsc(DBInit.KEYCODE_CUSTOMER_SERVICE_ROOT);

        if (roots != null && roots.size() > 0) {
            root = roots.get(0);
        }

        List<CarouselSelectCarouselItem> items = new ArrayList<>();
        CarouselSelectCarouselItem item;
        List<String> suggestions = new ArrayList<String>();

        for (Category category : root.getChildren()) {
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
            responseBuilder.addSuggestions(suggestions.toArray(new String[suggestions.size()]));
        responseBuilder.add("어떤 부분을 도와드릴까요?").add(new SelectionCarousel().setItems(items));

        return responseBuilder.build();
    }

    @ForIntent("Support - selectoption")
    public ActionResponse processSupportOption(ActionRequest request) throws ExecutionException, InterruptedException {
        ResponseBuilder responseBuilder = getResponseBuilder(request);

        long parentId = 0;
        String selectedItem = request.getSelectedOption();
        if (selectedItem != null) {
            parentId = Long.parseLong(selectedItem);
        } else {
            return responseBuilder.add("내부 오류 입니다.").build();
        }

        System.out.println("selectedItem : " + parentId);

        Optional<Category> current = categoryRepository.findById(parentId);
        if (current.isPresent()) {
            Category parent = current.get();
            List<CarouselSelectCarouselItem> items = new ArrayList<>();
            CarouselSelectCarouselItem item;
            List<String> suggestions = new ArrayList<String>();

            if (parent.getChildren() == null || parent.getChildren().size() <= 1) {
                responseBuilder.add("");
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
                    responseBuilder.addSuggestions(suggestions.toArray(new String[suggestions.size()]));
                responseBuilder.add("어떤 부분을 도와드릴까요?").add(new SelectionCarousel().setItems(items));
            }
        } else {
            responseBuilder.add("내부 오류 입니다.해당 내용을 찾을 수 없습니다.");
        }


        return responseBuilder.build();
    }
}
