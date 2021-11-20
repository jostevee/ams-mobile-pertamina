package com.example.basicapplication;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;

public class HomeNavigation extends AppCompatActivity {


    private static final String TAG = "HomeActivity";
    private Fragment fragment = null;
    private Class fragmentClass = null;
    private String mUsername;  //FOR UserName
    private static final String ANONYMOUS = "anonymous";
    private String myUID, UID_get;
    private CoordinatorLayout coordinatorLayout;
    private ImageView iView;
    private TextView profiles;
    private Toolbar toolbar;
    private int fragmentId;
    SharedPreferences sharedPreferences;
    String username;

    //Toolbar toolbar;
    //private TextView UserName;
    //private InterstitialAd mInterstitialAd; //Kalo ada Interstitial Ads!

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_navigation);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //toolbar.setSubtitle("Cek Pulsa"); // Set toolbar subtitle //

        // Get the previous FRAGMENT_ID code
        fragmentId = getIntent().getIntExtra("FRAGMENT_ID_NAV", 100);

        // ============================= Floating Action Button Function ============================= //
        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_new_chat);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });
        */
        // ============================= Floating Action Button Function ============================= //

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (fragmentId == 0){
            fragmentClass = AssetSOFragment.class;
            toolbar.setSubtitle("Menu SO");
        } else if (fragmentId == 1){
            fragmentClass = ConfirmationSOFragment.class;
            toolbar.setSubtitle("Asset SO");
        } else if (fragmentId == 100){
            fragmentClass = MainFragment.class;
        }

        NavigationView navigationView = findViewById(R.id.nav_view);
        setupDrawerContent(navigationView);
        //navigationView.setNavigationItemSelectedListener(this);

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        // Get the fragment object and replace the layout
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameutama, fragment).setMaxLifecycle(fragment, Lifecycle.State.RESUMED).commit();
    }

    // ======================================== Rounded Image function ======================================== //
    private Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }
    // ======================================== Rounded Image function ======================================== //

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        }

            /*
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            User = FirebaseDatabase.getInstance().getReference("User").child(myUID);
            User.keepSynced(true);

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("Status", "Last time available : "+ currentDateTimeString);
            User.updateChildren(map);
            */

        this.finish();
        super.onBackPressed();
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_navigation, menu);
        return true;
    }
    */

    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        /* ALL ITEMS IN TOOLBAR PUT OVER HERE
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if(id == R.id.action_search){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
     */

    private void setupDrawerContent(NavigationView navigationView) {
        View headerView = navigationView.getHeaderView(0);
        profiles = headerView.findViewById(R.id.username_text);
        sharedPreferences = this.getSharedPreferences("CredentialsDB", MODE_PRIVATE);
        if (sharedPreferences != null) {
            username = sharedPreferences.getString("Username", "");
            Log.e("Username", username);
            profiles.setText(getString(R.string.welcome_messages, username));
        }
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    selectDrawerItem(menuItem);
                    return true;
                });
    }

    private void selectDrawerItem(MenuItem menuItem) {
        //fragmentClass = MainFragment.class;
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();

        // ALL ICON IN NAVIGATION DRAWER PUT OVER HERE
        if (id == R.id.menuSO) {
            // Handle the menuSO action
            fragmentClass = AssetSOFragment.class;
            toolbar.setSubtitle("Menu SO");
        } else if (id == R.id.menuConfirm) {
            //Toast.makeText(this, "Masih dalam tahap pengembangan", Toast.LENGTH_SHORT).show();
            // Handle the menuConfirm action
            fragmentClass = ConfirmationSOFragment.class;
            toolbar.setSubtitle("Menu Confirm");
        } else if (id == R.id.menuAsset) {
            //Toast.makeText(this, "Masih dalam tahap pengembangan", Toast.LENGTH_SHORT).show();
            fragmentClass = AllAssetFragment.class;
            toolbar.setSubtitle("Menu Asset");
        } /* else if (id == R.id.nav_manage) {
            logout();
        }
        */

        // Make a class into a new instances
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        // Get the fragment object and replace the layout
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameutama, fragment).setMaxLifecycle(fragment, Lifecycle.State.RESUMED).commit();

        // Get the drawer object and close it
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    /*
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
     */
}