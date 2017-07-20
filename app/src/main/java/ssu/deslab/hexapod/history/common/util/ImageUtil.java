package ssu.deslab.hexapod.history.common.util;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by critic on 2017. 7. 17..
 */

public class ImageUtil {
    public static void loadImage(ImageView imageView, String url, Drawable errorDrawable) {
        Glide.with(imageView.getContext()).load(url).error(errorDrawable).into(imageView);
    }
}
