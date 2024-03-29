package edu.bluejack22_2.nitip.Repository;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import edu.bluejack22_2.nitip.Model.Bill;
import edu.bluejack22_2.nitip.Model.Titip;

public class BillRepository {

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth fAuth;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    public BillRepository() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }

    public void createBill(Bill bill) {
        HashMap<String, Object> billData = new HashMap<>();

        billData.put("lender_email", bill.getLender_email());
        billData.put("debtor_email", bill.getDebtor_email());
        billData.put("amount", bill.getAmount());
        billData.put("status", bill.getStatus());
        billData.put("date", bill.getDate());
        billData.put("proof", bill.getProof());

        firebaseFirestore.collection("bill").add(billData);
    }

    public void getBillsByEmailAndStatusLiveData(MutableLiveData<List<Bill>> billLiveDatas, String email, String status) {
        Query lenderQuery = firebaseFirestore.collection("bill")
                                .whereEqualTo("lender_email", email)
                                .whereEqualTo("status", status);

        Query debtorQuery = firebaseFirestore.collection("bill")
                                .whereEqualTo("debtor_email", email)
                                .whereEqualTo("status", status);

        List<Bill> billsList = new ArrayList<>();

        lenderQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Bill bill = document.toObject(Bill.class);
                        bill.setId(document.getId());
                        billsList.add(bill);
                    }

                    debtorQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Bill bill = document.toObject(Bill.class);
                                    bill.setId(document.getId());
                                    billsList.add(bill);
                                }

                                billLiveDatas.setValue(billsList);
                            }
                        }
                    });

                }
            }
        });
    }

    public void rejectBill(String billID) {
        Query titipsRef = firebaseFirestore.collection("bill");
        titipsRef.whereEqualTo(FieldPath.documentId(), billID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (!querySnapshot.isEmpty()) {
                    DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                    document.getReference().update("status", "Rejected");
                }
            }
        });
    }

    public void changeBillStatus(String billID, String uri) {
        Query titipsRef = firebaseFirestore.collection("bill");
        titipsRef.whereEqualTo(FieldPath.documentId(), billID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (!querySnapshot.isEmpty()) {
                    DocumentSnapshot document = querySnapshot.getDocuments().get(0);
//                    document.getReference().update("status", "Pending Confirmation");
                    document.getReference().update("proof", uri);
                }
            }
        });
    }

    public void acceptBill(String billID) {
        Query titipsRef = firebaseFirestore.collection("bill");
        titipsRef.whereEqualTo(FieldPath.documentId(), billID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (!querySnapshot.isEmpty()) {
                    DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                    document.getReference().update("status", "Finished");
                }
            }
        });
    }

    public void cancelBill(String billID) {
        Query titipsRef = firebaseFirestore.collection("bill");
        titipsRef.whereEqualTo(FieldPath.documentId(), billID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (!querySnapshot.isEmpty()) {
                    DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                    document.getReference().update("status", "Canceled");
                }
            }
        });
    }

    public void getProof(String id, LiveData<String> proof) {
        Query billRef = firebaseFirestore.collection("bill");
        billRef.whereEqualTo(FieldPath.documentId(), id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (!querySnapshot.isEmpty()) {
                    DocumentSnapshot document = querySnapshot.getDocuments().get(0);

                }
            }
        });
    }

    public LiveData<Uri> uploadProof(Uri uri, String name) {
        MutableLiveData<Uri> uriMutableLiveData = new MutableLiveData<>();
        StorageReference path = storageReference.child("bill_proof/" + name);

        UploadTask uploadTask = path.putFile(uri);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            path.getDownloadUrl().addOnCompleteListener(url -> {
                Uri firebaseUri = url.getResult();
                uriMutableLiveData.setValue(firebaseUri);
            });
        });

        return uriMutableLiveData;
    }
}
