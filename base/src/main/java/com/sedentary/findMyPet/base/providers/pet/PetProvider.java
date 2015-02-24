package com.sedentary.findMyPet.base.providers.pet;

import com.sedentary.findMyPet.base.providers.BaseProvider;
import com.sedentary.findMyPet.base.providers.pet.models.Pet;
import com.squareup.okhttp.Call;

import java.util.ArrayList;

/**
 * Created by rodrigo on 24/02/15.
 */
public abstract class PetProvider extends BaseProvider {
    public static final String PET_CALL = "pet_http_call";

    /**
     * Get a list of Pet items from the provider
     *
     * @param filters  Filters the provider can use to sort or search
     * @param callback PetProvider callback
     */
    public void getList(Filters filters, Callback callback) {
        getList(null, filters, callback);
    }

    /**
     * Get a list of Pet items from the provider
     *
     * @param currentList Input the current list so it can be extended
     * @param filters     Filters the provider can use to sort or search
     * @param callback    PetProvider callback
     * @return Call
     */
    public abstract Call getList(ArrayList<Pet> currentList, Filters filters, Callback callback);

    /**
     *
     * @param petId
     * @param callback
     * @return
     */
    public abstract Call getDetail(String petId, Callback callback);

    /**
     *
     * @return
     */
    public abstract int getLoadingMessage();

    /**
     *
     */
    public interface Callback {
        public void onSuccess(ArrayList<Pet> items);

        public void onFailure(Exception e);
    }

    /**
     *
     */
    public static class Filters {
        public enum Order { ASC, DESC }

        public enum Sort { POPULARITY, YEAR, DATE, RATING, ALPHABET }

        public String keywords = null;
        public String genre = null;
        public Order order = Order.DESC;
        public Sort sort = Sort.POPULARITY;
        public Integer page = null;
    }
}
