package cn.xuyuan.utillib.permission;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.subjects.PublishSubject;

public class RxPermissionFragment extends Fragment {
    private static final int PERMISSIONS_REQUEST_CODE = 42;

    // Contains all the current permission requests.
    // Once granted or denied, they are removed from it.
    private Map <String, PublishSubject <Permission>> mSubjects = new HashMap <> ();
    private boolean mLogging;

    private String[] permissions;

    public RxPermissionFragment () {
    }

    @Override
    public void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
        setRetainInstance ( true );
        if (permissions != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions ( permissions , PERMISSIONS_REQUEST_CODE );
            }
        }
    }

    /**
     * 由于低版本ACTIVITY的commit为延时任务，所以不能立即进行权限请求，需要等待fragment被载入activity后进行
     *
     * @param permissions
     */
    @TargetApi(Build.VERSION_CODES.M)
    void requestPermissions ( @NonNull String[] permissions ) {
        if (getActivity () == null) {//如果还未被加载，则记录所要请求的权限
            this.permissions = permissions;
        } else {//如果已加载，则直接取请求权限
            this.permissions = null;
            requestPermissions ( permissions , PERMISSIONS_REQUEST_CODE );
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult ( int requestCode , @NonNull String permissions[] , @NonNull int[] grantResults ) {
        super.onRequestPermissionsResult ( requestCode , permissions , grantResults );
        this.permissions = null; //清除记录的所要请求的权限
        if (requestCode != PERMISSIONS_REQUEST_CODE) return;

        boolean[] shouldShowRequestPermissionRationale = new boolean[permissions.length];

        for (int i = 0; i < permissions.length; i++) {
            shouldShowRequestPermissionRationale[i] = shouldShowRequestPermissionRationale ( permissions[i] );
        }

        onRequestPermissionsResult ( permissions , grantResults , shouldShowRequestPermissionRationale );
    }

    void onRequestPermissionsResult ( String permissions[] , int[] grantResults , boolean[] shouldShowRequestPermissionRationale ) {
        for (int i = 0, size = permissions.length; i < size; i++) {
            log ( "onRequestPermissionsResult  " + permissions[i] );
            // Find the corresponding subject
            PublishSubject <Permission> subject = mSubjects.get ( permissions[i] );
            if (subject == null) {
                // No subject found
                Log.e ( RxPermission.TAG , "RxPermissions.onRequestPermissionsResult invoked but didn't find the corresponding permission request." );
                return;
            }
            mSubjects.remove ( permissions[i] );
            boolean granted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
            subject.onNext ( new Permission ( permissions[i] , granted , shouldShowRequestPermissionRationale[i] ) );
            subject.onComplete ();
        }
    }


    public void setLogging ( boolean logging ) {
        mLogging = logging;
    }

    public PublishSubject <Permission> getSubjectByPermission ( @NonNull String permission ) {
        return mSubjects.get ( permission );
    }

    public boolean containsByPermission ( @NonNull String permission ) {
        return mSubjects.containsKey ( permission );
    }

    public void setSubjectForPermission ( @NonNull String permission , @NonNull PublishSubject <Permission> subject ) {
        mSubjects.put ( permission , subject );
    }

    void log ( String message ) {
        if (mLogging) {
            Log.d ( RxPermission.TAG , message );
        }
    }
}
