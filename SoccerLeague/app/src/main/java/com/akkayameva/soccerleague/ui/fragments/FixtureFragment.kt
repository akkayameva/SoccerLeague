package com.akkayameva.soccerleague.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieAnimationSpec
import com.akkayameva.soccerleague.R
import com.akkayameva.soccerleague.data.db.Fixture
import com.akkayameva.soccerleague.ui.base.ApiResultUIModel
import com.akkayameva.soccerleague.ui.base.Result
import com.akkayameva.soccerleague.ui.theme.SoccerLeagueTheme
import com.akkayameva.soccerleague.ui.viewmodel.FixtureData

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

                        val navController = rememberNavController()

                        val bottomNavigationItems = listOf(
                            BottomNavigationScreens.Round1,
                            BottomNavigationScreens.Round2
                        )

                        Scaffold(
                            bottomBar = {
                                AppBottomNavigation(navController, bottomNavigationItems)
                            },
                        ) {
                            BottomNavigationConfigurations(navController)
                        }
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
    fun CalculateFixture(matchesMap: Map<Int, FixtureData>, offsetMargin: Int = 0) {
        val showProgress by viewModel.showProgressLiveData.observeAsState(initial = true)
        if (showProgress) {
            val animationSpec = remember { LottieAnimationSpec.RawRes(R.raw.fixture) }
            LottieAnimation(
                animationSpec,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            FixturePager(matchesMap = matchesMap, offsetMargin = offsetMargin)
        }
    }

    @OptIn(ExperimentalPagerApi::class)
    @Composable
    fun FixturePager(matchesMap: Map<Int, FixtureData>, offsetMargin: Int = 0) {

        val pagerState = rememberPagerState(pageCount = matchesMap.keys.size)
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
                matchesMap.keys.forEachIndexed { index, key ->
                    Tab(
                        text = {
                            Text(
                                "Week ${key + 1}",
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
                val matchesData = matchesMap[pagerIndex + offsetMargin]!!
                Column {
                    LazyColumn(Modifier.weight(1f)) {
                        items(matchesData.listMatches?.toTypedArray()!!) { match ->
                            Match(teamData = match)
                        }
                        item {
                            Spacer(modifier = Modifier.padding(all = 10.dp))
                        }
                    }

                    Spacer(modifier = Modifier.padding(all = 5.dp))
                    PassingTeam("Passing Team:  ${matchesData.passingTeam}")
                    Spacer(modifier = Modifier.padding(all = 30.dp))
//                    item {
//
//                    }
//
//                    item {
//
//                    }
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

    @Composable
    private fun BottomNavigationConfigurations(
        navController: NavHostController
    ) {
        NavHost(navController, startDestination = BottomNavigationScreens.Round1.route) {
            composable(BottomNavigationScreens.Round1.route) {
                CalculateFixture(matchesMap = viewModel.matchesMap, offsetMargin = 0)
            }
            composable(BottomNavigationScreens.Round2.route) {
                CalculateFixture(
                    matchesMap = viewModel.matchesRound2Map,
                    offsetMargin = viewModel.matchesMap.size
                )
            }
        }
    }

    @Composable
    private fun AppBottomNavigation(
        navController: NavHostController,
        items: List<BottomNavigationScreens>
    ) {
        BottomNavigation(backgroundColor = MaterialTheme.colors.surface) {
            val currentRoute = currentRoute(navController)
            items.forEach { screen ->
                BottomNavigationItem(
                    icon = {
                        Image(
                            painter = painterResource(id = screen.iconResId),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    label = { Text(stringResource(id = screen.resourceId)) },
                    selected = currentRoute == screen.route,
                    onClick = {
                        // This if check gives us a "singleTop" behavior where we do not create a
                        // second instance of the composable if we are already on that destination
                        if (currentRoute != screen.route) {
                            navController.navigate(
                                route = screen.route, builder = {
                                    this.launchSingleTop = true
                                    this.popUpTo = navController.graph.startDestination
                                }
                            )
                        }
                    }
                )
            }
        }
    }

    @Composable
    private fun currentRoute(navController: NavHostController): String? {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        return navBackStackEntry?.arguments?.getString(KEY_ROUTE)
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        SoccerLeagueTheme {
            CalculateFixture(matchesMap = viewModel.matchesMap)
        }
    }
}

sealed class BottomNavigationScreens(
    val route: String,
    @StringRes val resourceId: Int,
    val iconResId: Int
) {
    object Round1 : BottomNavigationScreens("Round1", R.string.round1, R.drawable.ic_round )
    object Round2 : BottomNavigationScreens("Round2", R.string.round2, R.drawable.ic_round)
}