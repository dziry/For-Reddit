package fr.upmc.tpdev.interfaces;

import android.widget.ImageView;
import android.widget.RelativeLayout;

public interface OnPostDetailsClickListener {
    void onUrl(RelativeLayout view, int position);
    void onUpvote(ImageView view, int position);
    void onDownvote(ImageView view, int position);
    void onShare(RelativeLayout view, int position);
}
