package com.example.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Curriculum extends AppCompatActivity {

    //week number
    private RelativeLayout day;

    //SQLite Helper
    private DatabaseHelper databaseHelper;

    int WEEK = 0;//number of this week
    ImageView imageView;
    String change_day;//last edition week

    int currentCoursesNumber = 0;
    int maxCoursesNumber = 0;
    Toolbar myToolbar;
    Spinner spinner;

    List<View> viewList1 ;
    List<View> viewList2 ;
    private SharedPreferences preferences;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        databaseHelper = new DatabaseHelper
                (this, "database.db", null, 1);


        //return the date info
        Calendar calendar =  Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String the_day = simpleDateFormat.format(calendar.getTime());//Today
        //read the total number of week
        preferences = getSharedPreferences("week_location", Context.MODE_PRIVATE);
        WEEK = preferences.getInt("week",0);
        change_day = preferences.getString("change_day",the_day);//last edition week

        if(calendar.get(Calendar.DAY_OF_WEEK)== Calendar.MONDAY){
            if(!change_day.equals(the_day)){
                ++WEEK;
                //store the current week number
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("week",WEEK);
                editor.putString("change_day",the_day);
                editor.commit();
            }
        }

        myToolbar= findViewById(R.id.myToolbar3);
        //clear
        Menu menu = myToolbar.getMenu();
        menu.clear();
        //load the toolbar
        myToolbar.inflateMenu(R.menu.toolbar3);
        myToolbar.setTitle("Calendar");

        myToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.add_courses:
                        Intent intent = new Intent(Curriculum.this, AddCourseActivity.class);
                        startActivityForResult(intent, 0);
                        break;
                    case R.id.menu_delet:
                        onDeleteAllClic();
                        break;
                    case R.id.login:
                        Intent login = new Intent(Curriculum.this, LoginActivity.class);
                        startActivityForResult(login, 0);
                        break;
                }

                return true;
            }
        });
        spinner = findViewById(R.id.week_spinner);
        spinner.setSelection(WEEK);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(Curriculum.this,"You are located in the NO."+(position+1)+" week",Toast.LENGTH_SHORT).show();
