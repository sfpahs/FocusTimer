package com.example.focustimer.Page

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.components.Legends
import co.yml.charts.common.extensions.getMaxElementInYAxis
import co.yml.charts.common.extensions.isNotNull
import co.yml.charts.common.model.LegendLabel
import co.yml.charts.common.model.LegendsConfig
import co.yml.charts.common.model.Point
import co.yml.charts.ui.barchart.StackedBarChart
import co.yml.charts.ui.barchart.models.BarData
import co.yml.charts.ui.barchart.models.BarPlotData
import co.yml.charts.ui.barchart.models.BarStyle
import co.yml.charts.ui.barchart.models.GroupBar
import co.yml.charts.ui.barchart.models.GroupBarChartData
import co.yml.charts.ui.barchart.models.SelectionHighlightData
import com.example.shared.Myfirebase.loadWeekHistoryData
import com.example.shared.model.WatchViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.TimeZone

@Preview
@Composable
fun weekHistoryApp(){
    val user = FirebaseAuth.getInstance().currentUser
    val viewModel by lazy { WatchViewModel.getInstance() }
    var isLoading by remember { mutableStateOf(true) }
    val timerSettings by viewModel.timerSettings.collectAsState()
    val scope = rememberCoroutineScope()
    var graphSetting by remember { mutableStateOf<Pair<LegendsConfig, GroupBarChartData>?>(null) }
    // TODO: 이후에 weekHistoryGraph에 세팅넣어서 색상하고 기록데이터 들고와서 그래프그리기
    // FIXME: 이거 해결해라 임마


    if(timerSettings.isEmpty()){
        Column(modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
            ){
            Text(text = "기록이 없습니다", color = Color.Gray)
            Log.e("History", "weekHistoryApp: ${timerSettings }}", )
        }
    }
    else {
        weekHistoryGraph(timerSettings){ x->
            graphSetting = Pair<LegendsConfig, GroupBarChartData>(x.first, x.second)
            isLoading = false

        }
        if(graphSetting.isNotNull()&&!isLoading){
            Column(modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.White),
                verticalArrangement = Arrangement.Center
            ) {
                StackedBarChart(  // 스택형 바 차트 컴포넌트
                    modifier = Modifier
                        .height(400.dp),
                    groupBarChartData = graphSetting!!.second
                )
                Legends(  // 차트 하단에 범례 표시
                    legendsConfig = graphSetting!!.first
                )
            }
        }
        else LoadingScreen(isLoading = isLoading)

    }
    user?.let {
        scope.launch {
            viewModel.loadTimerSettings(it.uid)
            delay(1000)
        }

    }

}

//그래프관련
fun weekHistoryGraph(timerSettings : List<com.example.shared.model.TimerSetting>, callback: (Pair<LegendsConfig, GroupBarChartData>) -> Unit) {
    val barSize = 5      // 각 그룹에 포함될 바의 개수 (3개 카테고리)
    val listSize = 7    // 차트에 표시할 그룹(X축 데이터 포인트)의 개수
    val yStepSize = 8

    //todo 데이터리스트 만드는것 수작업할것 기록 ->데이터리스트
    var groupBarData : List<GroupBar>

    createCustomGroupBarData(listSize,  barSize, callback = { newData ->
        groupBarData = newData

        val colorPaletteList = mutableListOf<Color>()

        val listtest = mutableListOf<LegendLabel>()

        timerSettings.map {element ->
            colorPaletteList.add(Color(element.backgroundColor))
            listtest.add(LegendLabel(color = Color(element.backgroundColor),name = element.name))
        }
        val legendsConfig = LegendsConfig(
            legendLabelList = listtest,
            gridColumnCount = 3  // 범례를 3열로 표시
        )

        val xAxisData = AxisData.Builder()
            .axisStepSize(30.dp)           // X축 눈금 간격
            .steps(listSize - 1)           // X축 눈금 개수 (9개)
            .startDrawPadding(16.dp)       // 시작 지점 여백
            .labelData { index ->
                val daysOfWeek = listOf("월", "화", "수", "목", "금", "토", "일")
                daysOfWeek[index % daysOfWeek.size] // 인덱스를 요일로 매핑
            }
            .build()

        val yAxisData = AxisData.Builder()
            .steps(yStepSize)              // Y축 눈금 개수 (10개)
            .labelAndAxisLinePadding(20.dp)// 라벨과 축 사이 간격
            // 축 오프셋
            .labelData { index ->          // Y축 라벨 생성 로직
                // 각 그룹의 총 높이 계산
                val valueList = mutableListOf<Float>()
                groupBarData.map { groupBar ->
                    var yMax = 24f
                    valueList.add(yMax)
                }

                // 최대값 계산 및 라벨 생성
                val maxElementInYAxis = getMaxElementInYAxis(valueList.maxOrNull() ?: 0f, yStepSize)
                (index * (maxElementInYAxis / yStepSize)).toString()

            }
            .topPadding(36.dp)
            .backgroundColor(Color.White)// 상단 여백
            .build()

        val groupBarPlotData = BarPlotData(
            groupBarList = groupBarData,
            barStyle = BarStyle(
                barWidth = 35.dp,  // 바 너비
                selectionHighlightData = SelectionHighlightData(
                    isHighlightFullBar = true,  // 바 전체 강조
                    groupBarPopUpLabel = { name, value ->
                        "총 시간: ${String.format("%.1f", value*3)}시간"},
                        highlightTextBackgroundColor = Color.White

                )
            ),
            barColorPaletteList = colorPaletteList
        )
        val groupBarChartData = GroupBarChartData(
            barPlotData = groupBarPlotData,
            xAxisData = xAxisData,
            yAxisData = yAxisData,
            paddingBetweenStackedBars = 4.dp,  // 스택 바 사이 간격
            drawBar = { drawScope, barChartData, barStyle, drawOffset, height, barIndex ->
                with(drawScope) {
                    drawRoundRect(
                        color = colorPaletteList[barIndex],
                        topLeft = drawOffset,
                        size = Size(barStyle.barWidth.toPx(), height),
                        cornerRadius = CornerRadius(5.dp.toPx(), 5.dp.toPx()),
                        style = barStyle.barDrawStyle,
                        blendMode = barStyle.barBlendMode
                    )
                }
            },
        )

        callback(Pair(legendsConfig,groupBarChartData))

    })



}

private fun createCustomGroupBarData(listSize: Int, barSize: Int, callback: (List<GroupBar>) -> Unit) {
    val uid = FirebaseAuth.getInstance().uid
    loadWeekHistoryData(
        uid = uid!!,
        startMonday = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
    ) { data ->
        val groupBarList = mutableListOf<GroupBar>()
        val timeZone = TimeZone.getDefault()
        for (i in 0 until listSize) {
            val barList = mutableListOf<BarData>()
            val daysOfWeek = listOf("월", "화", "수", "목", "금", "토", "일")

            if (data[i].isNotEmpty()) {
                for (j in 0 until data[i].size) {
                    barList.add(
                        BarData(
                            point = Point(x = i.toFloat(), y = data[i][j].first.toFloat() / 3 / 60),
                            label = "Category ${j + 1}",
                            description = "${daysOfWeek[i]}, Category ${j + 1}: ${data[i][j].first}"
                        )
                    )
                }
            } else {
                barList.add(
                    BarData(
                        point = Point(x = i.toFloat(), y = 0.1f),
                        label = "No Data",
                        description = "${daysOfWeek[i]}, No Data"
                    )
                )
            }

            groupBarList.add(
                GroupBar(
                    label = daysOfWeek[i],
                    barList = barList
                )
            )
        }

        callback(groupBarList)
    }
}