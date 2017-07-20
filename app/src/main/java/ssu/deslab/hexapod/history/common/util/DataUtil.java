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
    public static List<HistoryData> getDatas(){

        List<HistoryData> datas = new ArrayList();

        datas.add(new HistoryData(getDate(), "숭실대", "http://cfile25.uf.tistory.com/image/251F6B4C558E627E26807B"));
        datas.add(new HistoryData(getDate(), "숭실대", "http://image.celebtide.com/celeb/new/ve/279_ve_1452259300.jpg"));
        datas.add(new HistoryData(getDate(), "숭실대", "http://res.heraldm.com/content/image/2013/12/01/20131201000224_0.jpg"));
        datas.add(new HistoryData(getDate(), "숭실대", "http://cfile8.uf.tistory.com/image/194599374F7049A9010251"));

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