//                clearView();//clear the view
                //load data from the database
                loadData(date(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        imageView = findViewById(R.id.kc_location);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WEEK=spinner.getSelectedItemPosition();

                //change the number of the date
                Calendar calendar =  Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                change_day = simpleDateFormat.format(calendar.getTime());

                //store current week
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("week",WEEK);
                editor.putString("change_day",change_day);
                editor.commit();

                Toast.makeText(Curriculum.this,"Success adding this week as the initial week",Toast.LENGTH_SHORT).show();
            }
        });
    }


    private ArrayList<Course>  date(int n){
        ArrayList<Course> coursesList = new ArrayList<>(); //the list of the course
        SQLiteDatabase sqLiteDatabase =  databaseHelper.getWritableDatabase();
        //Cursor cursor = sqLiteDatabase.rawQuery("select * from courses", null);

        Cursor cursor = sqLiteDatabase.query("courses",null,null,null,null, null,null );
        if (cursor.moveToFirst()) {
            do {
                coursesList.add(new Course(
                        cursor.getString(cursor.getColumnIndex("course_name")),
                        cursor.getString(cursor.getColumnIndex("teacher")),
                        cursor.getString(cursor.getColumnIndex("class_room")),
                        cursor.getInt(cursor.getColumnIndex("day")),
                        cursor.getInt(cursor.getColumnIndex("class_start")),
                        cursor.getInt(cursor.getColumnIndex("class_end")),
                        cursor.getString(cursor.getColumnIndex("week"))
                ));
            } while(cursor.moveToNext());
        }
        cursor.close();

        //find the best position for save curriculum
        ArrayList<Course> coursesList2 = new ArrayList<>();

        for(Course course :coursesList){
            String s = course.getWeek();
            String s_week ;
            if(n<20){
                s_week = s.substring(n,n+1);
            }
            else{
                s_week = s.substring(n);//get the end of week
            }


            if(s_week.equals("1")){
                coursesList2.add(course);
            }
        }

        return coursesList2;
    }



    //load the data from the database
    private void loadData(ArrayList<Course> coursesList) {
        //load the curriculum to the context view
        for (Course course : coursesList) {
           viewList1= createLeftView(course);
           viewList2= createItemCourseView(course);
        }
    }

    //save the data from the database
    private void saveData(Course course) {
        SQLiteDatabase sqLiteDatabase =  databaseHelper.getWritableDatabase();
        sqLiteDatabase.execSQL
                ("insert into courses(course_name, teacher, class_room, day, class_start, class_end, week) " + "values(?, ?, ?, ?, ?, ?, ?)",
                        new String[] {course.getCourseName(),
                                course.getTeacher(),
                                course.getClassRoom(),
                                course.getDay()+"",
                                course.getStart()+"",
                                course.getEnd()+"",
                                course.getWeek()}
                );
    }

    //create the view of specific curriculum
    private  List<View> createLeftView(Course course) {
        int endNumber = course.getEnd();
        List<View> viewList =new ArrayList<View>();
        if (endNumber > maxCoursesNumber) {
            for (int i = 0; i < endNumber-maxCoursesNumber; i++) {
                View view;
                view= View.inflate(Curriculum.this, R.layout.left_view, null);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(110,180);
                view.setLayoutParams(params);

                TextView text = view.findViewById(R.id.class_number_text);
                text.setText(String.valueOf(++currentCoursesNumber));

                LinearLayout leftViewLayout = findViewById(R.id.left_view_layout);
                leftViewLayout.addView(view);
                viewList.add(view);
            }
            maxCoursesNumber = endNumber;
        }
        return viewList;
    }

    //create single card of course
    private  List<View> createItemCourseView(final Course course) {
        int getDay = course.getDay();
        List<View> viewList =new ArrayList<View>();
        if ((getDay < 1 || getDay > 7) || course.getStart() > course.getEnd()){
            Toast.makeText(this, "wrong info of courses week", Toast.LENGTH_LONG).show();
            SQLiteDatabase sqLiteDatabase =  databaseHelper.getWritableDatabase();
            sqLiteDatabase.execSQL("delete from courses where course_name = ?", new String[] {course.getCourseName()});
        }
        else {
            int dayId = 0;
            switch (getDay) {
                case 1: dayId = R.id.monday; break;
                case 2: dayId = R.id.tuesday; break;
                case 3: dayId = R.id.wednesday; break;
                case 4: dayId = R.id.thursday; break;
                case 5: dayId = R.id.friday; break;
                case 6: dayId = R.id.saturday; break;
                case 7: dayId = R.id.weekday; break;
            }
            day = findViewById(dayId);

            int height = 180;

            final View v = View.inflate(Curriculum.this, R.layout.course_card, null); //load the frame of curriculum
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                v.setY(height * (course.getStart()-1)); //set the position
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                    (ViewGroup.LayoutParams.MATCH_PARENT,(course.getEnd()-course.getStart()+1)*height - 8); //set the high of curriculum
            v.setLayoutParams(params);
            TextView text = v.findViewById(R.id.text_view);
            text.setText(course.getCourseName() + "\n" + course.getTeacher() + "\n" + course.getClassRoom()); //display the name of course
            day.addView(v);
            viewList.add(v);
            //long press for deleting
            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View v) {
                    //pop out info
                    new AlertDialog.Builder(Curriculum.this)
                            .setMessage("delete？")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                  //delete
                                    v.setVisibility(View.GONE);//invisible
                                    day.removeView(v);//remove the view
                                    SQLiteDatabase sqLiteDatabase =  databaseHelper.getWritableDatabase();
                                    sqLiteDatabase.execSQL("delete from courses where course_name = ?", new String[] {course.getCourseName()});


                                }
                            }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();//create and display the view
                    return true;
                }
            });
        }
        return viewList;
    }

    private void onDeleteAllClic(){
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you wanna clear this curriculum？")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase sqLiteDatabase =  databaseHelper.getWritableDatabase();
                        sqLiteDatabase.delete("courses", null, null);
                        sqLiteDatabase.execSQL("update sqlite_sequence set seq=0 where name='courses'");

                        //clear all the view
                        clearView();
                        Toast.makeText(Curriculum.this,"Finished",Toast.LENGTH_SHORT).show();

                    }
                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }


    public void clearView(){

        //课程删除
        RelativeLayout day1 = findViewById(R.id.monday);
        RelativeLayout day2 = findViewById(R.id.tuesday);
        RelativeLayout day3 = findViewById(R.id.wednesday);
        RelativeLayout day4 = findViewById(R.id.thursday);
        RelativeLayout day5 = findViewById(R.id.friday);
        RelativeLayout day6 = findViewById(R.id.saturday);
        RelativeLayout day7 = findViewById(R.id.weekday);
        day1.removeAllViews();
        day2.removeAllViews();
        day3.removeAllViews();
        day4.removeAllViews();
        day5.removeAllViews();
        day6.removeAllViews();
        day7.removeAllViews();


        currentCoursesNumber=0;//当前课程归0
        maxCoursesNumber=0;//左侧数目归0
        //左侧节数删除
        LinearLayout left_view = findViewById(R.id.left_view_layout);
        left_view.removeAllViewsInLayout();
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK && data != null) {
            Course course = (Course) data.getSerializableExtra("course");
            //存储数据到数据库
            saveData(course);
            clearView();//清空视图
            //从数据库读取数据
            loadData(date(spinner.getSelectedItemPosition()));

        }
    }

}
