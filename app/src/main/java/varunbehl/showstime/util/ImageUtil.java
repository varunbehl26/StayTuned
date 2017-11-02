package varunbehl.showstime.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.widget.ImageView;

/**
 * Created by varunbehl on 19/10/17.
 */

public class ImageUtil {


    private static final String wallpaper_resolution = "w342";
    private static String placeholder_resolution = "w92";
    private static String low_resolution = "w185";
    private static String carousal_resolution = "w300";

    private static boolean deviceOnWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return networkInfo.isConnected();
    }


    public static String getImageUri(String uri) {
        String IMAGE_POSTER_BASE_URL = "http://image.tmdb.org/t/p/" + wallpaper_resolution;
        return IMAGE_POSTER_BASE_URL + "/" + uri;
    }

    private static String getImageUri(String uri, String resolution) {
        String IMAGE_POSTER_BASE_URL = "http://image.tmdb.org/t/p/" + resolution;
        return IMAGE_POSTER_BASE_URL + "/" + uri;
    }


    public static void loadImageWithFullScreen(final Context context, final ImageView imageView1, final String url) {

//        if (deviceOnWifi(context)) {
//            Glide
//                    .with(context)
//                    .load(getImageUri(url, fulscreen_resolution))
//                    .into(imageView1);
//        } else {
//        Picasso.with(context)
//                .load(getImageUri(url, carousal_resolution)) // thumbnail url goes here
//                .into(imageView1, new Callback() {
//                    @Override
//                    public void onSuccess() {
//                        Picasso.with(context)
//                                .load(getImageUri(url, fulscreen_resolution)) // image url goes here
//                                .placeholder(imageView1.getDrawable())
//                                .into(imageView1);
//                    }
//
//                    @Override
//                    public void onError() {
//
//                    }
//                });

        if (url != null) {
            String fulscreen_resolution = "w780";
            imageView1.setImageURI(Uri.parse(getImageUri(url, fulscreen_resolution)));

//            Glide
//                    .with(context)
//                    .load(getImageUri(url, fulscreen_resolution))
//                    .thumbnail(
//                            Glide
//                                    .with(context)
//                                    .load(getImageUri(url, carousal_resolution))
//                    )
//                    .apply(new RequestOptions()
//                            .placeholder(R.drawable.placeholder)
//                            .dontTransform())
//                    .into(imageView1);

//        }
        }
    }

    public static void loadImage(final Context context, final ImageView imageView1, final String url) {

//        if (deviceOnWifi(context)) {
//            Glide
//                    .with(context)
//                    .load(getImageUri(url, wallpaper_resolution))
//                    .into(imageView1);
//        } else {

//        Picasso.with(context)
//                .load(getImageUri(url, placeholder_resolution)) // thumbnail url goes here
//                .into(imageView1, new Callback() {
//                    @Override
//                    public void onSuccess() {
//                        Picasso.with(context)
//                                .load(getImageUri(url, wallpaper_resolution)) // image url goes here
//                                .placeholder(imageView1.getDrawable())
//                                .into(imageView1);
//                    }
//
//                    @Override
//                    public void onError() {
//
//                    }
//                });

        if (url != null) {
            imageView1.setImageURI(Uri.parse(getImageUri(url)));
//
//            Glide
//                    .with(context)
//                    .load(getImageUri(url, wallpaper_resolution))
//                    .thumbnail(
//                            Glide
//                                    .with(context)
//                                    .load(getImageUri(url, placeholder_resolution))
//                    )
//                    .apply(new RequestOptions()
//                            .placeholder(R.drawable.placeholder)
//                            .dontTransform())
//                    .into(imageView1);


        }
    }

    public static void loadImageWithoutThumbnail(final Context context, final ImageView imageView1, final String url) {

        if (url != null)
            imageView1.setImageURI(Uri.parse(getImageUri(url)));


//        if (deviceOnWifi(context)) {
//            Glide
//                    .with(context)
//                    .load(getImageUri(url, wallpaper_resolution))
//                    .into(imageView1);
//        } else {

//        Picasso.with(context)
//                .load(getImageUri(url, wallpaper_resolution)) // thumbnail url goes here
//                .into(imageView1);


//        Glide
//                .with(context)
//                .load(getImageUri(url, wallpaper_resolution))
//                .apply(new RequestOptions()
//                        .placeholder(R.drawable.placeholder)
//                        .dontTransform())
//                .into(imageView1);

        //   }
    }

    public static void loadImageWithFullUrl(Context context, ImageView imageView1, String url) {
        if (url != null) {
            imageView1.setImageURI(Uri.parse((url)));

//            if (deviceOnWifi(context)) {
//                Glide
//                        .with(context)
//                        .load(url)
//                        .apply(new RequestOptions()
//                                .placeholder(R.drawable.placeholder)
//                                .dontTransform())
//                        .into(imageView1);
//            } else {
//                Glide
//                        .with(context)
//                        .load((url))
//                        .thumbnail(
//                                Glide
//                                        .with(context)
//                                        .load(url)
//                        )
//                        .apply(new RequestOptions()
//                                .placeholder(R.drawable.placeholder)
//                                .dontTransform())
//                        .into(imageView1);
//
//            }
        }
    }



    /*
    "backdrop_sizes": [
  "w300",
  "w780",
  "w1280",
  "original"
],
"logo_sizes": [
  "w45",
  "w92",
  "w154",
  "w185",
  "w300",
  "w500",
  "original"
],
"poster_sizes": [
  "w92",
  "w154",
  "w185",
  "w342",
  "w500",
  "w780",
  "original"
],
"profile_sizes": [
  "w45",
  "w185",
  "h632",
  "original"
],
"still_sizes": [
  "w92",
  "w185",
  "w300",
  "original"
]
     */
}
