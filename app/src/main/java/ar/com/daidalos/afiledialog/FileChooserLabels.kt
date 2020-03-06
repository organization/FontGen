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

import java.io.Serializable

/**
 * Instances of this classes are used to re-define the value of the labels of a file chooser.
 *
 *
 * If an attribute is set to null, then the default value is going to be used.
 */
class FileChooserLabels : Serializable {
    /**
     * The label for the button used to create a file or a folder.
     */
    var labelAddButton: String? = null
    /**
     * The label for the cancel button.
     */
    var labelCancelButton: String? = null
    /**
     * The label for the button for select the current folder (when using the file chooser for select folders).
     */
    var labelSelectButton: String? = null
    /**
     * The message displayed by the confirmation dialog, when selecting a file.
     *
     *
     * In this string, the character sequence '$file_name' is going to be replace by the file's name.
     */
    var messageConfirmSelection: String? = null
    /**
     * The message displayed by the confirmation dialog, when creating a file.
     *
     *
     * In this string, the character sequence '$file_name' is going to be replace by the file's name.
     */
    var messageConfirmCreation: String? = null
    /**
     * The label for the 'yes' button when confirming the selection o creation of a file.
     */
    var labelConfirmYesButton: String? = null
    /**
     * The label for the 'no' button when confirming the selection o creation of a file.
     */
    var labelConfirmNoButton: String? = null
    /**
     * The title of the dialog for create a file.
     */
    var createFileDialogTitle: String? = null
    /**
     * The message of the dialog for create a file.
     */
    var createFileDialogMessage: String? = null
    /**
     * The label of the 'accept' button in the dialog for create a file.
     */
    var createFileDialogAcceptButton: String?
    /**
     * The label of the 'cancel' button in the dialog for create a file.
     */
    var createFileDialogCancelButton: String?

    companion object {
        /**
         * Static field required by the interface Serializable.
         */
        private const val serialVersionUID = 1L
    }

    /**
     * Default's constructor.
     */
    init {
        createFileDialogTitle = null
        createFileDialogAcceptButton = null
        createFileDialogCancelButton = null
    }
}