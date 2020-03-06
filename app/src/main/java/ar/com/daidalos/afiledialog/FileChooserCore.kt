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

import android.app.AlertDialog
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import ar.com.daidalos.afiledialog.view.FileItem
import ar.com.daidalos.afiledialog.view.FileItem.OnFileClickListener
import ifteam.affogatoman.fontgen.R
import java.io.File
import java.util.*

/**
 * This class implements the common features of a file chooser.
 */
internal class FileChooserCore(
        /**
         * The file chooser in which all the operations are performed.
         */
        private val chooser: FileChooser) {
    companion object {
        // ----- Attributes ----- //
        /**
         * Static attribute for save the folder displayed by default.
         */
        private var defaultFolder: File? = null

        /**
         * Static constructor.
         */
        init {
            defaultFolder = null
        }
    }

    /**
     * The listeners for the event of select a file.
     */
    private val fileSelectedListeners: MutableList<OnFileSelectedListener>
    /**
     * The listeners for the event of select a file.
     */
    private val cancelListeners: MutableList<OnCancelListener>
    /**
     * A regular expression for filter the files.
     */
    private var filter: String?
    /**
     * A regular expression for filter the folders.
     */
    private var folderFilter: String?
    /**
     * A boolean indicating if only the files that can be selected (they pass the filter) must be show.
     */
    private var showOnlySelectable: Boolean
    /**
     * A boolean indicating if the user can create files.
     */
    private var canCreateFiles = false
    /**
     * A boolean indicating if the chooser is going to be used to select folders.
     */
    private var folderMode = false
    /**
     * A boolean indicating if the chooser is going to be used to select folders.
     */
    private var showCancelButton: Boolean
    /**
     * Returns the current folder.
     *
     * @return The current folder.
     */
    /**
     * A file that indicates the folder that is currently being displayed.
     */
    var currentFolder: File? = null
        private set
    /**
     * This attribut allows to override the default value of the labels.
     */
    private var labels: FileChooserLabels? = null
    /**
     * A boolean that indicates if a confirmation dialog must be displaying when selecting a file.
     */
    private var showConfirmationOnSelect: Boolean
    // ---- Static attributes ----- //
    /**
     * A boolean that indicates if a confirmation dialog must be displaying when creating a file.
     */
    private var showConfirmationOnCreate: Boolean
    /**
     * A boolean indicating if the folder's full path must be show in the title.
     */
    private var showFullPathInTitle: Boolean
    // ----- Constructor ----- //
    /**
     * Implementation of the click listener for when the add button is clicked.
     */
    private val addButtonClickListener = View.OnClickListener { v ->
        // Get the current context.
        val context = v.context
        // Create an alert dialog.
        val alert = AlertDialog.Builder(context)
        // Define the dialog's labels.
        var title: String? = context.getString(if (folderMode) R.string.daidalos_create_folder else R.string.daidalos_create_file)
        if (labels != null && labels!!.createFileDialogTitle != null) title = labels!!.createFileDialogTitle
        var message: String? = context.getString(if (folderMode) R.string.daidalos_enter_folder_name else R.string.daidalos_enter_file_name)
        if (labels != null && labels!!.createFileDialogMessage != null) message = labels!!.createFileDialogMessage
        val posButton = if (labels != null && labels!!.createFileDialogAcceptButton != null) labels!!.createFileDialogAcceptButton else context.getString(R.string.daidalos_accept)
        val negButton = if (labels != null && labels!!.createFileDialogCancelButton != null) labels!!.createFileDialogCancelButton else context.getString(R.string.daidalos_cancel)
        // Set the title and the message.
        alert.setTitle(title)
        alert.setMessage(message)
        // Set an EditText view to get the file's name.
        val input = EditText(context)
        input.setSingleLine()
        alert.setView(input)
        // Set the 'ok' and 'cancel' buttons.
        alert.setPositiveButton(posButton) { dialog, whichButton ->
            val fileName = input.text.toString()
            // Verify if a value has been entered.
            if (fileName.isNotEmpty()) { // Notify the listeners.
                notifyFileListeners(currentFolder, fileName)
            }
        }
        alert.setNegativeButton(negButton) { dialog, whichButton ->
            // Do nothing, automatically the dialog is going to be closed.
        }
        // Show the dialog.
        alert.show()
    }
    // ----- Events methods ----- //
    /**
     * Implementation of the click listener for when the ok button is clicked.
     */
    private val okButtonClickListener = View.OnClickListener {
        // Notify the listeners.
        notifyFileListeners(currentFolder, null)
    }
    /**
     * Implementation of the click listener for when the cancel button is clicked.
     */
    private val cancelButtonClickListener = View.OnClickListener {
        // Notify the listeners.
        notifyCancelListeners()
    }
    /**
     * Implementation of the click listener for when a file item is clicked.
     */
    private val fileItemClickListener = object: OnFileClickListener {
        override fun onClick(source: FileItem) {
            // Verify if the item is a folder.
            val file = source.getFile()
            if (file!!.isDirectory) { // Open the folder.
                this@FileChooserCore.loadFolder(file)
            } else { // Notify the listeners.
                notifyFileListeners(file, null)
            }
        }
    }

    /**
     * Add a listener for the event of a file selected.
     *
     * @param listener The listener to add.
     */
    fun addListener(listener: OnFileSelectedListener) {
        fileSelectedListeners.add(listener)
    }

    /**
     * Removes a listener for the event of a file selected.
     *
     * @param listener The listener to remove.
     */
    fun removeListener(listener: OnFileSelectedListener?) {
        fileSelectedListeners.remove(listener)
    }

    /**
     * Add a listener for the event of a file selected.
     *
     * @param listener The listener to add.
     */
    fun addListener(listener: OnCancelListener) {
        cancelListeners.add(listener)
    }

    /**
     * Removes a listener for the event of a file selected.
     *
     * @param listener The listener to remove.
     */
    fun removeListener(listener: OnCancelListener?) {
        cancelListeners.remove(listener)
    }

    /**
     * Removes all the listeners for the event of a file selected.
     */
    fun removeAllListeners() {
        fileSelectedListeners.clear()
        cancelListeners.clear()
    }

    /**
     * Notify to all listeners that the cancel button has been pressed.
     */
    private fun notifyCancelListeners() {
        for (i in cancelListeners.indices) {
            cancelListeners[i].onCancel()
        }
    }

    /**
     * Notify to all listeners that a file has been selected or created.
     *
     * @param file The file or folder selected or the folder in which the file must be created.
     * @param name The name of the file that must be created or 'null' if a file was selected (instead of being created).
     */
    private fun notifyFileListeners(file: File?, name: String?) { // Determine if a file has been selected or created.
        val creation = name != null && name.length > 0
        // Verify if a confirmation dialog must be show.
        if (creation && showConfirmationOnCreate || !creation && showConfirmationOnSelect) { // Create an alert dialog.
            val context = chooser.fileChooserContext
            val alert = AlertDialog.Builder(context)
            // Define the dialog's labels.
            var message: String? = null
            message = if (labels != null && (creation && labels!!.messageConfirmCreation != null || !creation && labels!!.messageConfirmSelection != null)) {
                if (creation) labels!!.messageConfirmCreation else labels!!.messageConfirmSelection
            } else {
                if (folderMode) {
                    context!!.getString(if (creation) R.string.daidalos_confirm_create_folder else R.string.daidalos_confirm_select_folder)
                } else {
                    context!!.getString(if (creation) R.string.daidalos_confirm_create_file else R.string.daidalos_confirm_select_file)
                }
            }
            if (message != null) message = message.replace("\$file_name", name ?: file!!.name)
            val posButton = if (labels != null && labels!!.labelConfirmYesButton != null) labels!!.labelConfirmYesButton else context!!.getString(R.string.daidalos_yes)
            val negButton = if (labels != null && labels!!.labelConfirmNoButton != null) labels!!.labelConfirmNoButton else context!!.getString(R.string.daidalos_no)
            // Set the message and the 'yes' and 'no' buttons.
            alert.setMessage(message)
            alert.setPositiveButton(posButton) { dialog, whichButton ->
                // Notify to listeners.
                for (i in fileSelectedListeners.indices) {
                    if (creation) {
                        fileSelectedListeners[i].onFileSelected(file, name)
                    } else {
                        fileSelectedListeners[i].onFileSelected(file)
                    }
                }
            }
            alert.setNegativeButton(negButton) { dialog, whichButton ->
                // Do nothing, automatically the dialog is going to be closed.
            }
            // Show the dialog.
            alert.show()
        } else { // Notify to listeners.
            for (i in fileSelectedListeners.indices) {
                if (creation) {
                    fileSelectedListeners[i].onFileSelected(file, name)
                } else {
                    fileSelectedListeners[i].onFileSelected(file)
                }
            }
        }
    }

    /**
     * Allows to define if a confirmation dialog must be show when selecting a file.
     *
     * @param show 'true' for show the confirmation dialog, 'false' for not show the dialog.
     */
    fun setShowConfirmationOnSelect(show: Boolean) {
        showConfirmationOnSelect = show
    }

    /**
     * Allows to define if a confirmation dialog must be show when creating a file.
     *
     * @param show 'true' for show the confirmation dialog, 'false' for not show the dialog.
     */
    fun setShowConfirmationOnCreate(show: Boolean) {
        showConfirmationOnCreate = show
    }
    // ----- Get and set methods ----- //
    /**
     * Allows to define if, in the title, must be show only the current folder's name or the full file's path..
     *
     * @param show 'true' for show the full path, 'false' for show only the name.
     */
    fun setShowFullPathInTitle(show: Boolean) {
        showFullPathInTitle = show
    }

    /**
     * Defines the value of the labels.
     *
     * @param labels The labels.
     */
    fun setLabels(labels: FileChooserLabels?) {
        this.labels = labels
        // Verify if the buttons for add a file or select a folder has been modified.
        if (labels != null) {
            val root = chooser.rootLayout
            if (labels.labelAddButton != null) {
                val addButton = root!!.findViewById<View>(R.id.buttonAdd) as Button
                addButton.text = labels.labelAddButton
            }
            if (labels.labelSelectButton != null) {
                val okButton = root!!.findViewById<View>(R.id.buttonOk) as Button
                okButton.text = labels.labelSelectButton
            }
            if (labels.labelCancelButton != null) {
                val cancelButton = root!!.findViewById<View>(R.id.buttonCancel) as Button
                cancelButton.text = labels.labelCancelButton
            }
        }
    }

    /**
     * Set a regular expression to filter the files that can be selected.
     *
     * @param filter A regular expression.
     */
    fun setFilter(filter: String?) {
        if (filter == null || filter.length == 0) {
            this.filter = null
        } else {
            this.filter = filter
        }
        // Reload the list of files.
        this.loadFolder(currentFolder)
    }

    /**
     * Set a regular expression to filter the folders that can be explored.
     *
     * @param folderFilter A regular expression.
     */
    fun setFolderFilter(folderFilter: String?) {
        if (folderFilter == null || folderFilter.isEmpty()) {
            this.folderFilter = null
        } else {
            this.folderFilter = folderFilter
        }
        // Reload the list of files.
        this.loadFolder(currentFolder)
    }

    /**
     * Defines if the chooser is going to be used to select folders, instead of files.
     *
     * @param folderMode 'true' for select folders or 'false' for select files.
     */
    fun setFolderMode(folderMode: Boolean) {
        this.folderMode = folderMode
        // Show or hide the 'Ok' button.
        updateButtonsLayout()
        // Reload the list of files.
        this.loadFolder(currentFolder)
    }

    /**
     * Defines if the chooser is going to be used to select folders, instead of files.
     *
     * @param showCancelButton 'true' for show the cancel button or 'false' for not showing it.
     */
    fun setShowCancelButton(showCancelButton: Boolean) {
        this.showCancelButton = showCancelButton
        // Show or hide the 'Cancel' button.
        updateButtonsLayout()
    }

    /**
     * Defines if the user can create files, instead of only select files.
     *
     * @param canCreate 'true' if the user can create files or 'false' if it can only select them.
     */
    fun setCanCreateFiles(canCreate: Boolean) {
        canCreateFiles = canCreate
        // Show or hide the 'Add' button.
        updateButtonsLayout()
    }

    /**
     * Defines if only the files that can be selected (they pass the filter) must be show.
     *
     * @param show 'true' if only the files that can be selected must be show or 'false' if all the files must be show.
     */
    fun setShowOnlySelectable(show: Boolean) {
        showOnlySelectable = show
        // Reload the list of files.
        this.loadFolder(currentFolder)
    }

    /**
     * Changes the height of the layout for the buttons, according if the buttons are visible or not.
     */
    private fun updateButtonsLayout() { // Get the buttons layout.
        val root = chooser.rootLayout
        // Verify if the 'Add' button is visible or not.
        val addButton = root!!.findViewById<View>(R.id.buttonAdd)
        addButton.visibility = if (canCreateFiles) View.VISIBLE else View.GONE
        // Verify if the 'Ok' button is visible or not.
        val okButton = root.findViewById<View>(R.id.buttonOk)
        okButton.visibility = if (folderMode) View.VISIBLE else View.GONE
        // Verify if the 'Cancel' button is visible or not.
        val cancelButton = root.findViewById<View>(R.id.buttonCancel)
        cancelButton.visibility = if (showCancelButton) View.VISIBLE else View.GONE
    }
    // ----- Miscellaneous methods ----- //
    /**
     * Loads all the files of a folder in the file chooser.
     *
     *
     * If no path is specified ('folderPath' is null) the root folder of the SD card is going to be used.
     *
     * @param folderPath The folder's path.
     */
    fun loadFolder(folderPath: String?) { // Get the file path.
        var path: File? = null
        if (folderPath != null && folderPath.length > 0) {
            path = File(folderPath)
        }
        this.loadFolder(path)
    }
    /**
     * Loads all the files of a folder in the file chooser.
     *
     *
     * If no path is specified ('folder' is null) the root folder of the SD card is going to be used.
     *
     * @param folder The folder.
     */
    /**
     * Loads all the files of the SD card root.
     */
    @JvmOverloads
    fun loadFolder(folder: File? = defaultFolder) { // Remove previous files.
        val root = chooser.rootLayout
        val layout = root!!.findViewById<View>(R.id.linearLayoutFiles) as LinearLayout
        layout.removeAllViews()
        // Get the file path.
        if (folder == null || !folder.exists()) {
            if (defaultFolder != null) {
                currentFolder = defaultFolder
            } else {
                currentFolder = Environment.getExternalStorageDirectory()
            }
        } else {
            currentFolder = folder
        }
        // Verify if the path exists.
        if (currentFolder!!.exists() && layout != null) {
            val fileItems: MutableList<FileItem> = LinkedList()
            // Add the parent folder.
            if (currentFolder!!.parent != null) {
                val parent = File(currentFolder!!.parent)
                if (parent.exists()) {
                    val parentFolder = FileItem(chooser.fileChooserContext, parent, "..")
                    if (parentFolder.isSelectable())
                        parentFolder.setSelectable(folderFilter == null || parent.absolutePath.matches(folderFilter!!.toRegex()))
                    fileItems.add(parentFolder)
                }
            }
            // Verify if the file is a directory.
            if (currentFolder!!.isDirectory) { // Get the folder's files.
                val fileList = currentFolder!!.listFiles()
                if (fileList != null) { // Order the files alphabetically and separating folders from files.
                    Arrays.sort(fileList, Comparator { file1, file2 ->
                        if (file1 != null && file2 != null) {
                            if (file1.isDirectory && !file2.isDirectory) return@Comparator -1
                            return@Comparator if (file2.isDirectory && !file1.isDirectory) 1 else file1.name.compareTo(file2.name)
                        }
                        0
                    })
                    // Iterate all the files in the folder.
                    for (i in fileList.indices) { // Verify if file can be selected.
                        var selectable = true
                        selectable = if (!fileList[i].isDirectory) { // File is selectable as long the user is not selecting folders and if pass the filter (if defined).
                            !folderMode && (filter == null || fileList[i].name.matches(filter!!.toRegex()))
                        } else { // Folders can be selected iif pass the filter (if defined).
                            folderFilter == null || fileList[i].absolutePath.matches(folderFilter!!.toRegex())
                        }
                        // Verify if the file must be show.
                        if (selectable || !showOnlySelectable) { // Create the file item and add it to the list.
                            val fileItem = FileItem(chooser.fileChooserContext, fileList[i])
                            if (fileItem.isSelectable())
                                fileItem.setSelectable(selectable)
                            fileItems.add(fileItem)
                        }
                    }
                }
                // Set the name of the current folder.
                val currentFolderName = if (showFullPathInTitle) currentFolder!!.path else currentFolder!!.name
                chooser.setCurrentFolderName(currentFolderName)
            } else { // The file is not a folder, add only this file.
                fileItems.add(FileItem(chooser.fileChooserContext, currentFolder))
            }
            // Add click listener and add the FileItem objects to the layout.
            for (i in fileItems.indices) {
                fileItems[i].addListener(fileItemClickListener)
                layout.addView(fileItems[i])
            }
            // Refresh default folder.
            defaultFolder = currentFolder
        }
    }

    /**
     * Interface definition for a callback to be invoked when a file is selected.
     */
    interface OnFileSelectedListener {
        /**
         * Called when a file has been selected.
         *
         * @param file The file selected.
         */
        fun onFileSelected(file: File?)

        /**
         * Called when an user wants to be create a file.
         *
         * @param folder The file's parent folder.
         * @param name   The file's name.
         */
        fun onFileSelected(folder: File?, name: String?)
    }

    /**
     * Interface definition for a callback to be invoked when the cancel button is clicked.
     */
    interface OnCancelListener {
        /**
         * Called when the cancel button is clicked.
         */
        fun onCancel()
    }

    /**
     * Creates an instance of this class.
     *
     * @param fileChooser The graphical file chooser.
     */
    init { // Initialize attributes.
        fileSelectedListeners = LinkedList()
        cancelListeners = LinkedList()
        filter = null
        folderFilter = null
        showOnlySelectable = false
        setCanCreateFiles(false)
        setFolderMode(false)
        currentFolder = null
        labels = null
        showConfirmationOnCreate = false
        showConfirmationOnSelect = false
        showFullPathInTitle = false
        showCancelButton = false
        // Add listener for the buttons.
        val root = chooser.rootLayout
        val addButton = root!!.findViewById<View>(R.id.buttonAdd) as Button
        addButton.setOnClickListener(addButtonClickListener)
        val okButton = root.findViewById<View>(R.id.buttonOk) as Button
        okButton.setOnClickListener(okButtonClickListener)
        val cancelButton = root.findViewById<View>(R.id.buttonCancel) as Button
        cancelButton.setOnClickListener(cancelButtonClickListener)
    }
}