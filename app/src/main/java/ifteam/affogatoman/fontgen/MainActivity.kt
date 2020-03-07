package ifteam.affogatoman.fontgen

import android.app.*
import android.content.*
import android.graphics.*
import android.os.*
import android.util.Log
import android.view.*
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import com.obsez.android.lib.filechooser.ChooserDialog
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

    var settingFontSize = 0
    var settingDrawX = 0
    var settingDrawY = 0

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
                else -> { }
            }
            sampleImageView.setImageBitmap(mainActivity.getSample(mainActivity.settingDrawX, mainActivity.settingDrawY + 13, mainActivity.settingFontSize))
        }

    }

    internal inner class FontGenRunnable(private val mainActivity: MainActivity, private val fontSize_: Int) : Runnable {
        var current = System.currentTimeMillis()
        var glyph = 0xAC
        override fun run() {
            try {
                //Disabled All FileI/O methods due to not-working-error.

                this.mainActivity.baseBitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888)
                this.mainActivity.canvas = Canvas(this.mainActivity.baseBitmap)
                if (this.mainActivity.typeFace != null)
                    this.mainActivity.paint.typeface = this.mainActivity.typeFace
                this.mainActivity.paint.color = Color.WHITE
                this.mainActivity.paint.textSize = fontSize_.toFloat()
                this.mainActivity.paint.isAntiAlias = true
                var i = 44032
                while (i <= 55203) {
                    //var file: File
                    Thread.sleep(1)
                    this.mainActivity.canvas.drawText(i.toChar().toString(), this.mainActivity.drawX.toFloat(), this.mainActivity.drawY.toFloat(), this.mainActivity.paint)
                    this.mainActivity.drawX += 16
                    val myHandler = this.mainActivity.handler

                    myHandler.sendMessage(myHandler.obtainMessage(100, i))
                    if (i == 44033) {
                        //file = File(Environment.getExternalStorageDirectory().absolutePath+"/FontGen/"+current.toString()+"/.nomedia")
                        //file.parentFile.mkdirs()
                        //file.createNewFile()
                    }
                    if ((i - 44032) % 16 == 15 && i != 44032) {
                        this.mainActivity.drawX = this.mainActivity.settingDrawX;
                        this.mainActivity.drawY += 16
                    }
                    if (this.mainActivity.drawY > 254 || i == 55203) {
                        //file = File(Environment.getExternalStorageDirectory().absolutePath+"/FontGen/"+current.toString()+"/glyph_"+"%X".format(glyph)+".png")
                        //file.parentFile.mkdirs()
                        //val fileOutputStream: OutputStream = FileOutputStream(file)
                        //val bufferedOutputStream: OutputStream = BufferedOutputStream(fileOutputStream)
                        //this.mainActivity.baseBitmap.compress(CompressFormat.PNG, 100, bufferedOutputStream)
                        //bufferedOutputStream.close()
                        //fileOutputStream.close()
                        if (i != 55203) {
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
                characterProgressDialog!!.setMessage(getString(R.string.current_char)+(message.obj as Int).toChar().toString())
                characterProgressBar!!.incrementProgressBy(1)
                characterProgressDialog!!.incrementProgressBy(1)
                if (message.obj as Int == 55203) {
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
                        /*
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
                        */
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
        settingDrawX = drawX
        settingDrawY = drawY - 13
        settingFontSize = fontSize
        val inflate = View.inflate(this, R.layout.setting, null)
        val imageView = inflate.findViewById<ImageView>(R.id.screen2)
        sizeSeekBar = inflate.findViewById(R.id.sizes)
        xSeekBar = inflate.findViewById(R.id.xs)
        ySeekBar = inflate.findViewById(R.id.ys)
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
        val builder = AlertDialog.Builder(this)
                .setView(inflate)
                .setTitle(R.string.setting_title)
                .setPositiveButton(R.string.confirm) { _, _ ->
                    drawX = settingDrawX
                    drawY = settingDrawY + 13
                    fontSize = settingFontSize
                }
                .setNegativeButton(R.string.cancel, null)
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
        paint_.isAntiAlias = false
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
        drawX = 0
        drawY = 13
    }
}