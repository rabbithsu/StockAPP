package edu.nccu.cs.nccustock;

import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.orm.SugarApp;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import edu.nccu.cs.nccustock.common.DebugLog;
import edu.nccu.cs.nccustock.common.NumberHelper;
import edu.nccu.cs.nccustock.data.StockPriceGen;
import edu.nccu.cs.nccustock.entity.FeaturedStock;
import edu.nccu.cs.nccustock.entity.StockInfoEntity;
import edu.nccu.cs.nccustock.parse.eneity.StockInfo;
import edu.nccu.cs.nccustock.entity.StockInfoHelper;

/**
 * Created by mrjedi on 2015/6/9.
 */
public class NCCUStockApplication extends SugarApp {
    @Override
    public void onCreate() {
        super.onCreate();
        DebugLog.log("NCCUStockApplication", "Oncreate!");
        //Parse.initialize(this, "iidApwONEHQHPP4TdnaGUlbcl796vH5ob3KHCJkF", "AtAVxqtkN26lUaPxGlWiZb0pmsbjipoJafdH2bW1");
        //ParseUser.enableRevocableSessionInBackground();
        initImageLoader(this);

    }



    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }
}
