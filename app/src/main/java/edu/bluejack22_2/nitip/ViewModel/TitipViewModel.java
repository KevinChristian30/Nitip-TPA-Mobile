package edu.bluejack22_2.nitip.ViewModel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import edu.bluejack22_2.nitip.Facade.Error;
import edu.bluejack22_2.nitip.Facade.Response;
import edu.bluejack22_2.nitip.Model.GroupRow;
import edu.bluejack22_2.nitip.Model.Titip;
import edu.bluejack22_2.nitip.Model.TitipDetail;
import edu.bluejack22_2.nitip.R;
import edu.bluejack22_2.nitip.Repository.TitipRepository;
import edu.bluejack22_2.nitip.Service.TimeService;
import edu.bluejack22_2.nitip.View.NitipDetailActivity;

public class TitipViewModel {

    MutableLiveData<List<Titip>> titipLiveDatas;
    MutableLiveData<Titip> titipLiveData;
    private TitipRepository titipRepository;
    public TitipViewModel() {
        titipRepository = new TitipRepository();
        titipLiveDatas = new MutableLiveData<>();
        titipLiveData = new MutableLiveData<>();
    }

    public Response CreateTitip(Context context, Titip titip) {

        Response response = new Response(null);

        if (titip.getTitip_name().trim().isEmpty() || titip.getClose_time().trim().isEmpty()) {
            response.setError(new Error(context.getResources().getString(R.string.field_must_filled)));
        }
        else if (!TimeService.isValidDate(titip.getClose_time())) {
            response.setError(new Error(context.getResources().getString(R.string.date_in_future)));
        }

        if (response.getError() != null) return response;

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String entrusterEmail = currentUser.getEmail();

        titip.setEntruster_email(entrusterEmail);

        titipRepository.CreateTitip(titip);

        return response;
    }

    public MutableLiveData<List<Titip>> getTitipLiveData() {
        getTitipData();
        return titipLiveDatas;
    }

    public void getTitipData() {
        titipRepository.getTitips(titipLiveDatas);
    }

    public MutableLiveData<Titip> getTitipById(String titipId) {
        titipLiveData = (MutableLiveData<Titip>) titipRepository.getTitipByID(titipId).getResponse();
        return titipLiveData;
    }

    public void addNewTitipDetail(String titipId, TitipDetail titipDetail) {
        titipRepository.addNewTitipDetail(titipId, titipDetail);
    }

    public LiveData<Response> getLastTitip(String groupCode) {
        return titipRepository.getLastTitip(groupCode);
    }

    public void refreshTitip() {
        getTitipData();
    }

    public void updateTitipDetail(String titipID, String email, String newDetail, LifecycleOwner context) {
        titipRepository.updateTitipDetail(titipID, email, newDetail, new TitipRepository.Listener() {
            @Override
            public void onSuccess(ArrayList<TitipDetail> titipDetails) {
//                getTitipById(titipID).observe(context, data -> {
//                    NitipDetailActivity.adapter.setData(data.getTitip_detail());
//                });

//                titipLiveDatas.observe(context, data -> {
//                    List<Titip> temp = new ArrayList<>();
//                    temp.addAll(data);
//                    for (int i = 0; i < temp.size(); i++) {
//                        if (temp.get(i).getId().equals(titipID)) {
//                            temp.get(i).setTitip_detail(titipDetails);
//                            titipLiveDatas.setValue(temp);
//                            break;
//                        }
//                    }
//                });

                List<Titip> currentData = titipLiveDatas.getValue();
                if (currentData != null) {
                    for (int i = 0; i < currentData.size(); i++) {
                        if (currentData.get(i).getId().equals(titipID)) {
                            currentData.get(i).setTitip_detail(titipDetails);
                            titipLiveDatas.setValue(currentData);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure() {

            }
        });
    }

    public void removeTitipDetail(String titipID, String email) {
        titipRepository.removeTitipDetail(titipID, email);
    }
}
