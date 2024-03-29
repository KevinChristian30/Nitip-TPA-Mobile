package edu.bluejack22_2.nitip.ViewModel;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import edu.bluejack22_2.nitip.Facade.Error;
import edu.bluejack22_2.nitip.Facade.Response;
import edu.bluejack22_2.nitip.Model.User;
import edu.bluejack22_2.nitip.R;
import edu.bluejack22_2.nitip.Repository.UserRepository;
import edu.bluejack22_2.nitip.Service.EmailService;
import edu.bluejack22_2.nitip.Service.RegisterService;

public class RegisterViewModel extends ViewModel {
    private UserRepository userRepository;
    public RegisterViewModel() {
        userRepository = new UserRepository();
    }

    public interface RegisterUserCallBack {
        void onRegister(Response response);
    }

    public void RegisterUser(Activity activity, User user, String confirm, RegisterUserCallBack callBack) {

        Response response = new Response(null);

        if (RegisterService.isFieldEmpty(user, confirm)) {
            response.setError(new Error(activity.getResources().getString(R.string.field_must_filled)));
        }
        else if (user.getUsername().length() < 6) {
            response.setError(new Error(activity.getResources().getString(R.string.username_5_char)));
        }
        else if (!RegisterService.isValidEmail(user.getEmail())) {
            response.setError(new Error(activity.getResources().getString(R.string.invalid_email)));
        }
        else if (user.getPassword().trim().length() < 7) {
            response.setError(new Error(activity.getResources().getString(R.string.password_6_char)));
        }
        else if (!RegisterService.isAlphanumeric(user.getPassword())) {
            response.setError(new Error(activity.getResources().getString(R.string.password_alphanumeric)));
        }
        else if (!confirm.equals(user.getPassword())) {
            response.setError(new Error(activity.getResources().getString(R.string.password_conf_not_match)));
        }


        if (response.getError() != null) {
            callBack.onRegister(response);
            return;
        }

        EmailService.isEmailExists(user.getEmail(), new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        response.setError(new Error(activity.getResources().getString(R.string.email_exist)));

                    } else {
                        try {
                            userRepository.registerUser(activity, user);

                        } catch (Exception e) {
                            response.setError(new Error(activity.getResources().getString(R.string.something_went_wrong)));
                        }
                    }
                } else {
                    response.setError(new Error(activity.getResources().getString(R.string.something_went_wrong)));
                }
                callBack.onRegister(response);
            }
        });

    }
}
