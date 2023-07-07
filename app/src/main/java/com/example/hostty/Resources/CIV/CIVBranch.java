package com.example.hostty.Resources.CIV;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.example.hostty.Account.LoginActivity;
import com.example.hostty.R;
import com.example.hostty.Resources.CS.CSBranch;
import com.example.hostty.Resources.CS.CSCourseAdapter;
import com.example.hostty.Resources.CourseClass;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

public class CIVBranch extends AppCompatActivity {

    private static final String TAG = CIVBranch.class.getSimpleName();
    private ArrayList<CourseClass> civCourseList;
    private CIVCourseAdapter civCIVCourseAdapter;

    private DatabaseReference civCourseDocRef;
    private FirebaseUser civCourseUser;
    private DatabaseReference civCourseRef;
    private ShimmerFrameLayout mShimmerViewContainer;

    private TextView emptyCourses;
    private FloatingActionButton civAddCourse;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.branch_listview);
        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);

        civInit();
        updateCourses();

        civAddCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addCourse();
            }
        });

    }

    private void addCourse() {


        final Dialog dialog = new Dialog(CIVBranch.this);
        dialog.setContentView(R.layout.add_course);
        dialog.setTitle(" Add Course ");
        dialog.setCancelable(true);


        final EditText courseNameEt = dialog.findViewById(R.id.course_add_name);
        final EditText courseNoEt = dialog.findViewById(R.id.course_add_no);
        Button addButton = dialog.findViewById(R.id.course_add_btn);


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.setCancelable(false);

                final String name;
                final String no;

                name = courseNameEt.getText().toString().trim();
                no = courseNoEt.getText().toString().trim();

                if (name.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fill Course name ", Toast.LENGTH_SHORT).show();
                    courseNameEt.requestFocus();
                    dialog.setCancelable(true);
                    return;
                }
                if (no.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fill Course no ", Toast.LENGTH_SHORT).show();
                    courseNoEt.requestFocus();
                    dialog.setCancelable(true);
                    return;
                }

                mShimmerViewContainer.startShimmer();

                Calendar calendar = Calendar.getInstance();
                final String date = new SimpleDateFormat("dd MMM yy h:mm:ss a", Locale.US).format(calendar.getTime());


                final String username = civCourseUser.getDisplayName();

                CourseClass newCourse = new CourseClass(name, no, date, username, "CSE");


                String userReference = civCourseRef.push().getKey();
                civCourseDocRef.child(name).push();

                assert userReference != null;
                civCourseRef.child(userReference).setValue(newCourse).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed to add Course", Toast.LENGTH_SHORT).show();
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        CourseClass newCourse = new CourseClass(name, no, date, username, "CIVIL");
                        civCourseList.add(newCourse);
                        civCIVCourseAdapter.notifyDataSetChanged();

                        if (emptyCourses.getVisibility() == View.VISIBLE) {
                            emptyCourses.setVisibility(View.GONE);
                        }
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);
                    }
                });
                dialog.dismiss();


            }
        });

        dialog.show();

    }


    private void updateCourses() {


        mShimmerViewContainer.startShimmer();
        civCourseRef.limitToLast(20).orderByChild("dateCreated").addListenerForSingleValueEvent(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.exists()) {
                    civCourseList.clear();

                    emptyCourses.setVisibility(View.GONE);

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Log.d(TAG, "onDataChange: reached");
                        CourseClass courses = snapshot.getValue(CourseClass.class);

                        if (courses != null) {
                            civCourseList.add(courses);
                        }

                    }
                    Collections.reverse(civCourseList);
                    civCIVCourseAdapter.notifyDataSetChanged();

                } else {
                    emptyCourses.setVisibility(View.VISIBLE);
                }
                mShimmerViewContainer.stopShimmer();
                mShimmerViewContainer.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.e(TAG, "Failed to read value.", databaseError.toException());
                mShimmerViewContainer.stopShimmer();
                mShimmerViewContainer.setVisibility(View.GONE);
            }
        });


    }

    private void civInit() {

        emptyCourses = findViewById(R.id.branch_course_empty);
        ListView listView = findViewById(R.id.branch_list_view);
        civAddCourse = findViewById(R.id.course_add_fab);
        civCourseList = new ArrayList<>();

        FirebaseAuth civCourseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase civCourseInstance = FirebaseDatabase.getInstance();
        DatabaseReference civCourseRootRef = civCourseInstance.getReference("Resources");
        civCourseRef = civCourseRootRef.child("Courses").child("CIVIL").getRef();
        civCourseDocRef = civCourseRootRef.child("CourseDocs").child("CIVIL").getRef();
        civCourseUser = civCourseAuth.getCurrentUser();


        if (civCourseUser == null) {
            startActivity(new Intent(CIVBranch.this, LoginActivity.class));
            finish();
        }


        civCIVCourseAdapter = new CIVCourseAdapter(this, civCourseList);
        listView.setAdapter(civCIVCourseAdapter);
    }

    protected void onResume() {
        super.onResume();

    }

    public boolean onCreateOptionsMenu(Menu menu) {

        androidx.appcompat.app.ActionBar FSportsActionBar = getSupportActionBar();
        assert FSportsActionBar != null;
        FSportsActionBar.setHomeButtonEnabled(true);
        FSportsActionBar.setDisplayHomeAsUpEnabled(true);
        FSportsActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#5cae80")));
        FSportsActionBar.setTitle(Html.fromHtml("<font color='#ffffff'>CIVIL</font>"));
        return super.onCreateOptionsMenu(menu);

    }

    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return true;

    }
}
