/*
 * <Copyright 2013 Jose F. Maldonado>
 *
 *  This file is part of aFileDialog.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package ar.com.daidalos.afiledialog.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import ifteam.affogatoman.fontgen.R
import java.io.File
import java.util.*

/**
 * This class is used to represents the files that can be selected by the user.
 */
class FileItem(context: Context?) : LinearLayout(context) {
    // ----- Attributes ----- //
    /**
     * The file which is represented by this item.
     */
    private var file: File?
    /**
     * The image in which show the file's icon.
     */
    private val icon: ImageView
    /**
     * The label in which show the file's name.
     */
    private val label: TextView
    /**
     * A boolean indicating if the item can be selected.
     */
    private var selectable: Boolean = false
    /**
     * The listeners for the click event.
     */
    private val listeners: MutableList<OnFileClickListener> = mutableListOf()
    // ----- Constructor ----- //
    /**
     * Listener for the click event.
     */
    private val clickListener = OnClickListener {
        // Verify if the item can be selected.
        if (selectable) { // Call the listeners.
            for (i in listeners.indices) {
                listeners[i].onClick(this@FileItem)
            }
        }
    }

    /**
     * A class constructor.
     *
     * @param context The application's context.
     * @param file    The file represented by this item
     */
    constructor(context: Context?, file: File?) : this(context) {
        // Set the file.
        setFile(file)
    }
    // ----- Get() and Set() methods ----- //
    /**
     * A class constructor.
     *
     * @param context The application's context.
     * @param file    The file represented by this item.
     * @param label   The label of this item.
     */
    constructor(context: Context?, file: File?, label: String?) : this(context, file) {
        // Set the label.
        setLabel(label)
    }

    /**
     * Returns the file represented by this item.
     *
     * @return A file.
     */
    fun getFile(): File? {
        return file
    }

    /**
     * Defines the file represented by this item.
     *
     * @param file A file.
     */
    fun setFile(file: File?) {
        if (file != null) {
            this.file = file
            // Replace the label by the file's name.
            setLabel(file.name)
            // Change the icon, depending if the file is a folder or not.
            updateIcon()
        }
    }

    /**
     * Changes the label of this item, which by default is the file's name.
     *
     *
     * This method must be called after invoking the method setFile(), otherwise
     * the label is going to be overwritten with the file's name.
     *
     * @param label A string value.
     */
    fun setLabel(label: String?) { // Verify if 'label' is not null.
        var label = label
        if (label == null) label = ""
        // Change the label.
        this.label.text = label
    }

    /**
     * Verifies if the item can be selected.
     *
     * @return 'true' if the item can be selected, 'false' if not.
     */
    fun isSelectable(): Boolean {
        return selectable
    }
    // ----- Miscellaneous methods ----- //
    /**
     * Defines if the item can be selected or not.
     *
     * @param selectable 'true' if the item can be selected, 'false' if not.
     */
    fun setSelectable(selectable: Boolean) { // Save the value.
        this.selectable = selectable
        // Update the icon.
        updateIcon()
    }
    // ----- Events ----- //
    /**
     * Updates the icon according to if the file is a folder and if it can be selected.
     */
    private fun updateIcon() { // Define the icon.
        var icon = R.drawable.document
        icon = if (file != null && file!!.isDirectory) {
            if (selectable) R.drawable.folder else R.drawable.folder_gray
        } else {
            if (selectable) R.drawable.document else R.drawable.document_gray
        }
        // Set the icon.
        this.icon.setImageDrawable(resources.getDrawable(icon))
        // Change the color of the text.
        if (icon != R.drawable.document_gray && icon != R.drawable.folder_gray) {
            label.setTextColor(resources.getColor(R.color.daidalos_active_file))
        } else {
            label.setTextColor(resources.getColor(R.color.daidalos_inactive_file))
        }
    }

    /**
     * Add a listener for the click event.
     *
     * @param listener The listener to add.
     */
    fun addListener(listener: OnFileClickListener) {
        listeners.add(listener)
    }

    /**
     * Removes a listener for the click event.
     *
     * @param listener The listener to remove.
     */
    fun removeListener(listener: OnFileClickListener?) {
        listeners.remove(listener)
    }

    /**
     * Removes all the listeners for the click event.
     */
    fun removeAllListeners() {
        listeners.clear()
    }

    /**
     * Interface definition for a callback to be invoked when a FileItem is clicked.
     */
    interface OnFileClickListener {
        /**
         * Called when a FileItem has been clicked.
         *
         * @param source The source of the event.
         */
        fun onClick(source: FileItem)
    }

    /**
     * The class main constructor.
     *
     * @param context The application's context.
     */
    init {
        // Define the layout.
        val inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.daidalos_file_item, this, true)
        // Initialize attributes.
        file = null
        selectable = true
        icon = findViewById<View>(R.id.imageViewIcon) as ImageView
        label = findViewById<View>(R.id.textViewLabel) as TextView
        // Add a listener for the click event.
        setOnClickListener(clickListener)
    }
}