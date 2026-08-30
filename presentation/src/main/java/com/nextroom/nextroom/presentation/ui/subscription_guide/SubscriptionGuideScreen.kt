package com.nextroom.nextroom.presentation.ui.subscription_guide

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.common.compose.NRColor
import com.nextroom.nextroom.presentation.common.compose.NRLoading
import com.nextroom.nextroom.presentation.common.compose.NRTypo
import com.nextroom.nextroom.presentation.extension.throttleClick
import com.nextroom.nextroom.presentation.ui.billing.BillingPeriod

@Composable
fun SubscriptionGuideScreen(
    state: SubscriptionGuideUiState,
    onCloseClick: () -> Unit,
    onStartFreeTrialClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NRColor.Dark01),
    ) {
        val plan = state.plan

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            // 상단에 고정된 닫기 버튼과 컨텐츠가 겹치지 않도록 확보한다.
            Spacer(modifier = Modifier.height(CLOSE_BUTTON_SIZE))

            // 기간과 가격을 Play의 offer에서 읽어오므로 조회 전에는 본문을 그리지 않는다.
            if (plan != null) {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    if (plan.isFreeTrial) {
                        Spacer(modifier = Modifier.height(12.dp))
                        LimitedOfferBadge()
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Headline(plan = plan)

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.subscription_guide_description),
                        style = NRTypo.Pretendard.size14,
                        lineHeight = 21.sp,
                        color = NRColor.Gray01,
                    )

                    Spacer(modifier = Modifier.height(28.dp))
                    PriceCard(plan = plan)

                    Spacer(modifier = Modifier.height(36.dp))
                    Text(
                        text = stringResource(R.string.subscription_guide_benefit_section),
                        style = NRTypo.Pretendard.size18SemiBold,
                        color = NRColor.White,
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    benefits.forEachIndexed { index, benefit ->
                        if (index > 0) Spacer(modifier = Modifier.height(12.dp))
                        BenefitItem(order = index + 1, benefit = benefit)
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Notes(plan = plan)
                }

                // 하단에 고정된 버튼 영역에 마지막 컨텐츠가 가려지지 않도록 확보한다.
                Spacer(modifier = Modifier.height(BOTTOM_BAR_HEIGHT))
            }
        }

        if (plan != null) {
            BottomBar(
                plan = plan,
                onStartFreeTrialClick = onStartFreeTrialClick,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }

        // 전면 팝업처럼 보이도록 툴바 대신 우측 상단 닫기 버튼만 띄운다.
        CloseButton(
            onClick = onCloseClick,
            modifier = Modifier.align(Alignment.TopEnd),
        )

        NRLoading(isVisible = state.loading || plan == null)
    }
}

@Composable
private fun CloseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(R.drawable.ic_exit24),
        contentDescription = stringResource(R.string.dialog_close),
        colorFilter = ColorFilter.tint(NRColor.White),
        modifier = modifier
            .size(CLOSE_BUTTON_SIZE)
            .throttleClick(onClick = onClick)
            .padding(16.dp),
    )
}

/**
 * "P3M" 같은 결제 주기를 "3개월"처럼 읽을 수 있는 문구로 바꾼다.
 * 여러 단위가 섞여 있으면 큰 단위부터 이어 붙인다.
 */
@Composable
private fun BillingPeriod.toDisplayText(): String = listOfNotNull(
    years.takeIf { it > 0 }?.let { stringResource(R.string.billing_period_years, it) },
    months.takeIf { it > 0 }?.let { stringResource(R.string.billing_period_months, it) },
    weeks.takeIf { it > 0 }?.let { stringResource(R.string.billing_period_weeks, it) },
    days.takeIf { it > 0 }?.let { stringResource(R.string.billing_period_days, it) },
).joinToString(separator = " ")

@Composable
private fun LimitedOfferBadge(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.subscription_guide_badge),
        style = NRTypo.Pretendard.size14SemiBold,
        color = NRColor.Green,
        modifier = modifier
            .clip(RoundedCornerShape(50.dp))
            .background(NRColor.Green15)
            .padding(horizontal = 14.dp, vertical = 7.dp),
    )
}

@Composable
private fun Headline(
    plan: SubscriptionGuideUiState.Plan,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        if (plan.freeTrialPeriod != null) {
            // 체험 기간만 강조하고 뒤따르는 조사와 서술어는 한 줄로 이어서 보여준다.
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = NRColor.Green)) {
                        append(
                            stringResource(
                                R.string.subscription_guide_headline_highlight,
                                plan.freeTrialPeriod.toDisplayText(),
                            ),
                        )
                    }
                    append(stringResource(R.string.subscription_guide_headline))
                },
                style = NRTypo.Pretendard.size24Bold,
                color = NRColor.White,
            )
        } else {
            Text(
                text = stringResource(R.string.subscription_guide_headline_highlight_no_trial),
                style = NRTypo.Pretendard.size24Bold,
                color = NRColor.Green,
            )
            Text(
                text = stringResource(R.string.subscription_guide_headline_no_trial),
                style = NRTypo.Pretendard.size24Bold,
                color = NRColor.White,
            )
        }
    }
}

