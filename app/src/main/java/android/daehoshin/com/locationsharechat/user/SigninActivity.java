package android.daehoshin.com.locationsharechat.user;

import android.Manifest;
import android.content.Intent;
import android.daehoshin.com.locationsharechat.BuildConfig;
import android.daehoshin.com.locationsharechat.R;
import android.daehoshin.com.locationsharechat.common.AuthManager;
import android.daehoshin.com.locationsharechat.common.StorageManager;
import android.daehoshin.com.locationsharechat.domain.user.UserInfo;
import android.daehoshin.com.locationsharechat.util.PermissionUtil;
import android.daehoshin.com.locationsharechat.util.ResourceUtil;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.io.IOException;

import static android.daehoshin.com.locationsharechat.constant.Consts.CAMERA_PERMISSION_REQ;
import static android.daehoshin.com.locationsharechat.constant.Consts.CAMERA_REQ;
import static android.daehoshin.com.locationsharechat.constant.Consts.GALLERY_REQ;
import static android.daehoshin.com.locationsharechat.constant.Consts.IS_SIGNIN;

public class SigninActivity extends AppCompatActivity {
    private boolean isSignin = true;

    private ImageView ivProfile;
    private ImageButton btnAddProfile;
    private EditText etNickname;
    private TextView tvNicknameMsg;
    private Uri profileUri = null;

    private ConstraintLayout popupChoice;
    private FrameLayout progress;

    // 저장된 파일의 경로를 가지는 컨텐츠 Uri
    private Uri fileUri = null;

    private UserInfo currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        isSignin = getIntent().getBooleanExtra(IS_SIGNIN, true);

        init();

