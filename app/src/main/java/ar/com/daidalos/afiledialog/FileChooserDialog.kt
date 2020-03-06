/*
 * <Copyright 2013 Jose F. Maldonado>
 *
 *  This file is part of aFileDialog.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package ar.com.daidalos.afiledialog

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import ifteam.affogatoman.fontgen.R
import java.io.File
import java.util.*

/**
 * A file chooser implemented in a Dialog.
 */
class FileChooserDialog @JvmOverloads constructor(context: Context, folderPath: String? = null) : Dialog(context), FileChooser {
    // ----- Attributes ----- //
    /**
     * The core of this file chooser.
     */
    private val core: FileChooserCore
    /**
     * The listeners for the event of select a file.
     */
    private val listeners: MutableList<OnFileSelectedListener>
    // ----- Events methods ----- //
    /**
     * Add a listener for the event of a file selected.
     *
     * @param listener The listener to add.
     */
    fun addListener(listener: OnFileSelectedListener) {
        listeners.add(listener)
    }

    /**
     * Removes a listener for the event of a file selected.
     *
     * @param listener The listener to remove.
     */
    fun removeListener(listener: OnFileSelectedListener?) {
        listeners.remove(listener)
    }

    /**
     * Removes all the listeners for the event of a file selected.
     */
    fun removeAllListeners() {
        listeners.clear()
    }

    /**
     * Set a regular expression to filter the files that can be selected.
     *
     * @param filter A regular expression.
     */
    fun setFilter(filter: String?) {
        core.setFilter(filter)
    }
    // ----- Miscellaneous methods ----- //
    /**
     * Set a regular expression to filter the folders that can be explored.
     *
     * @param folderFilter A regular expression.
     */
    fun setFolderFilter(folderFilter: String?) {
        core.setFolderFilter(folderFilter)
    }

    /**
     * Defines if only the files that can be selected (they pass the filter) must be show.
     *
     * @param show 'true' if only the files that can be selected must be show or 'false' if all the files must be show.
     */
    fun setShowOnlySelectable(show: Boolean) {
        core.setShowOnlySelectable(show)
    }

    /**
     * Loads all the files of the SD card root.
     */
    fun loadFolder() {
        core.loadFolder()
    }

    /**
     * Loads all the files of a folder in the file chooser.
     *
     *
     * If no path is specified ('folderPath' is null) the root folder of the SD card is going to be used.
     *
     * @param folderPath The folder's path.
     */
    fun loadFolder(folderPath: String?) {
        core.loadFolder(folderPath)
    }

    /**
     * Defines if the chooser is going to be used to select folders, instead of files.
     *
     * @param folderMode 'true' for select folders or 'false' for select files.
     */
    fun setFolderMode(folderMode: Boolean) {
        core.setFolderMode(folderMode)
    }

    /**
     * Defines if the user can create files, instead of only select files.
     *
     * @param canCreate 'true' if the user can create files or 'false' if it can only select them.
     */
    fun setCanCreateFiles(canCreate: Boolean) {
        core.setCanCreateFiles(canCreate)
    }

    /**
     * Defines if the cancel button must be show.
     *
     * @param canShow 'true' if the user can create files or 'false' if it can only select them.
     */
    fun setShowCancelButton(canShow: Boolean) {
        core.setShowCancelButton(canShow)
    }

    /**
     * Defines the value of the labels.
     *
     * @param labels The labels.
     */
    fun setLabels(labels: FileChooserLabels?) {
        core.setLabels(labels)
    }

    /**
     * Allows to define if a confirmation dialog must be show when selecting o creating a file.
     *
     * @param onSelect 'true' for show a confirmation dialog when selecting a file, 'false' if not.
     * @param onCreate 'true' for show a confirmation dialog when creating a file, 'false' if not.
     */
    fun setShowConfirmation(onSelect: Boolean, onCreate: Boolean) {
        core.setShowConfirmationOnCreate(onCreate)
        core.setShowConfirmationOnSelect(onSelect)
    }

    /**
     * Allows to define if, in the title, must be show only the current folder's name or the full file's path..
     *
     * @param show 'true' for show the full path, 'false' for show only the name.
     */
    fun setShowFullPath(show: Boolean) {
        core.setShowFullPathInTitle(show)
    }

    override val rootLayout: LinearLayout?
        get() {
            val root = findViewById<View>(R.id.rootLayout)
            return if (root is LinearLayout) root else null
        }

    // ----- FileChooser methods ----- //
    override fun setCurrentFolderName(name: String?) {
        this.setTitle(name)
    }

    override val fileChooserContext: Context
        get() = context

    /**
     * Interface definition for a callback to be invoked when a file is selected.
     */
    interface OnFileSelectedListener {
        /**
         * Called when a file has been selected.
         *
         * @param file The file selected.
         */
        fun onFileSelected(source: Dialog, file: File?)

        /**
         * Called when an user wants to be create a file.
         *
         * @param folder The file's parent folder.
         * @param name   The file's name.
         */
        fun onFileSelected(source: Dialog?, folder: File?, name: String?)
    }
    /**
     * Creates a file chooser dialog which lists all the file of a particular folder.
     *
     * @param context    The current context.
     * @param folderPath The folder which files are going to be listed.
     */
// ----- Constructors ----- //
    /**
     * Creates a file chooser dialog which, by default, lists all the files in the SD card.
     *
     * @param context The current context.
     */
    init { // Call superclass constructor.
        // Set layout.
        this.setContentView(R.layout.daidalos_file_chooser)
        // Maximize the dialog.
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(this.window!!.attributes)
        lp.width = WindowManager.LayoutParams.FILL_PARENT
        lp.height = WindowManager.LayoutParams.FILL_PARENT
        this.window!!.attributes = lp
        // By default, load the SD card files.
        core = FileChooserCore(this)
        core.loadFolder(folderPath)
        // Initialize attributes.
        listeners = LinkedList()
        // Set the background color.
        val layout = findViewById<View>(R.id.rootLayout) as LinearLayout
        layout.setBackgroundColor(context.resources.getColor(R.color.daidalos_backgroud))
        // Add a listener for when a file is selected.
        core.addListener(object : FileChooserCore.OnFileSelectedListener {
            override fun onFileSelected(folder: File?, name: String?) { // Call to the listeners.
                for (i in listeners.indices) {
                    listeners[i].onFileSelected(this@FileChooserDialog, folder, name)
                }
            }

            override fun onFileSelected(file: File?) { // Call to the listeners.
                for (i in listeners.indices) {
                    listeners[i].onFileSelected(this@FileChooserDialog, file)
                }
            }
        })
        // Add a listener for when the cancel button is pressed.
        core.addListener (object: FileChooserCore.OnCancelListener {
            override fun onCancel() {
                // Close activity.
                super@FileChooserDialog.onBackPressed()
            }
        })
    }
}