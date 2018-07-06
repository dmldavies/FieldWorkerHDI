package com.example.titomi.workertrackerloginmodule;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.titomi.workertrackerloginmodule.alert_manager.AlertMainActivity;
import com.example.titomi.workertrackerloginmodule.attendance_module.AttendanceMainActivity;
import com.example.titomi.workertrackerloginmodule.dashboard_fragments.FragmentAttendance;
import com.example.titomi.workertrackerloginmodule.dashboard_fragments.FragmentInventory;
import com.example.titomi.workertrackerloginmodule.dashboard_fragments.FragmentTask;
import com.example.titomi.workertrackerloginmodule.dashboard_fragments.ViewPagerAdapter;
import com.example.titomi.workertrackerloginmodule.inventory_module.InventoryActivity;
import com.example.titomi.workertrackerloginmodule.report_module.ReportMainActivity;
import com.example.titomi.workertrackerloginmodule.services.FieldMonitorLocationService;
import com.example.titomi.workertrackerloginmodule.services.FieldMonitorMessagingService;
import com.example.titomi.workertrackerloginmodule.shared_pref_manager.SharedPrefManager;
import com.example.titomi.workertrackerloginmodule.supervisor.User;
import com.example.titomi.workertrackerloginmodule.supervisor.activities.ActivityInstitutionListing;
import com.example.titomi.workertrackerloginmodule.supervisor.activities.ActivityInventoryRequestsListing;
import com.example.titomi.workertrackerloginmodule.supervisor.activities.ActivityLeaveApplication;
import com.example.titomi.workertrackerloginmodule.supervisor.activities.ActivityMessageListing;
import com.example.titomi.workertrackerloginmodule.supervisor.activities.ActivityReportListing;
import com.example.titomi.workertrackerloginmodule.supervisor.activities.ActivityTaskListing;
import com.example.titomi.workertrackerloginmodule.supervisor.util.ImageUtils;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Util;
import com.example.titomi.workertrackerloginmodule.supervisor_manager.SupervisorMainActivity;
import com.example.titomi.workertrackerloginmodule.user_profile.UserProfileActivity;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.splunk.mint.Mint;

public class DashboardActivity extends AppCompatActivity {

    public FieldMonitorLocationService locationService;

