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
import kotlin.math.pow

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
    lateinit var bitmapSizeSeekBar: SeekBar

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
    var drawPaddingX = 0
    var drawPaddingY = 0

    var bitmapSize: Int = 256

    internal inner class FontGenOnSeekBarChangeListener(private val mainActivity: MainActivity, private val settingImageView: ImageView?) : OnSeekBarChangeListener {
        override fun onStartTrackingTouch(seekBar: SeekBar) {}
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, z: Boolean) {
            when (seekBar.id) {
                R.id.sizes ->  mainActivity.settingFontSize = progress
                R.id.xs -> mainActivity.settingDrawX = progress - xSeekBar.max/2
                R.id.ys -> mainActivity.settingDrawY = progress - ySeekBar.max/2
                R.id.ys -> mainActivity.settingDrawY = progress - ySeekBar.max/2
                R.id.size_seekbar -> {
                    val tempSize = bitmapSize
                    mainActivity.bitmapSize = 2.0.pow(progress + 8).toInt()
                    mainActivity.fontSize = mainActivity.bitmapSize/16
                    mainActivity.drawPaddingX = drawPaddingX * bitmapSize / tempSize
                    mainActivity.drawPaddingY = drawPaddingY * bitmapSize / tempSize
                    mainActivity.findViewById<TextView>(R.id.size_text).text = "$bitmapSize × $bitmapSize"
                }
                else -> { }
            }
            settingImageView?.setImageBitmap(mainActivity.getSample(mainActivity.settingDrawX, mainActivity.settingDrawY, mainActivity.settingFontSize))
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            settingImageView?.setImageBitmap(mainActivity.getSample(mainActivity.settingDrawX, mainActivity.settingDrawY, mainActivity.settingFontSize))
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
                    if (this.mainActivity.sizeSeekBar.progress != this.mainActivity.sizeSeekBar.max) {
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
                    if (this.mainActivity.xSeekBar.progress != this.mainActivity.xSeekBar.max) {
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
                    if (this.mainActivity.ySeekBar.progress != this.mainActivity.ySeekBar.max) {
                        this.mainActivity.settingDrawY++
                        this.mainActivity.ySeekBar.incrementProgressBy(1)
                    }
                }
                R.id.anti_alias -> this.mainActivity.isAntiAlias = (view as Switch).isChecked
                R.id.only_korean -> this.mainActivity.isOnlyKorean = (view as Switch).isChecked
                else -> { }
            }
            sampleImageView.setImageBitmap(mainActivity.getSample(mainActivity.settingDrawX, mainActivity.settingDrawY, mainActivity.settingFontSize))
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
                var startX = drawPaddingX
                var startY = drawPaddingY

                if (!this.mainActivity.isOnlyKorean) {
                    glyph = 0x00
                    maxCount = 256*256-1
                    i = 0
                    startChar = 0
                }
                this.mainActivity.baseBitmap = Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_8888)
                this.mainActivity.canvas = Canvas(this.mainActivity.baseBitmap)
                if (this.mainActivity.typeFace != null)
                    this.mainActivity.paint.typeface = this.mainActivity.typeFace
                this.mainActivity.paint.color = Color.WHITE
                this.mainActivity.paint.textSize = fontSize_.toFloat()
                this.mainActivity.paint.isAntiAlias = this.mainActivity.isAntiAlias

                val piece: Bitmap = Bitmap.createBitmap(this.mainActivity.bitmapSize/16, this.mainActivity.bitmapSize, Bitmap.Config.ARGB_8888)
                var pieceCanvas = Canvas(piece)
                val rect = Rect()

                while (i <= maxCount) {
                    var file: File
                    //Thread.sleep(1)
                    this.mainActivity.paint.getTextBounds(i.toChar().toString(), 0, 1, rect)
                    Log.i(TAG, rect.left.toString())
                    pieceCanvas.drawText(i.toChar().toString(), -rect.left.toFloat(), -rect.top.toFloat(), this.mainActivity.paint)
                    this.mainActivity.canvas.drawBitmap(piece, startX.toFloat(), startY.toFloat(), null)
                    piece.eraseColor(Color.TRANSPARENT)
                    startX += bitmapSize/16

                    val myHandler = this.mainActivity.handler
                    myHandler.sendMessage(myHandler.obtainMessage(100, arrayOf(i, maxCount, startChar)))

                    if (i == startChar+1) {
                        file = File("${Environment.getExternalStorageDirectory().absolutePath}/FontGen/$current/.nomedia")
                        file.parentFile.mkdirs()
                        file.createNewFile()
                    }
                    if ((i - startChar) % 16 == 15 && i != startChar) {
                        startX = drawPaddingX
                        startY += bitmapSize/16
                    }
                    if (startY - drawPaddingY >= bitmapSize || i == maxCount) {
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
                        startY = drawPaddingY
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
                makeButton.text = (message.obj as Array<Int>)[0].toChar().toString()
                if(((message.obj as Array<Int>)[0]-(message.obj as Array<Int>)[2]) % 64 == 63 || (message.obj as Array<Int>)[0] == (message.obj as Array<Int>)[1])
                    screenImageView!!.setImageBitmap(baseBitmap)
                characterProgressDialog!!.setMessage(getString(R.string.current_char)+(message.obj as Array<Int>)[0].toChar().toString())
                characterProgressBar!!.max = (message.obj as Array<Int>)[1]-(message.obj as Array<Int>)[2]
                characterProgressBar!!.progress = (message.obj as Array<Int>)[0]-(message.obj as Array<Int>)[2]
                characterProgressDialog!!.max = (message.obj as Array<Int>)[1]-(message.obj as Array<Int>)[2]
                characterProgressDialog!!.progress = (message.obj as Array<Int>)[0]-(message.obj as Array<Int>)[2]
                if ((message.obj as Array<Int>)[0] == (message.obj as Array<Int>)[1]) {
                    makeButton.text = getString(R.string.make)
                    makeButton.isEnabled = true
                    settingButton.isEnabled = true
                    characterProgressBar!!.progress = 0
                    characterProgressDialog!!.dismiss()
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
        bitmapSizeSeekBar = findViewById(R.id.size_seekbar)
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
                        val chooserDialog = ChooserDialog(this@MainActivity)
                        chooserDialog.withOnBackPressedListener { _ -> chooserDialog.goBack() }
                                .withOnLastBackPressedListener { dialog -> dialog.cancel() }
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
        bitmapSizeSeekBar.setOnSeekBarChangeListener(FontGenOnSeekBarChangeListener(this, null))
    }

    private fun showSettingDialog() {
        val tempIsAntiAlias: Boolean = isAntiAlias
        val tempIsOnlyKorean: Boolean = isOnlyKorean
        settingDrawX = drawPaddingX
        settingDrawY = drawPaddingY
        settingFontSize = fontSize
        val inflate = View.inflate(this, R.layout.setting, null)
        val imageView = inflate.findViewById<ImageView>(R.id.screen2)
        sizeSeekBar = inflate.findViewById(R.id.sizes)
        xSeekBar = inflate.findViewById(R.id.xs)
        ySeekBar = inflate.findViewById(R.id.ys)

        sizeSeekBar.max = bitmapSize/16
        xSeekBar.max = bitmapSize/8
        ySeekBar.max = bitmapSize/8

        antiAliasSwitch = inflate.findViewById(R.id.anti_alias)
        antiAliasSwitch.isChecked = isAntiAlias
        onlyKoreanSwitch = inflate.findViewById(R.id.only_korean)
        onlyKoreanSwitch.isChecked = isOnlyKorean

        sizeSeekBar.progress = fontSize
        xSeekBar.progress = drawPaddingX + (16*bitmapSize/256)
        ySeekBar.progress = drawPaddingY + (16*bitmapSize/256)

        imageView.setImageBitmap(getSample(drawPaddingX, drawPaddingY, fontSize))
        val fontGenOnSeekBarChangeListener = FontGenOnSeekBarChangeListener(this, imageView)
        sizeSeekBar.setOnSeekBarChangeListener(fontGenOnSeekBarChangeListener)
        xSeekBar.setOnSeekBarChangeListener(fontGenOnSeekBarChangeListener)
        ySeekBar.setOnSeekBarChangeListener(fontGenOnSeekBarChangeListener)
        val fontGenOnClickListener = FontGenOnClickListener(this, imageView)
        inflate.findViewById<Button>(R.id.sizem).setOnClickListener(fontGenOnClickListener)
        inflate.findViewById<Button>(R.id.sizep).setOnClickListener(fontGenOnClickListener)
        inflate.findViewById<Button>(R.id.xm).setOnClickListener(fontGenOnClickListener)
        inflate.findViewById<Button>(R.id.xp).setOnClickListener(fontGenOnClickListener)
        inflate.findViewById<Button>(R.id.ym).setOnClickListener(fontGenOnClickListener)
        inflate.findViewById<Button>(R.id.yp).setOnClickListener(fontGenOnClickListener)
        antiAliasSwitch.setOnClickListener(fontGenOnClickListener)
        onlyKoreanSwitch.setOnClickListener(fontGenOnClickListener)
        val builder = AlertDialog.Builder(this)
                .setView(inflate)
                .setTitle(R.string.setting_title)
                .setPositiveButton(R.string.confirm) { _, _ ->
                    drawPaddingX = settingDrawX
                    drawPaddingY = settingDrawY
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

    fun getSample(paddingX: Int, paddingY: Int, fontSize: Int): Bitmap {
        val createBitmap = Bitmap.createBitmap(bitmapSize/16, bitmapSize/16, Bitmap.Config.ARGB_8888)
        createBitmap.eraseColor(Color.WHITE)
        val canvas = Canvas(createBitmap)
        val paint_ = Paint()
        if (typeFace != null)
            paint_.typeface = typeFace
        paint_.color = Color.BLACK
        paint_.textSize = fontSize.toFloat()
        paint_.isAntiAlias = isAntiAlias
        val rect = Rect()
        paint_.getTextBounds("가", 0, 1, rect)
        canvas.drawText("가", -rect.left.toFloat()+paddingX, -rect.top.toFloat()+paddingY, paint_)
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
        fontSize = bitmapSize/16
        isAntiAlias = true
        isOnlyKorean = true
    }
}