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

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import ar.com.daidalos.afiledialog.FileChooserActivity
import ifteam.affogatoman.fontgen.R
import java.io.File

/**
 * A file chooser implemented in an Activity.
 */
class FileChooserActivity : Activity(), FileChooser {
    /**
     * The folder that the class opened by default.
     */
    private var startFolder: File? = null
    /**
     * The core of the file chooser.
     */
    private lateinit var core: FileChooserCore
    /**
     * A boolean indicating if the 'back' button must be used to navigate to parent folders.
     */
    private var useBackButton = false
    // ---- Activity methods ----- //
    /**
     * Called when the activity is first created.
     */
    public override fun onCreate(savedInstanceState: Bundle?) { // Call superclass creator.
        super.onCreate(savedInstanceState)
        // Set layout.
        this.setContentView(R.layout.daidalos_file_chooser)
        // Set the background color.
        val layout = findViewById<View>(R.id.rootLayout) as LinearLayout
        layout.setBackgroundColor(resources.getColor(R.color.daidalos_backgroud))
        // Initialize fields.
        useBackButton = false
        // Create the core of the file chooser.
        core = FileChooserCore(this)
        // Verify if the optional parameters has been defined.
        var folderPath: String? = null
        val extras = this.intent.extras
        if (extras != null) {
            if (extras.containsKey(INPUT_START_FOLDER)) folderPath = extras.getString(INPUT_START_FOLDER)
            if (extras.containsKey(INPUT_REGEX_FILTER)) core.setFilter(extras.getString(INPUT_REGEX_FILTER))
            if (extras.containsKey(INPUT_REGEX_FOLDER_FILTER)) core.setFolderFilter(extras.getString(INPUT_REGEX_FOLDER_FILTER))
            if (extras.containsKey(INPUT_SHOW_ONLY_SELECTABLE)) core.setShowOnlySelectable(extras.getBoolean(INPUT_SHOW_ONLY_SELECTABLE))
            if (extras.containsKey(INPUT_FOLDER_MODE)) core.setFolderMode(extras.getBoolean(INPUT_FOLDER_MODE))
            if (extras.containsKey(INPUT_CAN_CREATE_FILES)) core.setCanCreateFiles(extras.getBoolean(INPUT_CAN_CREATE_FILES))
            if (extras.containsKey(INPUT_LABELS)) core.setLabels(extras[INPUT_LABELS] as FileChooserLabels?)
            if (extras.containsKey(INPUT_SHOW_CONFIRMATION_ON_CREATE)) core.setShowConfirmationOnCreate(extras.getBoolean(INPUT_SHOW_CONFIRMATION_ON_CREATE))
            if (extras.containsKey(INPUT_SHOW_CANCEL_BUTTON)) core.setShowCancelButton(extras.getBoolean(INPUT_SHOW_CANCEL_BUTTON))
            if (extras.containsKey(INPUT_SHOW_CONFIRMATION_ON_SELECT)) core.setShowConfirmationOnSelect(extras.getBoolean(INPUT_SHOW_CONFIRMATION_ON_SELECT))
            if (extras.containsKey(INPUT_SHOW_FULL_PATH_IN_TITLE)) core.setShowFullPathInTitle(extras.getBoolean(INPUT_SHOW_FULL_PATH_IN_TITLE))
            if (extras.containsKey(INPUT_USE_BACK_BUTTON_TO_NAVIGATE)) useBackButton = extras.getBoolean(INPUT_USE_BACK_BUTTON_TO_NAVIGATE)
        }
        // Load the files of a folder.
        core.loadFolder(folderPath)
        startFolder = core.currentFolder
        // Add a listener for when a file is selected.
        core.addListener(object : FileChooserCore.OnFileSelectedListener {
            override fun onFileSelected(folder: File?, name: String?) { // Pass the data through an intent.
                val intent = Intent()
                val bundle = Bundle()
                bundle.putSerializable(OUTPUT_FILE_OBJECT, folder)
                bundle.putString(OUTPUT_NEW_FILE_NAME, name)
                intent.putExtras(bundle)
                setResult(RESULT_OK, intent)
                finish()
            }

            override fun onFileSelected(file: File?) { // Pass the data through an intent.
                val intent = Intent()
                val bundle = Bundle()
                bundle.putSerializable(OUTPUT_FILE_OBJECT, file)
                intent.putExtras(bundle)
                setResult(RESULT_OK, intent)
                finish()
            }
        })
        // Add a listener for when the cancel button is pressed.
        core.addListener (object: FileChooserCore.OnCancelListener {
            override fun onCancel() {
                // Close activity.
                super@FileChooserActivity.onBackPressed()
            }
        })
    }

