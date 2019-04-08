package ru.demjanov_av.githubviewer.views;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import ru.demjanov_av.githubviewer.crypto.EncryptorGOST;
import ru.demjanov_av.githubviewer.models.RealmModelUser;


public class MoreUsersAdapter extends RecyclerView.Adapter<MoreUsersAdapter.MyViewHolder> implements OrderedRealmCollectionChangeListener {
    //-----Constants begin-------------------------------
    private final static int HEADER_VIEW = 0;
    private final static int ITEM_VIEW = 1;
    private final static String ADAPTER = "MORE_USERS_ADAPTER";
    //-----Constants end---------------------------------

    //-----Class variables begin-------------------------
    private RealmResults<RealmModelUser> moreUsers;
    private MyViewHolder mvh;
    private MoreUsersFragment moreUsersFragment;
    private EncryptorGOST encryptorGOST;
    //-----Class variables end---------------------------


    //---------------------------------------------------
    //---------------------------------------------------

    /////////////////////////////////////////////////////
    // Interface MoreUsersCall
    ////////////////////////////////////////////////////
    interface MoreUsersCall{
        void onCallUser(String userId);
    }


    //---------------------------------------------------
    //---------------------------------------------------


    /////////////////////////////////////////////////////
    // Constructor of MoreUsersAdapter
    ////////////////////////////////////////////////////
    MoreUsersAdapter(RealmResults<RealmModelUser> moreUsers, MoreUsersFragment moreUsersFragment, @Nullable EncryptorGOST encryptorGOST) {
        this.moreUsers = moreUsers;
        this.moreUsers.addChangeListener(this);

        this.moreUsersFragment = moreUsersFragment;
        this.encryptorGOST = encryptorGOST;
    }


    /////////////////////////////////////////////////////
    // Methods of getItemViewType
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------
    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return HEADER_VIEW;
        }

        return ITEM_VIEW;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }
    //-----End-------------------------------------------


    /////////////////////////////////////////////////////
    // Method onCreateViewHolder
    ////////////////////////////////////////////////////
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        switch (viewType) {
            case HEADER_VIEW:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.more_users_recycle_header, parent, false);

                this.mvh = new HeaderViewHolder(view);
                break;
            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.more_users_recycle_item, parent, false);

                this.mvh = new ItemViewHolder(view);
                break;
        }

        return this.mvh;
    }


    /////////////////////////////////////////////////////
    // Method onBindViewHolder
    ////////////////////////////////////////////////////
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            if(holder instanceof ItemViewHolder) {
                ItemViewHolder ivh = (ItemViewHolder) holder;
                if(position > 0) {
                    ivh.bind(position, this.moreUsers.get(position - 1).getLogin());
                } else { // условие, по идее, избыточное, но надежность
                    ivh.bind(position, this.moreUsers.get(position).getLogin());
                }
            }else if (holder instanceof  HeaderViewHolder){
                HeaderViewHolder hvh = (HeaderViewHolder) holder;
            }
        }catch (Exception e){
            Log.d(ADAPTER, ": " + e.getMessage());
        }
    }


    /////////////////////////////////////////////////////
    // Method  getItemCount
    ////////////////////////////////////////////////////
    @Override
    public int getItemCount() {
        if(this.moreUsers == null){
            return 0;
        }

        return this.moreUsers.size() + 1;
    }

//    public int getCurrentPosition(){
////        return mvh.getSelectedPosition();
//        return mvh.getAdapterPosition();
//    }

    //    private void supportClickItem(){
//        this.moreUsersFragment.onCallUser(this.moreUsers.get(getCurrentPosition()).getId());
//    }


    /////////////////////////////////////////////////////
    // Method  onChange
    ////////////////////////////////////////////////////
    @Override
    public void onChange(Object o, OrderedCollectionChangeSet changeSet) {
        notifyDataSetChanged();
    }


    /////////////////////////////////////////////////////
    // Method supportClickItem
    ////////////////////////////////////////////////////
    private void supportClickItem(int position) {
        if (position > 0) {
            this.moreUsersFragment.onCallUser(this.moreUsers.get(position - 1).getId());
        }
    }



    //---------------------------------------------------
    //---------------------------------------------------

    /////////////////////////////////////////////////////
    // ViewHolders Classes
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------

    /////////////////////////////////////////////////////
    // Class MyViewHolder
    ////////////////////////////////////////////////////
    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.more_users_item_text)
        TextView itemTextView;

        MyViewHolder(View itemView) {
            super(itemView);
//            ButterKnife.bind(this, itemView);
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    supportClickItem();
//                }
//            });

        }

        void bind(int position, String text){
            ButterKnife.bind(this, itemView);
            itemTextView.setText(text);
            itemTextView.setTag(position);
        }

        @Deprecated
        public int getSelectedPosition(){
            return this.getLayoutPosition();
        }


//        @OnClick(R.id.more_users_item_text)
//        public void onClickItem(View v){
//            supportClickItem((int)v.getTag());
//        }

    }
    //-----End of MyViewHolder class---------------------


    /////////////////////////////////////////////////////
    // Class HeaderViewHolder
    ////////////////////////////////////////////////////
    public class HeaderViewHolder extends MyViewHolder {
        HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }
    //-----End of HeaderViewHolder class-----------------


    /////////////////////////////////////////////////////
    // Class ItemViewHolder
    ////////////////////////////////////////////////////
    public class ItemViewHolder extends MyViewHolder {
        ItemViewHolder(View itemView) {
            super(itemView);
        }

        @OnClick(R.id.more_users_item_text)
        public void onClickItem(View v){
            supportClickItem((int)v.getTag());
        }

        @Override
        void bind(int position, String text){
            String str = text;

            if(encryptorGOST != null){
                str = encryptorGOST.decrypt(text);
            }

            super.bind(position, str);
        }
    }
    //-----End of ItemViewHolder class-------------------

    //-----End-------------------------------------------
}
