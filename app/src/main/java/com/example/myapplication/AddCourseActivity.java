package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AddCourseActivity extends AppCompatActivity {

    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);
        setFinishOnTouchOutside(false);

        if (getSupportActionBar() != null){//remove ActionBar
            getSupportActionBar().hide();
        }

        final EditText inputCourseName = findViewById(R.id.course_name);
        final EditText inputTeacher = findViewById(R.id.teacher_name);
        final EditText inputClassRoom = findViewById(R.id.class_room);
        final EditText inputDay = findViewById(R.id.week);
        final EditText inputStart = findViewById(R.id.classes_begin);
        final EditText inputEnd = findViewById(R.id.classes_ends);

        toolbar = findViewById(R.id.add_courses_Toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {//设置其点击事件
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button okButton = findViewById(R.id.button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String courseName = inputCourseName.getText().toString();
                String teacher = inputTeacher.getText().toString();
                String classRoom = inputClassRoom.getText().toString();
                String day = inputDay.getText().toString();
                String start = inputStart.getText().toString();
                String end = inputEnd.getText().toString();

                if (courseName.equals("") || day.equals("") || start.equals("") || end.equals("")) {
                    Toast.makeText(AddCourseActivity.this, "PLease enter your course info", Toast.LENGTH_SHORT).show();
                } else {
                    Course course = new Course(courseName, teacher, classRoom,
                            Integer.valueOf(day), Integer.valueOf(start), Integer.valueOf(end), getWeekString());

                    Log.d("he", "Week String:"+getWeekString());
                    Intent intent = new Intent(AddCourseActivity.this, Curriculum.class);
                    intent.putExtra("course", course);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }


    public String getWeekString(){//the week checkbox

//       CheckBox checkBox1 = findViewById(R.id.check_1);
//       CheckBox checkBox2 = findViewById(R.id.check_2);
//        CheckBox checkBox3 = findViewById(R.id.check_3);
//        CheckBox checkBox4 = findViewById(R.id.check_4);
//        CheckBox checkBox5 = findViewById(R.id.check_5);
//        CheckBox checkBox6 = findViewById(R.id.check_6);
//        CheckBox checkBox7 = findViewById(R.id.check_7);
//        CheckBox checkBox8 = findViewById(R.id.check_8);
//        CheckBox checkBox9 = findViewById(R.id.check_9);
//        CheckBox checkBox10 = findViewById(R.id.check_10);
//        CheckBox checkBox11 = findViewById(R.id.check_11);
//        CheckBox checkBox12 = findViewById(R.id.check_12);
//        CheckBox checkBox13 = findViewById(R.id.check_13);
//        CheckBox checkBox14 = findViewById(R.id.check_14);
//        CheckBox checkBox15 = findViewById(R.id.check_15);
//        CheckBox checkBox16 = findViewById(R.id.check_16);
//        CheckBox checkBox17 = findViewById(R.id.check_17);
//        CheckBox checkBox18 = findViewById(R.id.check_18);
//        CheckBox checkBox19 = findViewById(R.id.check_19);
//        CheckBox checkBox20 = findViewById(R.id.check_20);
//
//        String s=get_bool_w(checkBox1)+get_bool_w(checkBox2)+get_bool_w(checkBox3)+get_bool_w(checkBox4)+get_bool_w(checkBox5)
//                +get_bool_w(checkBox6)+get_bool_w(checkBox7)+get_bool_w(checkBox8)+get_bool_w(checkBox9)+get_bool_w(checkBox10)
//                +get_bool_w(checkBox11)+get_bool_w(checkBox12)+get_bool_w(checkBox13)+get_bool_w(checkBox14)+get_bool_w(checkBox15)
//                +get_bool_w(checkBox16)+get_bool_w(checkBox17)+get_bool_w(checkBox18)+get_bool_w(checkBox19)+get_bool_w(checkBox20);
        String s = "11111111111111111111";

        return s;
    }
    public String get_bool_w(CheckBox checkBox){
        if(checkBox.isChecked()){
            return "1";
        }
        else{
            return "0";
        }
    }



}
