package app.funcarddeals.com;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import app.funcarddeals.com.BeanClasses.FavouritesBeanClass;
import app.funcarddeals.com.BeanClasses.ReminderBeanClass;
import app.funcarddeals.com.CustomListAdapters.FuncardFavouritesListAdapter;
import app.funcarddeals.com.CustomListAdapters.FuncardReminderListAdapter;
import app.funcarddeals.com.FuncardDatabase.FunCardDealsDatabase;
import app.funcarddeals.com.Manager.MenuManager;
import app.funcarddeals.com.Manager.StandardMenus;
import app.funcarddeals.com.Manager.SwitchActivities;

public class FuncardFavourites extends Activity implements StandardMenus{

    /**
     * variable for footer menus.
     */
    private static Activity currentActivity;



    /**
     * Variable to send data in intent
     */
    public static String FUNCARD_PRODUCT_ID = "funcard_product_id";
    public static String FUNCARD_PRODUCT_NAME = "funcard_product_name";
    public static String FUNCARD_PRODUCT_OFFERS = "funcard_product_offers";


    /**
     * general class reference for change activity.
     */
    private static SwitchActivities switchActivity;


    private MenuManager menuManager;
    private static Context context;
    private static FunCardDealsDatabase funDB;

    private static FavouritesBeanClass[] favouritesList;

    private static ListView listView;
    private static String[] allProductID;
    private static LinearLayout no_fav;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_funcard_favourites);

        context = getApplicationContext();
        menuManager = new MenuManager(context);
        listView = (ListView) findViewById(R.id.fun_favourites_list);
        no_fav = (LinearLayout) findViewById(R.id.fav_empty);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switchActivity.openActivity(currentActivity, StoreOfferDetail.class, new String[]{Stores.STORE_ID,
                        Stores.STORE_NAME, Stores.STORE_LAT, Stores.STORE_LNG, FUNCARD_PRODUCT_ID, FUNCARD_PRODUCT_NAME, FUNCARD_PRODUCT_OFFERS}
                        , new String[]{favouritesList[position].getFAVOURITES_STORE_ID(), favouritesList[position].getFAVOURITES_STORE_NAME(),
                        favouritesList[position].getFAVOURITES__STORE_LATITUDE(), favouritesList[position].getFAVOURITES_STORE_LONGITUDE(),
                        favouritesList[position].getFAVOURITES_STORE_PRODUCT_ID(),
                        favouritesList[position].getFAVOURITES_STORE_PRODUCT_NAME(), favouritesList[position].getFAVOURITES_STORE_PRODUCT_OFFER()});

            }
        });


        /**
         * current activity reference
         */
        currentActivity = FuncardFavourites.this;

        /**
         * initialize database
         */
        funDB = new FunCardDealsDatabase(context);

        /**
         * Initialize activity switcher.
         */
        switchActivity = new SwitchActivities();



    }


    /**
     * set list view.
     */
    public static void setListView() {

        /**
         * get data from favourites database
         */
        favouritesList = funDB.getAllFavourites();

        allProductID=new String[favouritesList.length];



        for (int i = 0; i < favouritesList.length; i++) {
            allProductID[i] = favouritesList[i].getFAVOURITES_STORE_PRODUCT_ID();
        }

        if(favouritesList.length==0){   /** show empty list message if no reminder */
            no_fav.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }else{                          /** show reminders in list message if reminders present*/
            no_fav.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);

            final FuncardFavouritesListAdapter catAdapter = new FuncardFavouritesListAdapter(context, R.layout.favourites_list_row, allProductID, favouritesList,currentActivity);
            listView.setAdapter(catAdapter);
        }




    }

    @Override
    public void onResume() {
        super.onResume();



        setListView();



    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.common_menus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (MenuManager.selectedMenu(id, currentActivity)) return true;

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void home(View view) {
        menuManager.goToHomePage(currentActivity, MainPage.class);
    }

    @Override
    public void favorite(View view) {

        /**
         * should not open again from same activity.
         */

        //switchActivity.openActivity(currentActivity, FuncardFavourites.class);
    }

    @Override
    public void reminder(View view) {
        switchActivity.openActivityFinishCurrent(currentActivity, FuncardReminders.class);
    }

    @Override
    public void scanner(View view) {

    }


    public void exit(View view) {
        switchActivity.openActivityFinishCurrent(currentActivity, AddFunCrad.class);
//        menuManager=new MenuManager(this);
//        menuManager.closeAppGoToDeviceHome(currentActivity);
    }

    public void menu(View view) {
        menuManager=new MenuManager(this);
        menuManager.openMenu(currentActivity);
    }
}
