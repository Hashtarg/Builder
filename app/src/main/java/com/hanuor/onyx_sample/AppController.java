package com.hanuor.onyx_sample;



        import android.app.Application;
        import android.content.Context;

        import com.parse.Parse;
        import com.parse.ParseInstallation;
        import com.parse.ParseUser;

public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();
    private static AppController mInstance;


    public static AppController getAppInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "ADplz15kieprgFfZ06lpx9PXLp31irLGIl0qzYwn", "poXH5hxcrP6AfivnRXTcrDzy0Fl5Bq6kUj7WuxMv");
        mInstance = this;

        if (ParseUser.getCurrentUser() != null) {
        }
        ParseInstallation.getCurrentInstallation().saveInBackground();

    }


    private static void reInitPArse(Context context) {
        Parse.initialize(new Parse.Configuration.Builder(context)
                .applicationId("octo")
                .clientKey("Holdme2@")
                .server("http://spartra.azurewebsites.net/parse/").enableLocalDataStore()
                .build()
        );
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }


}