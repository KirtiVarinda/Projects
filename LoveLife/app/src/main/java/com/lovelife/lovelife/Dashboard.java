package com.lovelife.lovelife;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lovelife.lovelife.BeanClasses.BeanRequestsFromPeople;
import com.lovelife.lovelife.BeanClasses.ChatAttachmentBeanClass;
import com.lovelife.lovelife.BeanClasses.ConnectedPartnerBean;
import com.lovelife.lovelife.ImageProcessing.ImageHandler;
import com.lovelife.lovelife.LoveChat.Chats;
import com.lovelife.lovelife.LoveLifeAbstraction.PartnerConnectionInterface;
import com.lovelife.lovelife.LoveLifeUtility.ManagePartnerConnection;
import com.lovelife.lovelife.LoveLifeUtility.SwitchActivities;
import com.lovelife.lovelife.NetworkManager.ServerSync;
import com.lovelife.lovelife.Popups.LoveLifePopups;
import com.lovelife.lovelife.SharedData.MySharedData;
import com.lovelife.lovelife.StringResources.GetUserProfileResource;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import io.github.rockerhieu.emojicon.EmojiconGridFragment;
import io.github.rockerhieu.emojicon.EmojiconsFragment;
import io.github.rockerhieu.emojicon.emoji.Emojicon;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener, PartnerConnectionInterface {

    public static boolean isReturnFromConnectToPartnerPage = false;
    DrawerLayout drawer;
    static String pendingPartnerEmail;
    public static BeanRequestsFromPeople[] beanRequestsFromPeoples1;
    static ConnectedPartnerBean connectedPartnerBean;
    TextView addPartnerActionBarIcon;
    ImageView partnerImage;
    ImageView nav_close;
    public static ManagePartnerConnection managePartnerConnection;
    private Dashboard partnerConnectionInterface;

    static String requestResultType = "";


    /**
     * variable for activity switcher (to open another activity)
     */

    SwitchActivities switchActivity;

    /**
     * user picture fro drawer
     */
    ImageView profilePic;

    /**
     * initialize activity switcher
     */
    SwitchActivities switchActivities = new SwitchActivities();
    private static int RESULT_LOAD_IMAGE = 1;
    private static int REQUEST_CAMERA = 1;
    private static int SELECT_FILE = 1;
    TextView nav_username, nav_email;
    String username, email;
    /**
     * session variable
     */
    MySharedData mySharedData;
    TabLayout tabLayout;

    /**
     * current activity instance
     */
    private Dashboard currentActivity;


    private static final String TAG = "Dashboard";

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    ServerSync serverSync;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Emojiconize.activity(this).go();
        super.onCreate(savedInstanceState);

        isReturnFromConnectToPartnerPage = false;

        setContentView(R.layout.activity_dashboard);
        /** initialize current activity variable */
        currentActivity = Dashboard.this;
        serverSync = new ServerSync();

        /** initialize session variable */
        mySharedData = new MySharedData(currentActivity);


        /** get toolbar instance*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);




        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        };

        drawer.addDrawerListener(toggle);
        toggle.syncState();
      /*  getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);*/


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);


        nav_close = (ImageView) header.findViewById(R.id.imageView4);
        nav_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("cccccclose");
                drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawers();
            }
        });

        nav_username = (TextView) header.findViewById(R.id.nav_name);
        nav_email = (TextView) header.findViewById(R.id.nav_email);


        /** up casting to parent reference*/
        partnerConnectionInterface = Dashboard.this;
        managePartnerConnection = new ManagePartnerConnection(getApplicationContext(), partnerConnectionInterface);


        /**
         * initialize activity switcher
         */
        switchActivity = new SwitchActivities();


        /**
         * Inisialize imageview To set Profile Pic on Drawer.
         */
        profilePic = (ImageView) header.findViewById(R.id.profilepic);


        /** first we load user profile pic that was save earlier */
        if (mySharedData.getGeneralSaveSession(MySharedData.saveProfileImage).equals("")) {
            if (mySharedData.getGeneralSaveSession(MySharedData.USER_GENDER).equals("Female")) {
                profilePic.setImageResource(R.drawable.girl_heart);
            } else {
                profilePic.setImageResource(R.drawable.add);
            }
        } else {

            profilePic.setImageBitmap(ImageHandler.getProfileImageFromSharedPrefernces(mySharedData));
        }

        /** code for profile pic */
        if (mySharedData.getGeneralSaveSession(MySharedData.USER_PROFILE_PIC).equals("")) {
            if (mySharedData.getGeneralSaveSession(MySharedData.USER_GENDER).equals("Female")) {
                profilePic.setImageResource(R.drawable.girl_heart);
            } else {
                profilePic.setImageResource(R.drawable.add);
            }
        } else {
            ImageHandler imageHandler = new ImageHandler();
            imageHandler.loadProfilePicForDrawer(profilePic, currentActivity);
        }


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        /** Set up the ViewPager with the sections adapter.*/
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(mViewPager);
            }
        });

        /**
         * get data (username , email) from session
         */
        username = mySharedData.getGeneralSaveSession(mySharedData.USERNAME);
        email = mySharedData.getGeneralSaveSession(mySharedData.USEREMAIL);
        System.out.println("zzzzzzzzxx" + username + email);
        /**
         * set session values(username , email) in textview
         */


        nav_username.setText(username);
        nav_email.setText(email);


    }


    private void stopLoadingInActionBar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                partnerContainer.setVisibility(View.VISIBLE);
                progressBar2.setVisibility(View.GONE);
            }
        });

    }

    private void startLoadingInActionBar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                partnerContainer.setVisibility(View.GONE);
                progressBar2.setVisibility(View.VISIBLE);
            }
        });

    }


    MenuItem filterMenuItem;
    ProgressBar progressBar2;
    RelativeLayout partnerContainer;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        /** load partner data */
        new Thread() {
            public void run() {
                startLoadingInActionBar();
                managePartnerConnection.loadRequests();
            }

        }.start();


        inflater.inflate(R.menu.profile_page, menu);
        final Menu m = menu;
        filterMenuItem = menu.findItem(R.id.partnerAdded);
        final View menu_list = filterMenuItem.getActionView();

        addPartnerActionBarIcon = (TextView) menu_list.findViewById(R.id.actionbar_notifcation_textview1);
        partnerContainer = (RelativeLayout) menu_list.findViewById(R.id.partnerContainer);
        progressBar2 = (ProgressBar) menu_list.findViewById(R.id.progressBar2);
        partnerImage = (ImageView) menu_list.findViewById(R.id.partnerImage);

        /** set tick image instead of add partner image if already connected to partner */
        if (mySharedData.getGeneralSaveSession(MySharedData.isConnectedToPartner).equals("yes")) {

            addPartnerActionBarIcon.setBackgroundResource(R.drawable.check);

        } else {
            addPartnerActionBarIcon.setBackgroundResource(R.drawable.plus);
        }


        filterMenuItem.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("ddddddddccc");
                m.performIdentifierAction(filterMenuItem.getItemId(), 0);
            }
        });
        return true;


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.partnerAdded) {

            if (requestResultType.equals("noEventOccur")) {
                Toast.makeText(Dashboard.this, "noEventOccur", Toast.LENGTH_LONG).show();
                switchActivity.openActivityWithPutExtraAndAnim(currentActivity, ConnectToPartner.class, new String[]{GetUserProfileResource.TRANSITION_TYPE}, new String[]{"Dashboard"});

            } else if (requestResultType.equals("requestIsPending")) {

                Toast.makeText(Dashboard.this, "requestIsPending", Toast.LENGTH_LONG).show();
                switchActivity.openActivityWithPutExtraAndAnim(currentActivity, ConnectToPartner.class, new String[]{GetUserProfileResource.TRANSITION_TYPE}, new String[]{"Dashboard"});

            } else if (requestResultType.equals("requestsReceived")) {

                Toast.makeText(Dashboard.this, "requestsReceived", Toast.LENGTH_LONG).show();
                switchActivity.openActivityWithPutExtraAndAnim(currentActivity, ConnectToPartner.class, new String[]{GetUserProfileResource.TRANSITION_TYPE}, new String[]{"Dashboard"});

            } else if (requestResultType.equals("onPartnerConnected")) {

                Toast.makeText(Dashboard.this, "onPartnerConnected", Toast.LENGTH_LONG).show();
                switchActivity.openActivityWithPutExtraAndAnim(currentActivity, PartnerProfile.class, new String[]{GetUserProfileResource.TRANSITION_TYPE}, new String[]{"Dashboard"});

            } else if (requestResultType.equals("onNetworkError")) {
                popup();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void popup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("Sorry!");
        builder.setMessage("Connectivity problem. Please click ok to reload");
        // builder.setCustomTitle(title);
        //builder.setView(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();

                partnerContainer.setVisibility(View.GONE);
                progressBar2.setVisibility(View.VISIBLE);

                /** load partner data */
                new Thread() {
                    public void run() {
                        startLoadingInActionBar();
                        managePartnerConnection.loadRequests();
                    }

                }.start();


            }
        });

        builder.create().show();


    }


    @Override
    public void noEventOccur() {
        System.out.println("noEventOccur ");
        requestResultType = "noEventOccur";
        stopLoadingInActionBar();
    }

    @Override
    public void requestIsPending(String partnerEmail) {
        System.out.println("requestIsPending " + partnerEmail);
        requestResultType = "requestIsPending";
        this.pendingPartnerEmail = partnerEmail;
        stopLoadingInActionBar();

    }


    @Override
    public void requestsReceived(BeanRequestsFromPeople[] beanRequestsFromPeoples) {
        System.out.println("requestsReceived " + beanRequestsFromPeoples.length);
        requestResultType = "requestsReceived";
        this.beanRequestsFromPeoples1 = beanRequestsFromPeoples;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addPartnerActionBarIcon.setBackgroundResource(R.drawable.blank_red);

                addPartnerActionBarIcon.setText("" + beanRequestsFromPeoples1.length);
            }
        });

        stopLoadingInActionBar();
    }

    @Override
    public void onPartnerConnected(final ConnectedPartnerBean connectedPartnerBean) {
        System.out.println("onPartnerConnected ");
        requestResultType = "onPartnerConnected";
        this.connectedPartnerBean = connectedPartnerBean;
        System.out.println("getPartnerProfilePicss " + connectedPartnerBean.getPartnerProfilePic());
        /** set connected partner sessions */
        mySharedData.connectedPartnerSession(connectedPartnerBean, mySharedData);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addPartnerActionBarIcon.setBackgroundResource(R.drawable.check);
                addPartnerActionBarIcon.setText("");
            }
        });

        /** set partner image in heart at action bar if connected */
        setImageFromString(connectedPartnerBean.getPartnerProfilePic(), partnerImage);


    }

    @Override
    public void onNetworkError() {
        System.out.println("onNetworkError ");
        requestResultType = "onNetworkError";
        stopLoadingInActionBar();
    }

    @Override
    public void sessionExpired() {
        LoveLifePopups.loveLifePopupFinishCurrentActivityAndLogout(currentActivity, "Alert!", getResources().getString(R.string.sessionExpired), LoginActivity.class, mySharedData);
    }


    /**
     * For getting Profile Details
     */


    public void setImageFromString(String imagePath, final ImageView partnerImage) {

        try {

            URL url = new URL(imagePath);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            InputStream is = con.getInputStream();
            final Bitmap bmp = BitmapFactory.decodeStream(is);

            if (null != bmp) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connectedPartnerBean.setPartnerImageBitmap(bmp);
                        partnerImage.setImageBitmap(bmp);

                    }
                });


                System.out.println("The Bitmap okkk");
            } else {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connectedPartnerBean.setPartnerImageBitmap(ImageHandler.drawableToBitmap(R.drawable.add, currentActivity));
                        partnerImage.setImageDrawable(getResources().getDrawable(R.drawable.add));
                    }
                });


                System.out.println("The Bitmap is NULL");
            }

        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    connectedPartnerBean.setPartnerImageBitmap(ImageHandler.drawableToBitmap(R.drawable.add, currentActivity));
                    partnerImage.setImageDrawable(getResources().getDrawable(R.drawable.add));
                }
            });

            System.out.println("error1111111" + e);
        }
        stopLoadingInActionBar();


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /**
         * After Choose image from gallery this activity will start to get image
         * path and set the image in image profilePicView.
         */
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            /** Result For Gallery */
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            /** Result for Camera */
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    /**
     * Result For Gallery
     */
    private void onSelectFromGalleryResult(Intent data) {

        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

        /**
         * set the Image PAth Into a string
         */
        String picturePath = cursor.getString(columnIndex);
        System.out.println("picture Path" + picturePath);

        /**
         * Set the Image Path Into Bean Class
         */

        ChatAttachmentBeanClass bean = new ChatAttachmentBeanClass();
        bean.setImagePath(picturePath);
        cursor.close();
        REQUEST_CAMERA = 1;
    }

    /**
     * Result for Camera
     */
    private void onCaptureImageResult(Intent data) {
        Bitmap photo = (Bitmap) data.getExtras().get("data");
        /** CALL THIS METHOD TO GET THE URI FROM THE BITMAP */
        Uri tempUri = getImageUri(getApplicationContext(), photo);
        String path = getRealPathFromURI(tempUri);
        File imgFile = new File(path);

        if (imgFile.exists()) {

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

//            ImageView myImage = (ImageView) findViewById(R.id.imageviewTest);

//            myImage.setImageBitmap(myBitmap);

        }
        SELECT_FILE = 1;
    }

    /**
     * @param inContext contect for current activity
     * @param inImage   bitmap for selected image from camera
     * @return Captured Image URL
     */

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    /**
     * For getting Captured Image Path from URL
     *
     * @param uri Captured Image URL
     * @return path of Image
     */
    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation profilePicView item clicks here.
        int id = item.getItemId();

        if (id == R.id.logout) {
            LoveLifePopups.loveLifePopupFinishCurrentActivityAndLogout(currentActivity, "Alert!", getResources().getString(R.string.logoutAlert), LoginActivity.class, mySharedData);

        }/* else if (id == R.id.nav_movie) {
        } else if (id == R.id.nav_profile) {
            switchActivities.openActivity(this, ProfilePage.class);
        } else if (id == R.id.nav_share) {
        } else if (id == R.id.nav_map) {
            switchActivities.openActivity(this, MapActivity.class);
        } else if (id == R.id.nav_setting) {
            switchActivities.openActivity(this, SettingsActivity.class);
        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private String[] tabTitles = new String[]{" Chat", " Timeline", " Suggetions"};

        private int[] imageResId = {
                R.drawable.chat,
                R.drawable.timeline,
                R.drawable.suggestion
        };


        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new Chats();
            } else if (position == 1) {
                return new Suggetions();
            } else if (position == 2) {
                return new Suggetions();
            } else {
                return PlaceholderFragment.newInstance(position + 1);
            }
        }

        @Override
        public int getCount() {
            // Show 1 total pages.
            return tabTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            Drawable image = getResources().getDrawable(imageResId[position]);
            image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
            // Replace blank spaces with image icon
            SpannableString sb = new SpannableString(" " + tabTitles[position]);
            ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
            sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return sb;
        }

    }


    @Override
    public void onBackPressed() {


        Chats.onBackPressed();

    }


    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_chat, container,
                    false);
            return rootView;
        }
    }

  /*  @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }*/


    public void setNoImage(final ImageView pic) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pic.setBackground(getResources().getDrawable(R.drawable.noimage));
            }
        });


    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {

    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        Chats.msg_edittext.getText().insert(Chats.msg_edittext.getSelectionStart(), emojicon.getEmoji());
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (isReturnFromConnectToPartnerPage) {
            new Thread() {
                public void run() {
                    startLoadingInActionBar();
                    managePartnerConnection.loadRequests();
                    isReturnFromConnectToPartnerPage = false;
                }

            }.start();
        }

    }


}
