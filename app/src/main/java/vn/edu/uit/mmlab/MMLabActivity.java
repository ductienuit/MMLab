package vn.edu.uit.mmlab;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;
import vn.edu.uit.mmlab.adapter.AlgorithmPager;

import static cn.pedant.SweetAlert.SweetAlertDialog.*;

public class MMLabActivity extends AppCompatActivity {
    private final String[] ALGORITHM = {"Face Detection", "Face Recognition", "Image Search", "Object Detection", "Personal attribute", "Upload Gallery"};
    PagerSlidingTabStrip tabs;
    ViewPager pager;
    Toolbar toolbar;
    Button btnAlgorithm;
    int REQUEST_WRITE_PERMISSION=4;

    private AlgorithmPager adapter;
    private Drawable oldBtnBackground = null;
    private Drawable evenBtnBackgroud = null;

    String mCurrentPhotoPath;

    private int REQUEST_CODE_IMAGE = 2;
    private int REQUEST_IMAGE_CAPTURE=4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mmlab);

        addControl();
        marginLayout();
        pager.setCurrentItem(1);
        addEvent();
    }

    private void addEvent() {

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            /**
             * This method will be invoked when a new page becomes selected.
             */
            @Override
            public void onPageSelected(int position) {
                btnAlgorithm.setText(ALGORITHM[position]);
                changeColor(position);
            }

            /**
             * Called when the scroll state changes.
             */
            @Override
            public void onPageScrollStateChanged(int state) {

                //TODO
            }

            /**
             * This method will be invoked when the current page is scrolled,
             either as part of a programmatically initiated
             smooth scroll or a user initiated touch scroll.
             */
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //This method will be invoked when the current page is scrolled,
                // either as part of a programmatically initiated
                // smooth scroll or a user initiated touch scroll.
                //TODO
            }
        });

        btnAlgorithm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(MMLabActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Let's start with "+ ALGORITHM[pager.getCurrentItem()])
                        .setContentText("Get a photo from ...")
                        .setConfirmText("Camera")
                        .setCancelText("In your phone")
                        /** LAZY CODE
                         * Handle event click Camera button
                         */
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                dispatchTakePictureIntent();
                            }
                        })
                        /** LAZY CODE
                         * Handle event click In Your Phone button
                         */
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                requestPermission();
                            }
                        })
                        .show();
            }
        });
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
        } else {
            openFileBrowser();
        }
    }

    private void openFileBrowser() {
        /*CODE BELOW IS OPEN FILE VIA THE SYSTEM'S FILE*/

//        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
//        // browser.
//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);//Intent.ACTION_PICK
//
//        // Filter to only show results that can be "opened", such as a
//        // file (as opposed to a list of contacts or timezones)
//        intent.addCategory(Intent.CATEGORY_OPENABLE);

        /*CODE BELOW IS OPEN IMAGE IN GALLERY*/

        // ACTION_PICK is the intent to choose a file in Gallery
        Intent intent = new Intent(Intent.ACTION_PICK);//Intent.ACTION_PICK
        intent.setType("image/*");

        startActivityForResult(intent, REQUEST_CODE_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openFileBrowser();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_CODE_IMAGE)
        {
            if(resultCode == Activity.RESULT_OK) {
                //Handle Sucessful
                Uri uri = null;
                if (data != null) {
                    uri = data.getData();

                    Log.i(MMLabActivity.class.getSimpleName(), "Uri: " + uri.toString());

                    Intent intent = new Intent(MMLabActivity.this,ConfirmActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putString("imageFrom","local");
                    bundle.putString("uriImage",uri.toString());
                    intent.putExtra("data",bundle);
                    startActivity(intent);
                }
            } else {
                //Failure
                Toast.makeText(this, "Hey Man, System's error", Toast.LENGTH_SHORT).show();
            }
        }
        else if(requestCode == REQUEST_IMAGE_CAPTURE)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                Intent intent = new Intent(MMLabActivity.this,ConfirmActivity.class);

                File f = new File(mCurrentPhotoPath);
                Uri contentUri = Uri.fromFile(f);

                Bundle bundle = new Bundle();

                bundle.putString("imageFrom","camera");

                bundle.putString("uriImage",contentUri.toString());

                intent.putExtra("data",bundle);

                startActivity(intent);
            }
        }
    }

    private void galleryAddPic() {
        //TODO
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void changeColor(int position) {
        Drawable newDrawable;
        if (position % 2 == 0) {
            newDrawable = evenBtnBackgroud;
        } else {
            newDrawable = oldBtnBackground;
        }

        btnAlgorithm.setBackground(newDrawable);
    }

    private void marginLayout() {
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);
    }

    private void addControl() {
        btnAlgorithm = findViewById(R.id.btnAlgorithm);
        toolbar = findViewById(R.id.toolbar);
        tabs = findViewById(R.id.tabs);
        pager = findViewById(R.id.pager);

        setSupportActionBar(toolbar);
        adapter = new AlgorithmPager(getSupportFragmentManager());
        pager.setAdapter(adapter);
        tabs.setViewPager(pager);

        oldBtnBackground = getDrawable(R.drawable.button_style_tab_even);
        evenBtnBackgroud = getDrawable(R.drawable.button_style_tab_old);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_contact:
                //TODO
                Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putInt("currentColor", currentColor);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //currentColor = savedInstanceState.getInt("currentColor");
        //changeColor(currentColor);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }
}
