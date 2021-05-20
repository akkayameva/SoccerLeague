package com.akkayameva.soccerleague.ui.fragments

import   android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment

import com.akkayameva.soccerleague.data.db.Fixture
import com.akkayameva.soccerleague.ui.theme.SoccerLeagueTheme
import com.akkayameva.soccerleague.ui.viewmodel.MatchData
import com.akkayameva.soccerleague.ui.viewmodel.SoccerViewModel
import com.akkayameva.soccerleague.ui.base.Result

import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class FixtureFragment : Fragment() {

    val viewModel: SoccerViewModel by sharedViewModel()

    companion object {
        private val ARG_PAGE: String = "arg_page"
        fun newInstance(page: Int): FixtureFragment {
            val args = Bundle()
            args.putInt(ARG_PAGE, page)
            val fragment = FixtureFragment()
            fragment.arguments = args
            return fragment
        }
    }

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
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getFixture(viewModel.listTeams.size)
//        val roundCount = viewModel.listTeams.size - 1
//        viewModel.calculateFixForSingle(rounds_count = roundCount, listTeams = viewModel.listTeams)

        val listTeamsNames = mutableListOf<String>()
        viewModel.listTeams.forEach {
            listTeamsNames.add(it.team_name!!)
        }
        viewModel.listMatches(listTeamsParam = listTeamsNames)
    }

    @Composable
    fun CalculateFixture() {
        ListFixture()
    }

    @OptIn(ExperimentalPagerApi::class)
    @Composable
    fun FixturePager(listFixture: List<Fixture>) {

        val pagerState = rememberPagerState(pageCount = viewModel.matchesMap.keys.size)

        HorizontalPager(state = pagerState) { pagerIndex ->

            val matchesData = viewModel.matchesMap[pagerIndex]!!

            Column {
                Text("Week ${pagerIndex + 1}")
                if (pagerIndex < viewModel.listPassingTeams.size) {
                    Text("Passing Team:  ${viewModel.listPassingTeams[pagerIndex]}")
                }
                LazyColumn(Modifier.fillMaxWidth()) {
                    items(matchesData.toTypedArray()) { match ->
                        Match(teamData = match)
                    }
                }
            }
        }

    }

    @Composable
    fun Match(teamData: MatchData) {
        Card(
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .padding(bottom = 6.dp, top = 6.dp)
                .fillMaxWidth(),
            elevation = 8.dp
        ) {
            Text(
                text = "${teamData.teamHome} vs ${teamData.teamAway}",
                modifier = Modifier.padding(all = 6.dp)
            )
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun ListFixture() {
        val resultModel by viewModel.fixtureState.observeAsState()
        resultModel?.showSuccess?.consume()?.let { result ->
            when (result) {
                is Result.Error -> {

                }
                is Result.Success -> {
                    val listFixture = result.data ?: mutableListOf()
                    FixturePager(listFixture = listFixture)
                }
            }
        }

    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        SoccerLeagueTheme {
            CalculateFixture()
        }
    }
}