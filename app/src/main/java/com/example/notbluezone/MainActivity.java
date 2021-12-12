package com.example.notbluezone;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.notbluezone.account.AccountFragment;
import com.example.notbluezone.declare.DeclareFragment;
import com.example.notbluezone.manage.ManageFragment;
import com.example.notbluezone.sqlite.ContractClass;
import com.example.notbluezone.sqlite.DatabaseHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView navigation;

    public static int user_id;
    public static int loc_id;
    public static int admin_id;

    private final String TAG = "MainActivity";

    HomeFragment homeFragment = new HomeFragment();
    DeclareFragment declareFragment = new DeclareFragment();
    ManageFragment manageFragment = new ManageFragment();
    AccountFragment accountFragment = new AccountFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Cursor cursor = dbHelper.getLoggedInAccount();
        if (cursor != null) {
            user_id = cursor.getInt(cursor.getColumnIndex(ContractClass.CUser._ID));
        }

        loadFragment(homeFragment);

        transparentStatusAndNavigation();

        navigation = findViewById(R.id.bottom_nav_bar);
        navigation.setSelectedItemId(R.id.menu_home);
        navigation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_home:
                    loadFragment(homeFragment);
                    break;
                case R.id.menu_declare:
                    if (user_id > 0)
                        loadFragment(declareFragment);
                    else
                        Toast.makeText(this, "You must be logged in to access this function!", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.menu_manage:
                    if (user_id > 0)
                        loadFragment(manageFragment);
                    else
                        Toast.makeText(this, "You must be logged in to access this function!", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.menu_account:
                    loadFragment(accountFragment);
                    break;
                default:
                    return false;
            }
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void transparentStatusAndNavigation() {
        //make full transparent statusBar
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            );
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    private void setWindowFlag(final int bits, boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }
}