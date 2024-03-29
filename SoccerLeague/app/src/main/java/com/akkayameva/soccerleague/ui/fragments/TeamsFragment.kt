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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieAnimationSpec

import com.akkayameva.soccerleague.R
import com.akkayameva.soccerleague.data.db.Team
import com.akkayameva.soccerleague.ui.base.ApiResultUIModel
import com.akkayameva.soccerleague.ui.base.Event
import com.akkayameva.soccerleague.ui.theme.SoccerLeagueTheme
import com.akkayameva.soccerleague.ui.viewmodel.SoccerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.akkayameva.soccerleague.ui.base.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class TeamsFragment : Fragment() {

    private val viewModel: SoccerViewModel by sharedViewModel()


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
                        Scaffold(topBar = {

                        },
                            //floatingActionButtonPosition = FabPosition.End,
                            floatingActionButton = {
                                FixtureButton {
                                    lifecycleScope.launch {
                                        withContext(Dispatchers.IO) {
                                            viewModel.createFixture(viewModel.listTeams.size)
                                        }

                                        withContext(Dispatchers.Main) {
                                            findNavController().navigate(R.id.action_to_fixtureFragment)
                                        }
                                    }
                                }
                            },
                            content = {
                                Teams()
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getTeams()
    }

    @Composable
    fun Teams() {
        val resultModel by viewModel.teamsState.observeAsState(
            initial = ApiResultUIModel(
                showProgress = true, showSuccess = Event(
                    content = Result.Success(
                        emptyList()
                    )
                )
            )
        )
        ListTeams(resultModel)
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun ListTeams(resultModel: ApiResultUIModel<List<Team>?>) {
        if (resultModel.showProgress) {
            val animationSpec = remember { LottieAnimationSpec.RawRes(R.raw.soccer) }
            LottieAnimation(
                animationSpec,
                modifier = Modifier.fillMaxSize()
            )
        } else if (!resultModel.showSuccess.consumed) {

            resultModel.showSuccess.consume()?.let { result ->
                when (result) {
                    is Result.Error -> {

                    }
                    is Result.Success -> {
                        val listTeams = result.data ?: mutableListOf()
                        LazyColumn(Modifier.fillMaxWidth()) {

                            val listTeamsSorted = listTeams.sortedBy {
                                it.team_name
                            }

                            val grouped = listTeamsSorted.groupBy {
                                it.team_name?.first()
                            }
                            grouped.keys.forEach { key ->
                                val initial = key
                                val teams = grouped[key]?.toList() ?: mutableListOf()
                                stickyHeader {
                                    CharacterHeader(char = initial!!)
                                }
                                items(teams) { team ->
                                    Card(
                                        shape = RoundedCornerShape(3.dp),
                                        modifier = Modifier
                                            .padding(bottom = 6.dp, top = 6.dp)
                                            .fillMaxWidth(),
                                    ) {
                                        Row {
                                            Card(
                                                modifier = Modifier.size(48.dp),
                                                shape = CircleShape,
                                                elevation = 2.dp
                                            ) {
                                                Image(
                                                    painterResource(R.drawable.ic_teams),
                                                    contentDescription = "",
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = team.team_name!!,
                                                modifier = Modifier.padding(all = 6.dp),
                                                color = MaterialTheme.colors.secondary
                                            )
                                        }
                                    }
                                }

                            }
                        }
                    }
                }

            }

        }

    }

    @Composable
    fun CharacterHeader(char: Char) {
        Card(backgroundColor = MaterialTheme.colors.primary) {
            Text(
                text = char.toString(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
            )
        }
    }

    @Composable
    fun FixtureButton(onClick: () -> Unit) {
        ExtendedFloatingActionButton(
            icon = { Icon(Icons.Filled.List, "") },
            text = { Text(text = "Draw Fixture") },
            onClick = {
                onClick()
            },
            elevation = FloatingActionButtonDefaults.elevation(8.dp)
        )

    }


    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        SoccerLeagueTheme {
            Teams()
        }
    }
}