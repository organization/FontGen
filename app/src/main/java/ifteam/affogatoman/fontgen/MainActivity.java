package ifteam.affogatoman.fontgen;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import ar.com.daidalos.afiledialog.FileChooserDialog;
import ar.com.daidalos.afiledialog.FileChooserDialog.OnFileSelectedListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class MainActivity extends Activity {
    public static final String TAG = "AFFO";
    Bitmap base = Bitmap.createBitmap(256, 256, Config.ARGB_8888);
    Canvas canvas;
    Button find;
    int h;
    MyHandler handler;
    Button make;
    Paint p;
    EditText path;
    ProgressBar pb;
    ProgressDialog pd;
    int presize;
    int prex;
    int prey;
    ImageView screen;
    Button setting;
    int size;
    SeekBar sizes;
    Typeface tf;
    int w;
    SeekBar xs;
    SeekBar ys;

    /* renamed from: ifteam.affogatoman.fontgen.MainActivity$100000002 */
    static class AnonymousClass100000002 implements OnSeekBarChangeListener {
        private final MainActivity this$0;
        private final ImageView val$screen;

        AnonymousClass100000002(MainActivity mainActivity, ImageView imageView) {
            AnonymousClass100000002 anonymousClass100000002 = this;
            this.this$0 = mainActivity;
            this.val$screen = imageView;
        }

        static MainActivity access$0(AnonymousClass100000002 anonymousClass100000002) {
            return anonymousClass100000002.this$0;
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
            this.val$screen.setImageBitmap(this.this$0.getSample(this.this$0.prex, this.this$0.prey + 13, this.this$0.presize));
            switch (seekBar.getId()) {
                case R.id.sizes:
                    this$0.presize = i;
                    return;
                case R.id.xs:
                    this$0.prex = i - 16;
                    return;
                case R.id.ys:
                    this$0.prey = i - 16;
                    return;
                default:
                    return;
            }
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            this.val$screen.setImageBitmap(this.this$0.getSample(this.this$0.prex, this.this$0.prey + 13, this.this$0.presize));
        }
    }

    /* renamed from: ifteam.affogatoman.fontgen.MainActivity$100000003 */
    static class AnonymousClass100000003 implements OnClickListener {
        private final MainActivity this$0;
        private final ImageView val$screen;

        AnonymousClass100000003(MainActivity mainActivity, ImageView imageView) {
            AnonymousClass100000003 anonymousClass100000003 = this;
            this.this$0 = mainActivity;
            this.val$screen = imageView;
        }

        static MainActivity access$0(AnonymousClass100000003 anonymousClass100000003) {
            return anonymousClass100000003.this$0;
        }

        public void onClick(View view) {
            this.val$screen.setImageBitmap(this.this$0.getSample(this.this$0.prex, this.this$0.prey + 13, this.this$0.presize));
            MainActivity mainActivity;
            switch (view.getId()) {
                case R.id.sizem:
                    if (this$0.sizes.getProgress() != 0) {
                        mainActivity = this$0;
                        mainActivity.presize--;
                        this$0.sizes.incrementProgressBy(-1);
                    }
                    return;
                case R.id.sizep:
                    if (this$0.sizes.getProgress() != 16) {
                        mainActivity = this$0;
                        mainActivity.presize++;
                        this$0.sizes.incrementProgressBy(1);
                    }
                    return;
                case R.id.xm:
                    if (this$0.xs.getProgress() != 0) {
                        mainActivity = this$0;
                        mainActivity.prex--;
                        this$0.xs.incrementProgressBy(-1);
                    }
                    return;
                case R.id.xp:
                    if (this$0.xs.getProgress() != 32) {
                        mainActivity = this$0;
                        mainActivity.prex++;
                        this$0.xs.incrementProgressBy(1);
                    }
                    return;
                case R.id.ym:
                    if (this$0.ys.getProgress() != 0) {
                        mainActivity = this$0;
                        mainActivity.prey--;
                        this$0.ys.incrementProgressBy(-1);
                    }
                    return;
                case R.id.yp:
                    if (this$0.ys.getProgress() != 32) {
                        mainActivity = this$0;
                        mainActivity.prey++;
                        this$0.ys.incrementProgressBy(1);
                    }
                    return;
                default:
            }
        }
    }

    /* renamed from: ifteam.affogatoman.fontgen.MainActivity$100000005 */
    static class AnonymousClass100000005 implements Runnable {
        long current = System.currentTimeMillis();
        int glyph = 172;
        private final MainActivity this$0;
        private final int val$size;

        AnonymousClass100000005(MainActivity mainActivity, int i) {
            AnonymousClass100000005 anonymousClass100000005 = this;
            this.this$0 = mainActivity;
            this.val$size = i;
        }

        static MainActivity access$0(AnonymousClass100000005 anonymousClass100000005) {
            return anonymousClass100000005.this$0;
        }

        public void run() {
            StringBuffer stringBuffer;
            StringBuffer stringBuffer2;
            try {
                this.this$0.base = Bitmap.createBitmap(256, 256, Config.ARGB_8888);
                MainActivity mainActivity = this$0;
                mainActivity.canvas = new Canvas(this$0.base);
                if (this$0.tf != null) {
                    Typeface typeface = this$0.p.setTypeface(this$0.tf);
                }
                this$0.p.setColor(-1);
                this$0.p.setTextSize((float) val$size);
                this$0.p.setAntiAlias(true);
                int i = 44032;
                while (i <= 55203) {
                    File file;
                    StringBuffer stringBuffer3;
                    StringBuffer stringBuffer4;
                    File file2;
                    File file3;
                    Thread.sleep((long) 1);
                    Canvas canvas3 = this$0.canvas;
                    stringBuffer = new StringBuffer();
                    canvas3.drawText(stringBuffer.append((char) i).append("").toString(), (float) this$0.w, (float) this$0.h, this$0.p);
                    MainActivity mainActivity2 = this$0;
                    mainActivity2.w += 16;
                    MyHandler myHandler = this$0.handler;
                    MyHandler myHandler2 = this$0.handler;
                    Integer num = i;
                    boolean sendMessage = myHandler.sendMessage(myHandler2.obtainMessage(100, num));
                    if (i == 44033) {
                        stringBuffer2 = new StringBuffer();
                        stringBuffer3 = new StringBuffer();
                        stringBuffer4 = new StringBuffer();
                        file = new File(stringBuffer2.append(stringBuffer3.append(stringBuffer4.append(Environment.getExternalStorageDirectory()).append("/아포카토맨/FontGen/").toString()).append(current).toString()).append("/.nomedia").toString());
                        file3 = file;
                        file3.getParentFile().mkdirs();
                        file3.createNewFile();
                    }
                    if ((i - 44032) % 16 == 15 && i != 44032) {
                        this$0.w = 0;
                        mainActivity2 = this$0;
                        mainActivity2.h += 16;
                    }
                    if (this$0.h > 254 || i == 55203) {
                        stringBuffer2 = new StringBuffer();
                        stringBuffer3 = new StringBuffer();
                        stringBuffer4 = new StringBuffer();
                        StringBuilder stringBuffer5 = new StringBuilder();
                        StringBuilder stringBuffer6 = new StringBuilder();
                        file = new File(stringBuffer2.append(stringBuffer3.append(stringBuffer4.append(stringBuffer5.append(stringBuffer6.append(Environment.getExternalStorageDirectory()).append("/아포카토맨/FontGen/").toString()).append(current).toString()).append("/glyph_").toString()).append(Integer.toHexString(glyph).toUpperCase()).toString()).append(".png").toString());
                        file3 = file;
                        file3.getParentFile().mkdirs();
                        OutputStream fileOutputStream = new FileOutputStream(file3);
                        OutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                        this$0.base.compress(CompressFormat.PNG, 100, bufferedOutputStream);
                        bufferedOutputStream.close();
                        fileOutputStream.close();
                        if (i != 55203) {
                            this$0.base.eraseColor(0);
                        }
                        mainActivity = this$0;
                        mainActivity.h = mainActivity.prey + 13;
                        glyph++;
                    }
                    i++;
                }
            } catch (Exception e) {
                stringBuffer = new StringBuffer();
                int i2 = Log.i(MainActivity.TAG, stringBuffer.append(e).append("").toString());
            }
        }
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            if (message.what == 100) {
                screen.setImageBitmap(base);
                ProgressDialog progressDialog = pd;
                StringBuilder stringBuffer = new StringBuilder();
                progressDialog.setMessage(stringBuffer.append("현재 글자 : ").append((char) ((Integer) message.obj).intValue()).toString());
                pb.incrementProgressBy(1);
                pd.incrementProgressBy(1);
                if ((Integer) message.obj == 55203) {
                    make.setClickable(true);
                    setting.setClickable(true);
                    pb.setProgress(0);
                    pd.dismiss();
                    w = 0;
                    h = 13;
                }
            }
            super.handleMessage(message);
        }
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.main);
        this.pb = findViewById(R.id.progress);
        this.path = findViewById(R.id.path);
        this.find = findViewById(R.id.find);
        this.screen = findViewById(R.id.screen);
        this.setting = findViewById(R.id.setting);
        this.make = findViewById(R.id.make);
        OnClickListener anonymousClass1000000013 = new OnClickListener() {
            public void onClick(View view) {
                try {
                    switch (view.getId()) {
                        case R.id.find:
                            FileChooserDialog fileChooserDialog = new FileChooserDialog(MainActivity.this);
                            FileChooserDialog fileChooserDialog3 = fileChooserDialog;
                            fileChooserDialog3.setFilter(".*TTF|.*ttf");
                            fileChooserDialog = fileChooserDialog3;
                            fileChooserDialog.addListener(new OnFileSelectedListener() {
                                public void onFileSelected(Dialog dialog, File file, String str) {
                                }

                                public void onFileSelected(Dialog dialog, File file) {
                                    path.setText(file.getAbsolutePath());
                                    tf = Typeface.createFromFile(file);
                                    dialog.hide();
                                }
                            });
                            fileChooserDialog3.show();
                            return;
                        case R.id.setting:
                            showSettingDialog();
                            return;
                        case R.id.make:
                            pd = new ProgressDialog(MainActivity.this);
                            pd.setProgressStyle(1);
                            pd.setTitle("입력중입니다...");
                            pd.setMessage("Wait");
                            pd.setMax(11172);
                            pd.show();
                            makeFont(size);
                            setting.setClickable(false);
                            make.setClickable(false);
                            return;
                        default:
                    }
                } catch (Exception e) {
                    StringBuilder stringBuffer = new StringBuilder();
                    Log.i(MainActivity.TAG, stringBuffer.append(e).append("").toString());
                }
            }
        };
        this.make.setOnClickListener(anonymousClass1000000013);
        this.find.setOnClickListener(anonymousClass1000000013);
        this.setting.setOnClickListener(anonymousClass1000000013);
    }

    public void showSettingDialog() {
        this.prex = this.w;
        this.prey = this.h - 13;
        this.presize = this.size;
        View inflate = View.inflate(this, R.layout.setting, null);
        ImageView imageView = inflate.findViewById(R.id.screen2);
        this.sizes = inflate.findViewById(R.id.sizes);
        this.xs = inflate.findViewById(R.id.xs);
        this.ys = inflate.findViewById(R.id.ys);
        this.sizes.setProgress(this.size);
        this.xs.setProgress(this.w + 16);
        this.ys.setProgress(this.h + 3);
        imageView.setImageBitmap(getSample(this.w, this.h, this.size));
        AnonymousClass100000002 anonymousClass100000002 = new AnonymousClass100000002(this, imageView);
        this.sizes.setOnSeekBarChangeListener(anonymousClass100000002);
        this.xs.setOnSeekBarChangeListener(anonymousClass100000002);
        this.ys.setOnSeekBarChangeListener(anonymousClass100000002);
        Button button = inflate.findViewById(R.id.sizem);
        Button button2 = inflate.findViewById(R.id.sizep);
        Button button3 = inflate.findViewById(R.id.xm);
        Button button4 = inflate.findViewById(R.id.xp);
        Button button5 = inflate.findViewById(R.id.ym);
        Button button6 = inflate.findViewById(R.id.yp);
        AnonymousClass100000003 anonymousClass100000003 = new AnonymousClass100000003(this, imageView);
        button.setOnClickListener(anonymousClass100000003);
        button2.setOnClickListener(anonymousClass100000003);
        button3.setOnClickListener(anonymousClass100000003);
        button4.setOnClickListener(anonymousClass100000003);
        button5.setOnClickListener(anonymousClass100000003);
        button6.setOnClickListener(anonymousClass100000003);
        Builder builder = new Builder(this)
                .setView(inflate)
                .setTitle("스타일 설정하기")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        w = prex;
                        h = prey + 13;
                        size = presize;
                    }
                })
                .setNegativeButton("취소", null);
        builder.create().show();
    }

    public void makeFont(int i) {
        AnonymousClass100000005 anonymousClass100000005 = new AnonymousClass100000005(this, i);
        Thread thread = new Thread(anonymousClass100000005);
        thread.start();
    }

    public Bitmap getSample(int i, int i2, int i3) {
        Bitmap createBitmap = Bitmap.createBitmap(16, 16, Config.ARGB_8888);
        createBitmap.eraseColor(-1);
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = new Paint();
        if (this.tf != null) {
            Typeface typeface = paint.setTypeface(tf);
        }
        paint.setColor(-16777216);
        paint.setTextSize((float) i3);
        paint.setAntiAlias(false);
        canvas.drawText("가", (float) i, (float) i2, paint);
        return Bitmap.createScaledBitmap(createBitmap, 256, 256, false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Menu menu2 = menu;
        MenuItem add = menu2.add(1, 123, 1, "정보");
        return super.onCreateOptionsMenu(menu2);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        Context context = this;
        if (menuItem.getItemId() == 123) {
            Intent intent;
            try {
                intent = new Intent(context, Class.forName("ifteam.affogatoman.fontgen.LicenseActivity"));
                startActivity(intent);
            } catch (Throwable e) {
                throw new NoClassDefFoundError(e.getMessage());
            }
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public MainActivity() {
        this.canvas = new Canvas(this.base);
        this.p = new Paint();
        this.handler = new MyHandler();
        this.size = 16;
        this.w = 0;
        this.h = 13;
    }
}