@Composable
private fun PriceCard(
    plan: SubscriptionGuideUiState.Plan,
    modifier: Modifier = Modifier,
) {
    // 체험 중이면 크게 보이는 금액이 0원이고 정가는 취소선으로 함께 보여준다.
    val priceSuffix = if (plan.isFreeTrial) plan.freeTrialPeriod else plan.recurringPeriod

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(NRColor.Sub1)
            .padding(20.dp),
    ) {
        if (plan.isFreeTrial) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // 취소선은 정가 금액에만 적용하고 결제 주기는 그대로 둔다.
                Text(
                    text = plan.recurringPrice,
                    style = NRTypo.Pretendard.size14,
                    color = NRColor.Gray01,
                    textDecoration = TextDecoration.LineThrough,
                )
                if (plan.recurringPeriod != null) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(
                            R.string.subscription_guide_price_period,
                            plan.recurringPeriod.toDisplayText(),
                        ),
                        style = NRTypo.Pretendard.size14,
                        color = NRColor.Gray01,
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.subscription_guide_discount_rate),
                    style = NRTypo.Caption.size12SemiBold,
                    color = NRColor.Dark01,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(NRColor.Green)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = plan.displayPrice,
                style = NRTypo.Pretendard.size32Bold,
                color = NRColor.White,
            )
            if (priceSuffix != null) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.subscription_guide_price_period, priceSuffix.toDisplayText()),
                    style = NRTypo.Pretendard.size14,
                    color = NRColor.Gray01,
                    modifier = Modifier.padding(bottom = 4.dp),
                )
            }
        }

        if (plan.isFreeTrial) {
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(NRColor.White12),
            )

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.subscription_guide_trial_end_label),
                    style = NRTypo.Pretendard.size14,
                    color = NRColor.Gray01,
                )
                Text(
                    text = plan.trialEndDate,
                    style = NRTypo.Pretendard.size14SemiBold,
                    color = NRColor.White,
                )
            }
        }
    }
}

@Composable
private fun BenefitItem(
    order: Int,
    benefit: Benefit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(NRColor.Sub1)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(NRColor.Green15),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "%02d".format(order),
                style = NRTypo.Poppins.size14,
                color = NRColor.Green,
            )
        }

        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = stringResource(benefit.titleRes),
                style = NRTypo.Pretendard.size16SemiBold,
                color = NRColor.White,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(benefit.descriptionRes),
                style = NRTypo.Pretendard.size12,
                color = NRColor.Gray01,
            )
        }
    }
}

@Composable
private fun Notes(
    plan: SubscriptionGuideUiState.Plan,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = if (plan.freeTrialPeriod != null) {
                stringResource(R.string.subscription_guide_note_1, plan.freeTrialPeriod.toDisplayText())
            } else {
                stringResource(
                    R.string.subscription_guide_note_1_no_trial,
                    plan.recurringPeriod?.toDisplayText().orEmpty(),
                )
            },
            style = NRTypo.Pretendard.size12,
            color = NRColor.Gray01,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (plan.isFreeTrial) {
                stringResource(R.string.subscription_guide_note_2)
            } else {
                stringResource(R.string.subscription_guide_note_2_no_trial)
            },
            style = NRTypo.Pretendard.size12,
            color = NRColor.Gray01,
        )
    }
}

@Composable
private fun BottomBar(
    plan: SubscriptionGuideUiState.Plan,
    onStartFreeTrialClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    0f to NRColor.Dark01.copy(alpha = 0f),
                    0.25f to NRColor.Dark01,
                )
            )
            .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = if (plan.freeTrialPeriod != null) {
                stringResource(R.string.subscription_guide_start_button, plan.freeTrialPeriod.toDisplayText())
            } else {
                stringResource(R.string.subscription_guide_start_button_no_trial)
            },
            style = NRTypo.Pretendard.size16SemiBold,
            color = NRColor.PrimaryButtonText,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(50.dp))
                .background(NRColor.PrimaryButtonBackground)
                .throttleClick(onClick = onStartFreeTrialClick)
                .padding(vertical = 18.dp),
        )
    }
}

private data class Benefit(
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
)

private val benefits = listOf(
    Benefit(
        titleRes = R.string.subscription_guide_benefit_1_title,
        descriptionRes = R.string.subscription_guide_benefit_1_description,
    ),
    Benefit(
        titleRes = R.string.subscription_guide_benefit_2_title,
        descriptionRes = R.string.subscription_guide_benefit_2_description,
    ),
    Benefit(
        titleRes = R.string.subscription_guide_benefit_3_title,
        descriptionRes = R.string.subscription_guide_benefit_3_description,
    ),
    Benefit(
        titleRes = R.string.subscription_guide_benefit_4_title,
        descriptionRes = R.string.subscription_guide_benefit_4_description,
    ),
    Benefit(
        titleRes = R.string.subscription_guide_benefit_5_title,
        descriptionRes = R.string.subscription_guide_benefit_5_description,
    ),
)

private val BOTTOM_BAR_HEIGHT = 120.dp
private val CLOSE_BUTTON_SIZE = 56.dp

@Preview(name = "무료 체험 자격 있음", showBackground = true, backgroundColor = 0xFF151516, heightDp = 1000)
@Composable
private fun SubscriptionGuideScreenPreview() {
    SubscriptionGuideScreen(
        state = SubscriptionGuideUiState(
            plan = SubscriptionGuideUiState.Plan(
                freeTrialPeriod = BillingPeriod(months = 3),
                trialEndDate = "2026. 10. 27.",
                displayPrice = "₩0",
                recurringPrice = "₩29,000",
                recurringPeriod = BillingPeriod(months = 1),
            ),
        ),
        onCloseClick = {},
        onStartFreeTrialClick = {},
    )
}

@Preview(name = "무료 체험 자격 없음", showBackground = true, backgroundColor = 0xFF151516, heightDp = 1000)
@Composable
private fun SubscriptionGuideScreenNoTrialPreview() {
    SubscriptionGuideScreen(
        state = SubscriptionGuideUiState(
            plan = SubscriptionGuideUiState.Plan(
                freeTrialPeriod = null,
                trialEndDate = "",
                displayPrice = "₩29,000",
                recurringPrice = "₩29,000",
                recurringPeriod = BillingPeriod(months = 1),
            ),
        ),
        onCloseClick = {},
        onStartFreeTrialClick = {},
    )
}
