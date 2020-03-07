package ifteam.affogatoman.fontgen

import android.Manifest
import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.graphics.*
import android.os.*
import android.util.Log
import android.view.*
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.obsez.android.lib.filechooser.ChooserDialog
import kotlinx.android.synthetic.main.setting.*
import java.io.*

class MainActivity : AppCompatActivity() {
    var baseBitmap: Bitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888)
    var canvas: Canvas
    var paint: Paint
    var typeFace: Typeface? = null

    var handler: MyHandler

    lateinit var findButton: Button
    lateinit var makeButton: Button
    lateinit var settingButton: Button
    lateinit var pathEditText: EditText

    var screenImageView: ImageView? = null
    var characterProgressBar: ProgressBar? = null
    var characterProgressDialog: ProgressDialog? = null
    lateinit var sizeSeekBar: SeekBar
    lateinit var xSeekBar: SeekBar
    lateinit var ySeekBar: SeekBar
    lateinit var antiAliasSwitch: Switch
    lateinit var onlyKoreanSwitch: Switch

    var settingFontSize = 0
    var settingDrawX = 0
    var settingDrawY = 0

    var isAntiAlias: Boolean
    var isOnlyKorean: Boolean
    var fontSize: Int
    var drawX: Int
    var drawY: Int

    internal inner class FontGenOnSeekBarChangeListener(private val mainActivity: MainActivity, private val settingImageView: ImageView) : OnSeekBarChangeListener {
        override fun onStartTrackingTouch(seekBar: SeekBar) {}
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, z: Boolean) {
            when (seekBar.id) {
                R.id.sizes ->  mainActivity.settingFontSize = progress
                R.id.xs -> mainActivity.settingDrawX = progress - 16
                R.id.ys -> mainActivity.settingDrawY = progress - 16
                else -> { }
            }
            settingImageView.setImageBitmap(mainActivity.getSample(mainActivity.settingDrawX, mainActivity.settingDrawY + 13, mainActivity.settingFontSize))
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            settingImageView.setImageBitmap(mainActivity.getSample(mainActivity.settingDrawX, mainActivity.settingDrawY + 13, mainActivity.settingFontSize))
        }

    }

    internal class FontGenOnClickListener(private val mainActivity: MainActivity, private val sampleImageView: ImageView) : View.OnClickListener {
        override fun onClick(view: View) {
            when (view.id) {
                R.id.sizem -> {
                    if (this.mainActivity.sizeSeekBar.progress != 0) {
                        this.mainActivity.settingFontSize--
                        this.mainActivity.sizeSeekBar.incrementProgressBy(-1)
                    }
                }
                R.id.sizep -> {
                    if (this.mainActivity.sizeSeekBar.progress != 16) {
                        this.mainActivity.settingFontSize++
                        this.mainActivity.sizeSeekBar.incrementProgressBy(1)
                    }
                }
                R.id.xm -> {
                    if (this.mainActivity.xSeekBar.progress != 0) {
                        this.mainActivity.settingDrawX--
                        this.mainActivity.xSeekBar.incrementProgressBy(-1)
                    }
                }
                R.id.xp -> {
                    if (this.mainActivity.xSeekBar.progress != 32) {
                        this.mainActivity.settingDrawX++
                        this.mainActivity.xSeekBar.incrementProgressBy(1)
                    }
                }
                R.id.ym -> {
                    if (this.mainActivity.ySeekBar.progress != 0) {
                        this.mainActivity.settingDrawY--
                        this.mainActivity.ySeekBar.incrementProgressBy(-1)
                    }
                }
                R.id.yp -> {
                    if (this.mainActivity.ySeekBar.progress != 32) {
                        this.mainActivity.settingDrawY++
                        this.mainActivity.ySeekBar.incrementProgressBy(1)
                    }
                }
                R.id.anti_alias -> this.mainActivity.isAntiAlias = (view as Switch).isChecked
                R.id.only_korean -> this.mainActivity.isOnlyKorean = (view as Switch).isChecked
                else -> { }
            }
            sampleImageView.setImageBitmap(mainActivity.getSample(mainActivity.settingDrawX, mainActivity.settingDrawY + 13, mainActivity.settingFontSize))
        }

    }

    internal inner class FontGenRunnable(private val mainActivity: MainActivity, private val fontSize_: Int) : Runnable {
        var current = System.currentTimeMillis()
        var glyph = 0xAC
        var maxCount = '힣'.toInt()
        var startChar = '가'.toInt()
        var i = 44032

        override fun run() {
            try {
                if (!this.mainActivity.isOnlyKorean) {
                    glyph = 0x00
                    maxCount = 256*256-1
                    i = 0
                    startChar = 0
                }
                this.mainActivity.baseBitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888)
                this.mainActivity.canvas = Canvas(this.mainActivity.baseBitmap)
                if (this.mainActivity.typeFace != null)
                    this.mainActivity.paint.typeface = this.mainActivity.typeFace
                this.mainActivity.paint.color = Color.WHITE
                this.mainActivity.paint.textSize = fontSize_.toFloat()
                this.mainActivity.paint.isAntiAlias = this.mainActivity.isAntiAlias
                while (i <= maxCount) {
                    var file: File
                    //Thread.sleep(1)
                    this.mainActivity.canvas.drawText(i.toChar().toString(), this.mainActivity.drawX.toFloat(), this.mainActivity.drawY.toFloat(), this.mainActivity.paint)
                    this.mainActivity.drawX += 16
                    val myHandler = this.mainActivity.handler

                    myHandler.sendMessage(myHandler.obtainMessage(100, arrayOf(i, maxCount)))
                    if (i == startChar+1) {
                        file = File("${Environment.getExternalStorageDirectory().absolutePath}/FontGen/$current/.nomedia")
                        file.parentFile.mkdirs()
                        file.createNewFile()
                    }
                    if ((i - startChar) % 16 == 15 && i != startChar) {
                        this.mainActivity.drawX = this.mainActivity.settingDrawX;
                        this.mainActivity.drawY += 16
                    }
                    if (this.mainActivity.drawY > 254 || i == maxCount) {
                        file = File("${Environment.getExternalStorageDirectory().absolutePath}/FontGen/$current/glyph_"+"%02X".format(glyph)+".png")
                        file.parentFile.mkdirs()
                        val fileOutputStream: OutputStream = FileOutputStream(file)
                        val bufferedOutputStream: OutputStream = BufferedOutputStream(fileOutputStream)
                        this.mainActivity.baseBitmap.compress(Bitmap.CompressFormat.PNG, 100, bufferedOutputStream)
                        bufferedOutputStream.close()
                        fileOutputStream.close()
                        if (i != maxCount) {
                            this.mainActivity.baseBitmap.eraseColor(Color.TRANSPARENT)
                        }
                        this.mainActivity.drawY = this.mainActivity.settingDrawY + 13
                        glyph++
                    }
                    i++
                }
            } catch (e: IOException) {
                Log.i(TAG, e.toString())
            }
        }

    }

    inner class MyHandler : Handler() {
        override fun handleMessage(message: Message) {
            if (message.what == 100) {
                screenImageView!!.setImageBitmap(baseBitmap)
                characterProgressDialog!!.setMessage(getString(R.string.current_char)+(message.obj as Array<Int>)[0].toChar().toString())
                characterProgressBar!!.max = (message.obj as Array<Int>)[1]
                characterProgressBar!!.incrementProgressBy(1)
                characterProgressDialog!!.max = (message.obj as Array<Int>)[1]
                characterProgressDialog!!.incrementProgressBy(1)
                if ((message.obj as Array<Int>)[0] == (message.obj as Array<Int>)[1]) {
                    makeButton.isEnabled = true
                    settingButton.isEnabled = true
                    characterProgressBar!!.progress = 0
                    characterProgressDialog!!.dismiss()
                    drawX = 0
                    drawY = 13
                }
            }
            super.handleMessage(message)
        }
    }

    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)

        setContentView(R.layout.main)
        characterProgressBar = findViewById(R.id.progress)
        pathEditText = findViewById(R.id.path)
        findButton = findViewById(R.id.find)
        screenImageView = findViewById(R.id.screen)
        settingButton = findViewById(R.id.setting)
        makeButton = findViewById(R.id.make)
        val viewOnClickListener = View.OnClickListener { view ->
            try {
                when (view.id) {
                    R.id.find -> {
                        ChooserDialog(this@MainActivity)
                                .withFilter(false, true, "ttf", "otf")
                                .withResources(R.string.choose_ttf_file, R.string.choose_ttf_file, R.string.cancel)
                                .withChosenListener { _, file ->
                                    pathEditText.setText(file.absolutePath)
                                    typeFace = Typeface.createFromFile(file)
                                }
                                .build()
                                .show()
                    }
                    R.id.setting -> showSettingDialog()
                    R.id.make -> {
                        characterProgressDialog = ProgressDialog(this@MainActivity)
                        characterProgressDialog!!.setProgressStyle(1)
                        characterProgressDialog!!.setTitle(R.string.writing)
                        characterProgressDialog!!.setMessage("Wait")
                        characterProgressDialog!!.max = 11172
                        characterProgressDialog!!.show()
                        makeFont(fontSize)
                        settingButton.isEnabled = false
                        makeButton.isEnabled = false
                    }
                    else -> return@OnClickListener
                }
            } catch (e: Exception) {
                val stringBuffer = StringBuilder()
                Log.i(TAG, stringBuffer.append(e).append("").toString())
            }
        }
        makeButton.setOnClickListener(viewOnClickListener)
        findButton.setOnClickListener(viewOnClickListener)
        settingButton.setOnClickListener(viewOnClickListener)
    }

    private fun showSettingDialog() {
        val tempIsAntiAlias: Boolean = isAntiAlias
        val tempIsOnlyKorean: Boolean = isOnlyKorean
        settingDrawX = drawX
        settingDrawY = drawY - 13
        settingFontSize = fontSize
        val inflate = View.inflate(this, R.layout.setting, null)
        val imageView = inflate.findViewById<ImageView>(R.id.screen2)
        sizeSeekBar = inflate.findViewById(R.id.sizes)
        xSeekBar = inflate.findViewById(R.id.xs)
        ySeekBar = inflate.findViewById(R.id.ys)
        antiAliasSwitch = inflate.findViewById(R.id.anti_alias)
        antiAliasSwitch.isChecked = isAntiAlias
        onlyKoreanSwitch = inflate.findViewById(R.id.only_korean)
        onlyKoreanSwitch.isChecked = isOnlyKorean
        sizeSeekBar.progress = fontSize
        xSeekBar.progress = drawX + 16
        ySeekBar.progress = drawY + 3
        imageView.setImageBitmap(getSample(drawX, drawY, fontSize))
        val fontGenOnSeekBarChangeListener = FontGenOnSeekBarChangeListener(this, imageView)
        sizeSeekBar.setOnSeekBarChangeListener(fontGenOnSeekBarChangeListener)
        xSeekBar.setOnSeekBarChangeListener(fontGenOnSeekBarChangeListener)
        ySeekBar.setOnSeekBarChangeListener(fontGenOnSeekBarChangeListener)
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
        antiAliasSwitch.setOnClickListener(fontGenOnClickListener)
        onlyKoreanSwitch.setOnClickListener(fontGenOnClickListener)
        val builder = AlertDialog.Builder(this)
                .setView(inflate)
                .setTitle(R.string.setting_title)
                .setPositiveButton(R.string.confirm) { _, _ ->
                    drawX = settingDrawX
                    drawY = settingDrawY + 13
                    fontSize = settingFontSize
                }
                .setNegativeButton(R.string.cancel) { _, _ ->
                    isAntiAlias = tempIsAntiAlias
                    isOnlyKorean = tempIsOnlyKorean
                }
        builder.create().show()
    }

    private fun makeFont(_fontSize: Int) {
        val fontGenRunnable = FontGenRunnable(this, _fontSize)
        val thread = Thread(fontGenRunnable)
        thread.start()
    }

    fun getSample(_drawX: Int, _drawY: Int, fontSize: Int): Bitmap {
        val createBitmap = Bitmap.createBitmap(16, 16, Bitmap.Config.ARGB_8888)
        createBitmap.eraseColor(Color.WHITE)
        val canvas = Canvas(createBitmap)
        val paint_ = Paint()
        if (typeFace != null)
            paint_.typeface = typeFace
        paint_.color = Color.BLACK
        paint_.textSize = fontSize.toFloat()
        paint_.isAntiAlias = isAntiAlias
        canvas.drawText("가", _drawX.toFloat(), _drawY.toFloat(), paint_)
        return Bitmap.createScaledBitmap(createBitmap, 256, 256, false)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val add = menu.add(1, 123, 1, R.string.info)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == 123) {
            try {
                val intent = Intent(this, Class.forName("ifteam.affogatoman.fontgen.LicenseActivity"))
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
        canvas = Canvas(baseBitmap)
        paint = Paint()
        handler = MyHandler()
        fontSize = 16
        isAntiAlias = true
        isOnlyKorean = true
        drawX = 0
        drawY = 13
    }
}