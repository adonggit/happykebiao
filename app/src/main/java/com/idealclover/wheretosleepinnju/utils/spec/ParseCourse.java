package com.idealclover.wheretosleepinnju.utils.spec;

import android.util.Log;

import com.idealclover.wheretosleepinnju.app.Constant;
import com.idealclover.wheretosleepinnju.app.Url;
import com.idealclover.wheretosleepinnju.data.bean.Course;
import com.idealclover.wheretosleepinnju.data.bean.CourseTime;
import com.idealclover.wheretosleepinnju.data.db.CoursesPsc;
import com.idealclover.wheretosleepinnju.utils.LogUtil;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * NJU课程解析
 * Created by mnnyang on 17-10-19.
 * Changed by idealclover on 18-07-07
 */

public class ParseCourse {
    private static final Pattern pattern1 = Pattern.compile("第\\d{1,2}-\\d{1,2}节");
    private static final Pattern pattern2 = Pattern.compile("\\d{1,2}-\\d{1,2}周");
    private static final Pattern pattern3 = Pattern.compile("第\\d{1,2}周");

    public static String parseViewStateCode(String html) {
        String code = "";
        Document doc = org.jsoup.Jsoup.parse(html);
        Elements inputs = doc.getElementsByAttributeValue("name", Url.__VIEWSTATE);
        if (inputs.size() > 0) {
            code = inputs.get(0).attr("value");
            LogUtil.d(CoursesPsc.class, "finded __VIEWSTATE code=" + code);
        } else {
            LogUtil.d(CoursesPsc.class, "Not find __VIEWSTATE code");
        }

        return code;
    }

    /**
     * @param html
     * @return 解析失敗返回空
     */
    public static CourseTime parseTime(String html) {
        Document doc = org.jsoup.Jsoup.parse(html);
        CourseTime courseTime = new CourseTime();

        Elements elements = doc.getElementsMatchingOwnText("^20[0-9][0-9]-20[0-9][0-9]学年第(一|二)学期$?");
        if(elements == null){
            LogUtil.e(ParseCourse.class, "get the course time failed ");
            return null;
        }

        for(Element x: elements){
            String year = x.text().substring(0, 9);
            String term = "第" +  x.text().substring(12, 13) + "学期";
            courseTime.years.add(year);
            courseTime.terms.add(term );
            courseTime.selectYear = year;
            courseTime.selectTerm = term;
        }
        return courseTime;
    }

    /**
     * @param html
     * @return 解析失败返回空
     */
    public static ArrayList<Course> parse(String html) {

        Document doc = org.jsoup.Jsoup.parse(html);

        Elements table1 = doc.select("tr.TABLE_TR_01");
        table1.addAll(doc.select("tr.TABLE_TR_02"));
        ArrayList<Course> courses = new ArrayList<>();

        int node = 0;
        for(Element tr: table1){
            ArrayList<Course> temp = new ArrayList<>();
            String timeAndPlace = tr.child(5).html();
            timeAndPlace = timeAndPlace.replaceAll("<br>", "\n");
            // Deal with the case that the time and place is empty
            if(timeAndPlace.isEmpty()){
                continue;
            }
            parseTimeAndClassroom(temp, timeAndPlace, node);
            for(Course course: temp) {
                course.setName(tr.child(2).text());
                course.setTeacher(tr.child(4).text());
            }
            courses.addAll(temp);
        }
        return courses;
    }

    private static void parseTimeAndClassroom(ArrayList<Course> courses, String time, int htmlNode) {
        String infos [] = time.split("\n");
        for(String info: infos){
            Course course = new Course();
            String str [] = info.split(" ");
            //week pattern "周一"
            if (info.charAt(0) == '周') {
                String weekStr = info.substring(0, 2);
                int week = getIntWeek(weekStr);
                course.setWeek(week);
            }
            //节数 pattern "3-5节"
            Matcher matcher = pattern1.matcher(info);
            if (matcher.find()) {
                String nodeInfo = matcher.group(0);
                String[] nodes = nodeInfo.substring(1, nodeInfo.length() - 1).split("-");
                course.setNodes(nodes);
            }
            //单双周 pattern "单周" "双周"
            if (info.contains("单周")) {
                course.setWeekType(Course.WEEK_SINGLE);
                course.setStartWeek(1);
                course.setEndWeek(17);
            } else if (info.contains("双周")) {
                course.setWeekType(Course.WEEK_DOUBLE);
                course.setStartWeek(1);
                course.setEndWeek(17);
            }
            //周数 pattern "1-17周"
            matcher = pattern2.matcher(info);
            if (matcher.find()) {
                String weekInfo = matcher.group(0);//第2-16周
                if (weekInfo.length() < 2) {
                    return;
                }
                String[] weeks = weekInfo.substring(0, weekInfo.length() - 1).split("-");

                if (weeks.length > 0) {
                    int startWeek = Integer.decode(weeks[0]);
                    course.setStartWeek(startWeek);
                }
                if (weeks.length > 1) {
                    int endWeek = Integer.decode(weeks[1]);
                    course.setEndWeek(endWeek);
                }
            }
            //TMD的坑爹教务系统 针对“第2周 第4周 第6周”的特殊修改
            matcher = pattern3.matcher(info);
            if (matcher.find()) {
                String startweek = matcher.group(0);//第2周
                startweek = startweek.substring(1, startweek.length() - 1); //提取
                int startWeek = Integer.decode(startweek);
                String endweek = matcher.group(0);
                int classes = 0;
                int endWeek;

                if(!matcher.find()){
                    //“从第3周开始 单周
                    endWeek = 17;
                }else {
                    while (matcher.find()) {
                        endweek = matcher.group(0);
                        classes++;
                    }
                    endweek = endweek.substring(1, endweek.length() - 1); //提取
                    endWeek = Integer.decode(endweek);

                    if (endWeek - startWeek == classes * 2) {
                        if (startWeek % 2 == 1) {
                            course.setWeekType(Course.WEEK_SINGLE);
                        } else {
                            course.setWeekType(Course.WEEK_DOUBLE);
                        }
                    }
                }
                course.setStartWeek(startWeek);
                course.setEndWeek(endWeek);
            }
            //地点
            course.setClassRoom(str[str.length - 1]);
            courses.add(course);
        }
    }

    /**
     * 汉字转换int
     */
    private static int getIntWeek(String chinaWeek) {
        for (int i = 0; i < Constant.WEEK.length; i++) {
            if (Constant.WEEK[i].equals(chinaWeek)) {
                return i;
            }
        }
        return 0;
    }
}
