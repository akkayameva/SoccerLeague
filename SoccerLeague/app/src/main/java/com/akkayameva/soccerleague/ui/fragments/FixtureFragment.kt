package com.akkayameva.soccerleague.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieAnimationSpec
import com.akkayameva.soccerleague.R
import com.akkayameva.soccerleague.data.db.Fixture
import com.akkayameva.soccerleague.ui.base.ApiResultUIModel
import com.akkayameva.soccerleague.ui.base.Result
import com.akkayameva.soccerleague.ui.theme.SoccerLeagueTheme

import com.akkayameva.soccerleague.ui.viewmodel.MatchData
import com.akkayameva.soccerleague.ui.viewmodel.SoccerViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
class FixtureFragment : Fragment() {

    val viewModel: SoccerViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        return ComposeView(requireContext()).apply {
            setContent {
                SoccerLeagueTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(color = MaterialTheme.colors.background) {
                        CalculateFixture()
                        lifecycleScope.launch(Dispatchers.IO) {
                            delay(1000)
                            withContext(Dispatchers.Main) {
                                viewModel.showProgressLiveData.postValue(false)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val listTeamsNames = mutableListOf<String>()
        viewModel.listTeams.forEach {
            listTeamsNames.add(it.team_name!!)
        }
        viewModel.listMatchesForTwoRounds()
    }

    @Composable
    fun CalculateFixture() {
        val showProgress by viewModel.showProgressLiveData.observeAsState(initial = true)
        if (showProgress) {
            val animationSpec = remember { LottieAnimationSpec.RawRes(R.raw.fixture) }
            LottieAnimation(
                animationSpec,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            ListFixture()
        }
    }

    @OptIn(ExperimentalPagerApi::class)
    @Composable
    fun FixturePager() {

        val pagerState = rememberPagerState(pageCount = viewModel.matchesMap.keys.size)
        Column {
            ScrollableTabRow(
                // Our selected tab is our current page
                selectedTabIndex = pagerState.currentPage,
                // Override the indicator, using the provided pagerTabIndicatorOffset modifier
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                    )
                },
            ) {
                // Add tabs for all of our pages
                viewModel.matchesMap.keys.forEachIndexed { index, key ->
                    Tab(
                        text = {
                            Text(
                                "Week ${index + 1}",
                                color = Color.Black
                            )
                        },
                        selected = pagerState.currentPage == index,
                        onClick = {
                            lifecycleScope.launch(Dispatchers.Main) {
                                pagerState.scrollToPage(page = index)
                            }
                        },
                    )
                }
            }

            HorizontalPager(state = pagerState, Modifier.padding(all = 5.dp)) { pagerIndex ->

                val matchesData = viewModel.matchesMap[pagerIndex]!!
                Column(Modifier.fillMaxSize()) {
                    LazyColumn(Modifier.weight(1f)) {
                        items(matchesData.listMatches?.toTypedArray()!!) { match ->
                            Match(teamData = match)
                        }
                    }
                    PassingTeam("Passing Team:  ${matchesData.passingTeam}")
                }
            }
        }

    }

    @Composable
    fun PassingTeam(teamName: String) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Card(
                backgroundColor = Color.Red,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = teamName,
                    style = MaterialTheme.typography.body1,
                    modifier =
                    Modifier
                        .padding(
                            all = 30.dp
                        ),
                    color = MaterialTheme.colors.onSecondary
                )
            }
        }

    }

    @Composable
    fun Match(teamData: MatchData) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Card(
                backgroundColor = MaterialTheme.colors.primary,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .padding(
                        all = 5.dp
                    )
            ) {
                Row() {
                    Text(
                        text = teamData.teamHome,
                        style = MaterialTheme.typography.body1,
                        modifier =
                        Modifier
                            .padding(
                                all = 10.dp
                            )
                            .alignByBaseline(),
                        color = MaterialTheme.colors.onPrimary
                    )
                    Image(
                        painterResource(id = R.drawable.ic_teamhome),
                        contentDescription = null,
                        modifier = Modifier
                            .size(width = 40.dp, height = 40.dp)
                            .aspectRatio(1f)
                            .padding(
                                all = 5.dp
                            )
                            .alignByBaseline()
                    )
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                text = "vs",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(
                    all = 5.dp
                ),
                color = MaterialTheme.colors.onPrimary
            )
            Card(
                backgroundColor = MaterialTheme.colors.primary,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .padding(
                        all = 5.dp
                    )
                    .alignByBaseline()
            ) {
                Row() {
                    Image(
                        painterResource(id = R.drawable.ic_teamaway),
                        contentDescription = null,
                        modifier = Modifier
                            .size(width = 40.dp, height = 40.dp)
                            .padding(
                                all = 5.dp
                            )
                            .aspectRatio(1f)
                    )
                    Text(
                        text = teamData.teamAway,
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier
                            .padding(
                                all = 10.dp
                            )
                            .alignByBaseline(),
                        color = MaterialTheme.colors.onPrimary
                    )
                }
            }
        }

    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun ListFixture() {
        FixturePager()
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        SoccerLeagueTheme {
            CalculateFixture()
        }
    }
}