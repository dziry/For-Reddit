package fr.upmc.tpdev.interfaces;

import android.widget.ImageView;
import android.widget.RelativeLayout;

public interface OnPostCardClickListener {

    void onShowDetails(RelativeLayout view, int position, int tab);
    void onUpvote(ImageView view, int position);
    void onDownvote(ImageView view, int position);
    void onShare(RelativeLayout view, int position, int tab);
}
