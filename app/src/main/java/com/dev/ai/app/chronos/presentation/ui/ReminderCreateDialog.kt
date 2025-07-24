package com.dev.ai.app.chronos.presentation.ui

import android.Manifest
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dev.ai.app.chronos.presentation.viewModel.ReminderViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID


@Composable
fun ReminderCreateDialog(
    viewModel: ReminderViewModel = hiltViewModel(),
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var uploading by remember { mutableStateOf(false) }
    var localImageUri by remember { mutableStateOf<Uri?>(null) }
    var showImagePickerMenu by remember { mutableStateOf(false) }
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }

    val calendar = remember {
        Calendar.getInstance().apply {
            timeInMillis = if(state.dateTime == 0L){
                System.currentTimeMillis()+60+1000// Default to 1 min from now
            }else{
                state.dateTime
            }
        }
    }

    val datePickerDialog = remember {
        android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                // Ensure the selected date is not in the past
                if (calendar.timeInMillis < System.currentTimeMillis()) {
                    calendar.timeInMillis = System.currentTimeMillis() + 60 * 1000
                }
                viewModel.onDateTimeChange(calendar.timeInMillis)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = System.currentTimeMillis()
        }
    }

    val timePickerDialog = remember {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                if (calendar.timeInMillis < System.currentTimeMillis()) {
                    calendar.timeInMillis = System.currentTimeMillis() + 60 * 1000
                }
                viewModel.onDateTimeChange(calendar.timeInMillis)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false // Set to false to show 12-hour format with AM/PM
        )
    }
    val displayDateTime by remember(state.dateTime) {
        mutableStateOf(
            SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                .format(Date(if (state.dateTime == 0L) System.currentTimeMillis() + 60 * 1000 else state.dateTime))
        )
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            localImageUri = it
            uploading = true
            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("reminder_images/${UUID.randomUUID()}")
            val uploadTask = imageRef.putFile(it)
            uploadTask.addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    viewModel.onImageUrlChange(downloadUrl.toString())
                    uploading = false
                }
            }.addOnFailureListener {
                uploading = false
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
        if (success && cameraImageUri != null) {
            localImageUri = cameraImageUri
            uploading = true
            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("reminder_images/${UUID.randomUUID()}")
            val uploadTask = imageRef.putFile(cameraImageUri!!)
            uploadTask.addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    viewModel.onImageUrlChange(downloadUrl.toString())
                    uploading = false
                }
            }.addOnFailureListener {
                uploading = false
            }
        }
    }

    fun createImageUri(context: Context): Uri? {
        val contentResolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "reminder_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }
        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    }

    val cameraPermission = Manifest.permission.CAMERA

    var askCameraPermission by remember { mutableStateOf(false) }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        askCameraPermission = false
        if (granted) {
            val uri = createImageUri(context)
            if (uri != null) {
                cameraImageUri = uri
                cameraLauncher.launch(uri)
            }
        }
    }

    AlertDialog(
        title = {Text("Add Reminder")},
        text = {
            Column (
                modifier = Modifier
                    .wrapContentWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ){
                OutlinedTextField(
                    value = state.title,
                    onValueChange = viewModel::onTitleChange,
                    label = {Text(text = "Title")}
                )
                OutlinedTextField(
                    value = state.notes,
                    onValueChange = viewModel::onNoteChange,
                    label = {Text(text = "Notes (optional)")}
                )

                Row (
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ){
                    Button(onClick = {datePickerDialog.show()}) { Text("Pick Date") }
                    Button(onClick = {timePickerDialog.show()}) { Text("Pick Time") }
                }
                Text("Date/Time: $displayDateTime")

                Button(
                    onClick = { showImagePickerMenu = true },
                    enabled = !uploading
                ) {
                    Text(if (uploading) "Uploading..." else "Pick Image")
                }
                DropdownMenu(
                    expanded = showImagePickerMenu,
                    onDismissRequest = { showImagePickerMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Pick from Gallery") },
                        onClick = {
                            showImagePickerMenu = false
                            galleryLauncher.launch("image/*")
                        },
                        leadingIcon = { Icon(Icons.Default.AccountBox, contentDescription = null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Take Photo") },
                        onClick = {
                            showImagePickerMenu = false
                            if (ContextCompat.checkSelfPermission(context, cameraPermission) == PackageManager.PERMISSION_GRANTED) {
                                val uri = createImageUri(context)
                                if (uri != null) {
                                    cameraImageUri = uri
                                    cameraLauncher.launch(uri)
                                }
                            } else {
                                askCameraPermission = true
                                cameraPermissionLauncher.launch(cameraPermission)
                            }
                        },
                        leadingIcon = { Icon(Icons.Default.AddCircle, contentDescription = null) }
                    )
                }
                Spacer(Modifier.height(8.dp))
                if (localImageUri != null) {
                    AsyncImage(
                        model = localImageUri,
                        contentDescription = null,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                    )
                } else if (state.imageUrl != null) {
                    AsyncImage(
                        model = state.imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                    )
                }

            }
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    onSave()
                }
            ) {
                Text(text = "save")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onDismiss()
                }
            ) {
                Text(text = "Cancel")
            }
        }
    )
}

@Preview
@Composable
fun ReminderCreateDialogPreview(modifier: Modifier = Modifier) {
    ReminderCreateDialog(
        onSave = {},
        onDismiss = {}
    )
}