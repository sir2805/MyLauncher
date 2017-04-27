package com.example.pc.mylauncher;

/**
 * Created by PC on 13.04.2017.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PC on 10.04.2017.
 */

public class FavouriteAppsAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//    List<AppModel> apps = new ArrayList<>();

    private final ListItemClickListener mOnClickListener;
//    private final ListMenuItemClickListener mMenuItemListener;

    public interface ListItemClickListener {
        void onListItemClick(AppModel app);
    }

//    public interface ListMenuItemClickListener {
//        boolean onListMenuItemClick(int id, String packageName);
//    }

//    String showInfo(int idx) {
//        return "Info about " + Integer.toHexString(data.get(idx)).toUpperCase();
//    }

    public FavouriteAppsAdapter(FavouriteAppsFragment listItemClickListener) {
        mOnClickListener = listItemClickListener;
//        mMenuItemListener = listItemClickListener;

        setHasStableIds(true);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem;
        LayoutInflater inflater;
        View view;
        layoutIdForListItem = R.layout.app_list_item;
        inflater = LayoutInflater.from(context);

        view = inflater.inflate(layoutIdForListItem, parent, false);

        return new FavouriteAppsAdapter.AppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((FavouriteAppsAdapter.AppViewHolder)holder).bind(position);
    }

    @Override
    public int getItemCount() {
        return AppsManager.getFavouriteApps().size();
    }

    @Override
    public long getItemId(int position) {
        return AppsManager.getFavouriteApps().get(position).hashCode();
    }

    class AppViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener  {

        ImageView listItemImage;
        TextView listItemTitle;

        public AppViewHolder(View itemView) {
            super(itemView);
            listItemImage = (ImageView) itemView.findViewById(R.id.iv_apps_item);
            listItemTitle = (TextView) itemView.findViewById(R.id.tv_item_number);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        private
        void bind(int listIndex) {
            listItemImage.setImageDrawable(AppsManager.getFavouriteApps().get(listIndex).getIcon());
            listItemTitle.setText(AppsManager.getFavouriteApps().get(listIndex).getLabel());
//            setImageDrawable(icons[setIcon(listIndex)]);
//            listItemTitle.setText(Integer.toHexString(data.get(listIndex)).toUpperCase());
        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onListItemClick(AppsManager.getFavouriteApps().get(getAdapterPosition()));
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            //menuInfo is null
//            if (getAdapterPosition() > (DataHandler.getColumns() + 1)* 2) {
            MenuItem deleteAction = contextMenu.add(Menu.NONE, 0,
                    Menu.NONE, "Delete from favourite");
            deleteAction.setOnMenuItemClickListener(this);
//            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {

//            int id = item.getItemId();
            int recyclerId = getLayoutPosition();
            AppsManager.removeFromFavourites(recyclerId);
            notifyDataSetChanged();
            return true;
//            return mMenuItemListener.onListMenuItemClick(id, AppsManager.get(recyclerId).getApplicationPackageName());
        }
    }
}