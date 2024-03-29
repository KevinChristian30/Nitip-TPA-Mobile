package edu.bluejack22_2.nitip.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import edu.bluejack22_2.nitip.Model.Titip;
import edu.bluejack22_2.nitip.R;
import edu.bluejack22_2.nitip.View.NitipDetailActivity;
import edu.bluejack22_2.nitip.ViewModel.GroupViewModel;

public class NitipRecyclerViewAdapter extends RecyclerView.Adapter<NitipRecyclerViewAdapter.ViewHolder>{

    public ArrayList<Titip> data;
    private GroupViewModel  groupViewModel;
    private FirebaseFirestore db;
    Context context;

    public NitipRecyclerViewAdapter(ArrayList<Titip> data, Context context){
        this.data = data;
        this.groupViewModel = new GroupViewModel((LifecycleOwner) context);
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public NitipRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.nitip_card, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NitipRecyclerViewAdapter.ViewHolder holder, int position) {
        Titip currentTitip = data.get(position);

        holder.getTvGroupName().setText(currentTitip.getGroup_name());
        holder.getTvTitipName().setText(currentTitip.getTitip_name());
        holder.getTvCreatorName().setText(currentTitip.getEntruster_email());

        String validUntil = context.getResources().getString(R.string.open_until) + currentTitip.getClose_time().substring(currentTitip.getClose_time().length() - 5);
        holder.getTvCloseTime().setText(validUntil);

        holder.getClNitipCard().setOnClickListener(e -> {
            Intent next = new Intent(context, NitipDetailActivity.class);
            next.putExtra("TitipID", currentTitip.getId());

            context.startActivity(next);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ConstraintLayout clNitipCard;
        private final TextView tvGroupName;
        private final TextView tvTitipName;
        private final TextView tvCreatorName;
        private final TextView tvFee;
        private final TextView tvCloseTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            clNitipCard = itemView.findViewById(R.id.clNitipCard);
            tvGroupName = itemView.findViewById(R.id.tvGroupName);
            tvTitipName = itemView.findViewById(R.id.tvTitipName);
            tvCreatorName = itemView.findViewById(R.id.tvCreatorName);
            tvFee = itemView.findViewById(R.id.tvFee);
            tvCloseTime = itemView.findViewById(R.id.tvCloseTime);
        }

        public ConstraintLayout getClNitipCard() {
            return clNitipCard;
        }

        public TextView getTvGroupName() {
            return tvGroupName;
        }

        public TextView getTvTitipName() {
            return tvTitipName;
        }

        public TextView getTvCreatorName() {
            return tvCreatorName;
        }

        public TextView getTvFee() {
            return tvFee;
        }

        public TextView getTvCloseTime() {
            return tvCloseTime;
        }
    }

}
