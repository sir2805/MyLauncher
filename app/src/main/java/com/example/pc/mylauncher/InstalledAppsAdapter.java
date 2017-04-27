package com.example.pc.mylauncher;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.StringTokenizer;


/**
 * Created by PC on 10.04.2017.
 */

public class InstalledAppsAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM_TYPE_NORMAL = 0;
    private static final int ITEM_TYPE_HEADER = 1;

    public void setColumns(int mColumns) {
        this.mColumns = mColumns;
        notifyDataSetChanged();
    }

    private int mColumns = 4;

    //List<AppModel> apps = new ArrayList<>();

    private final ListItemClickListener mOnClickListener;
    private final ListMenuItemClickListener mMenuItemListener;

    public interface ListItemClickListener {
        void onListItemClick(AppModel app);
    }

    public interface ListMenuItemClickListener {
        boolean onListMenuItemClick(int id, AppModel app);
    }

    public InstalledAppsAdapter(InstalledAppsFragment listItemClickListener) {
        //AppsManager.setColumns(mColumns);
        mOnClickListener = listItemClickListener;
        mMenuItemListener = listItemClickListener;

        setHasStableIds(true);
    }

    public void setData(ArrayList<AppModel> data) {
//        apps.clear();
//        if (data != null) {
//            apps.addAll(data);
//        }
        AppsManager.setAppList(data);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem;
        LayoutInflater inflater;
        View view;
        switch (viewType) {
            case ITEM_TYPE_NORMAL:
                layoutIdForListItem = R.layout.app_list_item;

                inflater = LayoutInflater.from(context);

                view = inflater.inflate(layoutIdForListItem, parent, false);
                return new InstalledAppsAdapter.AppViewHolder(view);
            case ITEM_TYPE_HEADER:
                layoutIdForListItem = R.layout.separator_item;
                inflater = LayoutInflater.from(context);

                view = inflater.inflate(layoutIdForListItem, parent, false);
                return new InstalledAppsAdapter.MyHeaderViewHolder(view);
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final int itemType = getItemViewType(position);

        if (itemType == ITEM_TYPE_NORMAL) {
            ((InstalledAppsAdapter.AppViewHolder)holder).bind(position);
        } else if (itemType == ITEM_TYPE_HEADER) {
            ((InstalledAppsAdapter.MyHeaderViewHolder)holder).setHeaderText(position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == mColumns + 1 || position == (mColumns + 1) * 2) {
            return ITEM_TYPE_HEADER;
        } else {
            return ITEM_TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        return AppsManager.size();
    }

    @Override
    public long getItemId(int position) {
        if (position == 0 || position == mColumns + 1 || position == (mColumns + 1) * 2) {
            return 0;
        }
        return AppsManager.getApp(position).hashCode();
    }

    class MyHeaderViewHolder extends RecyclerView.ViewHolder {

        private TextView headerLabel;

        public MyHeaderViewHolder(View view) {
            super(view);

            headerLabel = (TextView) view.findViewById(R.id.tv_apps_separator);
        }

        public void setHeaderText(int listIndex) {
            if (listIndex == 0) {
                headerLabel.setText(R.string.popular_apps);
            }
            else if (listIndex == mColumns + 1) {
                headerLabel.setText(R.string.new_apps);
            } else if (listIndex == (mColumns + 1) * 2) {
                headerLabel.setText(R.string.all_apps);
            }
        }
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
            AppModel app = AppsManager.getApp(listIndex);
            listItemImage.setImageDrawable(app.getIcon());
            listItemTitle.setText(app.getLabel());
        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onListItemClick(AppsManager.getApp(getAdapterPosition()));
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            //menuInfo is null
            if (getAdapterPosition() > (mColumns + 1)* 2) {
                MenuItem addtoFavouriteAction = contextMenu.add(Menu.NONE, 0,
                        Menu.NONE, "Add to favourite");
                MenuItem infoAction = contextMenu.add(Menu.NONE, 1, Menu.NONE, "Info");
                MenuItem deleteAction = contextMenu.add(Menu.NONE, 2,
                        Menu.NONE, "Delete");
                addtoFavouriteAction.setOnMenuItemClickListener(this);
                infoAction.setOnMenuItemClickListener(this);
                deleteAction.setOnMenuItemClickListener(this);
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            int id = item.getItemId();
            int recyclerId = getLayoutPosition();

            return mMenuItemListener.onListMenuItemClick(id, AppsManager.getApp(recyclerId));
        }
    }
}