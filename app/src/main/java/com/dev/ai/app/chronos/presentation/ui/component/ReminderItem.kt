package com.dev.ai.app.chronos.presentation.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReminderItem(
    title: String,
    notes: String?,
    dateTime: Long?,
    imageUrl: String?,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            if(!imageUrl.isNullOrEmpty()){
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                )
            }
            Column(modifier = Modifier.weight(1f).padding(8.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                if(dateTime!= null){
                    Text(
                        text = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(dateTime)),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                if(!notes.isNullOrEmpty()){
                    Text(notes, style = MaterialTheme.typography.bodySmall)
                }
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Create, contentDescription = "Delete")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Preview
@Composable
fun ReminderItemPreview(modifier: Modifier = Modifier) {
    ReminderItem(
        title = "Test Title",
        notes = "Test Notes",
        dateTime= System.currentTimeMillis(),
        imageUrl = "https://static.vecteezy.com/system/resources/thumbnails/036/226/872/small/ai-generated-nature-landscapes-background-free-photo.jpg",
        onEdit = {},
        onDelete = {}
    )
}