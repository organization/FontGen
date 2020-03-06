package ifteam.affogatoman.fontgen

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import ar.com.daidalos.afiledialog.FileChooserDialog
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class MainActivity : Activity() {
    var base = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888)
    var canvas: Canvas
    lateinit var find: Button
    var h: Int
    var handler: MyHandler
    lateinit var make: Button
    var p: Paint
    lateinit var path: EditText
    var pb: ProgressBar? = null
    var pd: ProgressDialog? = null
    var presize = 0
    var prex = 0
    var prey = 0
    var screen: ImageView? = null
    lateinit var setting: Button
    var size: Int
    lateinit var sizes: SeekBar
    var tf: Typeface? = null
    var w: Int
    lateinit var xs: SeekBar
    lateinit var ys: SeekBar

    internal inner class FontGenOnSeekBarChangeListener(private val mainActivity: MainActivity, private val screenImageView: ImageView) : OnSeekBarChangeListener {
        override fun onStartTrackingTouch(seekBar: SeekBar) {}
        override fun onProgressChanged(seekBar: SeekBar, i: Int, z: Boolean) {
            screenImageView.setImageBitmap(mainActivity.getSample(mainActivity.prex, mainActivity.prey + 13, mainActivity.presize))
            when (seekBar.id) {
                R.id.sizes -> {
                    mainActivity.presize = i
                    return
                }
                R.id.xs -> {
                    mainActivity.prex = i - 16
                    return
                }
                R.id.ys -> {
                    mainActivity.prey = i - 16
                    return
                }
                else -> return
            }
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            screenImageView.setImageBitmap(mainActivity.getSample(mainActivity.prex, mainActivity.prey + 13, mainActivity.presize))
        }

    }

    internal class FontGenOnClickListener(private val mainActivity: MainActivity, private val screenImageView: ImageView) : View.OnClickListener {
        override fun onClick(view: View) {
            screenImageView.setImageBitmap(mainActivity.getSample(mainActivity.prex, mainActivity.prey + 13, mainActivity.presize))
            when (view.id) {
                R.id.sizem -> {
                    if (this.mainActivity.sizes.progress != 0) {
                        this.mainActivity.presize--
                        this.mainActivity.sizes.incrementProgressBy(-1)
                    }
                    return
                }
                R.id.sizep -> {
                    if (this.mainActivity.sizes.progress != 16) {
                        this.mainActivity.presize++
                        this.mainActivity.sizes.incrementProgressBy(1)
                    }
                    return
                }
                R.id.xm -> {
                    if (this.mainActivity.xs.progress != 0) {
                        this.mainActivity.prex--
                        this.mainActivity.xs.incrementProgressBy(-1)
                    }
                    return
                }
                R.id.xp -> {
                    if (this.mainActivity.xs.progress != 32) {
                        this.mainActivity.prex++
                        this.mainActivity.xs.incrementProgressBy(1)
                    }
                    return
                }
                R.id.ym -> {
                    if (this.mainActivity.ys.progress != 0) {
                        this.mainActivity.prey--
                        this.mainActivity.ys.incrementProgressBy(-1)
                    }
                    return
                }
                R.id.yp -> {
                    if (this.mainActivity.ys.progress != 32) {
                        this.mainActivity.prey++
                        this.mainActivity.ys.incrementProgressBy(1)
                    }
                    return
                }
                else -> {
                }
            }
        }

    }

    internal inner class FontGenRunnable(private val mainActivity: MainActivity, private val size: Int) : Runnable {
        var current = System.currentTimeMillis()
        var glyph = 172
        override fun run() {
            var stringBuffer: StringBuffer
            var stringBuffer2: StringBuffer
            try {
                this.mainActivity.base = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888)
                this.mainActivity.canvas = Canvas(this.mainActivity.base)
                if (this.mainActivity.tf != null) {
                    this.mainActivity.p.typeface = this.mainActivity.tf
                }
                this.mainActivity.p.color = -1
                this.mainActivity.p.textSize = size.toFloat()
                this.mainActivity.p.isAntiAlias = true
                var i = 44032
                while (i <= 55203) {
                    var file: File?
                    var stringBuffer3: StringBuffer
                    var stringBuffer4: StringBuffer
                    var file3: File
                    Thread.sleep(1)
                    val canvas3 = this.mainActivity.canvas
                    stringBuffer = StringBuffer()
                    canvas3.drawText(stringBuffer.append(i.toChar()).append("").toString(), this.mainActivity.w.toFloat(), this.mainActivity.h.toFloat(), this.mainActivity.p)
                    this.mainActivity.w += 16
                    val myHandler = this.mainActivity.handler
                    val num = i
                    myHandler.sendMessage(myHandler.obtainMessage(100, num))
                    if (i == 44033) {
                        stringBuffer2 = StringBuffer()
                        stringBuffer3 = StringBuffer()
                        stringBuffer4 = StringBuffer()
                        file = File(stringBuffer2.append(stringBuffer3.append(stringBuffer4.append(Environment.getExternalStorageDirectory()).append("/아포카토맨/FontGen/").toString()).append(current).toString()).append("/.nomedia").toString())
                        file3 = file
                        file3.parentFile.mkdirs()
                        file3.createNewFile()
                    }
                    if ((i - 44032) % 16 == 15 && i != 44032) {
                        this.mainActivity.w = 0
                        this.mainActivity.h += 16
                    }
                    if (this.mainActivity.h > 254 || i == 55203) {
                        stringBuffer2 = StringBuffer()
                        stringBuffer3 = StringBuffer()
                        stringBuffer4 = StringBuffer()
                        val stringBuffer5 = StringBuilder()
                        val stringBuffer6 = StringBuilder()
                        file = File(stringBuffer2.append(stringBuffer3.append(stringBuffer4.append(stringBuffer5.append(stringBuffer6.append(Environment.getExternalStorageDirectory()).append("/아포카토맨/FontGen/").toString()).append(current).toString()).append("/glyph_").toString()).append(Integer.toHexString(glyph).toUpperCase()).toString()).append(".png").toString())
                        file3 = file
                        file3.parentFile.mkdirs()
                        val fileOutputStream: OutputStream = FileOutputStream(file3)
                        val bufferedOutputStream: OutputStream = BufferedOutputStream(fileOutputStream)
                        this.mainActivity.base.compress(CompressFormat.PNG, 100, bufferedOutputStream)
                        bufferedOutputStream.close()
                        fileOutputStream.close()
                        if (i != 55203) {
                            this.mainActivity.base.eraseColor(0)
                        }
                        this.mainActivity.h = this.mainActivity.prey + 13
                        glyph++
                    }
                    i++
                }
            } catch (e: Exception) {
                stringBuffer = StringBuffer()
                val i2 = Log.i(TAG, stringBuffer.append(e).append("").toString())
            }
        }

    }

    inner class MyHandler : Handler() {
        override fun handleMessage(message: Message) {
            if (message.what == 100) {
                screen!!.setImageBitmap(base)
                val progressDialog = pd
                val stringBuffer = StringBuilder()
                progressDialog!!.setMessage(stringBuffer.append("현재 글자 : ").append((message.obj as Int).toInt().toChar()).toString())
                pb!!.incrementProgressBy(1)
                pd!!.incrementProgressBy(1)
                if (message.obj as Int == 55203) {
                    make.isClickable = true
                    setting.isClickable = true
                    pb!!.progress = 0
                    pd!!.dismiss()
                    w = 0
                    h = 13
                }
            }
            super.handleMessage(message)
        }
    }

    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.main)
        pb = findViewById(R.id.progress)
        path = findViewById(R.id.path)
        find = findViewById(R.id.find)
        screen = findViewById(R.id.screen)
        setting = findViewById(R.id.setting)
        make = findViewById(R.id.make)
        val viewOnClickListener = View.OnClickListener { view ->
            try {
                when (view.id) {
                    R.id.find -> {
                        var fileChooserDialog = FileChooserDialog(this@MainActivity)
                        val fileChooserDialog3 = fileChooserDialog
                        fileChooserDialog3.setFilter(".*TTF|.*ttf")
                        fileChooserDialog = fileChooserDialog3
                        fileChooserDialog.addListener(object : FileChooserDialog.OnFileSelectedListener {
                            override fun onFileSelected(source: Dialog?, folder: File?, name: String?) {}
                            override fun onFileSelected(source: Dialog, file: File?) {
                                path.setText(file!!.absolutePath)
                                tf = Typeface.createFromFile(file)
                                source.hide()
                            }
                        })
                        fileChooserDialog3.show()
                        return@OnClickListener
                    }
                    R.id.setting -> {
                        showSettingDialog()
                        return@OnClickListener
                    }
                    R.id.make -> {
                        pd = ProgressDialog(this@MainActivity)
                        pd!!.setProgressStyle(1)
                        pd!!.setTitle("입력중입니다...")
                        pd!!.setMessage("Wait")
                        pd!!.max = 11172
                        pd!!.show()
                        makeFont(size)
                        setting.isClickable = false
                        make.isClickable = false
                        return@OnClickListener
                    }
                    else -> {
                    }
                }
            } catch (e: Exception) {
                val stringBuffer = StringBuilder()
                Log.i(TAG, stringBuffer.append(e).append("").toString())
            }
        }
        make.setOnClickListener(viewOnClickListener)
        find.setOnClickListener(viewOnClickListener)
        setting.setOnClickListener(viewOnClickListener)
    }

    private fun showSettingDialog() {
        prex = w
        prey = h - 13
        presize = size
        val inflate = View.inflate(this, R.layout.setting, null)
        val imageView = inflate.findViewById<ImageView>(R.id.screen2)
        sizes = inflate.findViewById(R.id.sizes)
        xs = inflate.findViewById(R.id.xs)
        ys = inflate.findViewById(R.id.ys)
        sizes.progress = size
        xs.progress = w + 16
        ys.progress = h + 3
        imageView.setImageBitmap(getSample(w, h, size))
        val fontGenOnSeekBarChangeListener = FontGenOnSeekBarChangeListener(this, imageView)
        sizes.setOnSeekBarChangeListener(fontGenOnSeekBarChangeListener)
        xs.setOnSeekBarChangeListener(fontGenOnSeekBarChangeListener)
        ys.setOnSeekBarChangeListener(fontGenOnSeekBarChangeListener)
        val button = inflate.findViewById<Button>(R.id.sizem)
        val button2 = inflate.findViewById<Button>(R.id.sizep)
        val button3 = inflate.findViewById<Button>(R.id.xm)
        val button4 = inflate.findViewById<Button>(R.id.xp)
        val button5 = inflate.findViewById<Button>(R.id.ym)
        val button6 = inflate.findViewById<Button>(R.id.yp)
        val fontGenOnClickListener = FontGenOnClickListener(this, imageView)
        button.setOnClickListener(fontGenOnClickListener)
        button2.setOnClickListener(fontGenOnClickListener)
        button3.setOnClickListener(fontGenOnClickListener)
        button4.setOnClickListener(fontGenOnClickListener)
        button5.setOnClickListener(fontGenOnClickListener)
        button6.setOnClickListener(fontGenOnClickListener)
        val builder = AlertDialog.Builder(this)
                .setView(inflate)
                .setTitle("스타일 설정하기")
                .setPositiveButton("확인") { dialogInterface, i ->
                    w = prex
                    h = prey + 13
                    size = presize
                }
                .setNegativeButton("취소", null)
        builder.create().show()
    }

    private fun makeFont(i: Int) {
        val fontGenRunnable = FontGenRunnable(this, i)
        val thread = Thread(fontGenRunnable)
        thread.start()
    }

    fun getSample(i: Int, i2: Int, i3: Int): Bitmap {
        val createBitmap = Bitmap.createBitmap(16, 16, Bitmap.Config.ARGB_8888)
        createBitmap.eraseColor(-1)
        val canvas = Canvas(createBitmap)
        val paint = Paint()
        if (tf != null) {
            val typeface = paint.setTypeface(tf)
        }
        paint.color = -16777216
        paint.textSize = i3.toFloat()
        paint.isAntiAlias = false
        canvas.drawText("가", i.toFloat(), i2.toFloat(), paint)
        return Bitmap.createScaledBitmap(createBitmap, 256, 256, false)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val add = menu.add(1, 123, 1, "정보")
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        val context: Context = this
        if (menuItem.itemId == 123) {
            val intent: Intent
            try {
                intent = Intent(context, Class.forName("ifteam.affogatoman.fontgen.LicenseActivity"))
                startActivity(intent)
            } catch (e: Throwable) {
                throw NoClassDefFoundError(e.message)
            }
        }
        return super.onOptionsItemSelected(menuItem)
    }

    companion object {
        const val TAG = "AFFO"
    }

    init {
        canvas = Canvas(base)
        p = Paint()
        handler = MyHandler()
        size = 16
        w = 0
        h = 13
    }
}