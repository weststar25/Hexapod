package ssu.deslab.hexapod.history.common.util;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ssu.deslab.hexapod.history.common.model.HistoryData;

/**
 * Created by critic on 2017. 7. 17..
 */

public class DataUtil {
    public static List<HistoryData> datas = new ArrayList();

    public static void addData(HistoryData data) {
        datas.add(data);
    }

    public static List<HistoryData> getDatas(){
        return datas;
    }

    private static String getDate() {
        String dateString = "";
        String day = "";

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayNum = cal.get(Calendar.DAY_OF_WEEK);
        switch (dayNum) {
            case 1:
                day = "일요일";
                break;
            case 2:
                day = "월요일";
                break;
            case 3:
                day = "화요일";
                break;
            case 4:
                day = "수요일";
                break;
            case 5:
                day = "목요일";
                break;
            case 6:
                day = "금요일";
                break;
            case 7:
                day = "토요일";
                break;

        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
        String getTime = dateFormat.format(date);
        StringBuffer sb = new StringBuffer(getTime);
        sb.insert(13, " " + day);
        dateString = sb.toString();
        Log.d("date", dateString);
        return dateString;
    }
}
