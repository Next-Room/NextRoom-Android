package com.nextroom.nextroom.presentation.ui.tutorial.timer.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.common.compose.NRColor
import com.nextroom.nextroom.presentation.common.compose.NRTypo
import com.nextroom.nextroom.presentation.extension.throttleClick

@Composable
fun TutorialExitBottomSheetContent(
    onExitClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.tutorial_exit_sheet_title),
            style = NRTypo.Pretendard.size20SemiBold,
            color = NRColor.White,
            textAlign = TextAlign.Center,
            lineHeight = 28.sp,
        )

        Spacer(modifier = Modifier.height(46.dp))

        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            FeatureItem(text = stringResource(R.string.tutorial_exit_sheet_feature_image))
            FeatureItem(text = stringResource(R.string.tutorial_exit_sheet_feature_background))
            FeatureItem(text = stringResource(R.string.tutorial_exit_sheet_feature_manage))
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(NRColor.White20)
                .throttleClick { onExitClick() }
                .padding(vertical = 16.dp),
            text = stringResource(R.string.tutorial_exit_sheet_exit_button),
            style = NRTypo.Pretendard.size16SemiBold,
            color = NRColor.White,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun FeatureItem(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(NRColor.Blue, CircleShape),
        )
        Text(
            text = text,
            style = NRTypo.Body.size14Regular,
            color = NRColor.ColorSurface,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1F2023)
@Composable
private fun TutorialExitBottomSheetPreview() {
    TutorialExitBottomSheetContent(onExitClick = {})
}
