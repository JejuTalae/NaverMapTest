package com.example.navermaptest

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.navermaptest.ui.theme.Typography
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.compose.CameraPositionState
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.LocationTrackingMode
import com.naver.maps.map.compose.MapProperties
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.Marker
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.compose.rememberFusedLocationSource
import com.naver.maps.map.compose.rememberMarkerState
import com.naver.maps.map.overlay.OverlayImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalNaverMapApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LocationTrackingScreen(navController: NavHostController) {

    val bottomSheetState = rememberBottomSheetScaffoldState()

    val coroutineScope = rememberCoroutineScope()

    BottomSheetScaffold(scaffoldState = bottomSheetState,
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopSearchBar(modifier = Modifier.statusBarsPadding())
        },
        sheetContent = {
            BottomSheetContent(
                modifier = Modifier.navigationBarsPadding(), navController = navController
            )
        },
        sheetPeekHeight = 100.dp
    ) { contentPadding ->
        Column(modifier = Modifier.padding(contentPadding)) {
            val options = listOf(
                stringResource(R.string.location_tracking_mode_none),
                stringResource(R.string.location_tracking_mode_no_follow),
                stringResource(R.string.location_tracking_mode_follow),
                stringResource(R.string.location_tracking_mode_face),
            )
            val (selectedOption, onOptionSelected) = remember { mutableStateOf(2) }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .selectableGroup()
                    .background(Color(0xFFF2FCFF)),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                options.forEachIndexed { index, text ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .selectable(
                                selected = selectedOption == index,
                                onClick = { onOptionSelected(index) },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedOption == index,
                            onClick = null,
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color.Black, unselectedColor = Color.Black
                            )
                        )
                        Text(
                            text = text, modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }

            val locationTrackingMode = when (selectedOption) {
                0 -> LocationTrackingMode.None
                1 -> LocationTrackingMode.NoFollow
                2 -> LocationTrackingMode.Follow
                3 -> LocationTrackingMode.Face
                else -> throw IllegalStateException()
            }
            val isCompassEnabled = when (locationTrackingMode) {
                LocationTrackingMode.Follow, LocationTrackingMode.Face -> true

                else -> false
            }
            val jeju = LatLng(33.4996, 126.5312)
            val cameraPositionState: CameraPositionState = rememberCameraPositionState {
                // 카메라 초기 위치를 설정합니다.
                position = CameraPosition(jeju, 14.0)
            }
            NaverMap(
                cameraPositionState = cameraPositionState,
                locationSource = rememberFusedLocationSource(
                    isCompassEnabled = isCompassEnabled,
                ),
                properties = MapProperties(
                    locationTrackingMode = locationTrackingMode, isTransitLayerGroupEnabled = true
                ),
                uiSettings = MapUiSettings(
                    isLocationButtonEnabled = true,
                    isCompassEnabled = isCompassEnabled,
                ),
                onOptionChange = {
                    cameraPositionState.locationTrackingMode?.let {
                        onOptionSelected(it.ordinal)
                    }
                },
            ) {
                Marker(
                    state = rememberMarkerState(
                        position = LatLng(33.4996, 126.5312)
                    ),
                    icon = OverlayImage.fromResource(R.drawable.marker),
                    width = 45.dp,
                    height = 45.dp,
                    onClick = {
                        coroutineScope.launch {
                            bottomSheetState.bottomSheetState.expand()
                        }
                        true
                    }// bottomsheet 가 올라오도록
                )
                Marker(state = rememberMarkerState(
                    position = LatLng(33.5104, 126.4914)
                ),
                    icon = OverlayImage.fromResource(R.drawable.marker),
                    width = 45.dp,
                    height = 45.dp,
                    onClick = {
                        coroutineScope.launch {
                            bottomSheetState.bottomSheetState.expand()
                        }
                        true
                    })
            }
        }
    }
}

@Composable
fun BottomSheetContent(modifier: Modifier = Modifier, navController: NavHostController) {
    Column(
        modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("선택된 정류소 이름", style = Typography.titleLarge)
        Spacer(modifier = Modifier.height(10.dp))
        Text("방향 명시 ex) [북] 방면", style = Typography.labelSmall)
        Spacer(modifier = Modifier.height(15.dp))
        Row() {
            Button(
                onClick = { navController.navigate("busStop") },
                border = BorderStroke(2.dp, Color(0xff41C3E7))
            ) {
                Text("  출발  ", style = Typography.labelSmall.copy(color = Color(0xff41C3E7)))
            }
            Spacer(modifier = Modifier.width(20.dp))
            Button(
                onClick = { /*TODO*/ },
                border = BorderStroke(2.dp, Color(0xff41C3E7)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xff41C3E7)
                )
            ) {
                Text("  도착  ", style = Typography.labelSmall.copy(color = Color.White))
            }
        }
    }
}
