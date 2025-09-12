package com.example.focustimer.survery

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.focustimer.R

// ScoreSelector.kt
@Composable
fun ScoreSelector(
    selectedScore: Int?,
    onScoreSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        for (score in 1..5) {
            ScoreButton(
                score = score,
                selected = selectedScore == score,
                onClick = { onScoreSelected(score) }
            )
        }
    }
}

@Composable
fun ScoreButton(
    score: Int,
    selected: Boolean,
    onClick: () -> Unit
) {

    val backgroundColor = if (selected) colorResource(R.color.myButtonColor) else colorResource(R.color.myGray)
    val contentColor = if (selected) colorResource(R.color.myBlack) else colorResource(R.color.myGray)

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(color = colorResource(R.color.myButtonColor))
            .clickable(onClick = onClick)
            .border(
                width = 1.dp,
                color = colorResource(R.color.myButtonColor),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = score.toString(),
            color = contentColor,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

// QuestionItem.kt
@Preview
@Composable
fun QuestionItem(
    question: Question = Question(text = "testQuestion", category = QuestionCategory.EVENING_TYPE, id = -1),
    selectedScore: Int? = null,
    onScoreSelected: (Int) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "${question.id}. ${question.text}",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )



        ScoreSelector(
            selectedScore = selectedScore,
            onScoreSelected = onScoreSelected
        )
    }
}