    /**
     * Called when the user push the 'back' button.
     */
    override fun onBackPressed() { // Verify if the activity must be finished or if the parent folder must be opened.
        val current = core.currentFolder
        if (!useBackButton || current == null || current.parent == null || current.path.compareTo(startFolder!!.path) == 0) { // Close activity.
            super.onBackPressed()
        } else { // Open parent.
            core.loadFolder(current.parent)
        }
    }

    // ----- FileChooser methods ----- //
    override val rootLayout: LinearLayout?
        get() {
            val root = findViewById<View>(R.id.rootLayout)
            return if (root is LinearLayout) root else null
        }

    //return this.getBaseContext();
    override val fileChooserContext: Context
        get() =//return this.getBaseContext();
            this

    override fun setCurrentFolderName(name: String?) {
        this.title = name
    }

    companion object {
        // ----- Fields ----- //
        /**
         * Constant used for represent the key of the bundle object (inside the start's intent) which contains the
         * path of the folder which files are going to be listed.
         */
        const val INPUT_START_FOLDER = "input_start_folder"
        /**
         * Constant used for represent the key of the bundle object (inside the start's intent) which contains
         * a boolean that indicates if the user is going to select folders instead of select files.
         */
        const val INPUT_FOLDER_MODE = "input_folder_mode"
        /**
         * Constant used for represent the key of the bundle object (inside the start's intent) which contains
         * a boolean that indicates if the user can create files.
         */
        const val INPUT_CAN_CREATE_FILES = "input_can_create_files"
        // ----- Constants ----- //
        /**
         * Constant used for represent the key of the bundle object (inside the start's intent) which contains
         * a boolean that indicates if the cancel button must be show.
         */
        const val INPUT_SHOW_CANCEL_BUTTON = "input_show_cancel_button"
        /**
         * Constant used for represent the key of the bundle object (inside the start's intent) which contains
         * a regular expression which is going to be used as a filter to determine which files can be selected.
         */
        const val INPUT_REGEX_FILTER = "input_regex_filter"
        /**
         * Constant used for represent the key of the bundle object (inside the start's intent) which contains
         * a regular expression which is going to be used as a filter to determine which folders can be explored.
         */
        const val INPUT_REGEX_FOLDER_FILTER = "input_regex_folder_filter"
        /**
         * Constant used for represent the key of the bundle object (inside the start's intent) which contains
         * a boolean that indicates if only the files that can be selected must be displayed.
         */
        const val INPUT_SHOW_ONLY_SELECTABLE = "input_show_only_selectable"
        /**
         * Constant used for represent the key of the bundle object (inside the start's intent) which contains
         * an instance of the class FileChooserLabels that allows to override the default value of the labels.
         */
        const val INPUT_LABELS = "input_labels"
        /**
         * Constant used for represent the key of the bundle object (inside the start's intent) which contains
         * a boolean that indicates if a confirmation dialog must be displayed when creating a file.
         */
        const val INPUT_SHOW_CONFIRMATION_ON_CREATE = "input_show_confirmation_on_create"
        /**
         * Constant used for represent the key of the bundle object (inside the start's intent) which contains
         * a boolean that indicates if a confirmation dialog must be displayed when selecting a file.
         */
        const val INPUT_SHOW_CONFIRMATION_ON_SELECT = "input_show_confirmation_on_select"
        /**
         * Constant used for represent the key of the bundle object (inside the start's intent) which contains
         * a boolean that indicates if the title must show the full path of the current's folder (true) or only
         * the folder's name (false).
         */
        const val INPUT_SHOW_FULL_PATH_IN_TITLE = "input_show_full_path_in_title"
        /**
         * Constant used for represent the key of the bundle object (inside the start's intent) which contains
         * a boolean that indicates if the 'Back' button must be used to navigate to the parents folder (true) or
         * if must follow the default behavior (and close the activity when the button is pressed).
         */
        const val INPUT_USE_BACK_BUTTON_TO_NAVIGATE = "input_use_back_button_to_navigate"
        /**
         * Constant used for represent the key of the bundle object (inside the result's intent) which contains the
         * File object, that represents the file selected by the user or the folder in which the user wants to create
         * a file.
         */
        const val OUTPUT_FILE_OBJECT = "output_file_object"
        /**
         * Constant used for represent the key of the bundle object (inside the result's intent) which contains the
         * name of the file that the user wants to create.
         */
        const val OUTPUT_NEW_FILE_NAME = "output_new_file_name"
    }
}