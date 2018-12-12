package ru.demjanov_av.githubviewer.views;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.RealmResults;
import ru.demjanov_av.githubviewer.R;
import ru.demjanov_av.githubviewer.models.RealmModelUser;


public class MoreUsersAdapter extends RecyclerView.Adapter<MoreUsersAdapter.MyViewHolder> implements OrderedRealmCollectionChangeListener {
    private RealmResults<RealmModelUser> moreUsers;
    private MyViewHolder mvh;
    private MoreUsersFragment moreUsersFragment;

    interface MoreUsersCall{
        void onCallUser(String userId);
    }

    public MoreUsersAdapter(RealmResults<RealmModelUser> moreUsers, MoreUsersFragment moreUsersFragment) {
        this.moreUsers = moreUsers;
        this.moreUsers.addChangeListener(this);

        this.moreUsersFragment = moreUsersFragment;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.more_users_recycle_item, parent, false);

        this.mvh = new MyViewHolder(view);

        return this.mvh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(this.moreUsers.get(position).getLogin());
    }

    @Override
    public int getItemCount() {
        return this.moreUsers.size();
    }

    public int getCurrentPosition(){
        return mvh.getSelectedPosition();
    }


    @Override
    public void onChange(Object o, OrderedCollectionChangeSet changeSet) {
        notifyDataSetChanged();
    }

    private void supportClickItem(){
        this.moreUsersFragment.onCallUser(this.moreUsers.get(getCurrentPosition()).getId());
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.more_users_item_text)
        TextView itemTextView;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        public void bind(String text){
            itemTextView.setText(text);
        }

        public int getSelectedPosition(){
            return this.getLayoutPosition();
        }

        @OnClick(R.id.more_users_item_text)
        public void onClickItem(){
            supportClickItem();
        }

    }
}