    private static Context cxt;
    Toolbar toolbar;
    String EmailHolder;
    PrimaryDrawerItem mProfile, mTask, mInventory, mAttendance, mWorkerTrack, mSupervisorManager, mMessage, mAlert, mReport, mLiveChat,supervisorInventoryRequest,supervisorReports,supervisorTasks,leaveApplication,institutionManager;
    SecondaryDrawerItem mSettings, mLogout;

//    Toolbar toolbar;
SharedPrefManager sharedPrefManager;
    User loggedInUser;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.refresh:
                Toast.makeText(this, "Refreshing...", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       /* MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);*/

        return true;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            String className = name.getClassName();

            if (className.endsWith("FieldMonitorLocationService")) {
                locationService = ((FieldMonitorLocationService.LocationServiceBinder) service).getService();

                locationService.startUpdatingLocation();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (name.getClassName().equals("FieldMonitorLocationService")) {
                locationService.stopUpdatingLocation();
                locationService = null;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Crash Analytic API call. via Splunk at mint.splunk.com
        Mint.setApplicationEnvironment(Mint.appEnvironmentTesting);

        Mint.initAndStartSession(this.getApplication(), "fa0aaf30");

        setContentView(R.layout.activity_dashboard);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Dashboard");
        cxt = this;


        //Get the user serializable sent from the login activity
        Bundle extras =getIntent().getExtras();
        if(extras != null){
            loggedInUser = (User)extras.getSerializable(getString(R.string.loggedInUser));
        }
        startService(new Intent(cxt, FieldMonitorMessagingService.class).putExtra(getString(R.string.loggedInUser),loggedInUser));
        sharedPrefManager = new SharedPrefManager(getApplicationContext());

        TabLayout tabLayout =  findViewById(R.id.tabLayout_id);
        ViewPager viewPager =  findViewById(R.id.viewpager_id);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.AddFragment(new FragmentTask(), "Task");
        adapter.AddFragment(new FragmentInventory(), "Inventory");
        adapter.AddFragment(new FragmentAttendance(), "Attendance");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        final String firstNameUser = sharedPrefManager.getSavedFirstName();
        final String lastNameUser = sharedPrefManager.getSavedLastName();
        final String emailUser = sharedPrefManager.getSavedEmail();




        ImageUtils.ImageStorage storage = new ImageUtils.ImageStorage(loggedInUser);
        String imageUrl = getString(R.string.server_url)+loggedInUser.getFeaturedImage();
        String imageName = ImageUtils.getImageNameFromUrlWithExtension(imageUrl);
        Drawable imageDrawable = null;

        if(storage.imageExists(imageName)){
             String imagePath = storage.getImage(imageName).getAbsolutePath();
             imageDrawable = Drawable.createFromPath(imagePath);

         }else{
            ImageUtils.GetImages getImages = new ImageUtils.GetImages(loggedInUser,imageUrl,imageName);

            getImages.execute();
        }

         if(imageDrawable == null){
             imageDrawable = getResources().getDrawable(R.drawable.no_image);
         }

        // Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.profile_header)
                .addProfiles(
                        new ProfileDrawerItem()
                                .withName(loggedInUser.getFullName())
                                .withEmail(Util.toSentenceCase(loggedInUser.getRole()))
                                .withIcon(imageDrawable)
                )
                .withOnAccountHeaderListener((view, profile, current) -> {
                    Intent i = new Intent(cxt,UserProfileActivity.class);
                    i.putExtra(getString(R.string.loggedInUser),loggedInUser);
                    startActivity(i);
                    return false;
                })
                .build();

/*

        mProfile = new PrimaryDrawerItem().withIdentifier(1).withName("My Profile").withIcon(R.drawable.ic_person_black_24dp);
      //  mTask = new PrimaryDrawerItem().withIdentifier(2).withName("Route Plans").withIcon(R.drawable.task_nav_icon_512);
        mInventory = new PrimaryDrawerItem().withIdentifier(3).withName("Inventory Manager");
        mAttendance = new PrimaryDrawerItem().withIdentifier(4).withName("Attendance Report");
        mWorkerTrack = new PrimaryDrawerItem().withIdentifier(5).withName("Worker Tracking");
        mSupervisorManager = new PrimaryDrawerItem().withIdentifier(6).withName("Supervisor Manager");
        mMessage = new PrimaryDrawerItem().withIdentifier(7).withName("Message & Notice");
        mAlert = new PrimaryDrawerItem().withIdentifier(8).withName("Alert Manager");
        mLiveChat = new PrimaryDrawerItem().withIdentifier(9).withName("Live Chat");
        mReport = new PrimaryDrawerItem().withIdentifier(10).withName("Report & Insight");
        mSettings = new SecondaryDrawerItem().withIdentifier(11).withName("General Settings");
        mLogout = new SecondaryDrawerItem().withIdentifier(12).withName("Logout");
        supervisorInventoryRequest = new PrimaryDrawerItem().withIdentifier(13).withName("Inventory Requests");
        supervisorReports  = new PrimaryDrawerItem().withIdentifier(14).withName("Reports");
        supervisorTasks = new PrimaryDrawerItem().withIdentifier(15).withName("Tasks");
        leaveApplication = new PrimaryDrawerItem().withIdentifier(16).withName("Leave Application");
        institutionManager = new PrimaryDrawerItem().withIdentifier(17).withName("Institution Manager");
*/

        mProfile = new PrimaryDrawerItem().withIdentifier(1).withName("My Profile")
                .withTextColor(getResources().getColor(R.color.primary_dark)).withIcon(R.drawable.ic_person_black_24dp);
        mTask = new PrimaryDrawerItem().withIdentifier(2).withName("Route Plans")
                .withTextColor(getResources().getColor(R.color.primary_dark)).withIcon(R.drawable.task_nav_icon_512);
        mInventory = new PrimaryDrawerItem().withIdentifier(3).withName("Inventory Manager")
                .withTextColor(getResources().getColor(R.color.primary_dark)).withIcon(R.drawable.inventory_checklist);
        mAttendance = new PrimaryDrawerItem().withIdentifier(4).withName("Attendance Report")
                .withTextColor(getResources().getColor(R.color.primary_dark)).withIcon(R.drawable.attendance);
        mWorkerTrack = new PrimaryDrawerItem().withIdentifier(5).withName("Worker Tracking")
                .withTextColor(getResources().getColor(R.color.primary_dark));
        mSupervisorManager = new PrimaryDrawerItem().withIdentifier(6).withName("Supervisor Manager")
                .withTextColor(getResources().getColor(R.color.primary_dark));
        mMessage = new PrimaryDrawerItem().withIdentifier(7).withName("Message & Notice")
                .withTextColor(getResources().getColor(R.color.primary_dark)).withIcon(R.drawable.message);
        mAlert = new PrimaryDrawerItem().withIdentifier(8).withName("Alert Manager")
                .withTextColor(getResources().getColor(R.color.primary_dark));
        mLiveChat = new PrimaryDrawerItem().withIdentifier(9).withName("Live Chat")
                .withTextColor(getResources().getColor(R.color.primary_dark));
        mReport = new PrimaryDrawerItem().withIdentifier(10).withName("Report & Insight")
                .withTextColor(getResources().getColor(R.color.primary_dark));
        mSettings = new SecondaryDrawerItem().withIdentifier(11).withName("General Settings")
                .withTextColor(getResources().getColor(R.color.primary_dark));
        mLogout = new SecondaryDrawerItem().withIdentifier(12).withName("Logout")
                .withTextColor(getResources().getColor(R.color.primary_dark)).withIcon(R.drawable.logout);
        supervisorInventoryRequest = new PrimaryDrawerItem().withIdentifier(13).withName("Inventory Requests")
                .withTextColor(getResources().getColor(R.color.primary_dark));
        supervisorReports  = new PrimaryDrawerItem().withIdentifier(14).withName("Reports")
                .withTextColor(getResources().getColor(R.color.primary_dark)).withIcon(R.drawable.report);
        supervisorTasks = new PrimaryDrawerItem().withIdentifier(15).withName("Route Plans")
                .withTextColor(getResources().getColor(R.color.primary_dark)).withIcon(R.drawable.task_nav_icon_512);
        leaveApplication = new PrimaryDrawerItem().withIdentifier(16).withName("Leave Application")
                .withTextColor(getResources().getColor(R.color.primary_dark)).withIcon(R.drawable.leave);
        institutionManager = new PrimaryDrawerItem().withIdentifier(17).withName("Institution Manager")
                .withTextColor(getResources().getColor(R.color.primary_dark)).withIcon(R.drawable.insitution);


        DrawerBuilder drawerBuilder = new DrawerBuilder();
        drawerBuilder.withActivity(this);
        drawerBuilder.withToolbar(toolbar);
        drawerBuilder.withAccountHeader(headerResult);
    //    drawerBuilder.withSliderBackgroundColor(getResources().getColor(R.color.material_drawer_primary_light));


        switch (loggedInUser.getRoleId()){
            case User.NURSE:
                drawerBuilder.addDrawerItems(
                        mProfile,
                        new DividerDrawerItem(),
                        mTask,
                        new DividerDrawerItem(),
                        mInventory,
                        new DividerDrawerItem(),
                        mAttendance,
                        new DividerDrawerItem(),
                       /* mWorkerTrack,
                        new DividerDrawerItem(),*/
                       /* mSupervisorManager,
                        new DividerDrawerItem(),*/
                        mMessage,
                        new DividerDrawerItem(),
                     /*   mAlert,
                        new DividerDrawerItem(),*/
                      /*  mLiveChat,
                        new DividerDrawerItem(),
*/
                      /*  mSettings,
                        new DividerDrawerItem(),*/
                        mLogout
                );
                break;
            case User.SUPERVISOR:

                drawerBuilder.addDrawerItems(
                        mProfile,
                        new DividerDrawerItem(),
                        mMessage,
                        new DividerDrawerItem(),
                        /*supervisorInventoryRequest,
                        new DividerDrawerItem(),*/
                        supervisorReports,
                        new DividerDrawerItem(),
                        supervisorTasks,
                        new DividerDrawerItem(),
                        mInventory,
                        new DividerDrawerItem(),

                        institutionManager,
                        new DividerDrawerItem(),
                        mAttendance,
                        new DividerDrawerItem(),
                       /* leaveApplication,
                        new SectionDrawerItem(),*/

                     /*   mReport,
                        new SectionDrawerItem(),*/
                       /* mSettings,
                        new DividerDrawerItem(),*/
                        mLogout
                );
                break;
        }
        drawerBuilder.withOnDrawerItemClickListener((view, position, drawerItem) -> {

            if (drawerItem != null) {
                Intent intent = null;
                Long identifier = drawerItem.getIdentifier();
                switch (identifier.intValue()){
                    case 1:
                        intent = new Intent(cxt, UserProfileActivity.class);

                        break;
                    case 2:

                        intent = new Intent(cxt, ActivityTaskListing.class);

                        break;
                    case 3:

                        intent = new Intent(cxt, InventoryActivity.class);

                        break;
                    case 4:
                        intent = new Intent(cxt, AttendanceMainActivity.class);
                        break;
                    case 5:

                        break;
                        case 6:
                            intent = new Intent(cxt, SupervisorMainActivity.class);
                        break;
                    case 7:
                        intent = new Intent(cxt, ActivityMessageListing.class);

                        break;
                    case 8:
                        intent = new Intent(cxt, AlertMainActivity.class);

                        break;
                    case 9:


                        break;
                    case 10:
                        intent = new Intent(cxt, ReportMainActivity.class);
                        break;
                    case 12:
                        sharedPrefManager.clearSession();
                        sharedPrefManager.logout();
                        startActivity(new Intent(cxt, LoginActivity.class));
                        finish();
                        break;
                    case 13:
                        intent = new Intent(cxt, ActivityInventoryRequestsListing.class);

                       // startActivity(intent);
                        break;
                    case 14:
                        intent = new Intent(cxt, ActivityReportListing.class);

                      //  startActivity(intent);
                        break;
                    case 15:
                        intent = new Intent(cxt, ActivityTaskListing.class);

                     //   startActivity(intent);
                        break;
                    case 16:
                        intent = new Intent(cxt, ActivityLeaveApplication.class);

                       // startActivity(intent);
                        break;

                    case 17:
                        intent = new Intent(cxt, ActivityInstitutionListing.class);

                        // startActivity(intent);
                        break;
                }

                if (intent != null) {
                    intent.putExtra(getString(R.string.loggedInUser),loggedInUser);
                    intent.putExtra("UserFirstName", firstNameUser);
                    intent.putExtra("UserLastName", lastNameUser);
                    intent.putExtra("UserEmail", emailUser);
                    intent.putExtra("UserId", sharedPrefManager.getSavedUserId());
                    cxt.startActivity(intent);
                }
            }
            return true;
        });
        Drawer mDrawer = drawerBuilder.build();

        final Intent serviceStart = new Intent(this.getApplication(), FieldMonitorLocationService.class);
        this.getApplication().startService(serviceStart);
        this.getApplication().bindService(serviceStart, serviceConnection, Context.BIND_AUTO_CREATE);
    }


}