        if(!isSignin){
            progress.setVisibility(View.VISIBLE);

            AuthManager.getInstance().getCurrentUser(new AuthManager.IAuthCallback() {
                @Override
                public void signinAnonymously(boolean isSuccessful) {

                }

                @Override
                public void getCurrentUser(UserInfo userInfo) {
                    currentUser = userInfo;
                    etNickname.setText(currentUser.getName());
                    currentUser.getProfile(new StorageManager.IDownloadCallback() {
                        @Override
                        public void downloaded(String id, Uri uri) {
                            profileUri = uri;
                            Glide.with(SigninActivity.this).load(profileUri).apply(RequestOptions.circleCropTransform()).into(ivProfile);

                            progress.setVisibility(View.GONE);
                        }
                    });
                }
            });
        }
    }

    private void init(){
        ivProfile = findViewById(R.id.ivProfile);
//        ivProfile.setBackground(new ShapeDrawable(new OvalShape()));
//        ivProfile.setClipToOutline(true);

        if(!isSignin) ((Button)findViewById(R.id.btnSignin)).setText(ResourceUtil.getString(this, R.string.btn_apply));

        btnAddProfile = findViewById(R.id.btnAddProfile);
        etNickname = findViewById(R.id.etNickname);
        etNickname.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                tvNicknameMsg.setVisibility(View.GONE);
                return false;
            }
        });
        tvNicknameMsg = findViewById(R.id.tvNicknameMsg);

        popupChoice = findViewById(R.id.popupChoice);
        popupChoice.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                popupChoice.setVisibility(View.GONE);
                return false;
            }
        });

        progress = findViewById(R.id.progress);
    }

    @Override
    public void onBackPressed() {
        if(popupChoice.getVisibility() == View.VISIBLE) popupChoice.setVisibility(View.GONE);
        else super.onBackPressed();
    }

    public void addProfile(View v){
        popupChoice.setVisibility(View.VISIBLE);
    }

    public void signin(View v){
        if(isSignin) {
            if ("".equals(etNickname.getText().toString())) {
                tvNicknameMsg.setVisibility(View.VISIBLE);
                return;
            }

            progress.setVisibility(View.VISIBLE);

            AuthManager.getInstance().signInAnonymously(etNickname.getText().toString(), profileUri, new AuthManager.IAuthCallback() {
                @Override
                public void signinAnonymously(boolean isSuccessful) {
                    if (isSuccessful) {
                        setResult(RESULT_OK);
                        progress.setVisibility(View.GONE);
                        Toast.makeText(SigninActivity.this, ResourceUtil.getString(SigninActivity.this, R.string.success_signin), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        progress.setVisibility(View.GONE);
                        Toast.makeText(SigninActivity.this, "Signin failed", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void getCurrentUser(UserInfo userInfo) {

                }
            });
        }
        else{
            if ("".equals(etNickname.getText().toString())) {
                tvNicknameMsg.setVisibility(View.VISIBLE);
                return;
            }

            progress.setVisibility(View.VISIBLE);

            currentUser.setName(etNickname.getText().toString());
            currentUser.save();

            if(profileUri != null) {
                StorageManager.uploadProfile(currentUser.getUid(), profileUri, new StorageManager.IUploadCallback() {
                    @Override
                    public void uploaded(boolean isSuccess, Uri uri) {

                    }
                });
            }
            progress.setVisibility(View.GONE);
            Toast.makeText(this, ResourceUtil.getString(this, R.string.success_apply), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void onCamera(View v){
        String[] Permission = new String[] { android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE };

        PermissionUtil pUtil = new PermissionUtil(CAMERA_PERMISSION_REQ, Permission);
        pUtil.check(this, new PermissionUtil.IPermissionGrant() {
            @Override
            public void run() {
                camera();
            }

            @Override
            public void fail() {

            }
        });
    }

    /**
     * 카메라 앱 띄워서 결과 이미지 저장하기
     */
    private void camera(){
        // 1. Intent 만들기
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 2. 호환성 처리 버전체크 - 롤리팝 이상
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            // 3. 실제 파일이 저장되는 파일 객체 < 빈 파일을 생성해 둔다
            File photoFile = null;

            // 3.1 실제 파일이 저장되는 곳에 권한이 부여되어 있어야 한다
            //     롤리팝 부터는 File Provider를 선언해 줘야만한다 > Manifest에
            try {
                photoFile = createFile();

                // 갤러리에서 나오지 않을때
                refreshMedia(photoFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            fileUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", photoFile);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(intent, CAMERA_REQ);
        }
        else startActivityForResult(intent, CAMERA_REQ);
    }

    /**
     * 미디어 파일 갱신
     * @param file
     */
    private void refreshMedia(File file){
        MediaScannerConnection.scanFile(this, new String[]{file.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String path, Uri uri) {

            }
        });
    }

    /**
     * 이미지를 저장하기 위해 쓰기 권한이 있는 빈 파일을 생성해두는 함수
     * @return
     */
    private File createFile() throws IOException {
        // 임시파일명 생성
        String tempFileName = "Temp_" + System.currentTimeMillis();

        // 임시파일 저장용 디렉토리 생성
        File tempDir = new File(Environment.getExternalStorageDirectory() + File.separator + "tempPicture" + File.separator);

        // 생성체크
        if(!tempDir.exists()) tempDir.mkdirs();

        //실제 임시파일을 생성
        File tempFile = File.createTempFile(tempFileName, ".jpg", tempDir);

        return tempFile;
    }

    public void onGallery(View v){
        String[] Permission = new String[] { Manifest.permission.READ_EXTERNAL_STORAGE };

        PermissionUtil pUtil = new PermissionUtil(CAMERA_PERMISSION_REQ, Permission);
        pUtil.check(this, new PermissionUtil.IPermissionGrant() {
            @Override
            public void run() {
//                btnAddProfile.setVisibility(View.GONE);
            }

            @Override
            public void fail() {
//                btnAddProfile.setVisibility(View.GONE);
            }
        });

        gallery();
    }

    private void gallery(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case CAMERA_REQ:
                if(resultCode == RESULT_OK){
                    // 버전체크
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        profileUri = fileUri;
                        Glide.with(SigninActivity.this).load(profileUri).apply(RequestOptions.circleCropTransform()).into(ivProfile);
                    }
                    else {
                        profileUri = data.getData();
                        Glide.with(SigninActivity.this).load(profileUri).apply(RequestOptions.circleCropTransform()).into(ivProfile);
                    }
                }
                break;
            case GALLERY_REQ:
                // 갤러리 액티비티 종료시 호출 - 정상종료 된 경우만 이미지설정
                if(resultCode == RESULT_OK) {
                    profileUri = data.getData();
                    Glide.with(SigninActivity.this).load(profileUri).apply(RequestOptions.circleCropTransform()).into(ivProfile);
                }
                break;
        }

        if(resultCode == RESULT_OK) popupChoice.setVisibility(View.GONE);
    }
}
