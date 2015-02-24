package com.sedentary.findMyPet.base.providers.pet;

import android.accounts.NetworkErrorException;
import android.os.Parcel;

import com.google.gson.internal.LinkedTreeMap;
import com.sedentary.findMyPet.base.providers.BaseProvider;
import com.sedentary.findMyPet.base.providers.pet.models.Pet;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by rodrigo on 24/02/15.
 */
public abstract class FindMyPetProvider extends PetProvider {

    protected String mApiUrl = "http://api.findmypet.io/";

    @Override
    public Call getList(final ArrayList<Pet> existingList, Filters filters, final Callback callback) {
        final ArrayList<Pet> currentList;
        if (existingList == null) {
            currentList = new ArrayList<>();
        } else {
            currentList = (ArrayList<Pet>) existingList.clone();
        }

        ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("limit", "30"));

        if (filters == null) {
            filters = new Filters();
        }

        if (filters.keywords != null) {
            params.add(new BasicNameValuePair("keywords", filters.keywords));
        }

        if (filters.order == Filters.Order.ASC) {
            params.add(new BasicNameValuePair("order", "asc"));
        } else {
            params.add(new BasicNameValuePair("order", "desc"));
        }

        String sort = "";
        switch (filters.sort) {
            default:
            case NAME:
                sort = "name";
                break;
            case AGE:
                sort = "age";
                break;
        }

        params.add(new BasicNameValuePair("sort", sort));

        String url = mApiUrl + "pets/";
        if (filters.page != null) {
            url += filters.page;
        } else {
            url += "1";
        }

        Request.Builder requestBuilder = new Request.Builder();
        String query = buildQuery(params);
        requestBuilder.url(url + "?" + query);
        requestBuilder.tag(PET_CALL);

        return fetchList(currentList, requestBuilder, callback);
    }

    /**
     * Fetch the list of pets
     *
     * @param currentList Current shown list to be extended
     * @param requestBuilder Request to be executed
     * @param callback Network callback
     *
     * @return Call
     */
    private Call fetchList(final ArrayList<Pet> currentList, final Request.Builder requestBuilder, final Callback callback) {
        return enqueue(requestBuilder.build(), new com.squareup.okhttp.Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                String url = requestBuilder.build().urlString();
                requestBuilder.url(url);
                fetchList(currentList, requestBuilder, callback);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        ArrayList<LinkedTreeMap<String, Object>> list =
                                (ArrayList<LinkedTreeMap<String, Object>>) mGson.fromJson(responseStr, ArrayList.class);
                        PetReponse result = new PetReponse(list);
                        if (list == null) {
                            callback.onFailure(new NetworkErrorException("Empty response"));
                        } else {
                            ArrayList<Pet> formattedData = result.formatList(currentList);
                            callback.onSuccess(formattedData);
                            return;
                        }
                    }
                } catch (Exception e) {
                    callback.onFailure(e);
                }
                callback.onFailure(new NetworkErrorException("Couldn't connect to FindMyPet API"));
            }
        });
    }

    private class PetReponse {
        LinkedTreeMap<String, Object> petData;
        ArrayList<LinkedTreeMap<String, Object>> petsList;

        public PetReponse(LinkedTreeMap<String, Object> petData) {
            this.petData = petData;
        }

        public ArrayList<Pet> formatDetail() {
            ArrayList<Pet> list = new ArrayList<>();
            try {
                Pet pet = new Pet();

                pet.name = (String) petData.get("name");
                pet.age = (String) petData.get("age");

                list.add(pet);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return list;
        }

        public PetReponse(ArrayList<LinkedTreeMap<String, Object>> petsList) {
            this.petsList = petsList;
        }

        public ArrayList<Pet> formatList(ArrayList<Pet> existingList) {
            for (LinkedTreeMap<String, Object> item : petsList) {
                Pet show = new Pet();

                show.name = item.get("name").toString();
                show.age = item.get("age").toString();

                existingList.add(show);
            }
            return existingList;
        }
    }

    @Override
    public int getLoadingMessage() {
        return 1; // FIXME criar resource R.string.loading_pets
    }
}
